package com.zcw.cmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zcw.cmall.order.constant.OrderConstant;
import com.zcw.cmall.order.dao.OrderItemDao;
import com.zcw.cmall.order.entity.OrderItemEntity;
import com.zcw.cmall.order.entity.PaymentInfoEntity;
import com.zcw.cmall.order.enume.OrderStatusEnum;
import com.zcw.cmall.order.feign.CartFeignService;
import com.zcw.cmall.order.feign.GoodsFeignService;
import com.zcw.cmall.order.feign.MemberFeignService;
import com.zcw.cmall.order.feign.StockFeignService;
import com.zcw.cmall.order.interceptor.LoginUserInterceptor;
import com.zcw.cmall.order.service.OrderItemService;
import com.zcw.cmall.order.service.PaymentInfoService;
import com.zcw.cmall.order.to.OrderCreateTo;
import com.zcw.cmall.order.vo.*;
import com.zcw.common.exception.NoStockException;
import com.zcw.common.to.OrderCloseTo;
import com.zcw.common.utils.R;
import com.zcw.common.vo.MemberRespVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.order.dao.OrderDao;
import com.zcw.cmall.order.entity.OrderEntity;
import com.zcw.cmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    private ThreadLocal<OrderSubmitVo> OrderSubmitVoThreadLocal = new ThreadLocal<>();

    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    GoodsFeignService goodsFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    StockFeignService stockFeignService;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo vo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        //多线程不能共享ThreadLocal数据，各自都有自己的ThreadLocal
        //所有先获取主线程的RequestAttributes，再给每个异步任务的
        // RequestContextHolder（request上下文使用的是ThreadLocal）放进数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1.远程查询收货地址列表
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            vo.setMemberAddressVos(address);
        }, threadPoolExecutor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //2.远程查询购物车中所有选中的购物项
            //原生远程调用没有使用拦截器对feign的远程调用进行增强，template直接封装成新的request，没有header请求头的新的请求
            //所有配置了feign的拦截器，将cookie放进header中
            List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            vo.setItems(currentUserCartItems);
        }, threadPoolExecutor).thenRunAsync(() -> {
            List<OrderItemVo> items = vo.getItems();
            //批量查询商品得库存信息
            List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skusHasStock = stockFeignService.getSkusHasStock(skuIds);
            List<SkuStockVo> data = skusHasStock.getData(new TypeReference<List<SkuStockVo>>() {});
            if (data!=null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                vo.setStocks(map);
            }
        },threadPoolExecutor);

        //3.查询用户积分
        Integer integration = memberRespVo.getIntegration();
        vo.setIntegration(integration);


        //防重令牌

        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,30, TimeUnit.MINUTES);
        vo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture,cartFuture).get();
        return vo;
    }

    /**
     * 下单
     * @param vo 用来和页面交互的， to 不同服务之间传输的对象
     * @return
     */
    //@GlobalTransactional 这是Seata方式的分布式事务，但是并发不高，性能不好
    @Transactional
    @Override
    public OrderSubmitRespVo submitOrder(OrderSubmitVo vo) {
        //放进本地线程共享页面传来的数据
        OrderSubmitVoThreadLocal.set(vo);
        //下单响应数据
        OrderSubmitRespVo respVo = new OrderSubmitRespVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        respVo.setCode(0);
        //验证令牌[对比和删除要保证原子性
        String script= "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //0 失败  1 成功
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L){
            //0成功  错误状态码
            respVo.setCode(1);
            return respVo;
        }else{
            //成功
            OrderCreateTo order = createOrder();
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                //金额对比成功
                //保存订单到数据库
                saveOrder(order);
                //库存锁定,锁定失败了，需要撤销保存订单的方法，所以这是个事务的，加@Transactional注解，开启事务
                //只要有异常，抛了异常，事务就回滚了
                StockSkuLockVo lockVo = new StockSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                R r = stockFeignService.orderLockStock(lockVo);

                if (r.getCode() == 0){
                    //锁定成功
                    respVo.setOrder(order.getOrder());
//                    int i= 10/0;
                    //订单创建成功，给MQ发送成功消息                                                                  为什么能感知到order.getOrder()才是正确的数据
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                    return respVo;
                }else{
                    //锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            }else {
                respVo.setCode(2);
                return respVo;
            }
        }

