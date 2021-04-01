package com.zcw.cmall.goods.feign;

import com.zcw.cmall.goods.feign.fallback.StockFeignServiceFallBack;
import com.zcw.common.to.SkuHasStockVo;
import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/19 - 9:13
 */
@FeignClient(value = "cmall-stock",fallback = StockFeignServiceFallBack.class)
public interface StockFeignService {

    //查询sku是否有库存,@RequestBody 将请求体中的数据转换成 List<Long> skuIds
    @PostMapping("/stock/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
