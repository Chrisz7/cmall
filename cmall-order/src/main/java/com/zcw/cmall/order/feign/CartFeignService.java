package com.zcw.cmall.order.feign;

import com.zcw.cmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/22 - 20:27
 */
@FeignClient("cmall-cart")
public interface CartFeignService {

    /**
     * 获取购物车所有选中的购物项
     * @return
     */
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
