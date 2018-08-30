/**
 * @(#)MktCamChlResultServiceImpl.java, 2018/7/27.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlResultConfRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlResultMapper;
import com.zjtelcom.cpct.domain.User;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalEditVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultService;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:
 * author: linchao
 * date: 2018/07/27 14:25
 * version: V1.0
 */
@Service
@Transactional
public class MktCamChlResultServiceImpl extends BaseService implements MktCamChlResultService {

    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;

    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired
    private MktCamChlConfService mktCamChlConfService;

    @Autowired
    private VerbalService verbalService;

    @Autowired
    private CamScriptService camScriptService;

    /**
     * 添加二次协同结果
     *
     * @param mktCamChlResult
     * @return
     */
    @Override
    public Map<String, Object> saveMktCamChlResult(MktCamChlResult mktCamChlResult) {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        Long mktCamChlResultId = 0L;
        MktCamChlResultDO mktCamChlResultDO = new MktCamChlResultDO();
        try {
            //存储结果信息
            CopyPropertiesUtil.copyBean2Bean(mktCamChlResultDO, mktCamChlResult);
            mktCamChlResultDO.setCreateStaff(UserUtil.loginId());
            mktCamChlResultDO.setCreateDate(new Date());
            mktCamChlResultDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlResultDO.setUpdateDate(new Date());
            mktCamChlResultMapper.insert(mktCamChlResultDO);
            // 获取结果Id
            mktCamChlResultId = mktCamChlResultDO.getMktCamChlResultId();
            // 存储协同渠道 并建立与结果的关联
            if (mktCamChlResult.getMktCamChlConfDetailList() != null) {
                for (MktCamChlConfDetail mktCamChlConfDetail : mktCamChlResult.getMktCamChlConfDetailList()) {
                    if (mktCamChlConfDetail.getEvtContactConfId() != null) {
                        // 结果与推送渠道的关联
                        MktCamChlResultConfRelDO mktCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                        mktCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResultId);
                        mktCamChlResultConfRelDO.setEvtContactConfId(mktCamChlConfDetail.getEvtContactConfId());
                        mktCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                        mktCamChlResultConfRelDO.setCreateDate(new Date());
                        mktCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                        mktCamChlResultConfRelDO.setUpdateDate(new Date());
                        mktCamChlResultConfRelMapper.insert(mktCamChlResultConfRelDO);
                    }
                }
            }
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("mktCamChlResultId", mktCamChlResultId);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to save mktCamChlResult = {}", mktCamChlResult);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlResultId", mktCamChlResultId);
        }
        return mktCamChlConfMap;
    }

    /**
     * 查询二次协同结果
     *
     * @param mktCamChlResultId
     * @return
     */
    @Override
    public Map<String, Object> getMktCamChlResult(Long mktCamChlResultId) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        MktCamChlResult mktCamChlResult = new MktCamChlResult();
        try {
            MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(mktCamChlResultId);
            CopyPropertiesUtil.copyBean2Bean(mktCamChlResult, mktCamChlResultDO);

            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultDO.getMktCamChlResultId());
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                Map<String, Object> mktCamChlConf = mktCamChlConfService.getMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
                MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConf.get("mktCamChlConfDetail");
                mktCamChlConfDetailList.add(mktCamChlConfDetail);
            }
            mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to get mktCamChlResultDO by mktCamChlResultId = {}", mktCamChlResultId);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("resultMsg", ErrorCode.GET_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        }
        return mktCamChlResultMap;
    }

    /**
     * 编辑二次协同结果信息
     *
     * @param mktCamChlResult
     * @return
     */
    @Override
    public Map<String, Object> updateMktCamChlResult(MktCamChlResult mktCamChlResult) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        MktCamChlResultDO mktCamChlResultDO = new MktCamChlResultDO();
        try {
            CopyPropertiesUtil.copyBean2Bean(mktCamChlResultDO, mktCamChlResult);
            mktCamChlResultDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlResultDO.setUpdateDate(new Date());
            mktCamChlResultMapper.updateByPrimaryKey(mktCamChlResultDO);
/*
            if (mktCamChlResult.getMktCamChlConfDetailList() != null) {
                for (MktCamChlConfDetail mktCamChlConfDetail : mktCamChlResult.getMktCamChlConfDetailList()) {
                    Long evtContactConfId = mktCamChlConfDetail.getEvtContactConfId();
                    if (evtContactConfId != null && evtContactConfId != 0) {
                        // 编辑推送渠道基本信息+属性信息
                        mktCamChlConfService.updateMktCamChlConf(mktCamChlConfDetail);
                        if (mktCamChlConfDetail.getVerbalEditVOList() != null) {
                            // 编辑推送渠道下的痛痒点话术和脚本
                            for (VerbalEditVO verbalEditVO : mktCamChlConfDetail.getVerbalEditVOList()) {
                                Long verbalId = verbalEditVO.getVerbalId();
                                if (verbalId != null && verbalId != 0) {
                                    verbalEditVO.setContactConfId(evtContactConfId);
                                    verbalService.editVerbal(UserUtil.loginId(), verbalEditVO);
                                } else {
                                    VerbalAddVO verbalAddVO = new VerbalAddVO();
                                    CopyPropertiesUtil.copyBean2Bean(verbalAddVO, verbalEditVO);
                                    verbalAddVO.setContactConfId(evtContactConfId);
                                    verbalService.addVerbal(UserUtil.loginId(), verbalAddVO);
                                }
                            }
                        }

                    } else {
                        // 编辑时 新增推送渠道
                        Map<String, Object> evtContactConfIdMap = mktCamChlConfService.saveMktCamChlConf(mktCamChlConfDetail);
                        evtContactConfId = (Long) evtContactConfIdMap.get("evtContactConfId");

                        // 添加痛痒点话术
                        if (mktCamChlConfDetail.getVerbalEditVOList() != null) {
                            for (VerbalEditVO verbalEditVO : mktCamChlConfDetail.getVerbalEditVOList()) {
                                VerbalAddVO verbalAddVO = new VerbalAddVO();
                                CopyPropertiesUtil.copyBean2Bean(verbalAddVO, verbalEditVO);
                                verbalAddVO.setContactConfId(evtContactConfId);
                                verbalService.addVerbal(UserUtil.loginId(), verbalAddVO);
                            }
                        }
                        // 添加脚本
                        CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                        CopyPropertiesUtil.copyBean2Bean(camScriptAddVO, mktCamChlConfDetail.getCamScript());
                        camScriptAddVO.setEvtContactConfId(evtContactConfId);
                        camScriptService.addCamScript(UserUtil.loginId(), camScriptAddVO);

                        MktCamChlResultConfRelDO mktCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                        mktCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResult.getMktCamChlResultId());
                        mktCamChlResultConfRelDO.setEvtContactConfId(evtContactConfId);
                        mktCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                        mktCamChlResultConfRelDO.setCreateDate(new Date());
                        mktCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                        mktCamChlResultConfRelDO.setUpdateDate(new Date());
                        mktCamChlResultConfRelMapper.insert(mktCamChlResultConfRelDO);
                    }
                }
            }
*/
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to update mktCamChlResult = {}", JSON.toJSON(mktCamChlResult));
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("resultMsg", ErrorCode.GET_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        }
        return mktCamChlResultMap;
    }

    /**
     * 删除二次协同结果
     *
     * @param mktCamChlResultId
     * @return
     */
    @Override
    public Map<String, Object> deleteMktCamChlResult(Long mktCamChlResultId) {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultId);
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                // 删除推送渠道以及对应的属性
                mktCamChlConfService.deleteMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
            }
            // 删除推送渠道与结果的关联
            mktCamChlResultConfRelMapper.deleteByMktCamChlResultId(mktCamChlResultId);
            //删除二次协同结果
            mktCamChlResultMapper.deleteByPrimaryKey(mktCamChlResultId);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("mktCamChlResultId", mktCamChlResultId);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to delete mktCamChlResultDO by mktCamChlResultId = {}", mktCamChlResultId);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.DELETE_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlResultId", mktCamChlResultId);
        }
        return mktCamChlConfMap;
    }

    /**
     * 复制二次协同渠道
     *
     * @param parentMktCamChlResultId
     * @return
     */
    @Override
    public Map<String, Object> copyMktCamChlResult(Long parentMktCamChlResultId) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        try {
            MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(parentMktCamChlResultId);
            mktCamChlResultDO.setMktCamChlResultId(null);
            mktCamChlResultDO.setCreateDate(new Date());
            mktCamChlResultDO.setCreateStaff(UserUtil.loginId());
            mktCamChlResultDO.setUpdateDate(new Date());
            mktCamChlResultDO.setUpdateStaff(UserUtil.loginId());
            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(parentMktCamChlResultId);

            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                Map<String, Object> mktCamChlConf = mktCamChlConfService.copyMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
      /*          MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConf.get("mktCamChlConfDetail");
                mktCamChlConfDetailList.add(mktCamChlConfDetail);*/
            }




/*            mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);*/
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to get mktCamChlResultDO by mktCamChlResultId = {}", parentMktCamChlResultId);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlResultMap.put("resultMsg", ErrorCode.GET_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
           // mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        }
        return mktCamChlResultMap;
    }


    @Override
    public Map<String, Object> selectAllMktCamChlResult() {

        return null;
    }
}