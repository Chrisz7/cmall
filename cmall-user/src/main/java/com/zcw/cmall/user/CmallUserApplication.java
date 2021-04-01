package com.zcw.cmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class CmallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallUserApplication.class, args);
    }

}
