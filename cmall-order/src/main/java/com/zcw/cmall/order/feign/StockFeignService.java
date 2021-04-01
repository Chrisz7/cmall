package com.zcw.cmall.order.feign;

import com.zcw.cmall.order.vo.StockSkuLockVo;
import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/24 - 10:26
 */
@FeignClient("cmall-stock")
public interface StockFeignService {


    /**
     * 锁库存
     * @param vo
     * @return
     */
    @PostMapping("/stock/waresku/orderLockStock")
    R orderLockStock(@RequestBody StockSkuLockVo vo);
    /**
     *
     * @param skuIds
     * @return
     */
    //查询sku是否有库存,@RequestBody 将请求体中的数据转换成 List<Long> skuIds
    @PostMapping("/stock/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);

    /**
     * 获取运费信息
     * @param addrId
     * @return
     */
    @GetMapping("/stock/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);
}
