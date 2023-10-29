package com.septangle.filter;

import com.alibaba.fastjson2.JSON;
import com.septangle.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
检查用户是否完成登录
    1.完善登录功能 #使用过滤器或者拦截器(SpringMvc)
* */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;

        //获取本次请求的URI
        String requestURI= request.getRequestURI();

        //定义不需要处理的请求路径
        String[]urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        boolean check=check(urls,requestURI);

        //判断不需要处理直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        //判断是否登录，若已登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");

        //若未登录则返回未登录结果，不需要跳转页面，通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("拦截到请求：{}",requestURI);
        return;
    }

    //检查本次请求是否需要放行
    private boolean check(String[]urls,String requestURI)
    {
        for(String url:urls)
        {
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
