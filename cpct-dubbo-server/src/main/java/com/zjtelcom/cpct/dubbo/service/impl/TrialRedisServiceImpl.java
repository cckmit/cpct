package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dubbo.service.TrialRedisService;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class TrialRedisServiceImpl implements TrialRedisService {
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String, Object> searchFromRedis(String key) {
        Map<String,Object> result = new HashMap<>();
        try {
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询成功");
            if (key.contains("ISSURE_")){
                result.put("result",redisUtils.hgetAllRedisList(key));
            }else {
                result.put("result",redisUtils.get(key));
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询失败");
        }
        return result;
    }
}
