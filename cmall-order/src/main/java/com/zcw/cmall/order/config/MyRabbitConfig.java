package com.zcw.cmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * @author Chrisz
 * @date 2020/12/21 - 11:30
 */
@Configuration
public class MyRabbitConfig {

    //@Autowired
    RabbitTemplate rabbitTemplate;
    //解决循环依赖
    //类中有 有参构造器，rabbitTemplate 这个参数就会从容器中得到
//    public MyRabbitConfig(RabbitTemplate rabbitTemplate){
//        this.rabbitTemplate = rabbitTemplate;
//        initRabbitTemplate();
//    }

    //@Primary主要的
    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }


    /**
     * 使用JSON序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){

        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * @PostConstruct MyRabbitConfig对象创建完成后，执行这个方法
     */
    //@PostConstruct
    public void initRabbitTemplate(){
        /**
         * 设置确认回调
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *只要消息抵达Broker就ack=true
             * @param correlationData  当前消息的唯一关联数据(消息的唯一id
             * @param ack     消息是否成功收到
             * @param cause   失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                //消息到达服务器
            }
        });

        /**
         * 设置消息抵达队列的确认回调
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有抵达指定的队列，就会触发这个失败回调
             * @param message 投递失败的消息的详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange 交换机
             * @param routingkey 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingkey) {
                //失败回调,把数据库中对应的消息状态修改为错误
            }
        });
    }
}
