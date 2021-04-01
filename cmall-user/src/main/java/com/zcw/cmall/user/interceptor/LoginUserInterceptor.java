package com.zcw.cmall.user.interceptor;

import com.zcw.common.constant.AuthServerConstant;
import com.zcw.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Chrisz
 * @date 2020/12/22 - 19:31
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //放行 /order/order/status/{orderSn}这个uri  url是包括服务器的整个地址
        //因为是远程调用
        String uri = request.getRequestURI();
        //AntPathMatcher
        boolean match = new AntPathMatcher().match("/user/**", uri);
        if (match){
            return true;
        }


        HttpSession session = request.getSession();
        MemberRespVo attribute = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute!=null){
            //登录了
            loginUser.set(attribute);
            return true;
        }else{
            //重定向去登陆
            request.getSession().setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.cmall.com/login.html");
            return false;
        }

    }
}
