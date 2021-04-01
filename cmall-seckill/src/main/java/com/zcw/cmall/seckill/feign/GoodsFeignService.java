package com.zcw.cmall.seckill.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Chrisz
 * @date 2021/1/3 - 12:36
 */
@FeignClient("cmall-goods")
public interface GoodsFeignService {

    /**
     * 信息
     */
    @RequestMapping("/goods/skuinfo/info/{skuId}")
    //@RequiresPermissions("goods:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);
}
