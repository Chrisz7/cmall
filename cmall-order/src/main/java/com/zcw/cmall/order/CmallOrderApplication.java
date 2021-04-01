package com.zcw.cmall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableAspectJAutoProxy(exposeProxy = true)//使用seata分布式事务，配置的动态代理
@EnableFeignClients("com.zcw.cmall.order.feign")
@EnableRedisHttpSession
@EnableRabbit
@SpringBootApplication
@EnableDiscoveryClient
public class CmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallOrderApplication.class, args);
    }

}
