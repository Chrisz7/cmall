package com.zcw.cmall.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 整合MyBatis-Plus
 */

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.zcw.cmall.goods.feign")
@MapperScan("com.zcw.cmall.goods.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class CmallGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallGoodsApplication.class, args);
    }

}
