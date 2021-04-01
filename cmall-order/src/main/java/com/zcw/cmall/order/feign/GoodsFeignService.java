package com.zcw.cmall.order.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Chrisz
 * @date 2020/12/24 - 10:25
 */
@FeignClient("cmall-goods")
public interface GoodsFeignService {

    /**
     * 根据skuId查spu信息
     * @param skuId
     * @return
     */
    @GetMapping("/goods/spuinfo/skuId/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
