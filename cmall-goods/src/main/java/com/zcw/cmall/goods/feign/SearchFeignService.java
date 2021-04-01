package com.zcw.cmall.goods.feign;

import com.zcw.cmall.goods.feign.fallback.SearchFeignServiceFallBack;
import com.zcw.common.to.es.SkuEsModel;
import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/19 - 10:05
 */
//SearchFeignServiceFallBack实现SearchFeignService好处是远程出错了，调用本地实现
@FeignClient(value = "cmall-search" ,fallback = SearchFeignServiceFallBack.class)
public interface SearchFeignService {

    //上架商品
    @PostMapping("/search/save/good")
    R goodStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
