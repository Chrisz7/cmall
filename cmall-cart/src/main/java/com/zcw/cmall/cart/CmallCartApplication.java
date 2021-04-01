package com.zcw.cmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.zcw.cmall.cart.feign")
@EnableDiscoveryClient
//exclude = DataSourceAutoConfiguration.class 不操作数据库
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallCartApplication.class, args);
    }

}
