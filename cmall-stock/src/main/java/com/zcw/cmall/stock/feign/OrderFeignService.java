package com.zcw.cmall.stock.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Chrisz
 * @date 2020/12/30 - 22:33
 */
@FeignClient("cmall-order")
public interface OrderFeignService {


    /**
     * 根据订单号，获取订单
     * @param orderSn
     * @return
     */
    @GetMapping("/order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
