package com.zjtelcom.cpct.open.base.interceptor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @Auther: anson
 * @Date: 2018/10/29
 * @Description:自定义拦截器
 */
public class MyInterceptor extends HandlerInterceptorAdapter {


    /**

     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       // System.out.println("在控制器执行前调用 ");
        Boolean flag = true;
        requestHeaderIsTrue(request);
        setResponseHeader(response);
        if(flag){
            System.out.println(request.getRequestURI());
            return true;
        }else{
            System.out.println(request.getRequestURI());
            return false;
        }
    }


    /**
     * 判断请求头信息是否正确
     * 集团openapi指定的用户验证
     * X-APP-ID           中国电信分配的APP-ID
     * X-APP-KEY          中国电信分配的APP-KEY
     * X-CTG-Request-Id   请求ID，唯⼀标示一次业务请求
     */
    public static void requestHeaderIsTrue(HttpServletRequest request){
        //获取所有的消息头名称
        //Enumeration<String> headerNames = request.getHeaderNames();
        //获取获取的消息头名称，获取对应的值，并输出
//        while(headerNames.hasMoreElements()){
//            String nextElement = headerNames.nextElement();
//            System.out.println(nextElement+":"+request.getHeader(nextElement));
//        }
        //判断对应的请求头信息是否正常
        String contentType = request.getHeader("Content-Type");
        String appId = request.getHeader("X-APP-ID");
        String appKey = request.getHeader("X-APP-KEY");
        String requestId = request.getHeader("X-CTG-Request-Id");



    }



    /**
     * 设置返回头信息
     * 集团openapi指定返回
     */
    public static void setResponseHeader(HttpServletResponse response){
        response.setHeader("Content-Type","application/json;charset=utf-8");   //这个目前设置不起作用 debug会发现他在后面改成了text/plain
        response.setHeader("Location","/openapi/capability/1");
        response.setHeader("Cache-Control","private");
        response.setHeader("X-CTG-Request-Id","22222");
        response.setHeader("X-RateLimit-Limit","11");
        response.setHeader("X-RateLimit-Reset","999");
        response.setHeader("X-RateLimit-Remaining","123456");
    }





    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("在后端控制器执行后调用 ");
//        super.postHandle(request, response, handler, modelAndView);
    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       // System.out.println("整个请求执行完成后调用 ");
       // super.afterCompletion(request, response, handler, ex);
    }




}
