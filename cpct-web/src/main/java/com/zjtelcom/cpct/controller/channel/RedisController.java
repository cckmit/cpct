package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.RedisUtils_es;
import com.zjtelcom.cpct.util.RedisUtils_prd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/redis")
public class RedisController extends BaseController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisUtils_es redisUtils_es;

    @Autowired
    private RedisUtils_prd redisUtils_prd;

    @RequestMapping(value = "/deleteRedis", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteRedis(@RequestBody String params) {
        Map result = new HashMap();
        try {
            redisUtils.remove(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/deleteRedisEs", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteRedisEs(@RequestBody String params) {
        Map result = new HashMap();
        try {
            redisUtils_es.remove(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/deleteRedisPrd", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteRedisPrd(@RequestBody String params) {
        Map result = new HashMap();
        try {
            redisUtils_prd.remove(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }

    /**
     * 反序列化
     */
    @RequestMapping(value = "/deserialize", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> deserialize(@RequestBody String serStr) {
        Map<String,Object> result = new HashMap<>();
        String newObj = null;
        try {
            if(serStr != null) {
                String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                System.out.println(objectInputStream.toString());
                newObj = objectInputStream.toString();
                objectInputStream.close();
                byteArrayInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg",newObj);
        return result;
    }

}
