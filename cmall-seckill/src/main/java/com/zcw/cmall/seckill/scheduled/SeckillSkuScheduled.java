package com.zcw.cmall.seckill.scheduled;

import com.zcw.cmall.seckill.service.SeckillService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Chrisz
 * @date 2021/1/2 - 21:33
 * 秒杀商品定时上架：
 *        每天晚上3点
 *        上架最近三天需要秒杀的商品
 */
@Service
public class SeckillSkuScheduled {

    private final String UPLOAD_LOCK = "seckill:upload:lock";
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SeckillService seckillService;
    @Async
    //* * * * * ? 秒 分 时 日 月 周   每天晚上3点
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){

        //加一个分布式锁
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        //无论成功失败都要解锁
        try{
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
