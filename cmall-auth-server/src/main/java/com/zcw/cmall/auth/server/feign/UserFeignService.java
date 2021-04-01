package com.zcw.cmall.auth.server.feign;

import com.zcw.cmall.auth.server.vo.SocialUser;
import com.zcw.cmall.auth.server.vo.UserLoginVo;
import com.zcw.cmall.auth.server.vo.UserRegistVo;
import com.zcw.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Chrisz
 * @date 2020/12/9 - 9:52
 */
@FeignClient("cmall-user")
public interface UserFeignService {

    /**
     * 注册
     * @param vo
     * @return
     */
    @PostMapping("/user/member/register")
    R register(@RequestBody UserRegistVo vo);

    /**
     * 登录
     * @param vo
     * @return
     */
    @PostMapping("/user/member/login")
    R login(@RequestBody UserLoginVo vo);

    /**
     * 社交登录
     * @param
     * @return
     */
    @PostMapping("/user/member/oauth2/login")
    R oauthlogin(@RequestBody SocialUser socialUser) throws Exception;
}
