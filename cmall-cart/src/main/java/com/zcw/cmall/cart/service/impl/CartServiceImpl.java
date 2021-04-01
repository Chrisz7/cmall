package com.zcw.cmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.cart.feign.GoodsFeignService;
import com.zcw.cmall.cart.interceptor.CartInterceptor;
import com.zcw.cmall.cart.service.CartService;
import com.zcw.cmall.cart.vo.Cart;
import com.zcw.cmall.cart.vo.CartItem;
import com.zcw.cmall.cart.vo.SkuInfoVo;
import com.zcw.cmall.cart.vo.UserInfoTo;
import com.zcw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author Chrisz
 * @date 2020/12/18 - 9:24
 */

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    //容器中自定义的线程池
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    GoodsFeignService goodsFeignService;
    //操作redis
    @Autowired
    StringRedisTemplate redisTemplate;

    private final String CART_PREFIX="cmall:cart:";
    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //抽取成方法，选中片段，Refactor，Extract，Method
        //获取购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();


        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)){
            CartItem item = new CartItem();
            //给购物车添加新商品
            //开启异步任务，runAsync没有返回值
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //远程调用查询要添加的商品得信息
                R skuInfo = goodsFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                item.setCheck(true);
                item.setCount(num);
                item.setImage(data.getSkuDefaultImg());
                item.setTitle(data.getSkuTitle());
                item.setSkuId(skuId);
                item.setPrice(data.getPrice());
            }, executor);

            CompletableFuture<Void> getSkuSaleAttrsTask = CompletableFuture.runAsync(() -> {
                //远程调用查询sku组合信息,上面还有一个远程调用，所以开线程池处理多任务
                List<String> skuSaleAttrValues = goodsFeignService.getSkuSaleAttrValues(skuId);
                item.setSkuAttr(skuSaleAttrValues);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrsTask).get();
            //往redis中存商品,存的是JSON格式的
            String s = JSON.toJSONString(item);
            cartOps.put(skuId.toString(),s);
            return item;
        }else{
            //购物车有此商品，修改次购物项的数量的信息
            CartItem item = JSON.parseObject(res, new TypeReference<CartItem>() {
            });
            item.setCount(item.getCount()+num);
            cartOps.put(skuId.toString(),JSON.toJSONString(item));
            return item;
        }


    }

    /**
     * 获取购物车某个购物项
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;
    }

    /**
     * 获取购物车
     * @return
     */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {

        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId()!=null){
            //登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //先获取临时购物车的购物项
            String tempKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempKey);
            if (tempCartItems!=null&&tempCartItems.size()>0){
                //临时购物车不为空，合并到用户购物车
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(),tempCartItem.getCount());
                }
                //合并完成后，清除临时购物车
                clearCart(tempKey);
            }
            //再来获取用户购物车的购物项
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else{
            //没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * 获取购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //从拦截器中的threadLocal得到用户是否登录
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId()!= null){
            //用户购物车
            cartKey = CART_PREFIX+userInfoTo.getUserId();
        }else{
            //userKey临时用户标识
            //临时购物车
            cartKey = CART_PREFIX+userInfoTo.getUserKey();
        }
        //绑定一个hash操作，要不然每次都要redisTemplate.opsForHash().get(cartKey,"");

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    /**
     * 获取购物车所有购物项
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey){

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);
        //这个用户对应的所有购物项
        //List<Object> 不能转换成 List<String> ,但是可以一个一个强转
        List<Object> values = ops.values();
        if (values!=null && values.size()>0){
            List<CartItem> collect = values.stream().map(val -> {
                String str = (String) val;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }

        return null;
    }

    /**
     * 清空购物车
     * @param cartKey
     */
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    /**
     * 改变购物项的选中状态
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String string = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),string);
    }

    /**
     * 改变购物项的数量
     * @param skuId
     * @param num
     */
    @Override
    public void countItem(Long skuId, Integer num) {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    /**
     * 删除购物项
     * @param deleteItemSkuId
     */
    @Override
    public void deleteItem(Long deleteItemSkuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(deleteItemSkuId.toString());
    }

    /**
     * 获取购物车所有选中的购物项
     * @return
     */
    @Override
    public List<CartItem> getCurrentUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId()==null){
            return null;
        }else {
            String cartKey = CART_PREFIX+userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            List<CartItem> collect = cartItems.stream()
                    .filter(cartItem -> cartItem.getCheck())
                    .map(item ->{
                        /**设置最新的价格*/
                        R price = goodsFeignService.getPrice(item.getSkuId());
                        String data = (String) price.get("data");
                        item.setPrice(new BigDecimal(data));
                        return item;
                    })
                    .collect(Collectors.toList());
            return collect;
        }

    }
}
