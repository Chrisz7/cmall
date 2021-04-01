package com.zcw.cmall.stock.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Chrisz
 * @date 2020/12/24 - 11:31
 */
@FeignClient("cmall-user")
public interface UserFeignService {
    /**
     * 信息
     */
    @RequestMapping("/user/memberreceiveaddress/info/{id}")
    //@RequiresPermissions("user:memberreceiveaddress:info")
    R addrInfo(@PathVariable("id") Long id);
}
