package com.zcw.cmall.thirdserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//服务注册与发现
@EnableDiscoveryClient
@SpringBootApplication
public class CmallThirdServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallThirdServerApplication.class, args);
    }

}
