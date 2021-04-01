package com.zcw.cmall.goods.feign;

import com.zcw.common.to.SkuReductionTo;
import com.zcw.common.to.SpuBoundTo;
import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Chrisz
 * @date 2020/10/20 - 9:16
 */
@FeignClient("cmall-coupon")
public interface CouponFeignService {

//    @RequestMapping("/coupon/coupon/goods/list")
//    public R goodsCoupons();

    //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
