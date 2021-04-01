package com.zcw.cmall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.seckill.feign.CouponFeignService;
import com.zcw.cmall.seckill.feign.GoodsFeignService;
import com.zcw.cmall.seckill.service.SeckillService;
import com.zcw.cmall.seckill.to.SeckillSkuRedisTo;
import com.zcw.cmall.seckill.vo.SeckillSessionsWithSkus;
import com.zcw.cmall.seckill.vo.SeckillSkuVo;
import com.zcw.cmall.seckill.vo.SkuInfoVo;
import com.zcw.common.utils.R;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Chrisz
 * @date 2021/1/2 - 21:41
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    GoodsFeignService goodsFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CouponFeignService couponFeignService;
    /**
     * 上架秒杀商品
     */
    @Override
    public void uploadSeckillSkuLatest3Days() {

        //查询秒杀的活动
        R r = couponFeignService.getLatest3DaysSession();
        if (r.getCode() == 0){
            List<SeckillSessionsWithSkus> data = r.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });

            //缓存到redis
            //缓存活动信息
            saveSessionInfos(data);
            //缓存活动的关联商品信息
            saveSessionSkuInfos(data);
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {

        //*所有
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        long currentTime = System.currentTimeMillis();
        for (String key : keys) {
            String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] split = replace.split("_");
            long startTime = Long.parseLong(split[0]);
            long endTime = Long.parseLong(split[1]);
            //当前秒杀活动处于有效期内
            if (currentTime > startTime && currentTime < endTime) {
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<Object> list = ops.multiGet(Collections.singleton(range));
                if (list!=null){
                    List<SeckillSkuRedisTo> collect1 = list.stream().map(item -> {
                        SeckillSkuRedisTo redis = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
                        return redis;
                    }).collect(Collectors.toList());
                    return collect1;
                }
                break;
//                List<SeckillSkuRedisTo> collect = range.stream().map(s -> {
//                    String json = (String) ops.get(s);
//                    SeckillSkuRedisTo redisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
//                    return redisTo;
//                }).collect(Collectors.toList());
//                return collect;
            }
        }
        return null;
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach( session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX+startTime+"_"+endTime;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (!hasKey){
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId()+"_"+item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key,collect);

            }
        });

    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session -> {
            //hash操作
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                Boolean key = ops.hasKey(seckillSkuVo.getPromotionSessionId()+"_"+seckillSkuVo.getSkuId().toString());
                if (!key){
                    //缓存商品
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    //sku的基本数据
                    R info = goodsFeignService.info(seckillSkuVo.getSkuId());
                    if (info.getCode() == 0){
                        SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                        redisTo.setSkuInfoVo(skuInfo);
                    }
                    //sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);
                    //设置时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    redisTo.setRandomCode(token);

                    String s = JSON.toJSONString(redisTo);
                    ops.put(seckillSkuVo.getPromotionSessionId()+"_"+seckillSkuVo.getSkuId().toString(),s);
                    //，引入分布式信号量，去redis中拿到一个信号量，限流，限制仅仅这么多的信号量进来
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //秒杀商品的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            });
        });
    }
}
