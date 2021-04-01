package com.zcw.cmall.order.listener;

import com.rabbitmq.client.Channel;
import com.zcw.cmall.order.entity.OrderEntity;
import com.zcw.cmall.order.service.OrderService;
import com.zcw.common.to.mq.StockLockedTo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Chrisz
 * @date 2021/1/1 - 10:09
 */
@Service//也是业务逻辑组件
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {


    @Autowired
    OrderService orderService;

    /**
     * 自动关单
     * @param orderEntity
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void handleOrderRelease(OrderEntity orderEntity, Message message, Channel channel) throws IOException {
        try {
            orderService.closeOrder(orderEntity);
            //手动调用支付宝收单

            //不是批量,解锁库存完毕，手动ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //如果出现问题，拒收并放回队列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
