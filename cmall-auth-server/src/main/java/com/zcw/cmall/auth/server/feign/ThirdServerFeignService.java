package com.zcw.cmall.auth.server.feign;

import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Chrisz
 * @date 2020/12/4 - 17:28
 */
//指定远程调用的服务名称，会到nacos中根据名称匹配，发送请求，远程调用
@FeignClient("cmall-third-server")
public interface ThirdServerFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
