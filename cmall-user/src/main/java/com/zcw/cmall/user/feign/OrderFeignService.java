package com.zcw.cmall.user.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Chrisz
 * @date 2021/1/1 - 22:20
 */
@FeignClient("cmall-order")
public interface OrderFeignService {

    /**
     * 订单列表
     */
    @PostMapping("/order/order/listWithItem")
    //@RequiresPermissions("order:order:list")
    R listWithItem(@RequestBody Map<String, Object> params);
}
