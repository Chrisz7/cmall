package com.zcw.cmall.stock.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zcw.cmall.stock.entity.WareOrderTaskDetailEntity;
import com.zcw.cmall.stock.entity.WareOrderTaskEntity;
import com.zcw.cmall.stock.service.WareSkuService;

import com.zcw.cmall.stock.vo.OrderVo;
import com.zcw.common.to.OrderCloseTo;
import com.zcw.common.to.mq.StockDetailTo;
import com.zcw.common.to.mq.StockLockedTo;
import com.zcw.common.utils.R;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Chrisz
 * @date 2020/12/30 - 23:00
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {


    @Autowired
    WareSkuService wareSkuService;
    /**
     * 库存解锁场景：
     * 1.下订单成功，订单没有支付，被系统自动取消，或被用户自动取消
     *
     * 2.下订单成功，库存锁定成功，后面的业务失败，导致订单回滚，之前锁定的库存要自动解锁
     *
     *
     * 1.订单失败，是由于锁库存失败
     *
     *可以直接监听到MQ中的StockLockedTo对象
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unLockStock(to);
            //不是批量,解锁库存完毕，手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //如果出现问题，拒收并放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    /**
     * 订单解锁后，解锁对应库存
     * @param to
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void handleOrderClosedRelease(OrderCloseTo to, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unLockStock(to);
            //不是批量,解锁库存完毕，手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //如果出现问题，拒收并放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
