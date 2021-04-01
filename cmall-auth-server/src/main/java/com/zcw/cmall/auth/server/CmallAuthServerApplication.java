package com.zcw.cmall.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//开启redis的Session存储
@EnableRedisHttpSession
//开启远程调用别的服务
@EnableFeignClients(basePackages = "com.zcw.cmall.auth.server.feign")
//服务注册与发现
@EnableDiscoveryClient
@SpringBootApplication
public class CmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallAuthServerApplication.class, args);
    }

}
