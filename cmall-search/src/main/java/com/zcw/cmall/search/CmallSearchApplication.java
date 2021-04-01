package com.zcw.cmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import javax.activation.DataSource;


@EnableRedisHttpSession
@EnableDiscoveryClient
//排除数据源，因为pom中导入了common，common已经配置好了数据源
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class CmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmallSearchApplication.class, args);
    }

}
