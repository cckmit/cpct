package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.bean.ResponseVO;
import com.zjtelcom.cpct.dao.blacklist.BlackListLogMapper;
import com.zjtelcom.cpct.dao.blacklist.BlackListMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListLogDO;
import com.zjtelcom.cpct.dubbo.service.BlackListService;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BlackListServiceImpl implements BlackListService {
    @Autowired
    BlackListMapper blackListMapper;
    @Autowired
    ResponseVO responseVO;
    @Autowired
    BlackListLogMapper blackListLogMapper;
    @Autowired
    private RedisUtils redisUtils;

    private static final String SUCCESS_CODE = "0";
    private static final String FAIL_CODE = "1";
    @Override
    public Map<String, Object> deleteBlackList(List<String> phoneNumsDeleted) {
        try {
            blackListMapper.deleteBlackListById(phoneNumsDeleted);
            for(String phone: phoneNumsDeleted){
                redisUtils.hdelRedis("BLACK_LIST", phone);
            }

            //添加操作日志
            for(String phone: phoneNumsDeleted){
                BlackListLogDO blackListLogDO = new BlackListLogDO();
                blackListLogDO.setMethod("delete");
                blackListLogDO.setAssetPhone(phone);
                blackListLogMapper.addBlacklistlog(blackListLogDO);
            }
            return responseVO.response(SUCCESS_CODE,"删除黑名单成功");
        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"删除黑名单失败");
        }
    }
}
