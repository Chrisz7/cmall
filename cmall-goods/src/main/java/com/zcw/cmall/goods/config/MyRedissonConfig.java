package com.zcw.cmall.goods.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author Chrisz
 * @date 2020/11/24 - 8:01
 */
@Configuration
public class MyRedissonConfig {
    //redisson的使用都是通过redissonClient对象
    //destroyMethod="shutdown" 服务停止之后调用销毁方法
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        //集群模式
//        config.useClusterServers()
//                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
        //单节点模式
        //Redis url should start with redis:// or rediss:// (for SSL connection)
        config.useSingleServer().setAddress("redis://192.168.60.137:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
