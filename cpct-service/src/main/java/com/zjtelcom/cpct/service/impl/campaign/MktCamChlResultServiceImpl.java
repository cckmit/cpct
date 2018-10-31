/**
 * @(#)MktCamChlResultServiceImpl.java, 2018/7/27.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.domain.User;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalEditVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultService;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import groovy.transform.ASTTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private MktCamChlConfService mktCamChlConfService;

    @Autowired
    MktCamResultRelMapper mktCamResultRelMapper;

    @Autowired
    ContactChannelMapper contactChannelMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    @Autowired
    private CamScriptService camScriptService;

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private RedisUtils redisUtils;

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
            if (mktCamChlResult.getMktCamChlResultId() != null) {
                mktCamChlResult.setMktCamChlResultId(null);
            }
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
                    // 保存话术
                    CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                    camScriptAddVO.setEvtContactConfId(mktCamChlConfDetail.getEvtContactConfId());
                    camScriptAddVO.setMktCampaignId(mktCamChlConfDetail.getMktCampaignId());
                    camScriptAddVO.setScriptDesc(mktCamChlConfDetail.getScriptDesc());
                    camScriptService.addCamScript(UserUtil.loginId(), camScriptAddVO);
                }
            }
            redisUtils.set("MktCamChlResult_" + mktCamChlResultId, mktCamChlResult);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("mktCamChlResultDO", mktCamChlResultDO);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to save mktCamChlResult = {}, Expertion:", mktCamChlResult, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlResultDO", mktCamChlResultDO);
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

            mktCamChlResultConfRelMapper.deleteByMktCamChlResultId(mktCamChlResult.getMktCamChlResultId());

            // 添加/编辑话术
            if (mktCamChlResult.getMktCamChlConfDetailList() != null) {
                for (int i = 0; i < mktCamChlResult.getMktCamChlConfDetailList().size(); i++) {
                    MktCamChlResultConfRelDO mktCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                    mktCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResult.getMktCamChlResultId());
                    mktCamChlResultConfRelDO.setEvtContactConfId(mktCamChlResult.getMktCamChlConfDetailList().get(i).getEvtContactConfId());
                    mktCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                    mktCamChlResultConfRelDO.setCreateDate(new Date());
                    mktCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                    mktCamChlResultConfRelDO.setUpdateDate(new Date());
                    mktCamChlResultConfRelMapper.insert(mktCamChlResultConfRelDO);

                    // 保存话术
                    CamScriptAddVO camScriptAddVO = new CamScriptAddVO();
                    camScriptAddVO.setEvtContactConfId(mktCamChlResult.getMktCamChlConfDetailList().get(i).getEvtContactConfId());
                    camScriptAddVO.setMktCampaignId(mktCamChlResult.getMktCampaignId());
                    camScriptAddVO.setScriptDesc(mktCamChlResult.getMktCamChlConfDetailList().get(i).getScriptDesc());
                    camScriptService.addCamScript(UserUtil.loginId(), camScriptAddVO);
                }
            }

            redisUtils.set("MktCamChlResult_" + mktCamChlResult.getMktCamChlResultId(), mktCamChlResult);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to update mktCamChlResult = {}", JSON.toJSON(mktCamChlResult), e);
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
            redisUtils.remove("MktCamChlResult_" + mktCamChlResultId);
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
            // 新增结果 并获取Id
            mktCamChlResultMapper.insert(mktCamChlResultDO);
            Long mktCamChlResultId = mktCamChlResultDO.getMktCamChlResultId();
            // 获取原二次协同渠道下结果的推送渠道
            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(parentMktCamChlResultId);
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            // 遍历获取原二次协同渠道下结果的推送渠道
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                // 复制推送渠道
                Map<String, Object> mktCamChlConfMap = mktCamChlConfService.copyMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
                MktCamChlConfDO mktCamChlConfDO = (MktCamChlConfDO) mktCamChlConfMap.get("mktCamChlConfDO");
                // 新的推送渠道与新的结果简历关联
                if (mktCamChlConfDO != null) {
                    // 结果与推送渠道的关联
                    MktCamChlResultConfRelDO childCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                    childCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResultId);
                    childCamChlResultConfRelDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                    childCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setCreateDate(new Date());
                    childCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setUpdateDate(new Date());
                    mktCamChlResultConfRelMapper.insert(childCamChlResultConfRelDO);
                }
            }
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResultDO", mktCamChlResultDO);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to get mktCamChlResultDO by mktCamChlResultId = {}", parentMktCamChlResultId);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlResultMap.put("resultMsg", ErrorCode.GET_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlResultMap;
    }


    /**
     * 复制二次协同渠道(从redis获取数据)
     *
     * @param mktCamChlResult
     * @return
     */
    @Override
    public Map<String, Object> copyMktCamChlResultFromRedis(MktCamChlResult mktCamChlResult) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        try {
            // MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(parentMktCamChlResultId);
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();

            Future<Map<String, Object>> resultFuture = null;
            if (mktCamChlResult != null) {
                resultFuture = executorService.submit(new CopyMktCamChlResultTask(mktCamChlResult));
                threadList.add(resultFuture);
            }
            Future<Map<String, Object>> mktCamChlConfFuture = null;
            if (mktCamChlResult != null) {
                //初始化线程池
                mktCamChlConfFuture = executorService.submit(new CopyMktCamChlConfTask(mktCamChlResult));
                threadList.add(mktCamChlConfFuture);
            }

            MktCamChlResult mktCamChlResultNew = new MktCamChlResult();
            if (resultFuture != null) {
                mktCamChlResultNew = (MktCamChlResult) resultFuture.get().get("mktCamChlResult");
            }
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            if (mktCamChlConfFuture != null) {
                mktCamChlConfDetailList = (List<MktCamChlConfDetail>) mktCamChlConfFuture.get().get("mktCamChlConfDetailList");
                mktCamChlResultNew.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            }
/*            for (MktCamChlConfDetail mktCamChlConfDetail:mktCamChlConfDetailList) {
                // 新的推送渠道与新的结果简历关联
                if (mktCamChlConfDetail != null) {
                    // 结果与推送渠道的关联
                    MktCamChlResultConfRelDO childCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                    childCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResultNew.getMktCamChlResultId());
                    childCamChlResultConfRelDO.setEvtContactConfId(mktCamChlConfDetail.getEvtContactConfId());
                    childCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setCreateDate(new Date());
                    childCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setUpdateDate(new Date());
                    mktCamChlResultConfRelMapper.insert(childCamChlResultConfRelDO);
                }
            }*/

            // redisUtils.set("MktCamChlResult_" + mktCamChlResultNew.getMktCamChlResultId(), mktCamChlResultNew);
            executorService.shutdown();
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResultNew);
        } catch (Exception e) {
            //logger.error("[op:MktCamChlResultServiceImpl] failed to get mktCamChlResultDO by mktCamChlResultId = {}", parentMktCamChlResultId);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlResultMap.put("resultMsg", ErrorCode.GET_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlResultMap;
    }

    class CopyMktCamChlResultTask implements Callable<Map<String, Object>> {
        private MktCamChlResult mktCamChlResult;

        public CopyMktCamChlResultTask(MktCamChlResult mktCamChlResult) {
            this.mktCamChlResult = mktCamChlResult;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> mktCamChlResultMap = new HashMap<>();
//            MktCamChlResult mktCamChlResult = (MktCamChlResult) redisUtils.get("MktCamChlResult_" + mktCamChlResultId);
/*            MktCamChlResultDO mktCamChlResultDO = BeanUtil.create(mktCamChlResult, new MktCamChlResultDO());
            mktCamChlResultDO.setMktCamChlResultId(null);
            mktCamChlResultDO.setCreateDate(new Date());
            mktCamChlResultDO.setCreateStaff(UserUtil.loginId());
            mktCamChlResultDO.setUpdateDate(new Date());
            mktCamChlResultDO.setUpdateStaff(UserUtil.loginId());*/
            // 新增结果 并获取Id
            // mktCamChlResultMapper.insert(mktCamChlResultDO);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
            return mktCamChlResultMap;
        }
    }

    class CopyMktCamChlConfTask implements Callable<Map<String, Object>> {
        private MktCamChlResult mktCamChlResult;

        public CopyMktCamChlConfTask(MktCamChlResult mktCamChlResult) {
            this.mktCamChlResult = mktCamChlResult;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> mktCamChlConfDetailMap = new HashMap<>();
            // 获取原二次协同渠道下结果的推送渠道
            //List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultId);
            List<MktCamChlConfDetail> mktCamChlConfDetailList = mktCamChlResult.getMktCamChlConfDetailList();
            List<MktCamChlConfDetail> mktCamChlConfDetailListNew = new ArrayList<>();
            // 遍历获取原二次协同渠道下结果的推送渠道
            for (MktCamChlConfDetail mktCamChlConfDetail : mktCamChlConfDetailList) {
                // 复制推送渠道
                Map<String, Object> mktCamChlConfMap = mktCamChlConfService.copyMktCamChlConfFormRedis(mktCamChlConfDetail.getEvtContactConfId(), mktCamChlConfDetail.getScriptDesc());
                MktCamChlConfDetail mktCamChlConfDetailNew = (MktCamChlConfDetail) mktCamChlConfMap.get("mktCamChlConfDetail");
                mktCamChlConfDetailListNew.add(mktCamChlConfDetailNew);
            }
            mktCamChlConfDetailMap.put("mktCamChlConfDetailList", mktCamChlConfDetailListNew);
            return mktCamChlConfDetailMap;
        }
    }


    /**
     * 查询所有 有二次协同 且二次协同为工单，且有效的
     *
     * @return
     */
    @Override
    public Map<String, Object> selectResultList() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> mktCampaignIdList = mktCamResultRelMapper.selectAllGroupByMktCampaignId();
        List<MktCamResultRelDeatil> mktCamResultRelDeatilList = new ArrayList<>();
        for (Long mktCampaignId : mktCampaignIdList) {
            List<MktCamChlResultDO> mktCamChlResultDOList = mktCamChlResultMapper.selectResultByMktCampaignId(mktCampaignId);
            List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
            for (MktCamChlResultDO mktCamChlResultDO : mktCamChlResultDOList) {
                MktCamChlResult mktCamChlResult = BeanUtil.create(mktCamChlResultDO, new MktCamChlResult());
                List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultDO.getMktCamChlResultId());
                List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                    MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(mktCamChlResultConfRelDO.getEvtContactConfId());
                    MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
                    // 获取触点渠道编码
                    Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
                    if (channel != null) {
                        mktCamChlConfDetail.setContactChlCode(channel.getContactChlCode());
                    }
                    mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    // 获取属性
                    List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDetail.getEvtContactConfId());
                    List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
                    for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                        MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                        mktCamChlConfAttrList.add(mktCamChlConfAttr);
                    }
                    mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
                }
                mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                mktCamChlResultList.add(mktCamChlResult);
            }

            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
            MktCamResultRelDeatil mktCamResultRelDeatil = BeanUtil.create(mktCampaignDO, new MktCamResultRelDeatil());
            mktCamResultRelDeatil.setMktCamChlResultList(mktCamChlResultList);
            mktCamResultRelDeatilList.add(mktCamResultRelDeatil);
        }
        resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        resultMap.put("resultMsg", "success");
        resultMap.put("mktCamResultRelDeatilList", mktCamResultRelDeatilList);
        return resultMap;
    }


    @Override
    public Map<String, Object> selectAllMktCamChlResult() {
        return null;
    }
}