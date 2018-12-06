package com.zjtelcom.cpct.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: FastJson util
 * author: pengy
 * date: 2018/3/25 17:20
 */
public class FastJsonUtils {

    private static SerializeConfig mapping = new SerializeConfig();

    static{
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * javaBean、list、map convert to json string
     */
    public static String objToJson(Object obj){
        return JSON.toJSONString(obj,mapping, SerializerFeature.WriteMapNullValue);
    }

    /**
     * json string convert to javaBean、map
     */
    public static <T> T jsonToObj(String jsonStr, Class<T> clazz){
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * json array string convert to list with javaBean
     */
    public static <T> List<T> jsonToList(String jsonArrayStr, Class<T> clazz){
        return JSON.parseArray(jsonArrayStr, clazz);
    }

    /**
     * json string convert to map
     */
    public static <T> Map<String,Object> jsonToMap(String jsonStr){
        return jsonToObj(jsonStr, Map.class);
    }

    /**
     * json string convert to map with javaBean
     */
    public static <T> Map<String,T> jsonToMap(String jsonStr, Class<T> clazz){
        Map<String,T> map = JSON.parseObject(jsonStr, new TypeReference<Map<String, T>>() {});
        for (Map.Entry<String, T> entry : map.entrySet()) {
            JSONObject obj = (JSONObject) entry.getValue();
            map.put(entry.getKey(), JSONObject.toJavaObject(obj, clazz));
        }
        return map;
    }

}
