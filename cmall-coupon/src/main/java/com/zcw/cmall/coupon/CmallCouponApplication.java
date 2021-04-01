package com.zcw.cmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallCouponApplication.class, args);
    }

}
