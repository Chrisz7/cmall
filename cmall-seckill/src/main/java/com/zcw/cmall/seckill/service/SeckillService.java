package com.zcw.cmall.seckill.service;

import com.zcw.cmall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author Chrisz
 * @date 2021/1/2 - 21:41
 */
public interface SeckillService {
    /**
     * 上架秒杀商品
     */
    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

}