//        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        if (orderToken!=null && orderToken.equals(redisToken)){
//            //通过
//            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        }else {
//
//        }
    }

    /**
     * 根据订单号，获取订单
     * @param orderSn
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    /**
     * 自动关单
     * @param orderEntity
     */
    @Override
    public void closeOrder(OrderEntity orderEntity) {
        //查询订单的支付状态
        OrderEntity byId = this.getById(orderEntity.getId());
        if (byId.getStatus()==OrderStatusEnum.CREATE_NEW.getCode()){
            //关闭订单，修改订单状态
            //
            OrderEntity update = new OrderEntity();
            update.setId(orderEntity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            //订单解锁成功，再给MQ发送消息
            OrderCloseTo orderCloseTo = new OrderCloseTo();
            BeanUtils.copyProperties(byId,orderCloseTo);
            try{
                //每一条消息做好日志记录，给数据库中保存
                //定期扫描数据库，将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange","order.release.other",orderCloseTo);

            }catch (Exception e){

            }
        }
    }

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);

        // .setScale(2, BigDecimal.ROUND_UP) 取两位小数，如果出现0.0001的情况，向上取值
        BigDecimal totalAmount = orderEntity.getTotalAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(totalAmount.toString());
        payVo.setOut_trade_no(orderEntity.getOrderSn());

        List<OrderItemEntity> items = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = items.get(0);
        String spuName = orderItemEntity.getSpuName();
        payVo.setSubject(spuName);
        payVo.setBody(spuName);
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(orderItemEntities);
            return order;
        }).collect(Collectors.toList());

        page.setRecords(collect);
        return new PageUtils(page);
    }

    /**
     * 支付宝是否支付处理
     * @param vo
     * @return
     */
    @Override
    public String handleAlipayed(PayAsyncVo vo) {
        //保存支付流水单
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);
        //修改订单的状态
        if (vo.getTrade_status().equals("TRADE_SUCCESS")||vo.getTrade_status().equals("TRADE_FINISHED")){
            String outTradeNo = vo.getOut_trade_no();
            //this.updateOrderStatus(outTradeNo,OrderStatusEnum.PAYED.getCode());
            this.baseMapper.updateOrderStatus(outTradeNo,OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

//    private void updateOrderStatus(String outTradeNo, Integer code) {
//    }

    //保存订单
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
//        this.baseMapper.insert(orderEntity);
        //使用自己的service  OrderService
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
//        orderItemDao
        orderItemService.saveBatch(orderItems);

    }


    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        //生成订单号
        String orderSn = IdWorker.getTimeId();
        //1.创建订单
        OrderEntity orderEntity = buildOrder(orderSn);

        //2.创建所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        //3.验价
        computePrice(orderEntity,itemEntities);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(itemEntities);
        return orderCreateTo;
    }

    /**计算价格*/
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal total = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal giftIntegration = new BigDecimal("0.0");
        BigDecimal giftGrowth = new BigDecimal("0.0");

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            BigDecimal realAmount = orderItemEntity.getRealAmount();
            coupon = coupon.add(orderItemEntity.getCouponAmount());
            integration = integration.add(orderItemEntity.getIntegrationAmount());
            promotion = promotion.add(orderItemEntity.getPromotionAmount());
            total = total.add(realAmount);
            giftIntegration.add(new BigDecimal(orderItemEntity.getGiftIntegration().toString()));
            giftGrowth.add(new BigDecimal(orderItemEntity.getGiftGrowth().toString()));

        }
        //订单价格相关
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));

        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);


        //积分
        orderEntity.setIntegration(giftIntegration.intValue());
        orderEntity.setGrowth(giftGrowth.intValue());
        //删除状态
        orderEntity.setDeleteStatus(0);//0 未删除

    }

    /**
     * 创建订单
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(memberRespVo.getId());
        //获取收货地址，上面从页面传来的vo放进ThreadLocal中的数据
        OrderSubmitVo orderSubmitVo = OrderSubmitVoThreadLocal.get();
        R fare = stockFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo data = fare.getData(new TypeReference<FareVo>() {});
        //设置运费
        BigDecimal fare1 = data.getFare();
        entity.setFreightAmount(fare1);
        //设置收货人信息
        entity.setReceiverProvince(data.getAddress().getProvince());
        entity.setReceiverCity(data.getAddress().getCity());
        entity.setReceiverDetailAddress(data.getAddress().getDetailAddress());
        entity.setReceiverName(data.getAddress().getName());
        entity.setReceiverPhone(data.getAddress().getPhone());
        entity.setReceiverPostCode(data.getAddress().getPostCode());
        entity.setReceiverRegion(data.getAddress().getRegion());

        //设置订单状态
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //订单过期时间
        entity.setAutoConfirmDay(7);
        return entity;
    }

    /**
     * 构建订单项
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //根据登陆的用户查购物车
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems!=null && currentUserCartItems.size()>0){
            List<OrderItemEntity> collect = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     *构建每一个订单项
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {

        OrderItemEntity orderItemEntity = new OrderItemEntity();

        Long skuId = cartItem.getSkuId();
        R r = goodsFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {});

        orderItemEntity.setSpuId(data.getId());
        orderItemEntity.setSpuBrand(data.getBrandId().toString());
        orderItemEntity.setSpuName(data.getSpuName());
        orderItemEntity.setCategoryId(data.getCatalogId());


        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        orderItemEntity.setSkuPic(cartItem.getImage());
        //spring家的,将集合按照指定的分隔符转成String
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        orderItemEntity.setSkuQuantity(cartItem.getCount());

        //intValue()
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal orgin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        BigDecimal real = orgin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(real);
        return orderItemEntity;
    }
}
