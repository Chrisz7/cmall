package com.zcw.cmall.cart.config;

import com.zcw.cmall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Chrisz
 * @date 2020/12/18 - 10:07
 */
@Configuration
public class CmallWebConfig implements WebMvcConfigurer {

    //实现WebMvcConfigurer的拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //使用new的话，就不需要加@Component注解往容器中放，或者@Autowried注入使用
        //往registry中添加拦截器CartInterceptor，并拦截所有请求/**
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
