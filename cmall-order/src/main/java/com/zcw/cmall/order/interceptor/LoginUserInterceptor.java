package com.zcw.cmall.order.interceptor;

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
        String uri = request.getRequestURI();
        String url = String.valueOf(request.getRequestURL());
        //AntPathMatcher
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/**", uri);
        //http://order.free.vipnps.vip/payed/notify uri=/error????
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        //本来第一次uri是对的，但是post的第二次请求uri是error？？
        if (match || match1 ){
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
