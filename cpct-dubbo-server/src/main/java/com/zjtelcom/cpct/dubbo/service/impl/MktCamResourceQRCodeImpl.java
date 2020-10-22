package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zjtelcom.cpct.dubbo.service.MktCamResourceQRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Transactional
@Service
public class MktCamResourceQRCodeImpl implements MktCamResourceQRCodeService {

    @Autowired
    private MktCamResourceQRCodeService mktCamResourceQRCodeService;

    @Override
    public Map<String, Object> generatePoster(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        try {
            data = mktCamResourceQRCodeService.generatePoster(params);
        }catch (Exception e) {
            e.printStackTrace();
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "失败");
            return  result;
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "成功");
        result.put("data",data);
        return result;
    }

    @Override
    public Map<String,Object> savePostUrl(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> savePostBackgroundUrl(Map<String, Object> params) {
        return null;
    }

    @Override
    public Map<String, Object> getPostgroundPathPage(Map<String, Object> params) {
        return null;
    }
}
