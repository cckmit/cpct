package com.zjtelcom.cpct.openConfig;

import com.zjtelcom.cpct.open.base.interceptor.MyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Auther: anson
 * @Date: 2018/10/29
 * @Description:自定义拦截器 判断请求头信息
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }


}
