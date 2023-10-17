package com.septangle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /*
    *@param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        log.info("开始进行路径映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
