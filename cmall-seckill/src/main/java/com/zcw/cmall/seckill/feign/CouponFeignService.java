package com.zcw.cmall.seckill.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Chrisz
 * @date 2021/1/2 - 21:44
 */
@FeignClient("cmall-coupon")
public interface CouponFeignService {


    /**
     * 获取三天秒杀活动
     * @return
     */
    @GetMapping("/coupon/seckillsession/latest3DaysSession")
    R getLatest3DaysSession();
}
