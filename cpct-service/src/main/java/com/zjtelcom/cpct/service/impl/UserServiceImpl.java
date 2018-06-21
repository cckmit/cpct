package com.zjtelcom.cpct.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.service.UserService;
import com.zjtelcom.cpct.dao.UserMapper;
import com.zjtelcom.cpct.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:HuangHua
 * @Descirption:
 * @Date: Created by huanghua on 2018/6/5.
 * @Modified By:
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    public List<User> query(User user) {
        /**
         * @Description: 查询用户列表
         * @author: Huang Hua
         * @param:  [user]
         * @return: java.util.List<com.zjtelcom.domain.User>
         * @Date: 2018/6/5
         */
        return userMapper.query(user);
    }

    public Map<String, Object> queryUserByName(String userName) {
        Map<String, Object> reslutMap = new HashMap<String, Object>();
        User user = null;
        try{
            user = userMapper.queryUserByName(userName);
        } catch(Exception e){
            logger.error("根据用户名查询用户信息异常,参数={},异常={}",userName);
            reslutMap.put("code", "1");
            reslutMap.put("msg", "根据用户名查询用户信息失败!");
            return reslutMap;
        }
        reslutMap.put("code", "0");
        reslutMap.put("data", user);
        logger.info("根据用户名查询用户信息,参数={},返回值={}",userName, JSON.toJSONString(user));
        return reslutMap;
    }
}
