/**
 * @(#)MktCamChlConfAttrServiceImpl.java, 2018/7/2.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/02 14:08
 * version: V1.0
 */
@Service
public class MktCamChlConfServiceImpl extends BaseService implements MktCamChlConfService {

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    @Override
    public Map<String, Object> saveMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail) {
        MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            //添加协同渠道基本信息
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConfDetail);
            mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getErrorCode());
            mktCamChlConfMapper.insert(mktCamChlConfDO);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", mktCamChlConfDO.getEvtContactConfId());

            // TODO 将协同规则存储到库中，并获取到对应的Id


            // 将属性插入库中
            List<MktCamChlConfAttr> mktCamChlConfAttrList = mktCamChlConfDetail.getMktCamChlConfAttrList();
            for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfAttrList) {
                MktCamChlConfAttrDO mktCamChlConfAttrDO = new MktCamChlConfAttrDO();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrDO, mktCamChlConfAttr);
                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
            }


        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to save MktCamChlConf = {}", mktCamChlConfDO, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", mktCamChlConfDO.getEvtContactConfId());
        }
        return mktCamChlConfMap;
    }

    @Override
    public Map<String, Object> updateMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail) {
        return null;
    }

    @Override
    public Map<String, Object> getMktCamChlConf(Long evtContactConfId) {
        return null;
    }

    @Override
    public Map<String, Object> deleteMktCamChlConf(Long evtContactConfId) {
        return null;
    }
}