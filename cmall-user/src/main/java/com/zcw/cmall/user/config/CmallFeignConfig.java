package com.zcw.cmall.user.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chrisz
 * @date 2020/12/22 - 21:04
 */
@Configuration
public class CmallFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //1.使用RequestContextHolder拿到刚进来的请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes!=null){
                    HttpServletRequest request = requestAttributes.getRequest();
                    if (request!=null){
                        //2.同步请求头数据  cookie
                        String cookie = request.getHeader("Cookie");
                        requestTemplate.header("Cookie",cookie);
                    }
                }
            }
        };
    }
}
