package com.zjtelcom.cpct.dubbo.out.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zjtelcom.cpct.bean.ResponseVO;
import com.zjtelcom.cpct.dao.blacklist.BlackListLogMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListLogDO;
import com.zjtelcom.cpct.dubbo.out.BlackListLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service
@Component
public class BlackListLogServiceImpl implements BlackListLogService {
    @Autowired
    BlackListLogMapper blackListLogMapper;
    @Autowired
    ResponseVO responseVO;
    private static final String SUCCESS_CODE = "0";
    private static final String FAIL_CODE = "1";

    @Override
    public Map<String, Object> getBlackListLog(Map<String, Object> params) {
        try {
            List<BlackListLogDO> blackListLogDoS = blackListLogMapper.getBlackListLog((String)params.get("assetPhone"),(String)params.get("serviceCate"),(String)params.get("maketingCate"),
                    (String)params.get("publicBenefitCate"),(String)params.get("channel"),(String)params.get("staffId"),(String)params.get("startDate"),(String)params.get("endDate"),
                    (String)params.get("operType"));
            return responseVO.response(SUCCESS_CODE,"黑名单日志获取成功",blackListLogDoS);
        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"黑名单日志获取失败");
        }
    }
}
