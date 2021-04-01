package com.zcw.cmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.order.vo.*;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:11:10
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单
     * @param vo
     * @return
     */
    OrderSubmitRespVo submitOrder(OrderSubmitVo vo);

    /**
     * 根据订单号，获取订单
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 自动关单
     * @param orderEntity
     */
    void closeOrder(OrderEntity orderEntity);

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 支付宝是否支付处理
     * @param vo
     * @return
     */
    String handleAlipayed(PayAsyncVo vo);
}

