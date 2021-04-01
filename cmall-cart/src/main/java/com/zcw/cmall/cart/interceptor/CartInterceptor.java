package com.zcw.cmall.cart.interceptor;

import com.zcw.cmall.cart.vo.UserInfoTo;
import com.zcw.common.constant.AuthServerConstant;
import com.zcw.common.constant.CartConstant;
import com.zcw.common.vo.MemberRespVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author Chrisz
 * @date 2020/12/18 - 9:35
 *
 * 使用Sping MVC 拦截器  HandlerInterceptor 属于WebMvcConfigurer
 *
 * 在controller处理请求之前，判断用户的登录状态
 * 并封装传递给controller处理
 *
 */
@Component//不仅需要加@Component注解放进容器，还要配置拦截什么请求
public class CartInterceptor implements HandlerInterceptor {


    //ThreadLocal 静态公共变量  同一个线程共享数据
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 请求处理执行之前
     * @param request
     * @param response
     * @param handler
     * @return true放行 | false不放行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();
        //这个request是包装过的
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberRespVo !=null){
            //登录了
            userInfoTo.setUserId(memberRespVo.getId());

        }
        Cookie[] cookies = request.getCookies();
        //非空判断
        if (cookies!= null && cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }

        if (StringUtils.isEmpty(userInfoTo.getUserKey())){
            //临时用户为空
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserKey(s);
        }
        //放进本地线程池，同一线程共享数据
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 请求处理完成之后
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        //保存一个cookie
        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.isTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            cookie.setDomain("cmall.com");
            response.addCookie(cookie);
        }

    }
}
