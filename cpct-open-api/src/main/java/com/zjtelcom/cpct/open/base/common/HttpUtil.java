package com.zjtelcom.cpct.open.base.common;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/31
 * @Description:http请求工具类
 */
public class HttpUtil {


    /**
     * queryString转Map<String, Object>
     * @param request
     * @return
     */
    public static Map<String, Object> getRequestMap(HttpServletRequest request) {
            Map<String, Object> map =new HashMap<>();
            String path = request.getQueryString();
            if (StringUtils.isBlank(path)) {
                return map;
            }
            try {
                //将得到的HTML数据用UTF-8转码
                path = URLDecoder.decode(path, "UTF-8");
                String[] params = path.split("&");
                for(String s:params){
                    int i = s.indexOf("=");
                    map.put(s.substring(0,i),s.substring(i+1));
                }
                return  map;
            } catch (UnsupportedEncodingException e) {

            }
            return map;
    }





}