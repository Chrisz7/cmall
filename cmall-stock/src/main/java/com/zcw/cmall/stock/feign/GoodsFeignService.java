package com.zcw.cmall.stock.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Chrisz
 * @date 2020/11/12 - 10:23
 */
@FeignClient("cmall-goods")
public interface GoodsFeignService {
    @RequestMapping("/goods/skuinfo/info/{skuId}")
    //@RequiresPermissions("goods:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);
}
