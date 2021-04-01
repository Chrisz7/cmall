package com.zcw.cmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Chrisz
 * @date 2020/10/21 - 21:50
 */
@Configuration
public class CmallCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin("*");
        //coocike
        configuration.setAllowCredentials(true);

        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",configuration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
