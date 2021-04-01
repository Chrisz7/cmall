package com.zcw.cmall.auth.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.auth.server.feign.UserFeignService;
import com.zcw.common.constant.AuthServerConstant;
import com.zcw.common.vo.MemberRespVo;
import com.zcw.cmall.auth.server.vo.SocialUser;
import com.zcw.common.utils.HttpUtils;
import com.zcw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chrisz
 * @date 2020/12/11 - 11:08
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    UserFeignService userFeignService;
    @GetMapping("/oauth2.0/weibo/success")
    //微博登录成功会执行成功跳转，带上一个code
    //根据code换取access_token
    public String weibo(@RequestParam("code")String code, RedirectAttributes attributes, HttpSession session) throws Exception {
        Map<String, String> query = new HashMap<>();
        query.put("client_id", "192380538");
        query.put("client_secret", "2779a2de614ab772f437768e2af94ecc");
        query.put("grant_type", "authorization_code");
        query.put("redirect_uri", "http://auth.cmall.com/oauth2.0/weibo/success");
        query.put("code", code);
        //发送post请求换取token
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<String, String>(), query, new HashMap<String, String>());

        if (response.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(response.getEntity());
            //使用fastjson将json转成制定的对象
            SocialUser socialUser = JSON.parseObject(json, new TypeReference<SocialUser>() {});
            R login = userFeignService.oauthlogin(socialUser);
            //2.1 远程调用成功，返回首页并携带用户信息
            if (login.getCode() == 0) {

                String jsonString = JSON.toJSONString(login.get("memberEntity"));
                MemberRespVo memberResponseVo = JSON.parseObject(jsonString, new TypeReference<MemberRespVo>() {
                });
                session.setAttribute(AuthServerConstant.LOGIN_USER,memberResponseVo);
                return "redirect:http://cmall.com";
            } else {
                //2.2 否则返回登录页
//                errors.put("msg", "登录失败，请重试");
//                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.cmall.com/login.html";
            }
        } else {
//            errors.put("msg", "获得第三方授权失败，请重试");
//            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.cmall.com/login.html";
        }
    }
}
