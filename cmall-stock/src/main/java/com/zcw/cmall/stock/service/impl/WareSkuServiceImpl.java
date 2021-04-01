package com.zcw.cmall.stock.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zcw.cmall.stock.entity.WareOrderTaskDetailEntity;
import com.zcw.cmall.stock.entity.WareOrderTaskEntity;
import com.zcw.cmall.stock.feign.GoodsFeignService;
import com.zcw.cmall.stock.feign.OrderFeignService;
import com.zcw.cmall.stock.service.WareOrderTaskDetailService;
import com.zcw.cmall.stock.service.WareOrderTaskService;
import com.zcw.cmall.stock.vo.OrderItemVo;
import com.zcw.cmall.stock.vo.OrderVo;
import com.zcw.cmall.stock.vo.SkuHasStockVo;
import com.zcw.cmall.stock.vo.StockSkuLockVo;
import com.zcw.common.exception.NoStockException;
import com.zcw.common.to.OrderCloseTo;
import com.zcw.common.to.mq.StockDetailTo;
import com.zcw.common.to.mq.StockLockedTo;
import com.zcw.common.utils.R;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.stock.dao.WareSkuDao;
import com.zcw.cmall.stock.entity.WareSkuEntity;
import com.zcw.cmall.stock.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

//@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    OrderFeignService orderFeignService;
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    WareOrderTaskService wareOrderTaskService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    GoodsFeignService goodsFeignService;



    /**
     * 库存解锁方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    private void unLockStock(Long skuId, Long wareId,Integer num,Long taskDetailId){
        wareSkuDao.unLockStock(skuId,wareId,num);
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(taskDetailId);
        wareOrderTaskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        String stockId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(stockId)){
            wrapper.eq("ware_id",stockId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities==null || wareSkuEntities.size() == 0){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);

            //如果因为各种原因，远程调用不成功，会触发事务回滚，这里可以try一下，catch到异常不进行处理，继续往下走,不回滚
            //TODO 还有一种办法
            try{
                R info = goodsFeignService.info(skuId);
                //Map中键都是String的，值时Object的
                Map<String ,Object> data = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }

            wareSkuDao.insert(wareSkuEntity);
        }else{
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();

            //查询当前sku库存量
            //SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id = 25
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count==null?false:count>0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 锁库存
     * (rollbackFor = NoStockException.class)
     * 默认只要是运行时异常都会回滚
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(StockSkuLockVo vo) {

        /**
         * 保存库存工作单的详情
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);
        //找到每个商品在哪个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        //锁定库存
        for (SkuWareHasStock stock : collect) {
            Boolean skuStockLocked = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareId();
            if (wareIds ==null ||wareIds.size()  ==0){
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //成功返回1，失败影响0行
                Long count = wareSkuDao.lockSkuStock(skuId,wareId,stock.getNum());
                if (count == 0){
                    //当前仓库锁失败，重试下一个仓库
                    skuStockLocked=false;
                }else {
                    //成功
                    skuStockLocked = true;
                    //TODO 给MQ发消息锁定成功
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null,
                            skuId,
                            "",
                            stock.getNum(),
                            wareOrderTaskEntity.getId(),
                            wareId,1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo detail = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity,detail);
                    stockLockedTo.setDetail(detail);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
                    //锁成功就没有必要重试其他仓库，跳出break
                    break;
                }
            }
            if (skuStockLocked == false){
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }

        }
        return true;
    }

    /**
     * 解锁库存
     * @param to
     */
    @Override
    public void unLockStock(StockLockedTo to) {

            StockDetailTo detail = to.getDetail();
            Long detailId = detail.getId();
            //解锁
            //查数据库关于这个订单的锁定库存信息
            //有：证明库存锁定成功了
            //     解锁：订单情况
            //            1.没有这个订单，必须解锁
            //            2.有这个订单
            //              看订单状态：已取消，解锁库存
            //                          没有取消：不能解锁
            //没有：库存锁定失败，库存回滚了，这种情况无需解锁
            WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
            if (byId!=null){

                Long id = to.getId();//库存工作单的id
                WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(id);
                String orderSn = orderTaskEntity.getOrderSn();
                //根据订单号查询订单的状态
                R r = orderFeignService.getOrderStatus(orderSn);
                if (r.getCode() == 0){
                    //订单数据返回成功
                    OrderVo data = r.getData(new TypeReference<OrderVo>() {});

                    if(data==null ||data.getStatus()==4){
                        //订单取消
                        if (byId.getLockStatus() ==1){
                            //解锁
                            unLockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                            //一定要手动ACK,不然消息自动ACK，MQ中就没有了，
                            // 在配置文件中设置spring.rabbitmq.listener.direct.acknowledge-mode=manual
                        }
                    }
                }
                else {
                    throw new RuntimeException("远程服务失败");
                }
            }else {
                //无需解锁
            }
    }

    /**
     * 订单解锁，库存解锁
     * @param to
     */
    @Transactional
    @Override
    public void unLockStock(OrderCloseTo to) {
        String orderSn = to.getOrderSn();
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = orderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id)
                .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : list) {
            unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }

    @Data
    class SkuWareHasStock{
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }
}
