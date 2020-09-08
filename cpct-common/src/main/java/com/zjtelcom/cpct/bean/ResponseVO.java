package com.zjtelcom.cpct.bean;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseVO<T> {
    private String resultCode;
    private String resultMessage;
    private T data;

    public Map<String,Object> response(String resultCode, String resultMessage){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("resultCode",resultCode);
        resultMap.put("resultMessage",resultMessage);
        return resultMap;
    }
    public Map<String,Object> response(String resultCode, String resultMessage,T data){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("resultCode",resultCode);
        resultMap.put("resultMessage",resultMessage);
        resultMap.put("data",data);
        return resultMap;
    }

}
