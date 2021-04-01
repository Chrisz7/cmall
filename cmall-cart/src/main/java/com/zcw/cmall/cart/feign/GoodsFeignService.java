package com.zcw.cmall.cart.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/18 - 21:01
 */
@FeignClient("cmall-goods")
public interface GoodsFeignService {

    /**
     * 获取购物项最新的价格
     * @param skuId
     * @return
     */
    @GetMapping("/goods/skuinfo/{skuId}/price")
    R getPrice(@PathVariable("skuId") Long skuId);
    /**
     * 商品详细信息
     */
    @RequestMapping("/goods/skuinfo/info/{skuId}")
    //@RequiresPermissions("goods:skuinfo:info")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 销售属性组合信息
     * @param skuId
     * @return
     */
    @GetMapping("/goods/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);//@PathVariable("skuId")
}
