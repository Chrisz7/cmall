package com.zcw.cmall.order.feign;

import com.zcw.cmall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/22 - 20:01
 */
@FeignClient("cmall-user")
public interface MemberFeignService {

    /**
     * 获取用户的收货地址
     * @param memberId
     * @return
     */
    @GetMapping("/user/memberreceiveaddress/{memberId}/address")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
