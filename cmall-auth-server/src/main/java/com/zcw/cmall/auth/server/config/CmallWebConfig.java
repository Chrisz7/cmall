package com.zcw.cmall.auth.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Chrisz
 * @date 2020/12/4 - 15:16
 */

/**
 * SpringMVC提供的页面视图跳转 视图映射
 */
//表明配置的注解
@Configuration
public class CmallWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        //写了这个映射不能实现自定义的业务逻辑，所以得看情况
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
    }
}
