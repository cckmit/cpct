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
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:  协同渠道基本信息配置 和属性配置
 * author: linchao
 * date: 2018/07/02 14:08
 * version: V1.0
 */
@Transactional
@Service
public class MktCamChlConfServiceImpl extends BaseService implements MktCamChlConfService {

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    @Override
    public Map<String, Object> saveMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail) {
        MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            //添加协同渠道基本信息
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConfDetail);
            mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getErrorCode());
            mktCamChlConfDO.setCreateDate(new Date());
            mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
            mktCamChlConfDO.setUpdateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfMapper.insert(mktCamChlConfDO);
            Long evtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", evtContactConfId);

            // TODO 将协同规则存储到库中

            // 更新属性
            List<MktCamChlConfAttr> mktCamChlConfAttrList = mktCamChlConfDetail.getMktCamChlConfAttrList();
            for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfAttrList) {
                MktCamChlConfAttrDO mktCamChlConfAttrDO = new MktCamChlConfAttrDO();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrDO, mktCamChlConfAttr);
                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                if (mktCamChlConfAttr.getAttrId() == ConfAttrEnum.RULE.getArrId()) {
                    mktCamChlConfAttrDO.setAttrValue(evtContactConfId.toString());
                }
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
        MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            // 更新协同渠道基本信息
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConfDetail);
            mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getErrorCode());
            mktCamChlConfDO.setUpdateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfMapper.updateByPrimaryKey(mktCamChlConfDO);
            Long evtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.UPDATE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", evtContactConfId);

            // TODO 将协同规则进行修改

            // 将属性插入库中
            List<MktCamChlConfAttr> mktCamChlConfAttrList = mktCamChlConfDetail.getMktCamChlConfAttrList();
            for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfAttrList) {
                MktCamChlConfAttrDO mktCamChlConfAttrDO = new MktCamChlConfAttrDO();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrDO, mktCamChlConfAttr);
                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                if (mktCamChlConfAttr.getAttrId() == ConfAttrEnum.RULE.getArrId()) {
                    mktCamChlConfAttrDO.setAttrValue(evtContactConfId.toString());
                }
                mktCamChlConfAttrMapper.updateByPrimaryKey(mktCamChlConfAttrDO);
            }
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to save MktCamChlConf = {}", mktCamChlConfDO, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.UPDATE_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", mktCamChlConfDO.getEvtContactConfId());
        }
        return mktCamChlConfMap;
    }

    @Override
    public Map<String, Object> getMktCamChlConf(Long evtContactConfId) {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
        try {
            MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDetail, mktCamChlConfDO);
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttr = new MktCamChlConfAttr();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttr, mktCamChlConfAttrDO);
                if (mktCamChlConfAttr.getAttrId() == ConfAttrEnum.RULE.getArrId()) {
                    //TODO 通过EvtContactConfId获取规则放入属性中
                }
                mktCamChlConfAttrList.add(mktCamChlConfAttr);
            }
            mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to getMktCamChlConf by evtContactConfId = {}", evtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetail);
        }
        return mktCamChlConfMap;
    }


    @Override
    public Map<String, Object> listMktCamChlConf() {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
        try {
            //获取所有的协同渠道配置基本信息
            List<MktCamChlConfDO> mktCamChlConfDOList = mktCamChlConfMapper.selectAll();
            for (MktCamChlConfDO mktCamChlConfDO : mktCamChlConfDOList) {
                // 遍历查询出对应的属性
                List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDetail, mktCamChlConfDO);
                for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                    MktCamChlConfAttr mktCamChlConfAttr = new MktCamChlConfAttr();
                    CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttr, mktCamChlConfAttrDO);
                    if (mktCamChlConfAttr.getAttrId() == ConfAttrEnum.RULE.getArrId()) {
                        //TODO 通过EvtContactConfId获取规则放入属性中
                    }
                    mktCamChlConfDetail.getMktCamChlConfAttrList().add(mktCamChlConfAttr);
                }
                mktCamChlConfDetailList.add(mktCamChlConfDetail);
            }
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetailList", mktCamChlConfDetailList);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to get mktCamChlConfDetailList evtContactConfId = {}", mktCamChlConfDetailList, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetailList", mktCamChlConfDetailList);
        }
        return mktCamChlConfMap;
    }

    @Override
    public Map<String, Object> deleteMktCamChlConf(Long evtContactConfId) {
        mktCamChlConfMapper.deleteByPrimaryKey(evtContactConfId);
        mktCamChlConfAttrMapper.deleteByEvtContactConfId(evtContactConfId);

        //TODO 根据evtContactConfId删除相关的规则


        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        mktCamChlConfMap.put("resultMsg", ErrorCode.DELETE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
        return mktCamChlConfMap;
    }
}