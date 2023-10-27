package com.septangle.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import com.septangle.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@Slf4j
public class  WebMvcConfig extends WebMvcConfigurationSupport {
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
    /**
     * 消息转换器的扩展||年月日+Long转换为String
     * 即扩展Mvc的转换器
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>>converters)
    {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转换成json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //index表示优先级，0为最优先
        converters.add(0,messageConverter);
    }

}
