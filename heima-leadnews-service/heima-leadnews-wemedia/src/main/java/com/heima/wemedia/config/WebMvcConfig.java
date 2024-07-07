package com.heima.wemedia.config;

import com.heima.wemedia.interceptor.WMInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ghy
 * @version 1.0.1
 * @date 2024-07-06 11:59:14
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WMInterceptor wmInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(wmInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/in");
    }
}