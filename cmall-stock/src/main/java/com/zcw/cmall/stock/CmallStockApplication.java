package com.zcw.cmall.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableRabbit
@EnableFeignClients("com.zcw.cmall.stock.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class CmallStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallStockApplication.class, args);
    }

}
