package com.zjtelcom.cpct.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 * http工具类
 */
public class HttpUtil {

    /**
     * post请求
     * @param url  URL地址
     * @param params  json格式的字符串
     * @return
     */
    public static String post(String url,String params) {

        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;utf-8");
            StringEntity se = new StringEntity(params);
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json;utf-8"));
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"utf-8");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

}
