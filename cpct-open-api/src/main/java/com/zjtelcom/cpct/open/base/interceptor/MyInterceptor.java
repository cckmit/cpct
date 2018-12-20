package com.zjtelcom.cpct.open.base.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/29
 * @Description:自定义拦截器   目前只提供标签api 和 营销活动详情
 */
@Component
public class MyInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private SysParamsMapper sysParamsMapper;
    /**

     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean flag = false;  //是否开启请求头验证 true开启
        if(sysParamsMapper==null) {   //如果注入失败则手动获取
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            sysParamsMapper = (SysParamsMapper) factory.getBean("sysParamsMapper");
        }
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign("IS_OPEN_HEAD");
        if (!sysParams.isEmpty()) {
            if ("2".equals(sysParams.get(0).getParamValue())) {
                flag = true;
            }
        }



//        请求头是否符合规范
        if(flag){
            Map<String, String> stringStringMap = requestHeaderIsTrue(request);
            if(!stringStringMap.get("resultCode").equals("0")){
                //返回请求头验证失败信息
                String news=stringStringMap.get("message");
                JSONObject json=new JSONObject();
                json.put("message",news);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter pw = response.getWriter();
                pw.write(json.toJSONString());
                pw.flush();
                pw.close();
                return false;
            }

        }
        setResponseHeader(response);
        return  true;
    }


    /**
     * 判断请求头信息是否正确
     * 集团openapi指定的用户验证
     * X-APP-ID           中国电信分配的APP-ID
     * X-APP-KEY          中国电信分配的APP-KEY
     * X-CTG-Request-Id   请求ID，唯⼀标示一次业务请求
     */
    public static  Map<String, String>requestHeaderIsTrue(HttpServletRequest request) {
        //获取所有的消息头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        //获取获取的消息头名称，获取对应的值，并输出
//        while (headerNames.hasMoreElements()) {
//            String nextElement = headerNames.nextElement();
//            System.out.println(nextElement + ":" + request.getHeader(nextElement));
//        }
        //判断对应的请求头信息是否正常
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Content-Type", request.getHeader("Content-Type"));
        headMap.put("X-APP-ID", request.getHeader("X-APP-ID"));
        headMap.put("X-APP-KEY", request.getHeader("X-APP-KEY"));
        headMap.put("X-CTG-Request-Id", request.getHeader("X-CTG-Request-Id"));
        headMap.put("X-CTG-Region-ID", request.getHeader("X-CTG-Region-ID"));
        //判断请求头是否符合规范
//        System.out.println("请求头的信息："+headMap);
        Map<String, String> stringStringMap = diffHead(headMap);
        return  stringStringMap;
    }



    /**
     * 设置返回头信息
     * 集团openapi指定返回
     */
    public static void setResponseHeader(HttpServletResponse response){
        response.setHeader("Content-Type","application/json");   //这个目前设置不起作用 debug会发现他在后面改成了text/plain

        response.setHeader("X-CTG-Request-Id","92598bee-7d30-4086-afc9-a7be6bd2cda0");
        response.setHeader("X-RateLimit-Limit","60");
        response.setHeader("X-RateLimit-Reset","48");
        response.setHeader("X-RateLimit-Remaining","14");
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


    /**
     * 集团文档规定的请求头信息
     * @return
     */
    public static Map<String, String> headNews(){
        Map<String, String> head = new HashMap<>();
        head.put("Content-Type","application/json");
        head.put("X-APP-ID","FFnN2hso42Wego3pWq4X5qlu");
        head.put("X-APP-KEY","UtOCzqb67d3sN12Kts4URwy8");
        head.put("X-CTG-Request-Id","92598bee-7d30-4086-afc9-a7be6bd2cda0");
        head.put("X-CTG-Region-ID","8110100");
        return head;
    }

    public static Map<String, String> diffHead( Map<String, String> head){
        Map<String, String> result=new HashMap<>();
        result.put("resultCode","0");
        Map<String, String> stringObjectMap = headNews();
        for (String str:head.keySet()){
            if(!head.get(str).equals(stringObjectMap.get(str))){
                  result.put("resultCode","1");
                  result.put("message",str+"信息验证失败");
            }
        }
        return  result;
    }


}
