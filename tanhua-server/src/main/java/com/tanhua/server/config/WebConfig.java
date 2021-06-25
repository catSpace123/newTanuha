package com.tanhua.server.config;

import com.tanhua.server.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类具体配置 要拦截的请求和要放行的请求   （这里的配置就相当于以前在springMVC配置文件的配置）
 * 实现WebMvcConfiguration接口
 * 点进去看自己要实现什么方法
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired  //注入自定义拦截器类
    private TokenInterceptor tokenInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")   //添加拦截路径
                .excludePathPatterns("/user/login","/user/loginVerification");  //要放行的路径

    }

}
