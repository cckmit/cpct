/**
 * @(#)MktStrategyConfServiceImpl.java, 2018/6/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlResultMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.*;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfRuleService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.service.thread.TarGrpRule;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Description:
 * author: linchao
 * date: 2018/06/25 17:23
 * version: V1.0
 */
@Transactional
@Service
public class MktStrategyConfServiceImpl extends BaseService implements MktStrategyConfService {

    /**
     * 策略配置基本信息
     */
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    /**
     * 策略配置下发城市关联
     */
    @Autowired
    private MktStrategyConfRegionRelMapper mktStrategyConfRegionRelMapper;

    /**
     * 策略配置规则
     */
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

    /**
     * 策略配置和规则关联
     */
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;

    /**
     * 策略配置和活动关联
     */
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;

    /**
     * 下发城市
     */
    @Autowired
    private SysAreaMapper sysAreaMapper;

    /**
     * 策略下发渠道
     */
    @Autowired
    private ContactChannelMapper contactChannelMapper;

    /*
     * 协同渠道
     */
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    /**
     * 客户分群
     */
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    /**
     * 注智标签
     */
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    /**
     * RedisUtils工具类
     */
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 规则service
     */
    @Autowired
    private MktStrategyConfRuleService mktStrategyConfRuleService;

    /**
     * 过滤规则与策略关联 Mapper
     */
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;

    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;


    /**
     * 删除活动配置
     *
     * @param mktStrategyConfId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> deleteMktStrategyConf(Long mktStrategyConfId) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        // 删除与策略关联的下发城市
        mktStrategyConfRegionRelMapper.deleteByMktStrategyConfId(mktStrategyConfId);
        // 删除与策略关联的过滤规则
        mktStrategyFilterRuleRelMapper.deleteByStrategyId(mktStrategyConfId);
        //删除策略下的规则，以及关联的表
        List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(mktStrategyConfId);
        for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
            mktStrategyConfRuleMapper.deleteByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
            mktStrategyConfRuleRelMapper.deleteByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleRelId());
        }
        //删除策略与活动的关联
        mktCamStrategyConfRelMapper.deleteByStrategyConfId(mktStrategyConfId);
        //删除策略
        mktStrategyConfMapper.deleteByPrimaryKey(mktStrategyConfId);
        mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_STR_CONF_SUCCESS.getErrorMsg());
        return mktStrategyConfMap;
    }

    /**
     * 添加策略配置信息
     *
     * @param mktStrategyConfDetail
     * @return
     */
    @Override
    public Map<String, Object> saveMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) throws Exception {

        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        try {
            // 添加属性配置信息
            MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDO, mktStrategyConfDetail);
            mktStrategyConfDO.setCreateStaff(UserUtil.loginId());
            mktStrategyConfDO.setCreateDate(new Date());
            mktStrategyConfDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfDO.setUpdateDate(new Date());
            // 策略下发渠道
            String channelIds = "";
            if (mktStrategyConfDetail.getChannelList() != null) {
                for (int i = 0; i < mktStrategyConfDetail.getChannelList().size(); i++) {
                    if (i == 0) {
                        channelIds += mktStrategyConfDetail.getChannelList().get(i);
                    } else {
                        channelIds += "/" + mktStrategyConfDetail.getChannelList().get(i);
                    }
                }
            }
            mktStrategyConfDO.setChannelsId(channelIds);
            // 下发城市
            String areaIds = "";
            if (mktStrategyConfDetail.getAreaIdList() != null) {
                for (int i = 0; i < mktStrategyConfDetail.getAreaIdList().size(); i++) {
                    if (i == 0) {
                        areaIds += mktStrategyConfDetail.getAreaIdList().get(i);
                    } else {
                        areaIds += "/" + mktStrategyConfDetail.getAreaIdList().get(i);
                    }
                }
            }
            mktStrategyConfDO.setAreaId(areaIds);
            // 插入策略配置基本，并返回策略Id -- mktStrategyConfId
            mktStrategyConfMapper.insert(mktStrategyConfDO);
            // 策略Id
            Long mktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();

            //保存策略与过滤规则关系
            if (mktStrategyConfDetail.getFilterRuleIdList() != null && mktStrategyConfDetail.getFilterRuleIdList().size() > 0) {
                for (Long FilterRuleId : mktStrategyConfDetail.getFilterRuleIdList()) {
                    MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                    mktStrategyFilterRuleRelDO.setRuleId(FilterRuleId);
                    mktStrategyFilterRuleRelDO.setStrategyId(mktStrategyConfId);
                    mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                    mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                    mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                }
            }

            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_STR_CONF_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfId", mktStrategyConfId);

            ExecutorService executorService = Executors.newCachedThreadPool();
            // 遍历策略下对应的规则
            try {
                for (MktStrategyConfRule mktStrategyConfRule : mktStrategyConfDetail.getMktStrategyConfRuleList()) {
                    mktStrategyConfRule.setMktCampaignId(mktStrategyConfDetail.getMktCampaignId());
                    mktStrategyConfRule.setMktCampaignName(mktStrategyConfDetail.getMktCampaignName());
                    mktStrategyConfRule.setStrategyConfId(mktStrategyConfId);
                    mktStrategyConfRule.setStrategyConfName(mktStrategyConfDetail.getMktStrategyConfName());
                    Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.saveMktStrategyConfRule(mktStrategyConfRule);
                    // 返回策略规则
                    MktStrategyConfRuleDO mktStrategyConfRuleDO = (MktStrategyConfRuleDO) mktStrategyConfRuleMap.get("mktStrategyConfRuleDO");
                    // 建立策略配置和规则的关系
                    MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                    mktStrategyConfRuleRelDO.setMktStrategyConfId(mktStrategyConfId);
                    mktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                    mktStrategyConfRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyConfRuleRelDO.setCreateDate(new Date());
                    mktStrategyConfRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyConfRuleRelDO.setUpdateDate(new Date());
                    mktStrategyConfRuleRelMapper.insert(mktStrategyConfRuleRelDO);

                    // 线程池执行规则存入redis
                    executorService.submit(new TarGrpRule(mktStrategyConfDetail.getMktCampaignId(), mktStrategyConfId, mktStrategyConfRuleDO, redisUtils, tarGrpConditionMapper, injectionLabelMapper));
                }
                // 关闭线程池
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            } catch (Exception e) {
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }

            // 建立策略与活动的关联
            MktCamStrategyConfRelDO mktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
            mktCamStrategyConfRelDO.setStrategyConfId(mktStrategyConfId);
            mktCamStrategyConfRelDO.setMktCampaignId(mktStrategyConfDetail.getMktCampaignId());
            mktCamStrategyConfRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktCamStrategyConfRelDO.setStatusDate(new Date());
            mktCamStrategyConfRelDO.setCreateStaff(UserUtil.loginId());
            mktCamStrategyConfRelDO.setCreateDate(new Date());
            mktCamStrategyConfRelDO.setUpdateStaff(UserUtil.loginId());
            mktCamStrategyConfRelDO.setUpdateDate(new Date());
            mktCamStrategyConfRelMapper.insert(mktCamStrategyConfRelDO);

        } catch (Exception e) {
            logger.error("[op:MktStrategyConfServiceImpl] fail to save MktStrategyConfDetail = {}, Exception: ", JSON.toJSON(mktStrategyConfDetail), e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_STR_CONF_FAILURE.getErrorMsg());

        }
        return mktStrategyConfMap;
    }


    /**
     * 更新策略配置信息
     *
     * @param mktStrategyConfDetail
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> updateMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<String, Object>();
        try {
            // 修改属性配置信息
            MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDO, mktStrategyConfDetail);
            mktStrategyConfDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfDO.setUpdateDate(new Date());
            // 策略下发渠道
            String channelIds = "";
            if (mktStrategyConfDetail.getChannelList() != null) {
                for (int i = 0; i < mktStrategyConfDetail.getChannelList().size(); i++) {
                    if (i == 0) {
                        channelIds += mktStrategyConfDetail.getChannelList().get(i);
                    } else {
                        channelIds += "/" + mktStrategyConfDetail.getChannelList().get(i);
                    }
                }
            }
            mktStrategyConfDO.setChannelsId(channelIds);
            // 下发城市
            String areaIds = "";
            if (mktStrategyConfDetail.getAreaIdList() != null) {
                for (int i = 0; i < mktStrategyConfDetail.getAreaIdList().size(); i++) {
                    if (i == 0) {
                        areaIds += mktStrategyConfDetail.getAreaIdList().get(i);
                    } else {
                        areaIds += "/" + mktStrategyConfDetail.getAreaIdList().get(i);
                    }
                }
            }
            mktStrategyConfDO.setAreaId(areaIds);

            // 更新策略配置基本，并返回策略Id -- mktStrategyConfId
            mktStrategyConfMapper.updateByPrimaryKey(mktStrategyConfDO);
            // 策略Id
            Long mktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();

            //重建策略与过滤规则关系
            mktStrategyFilterRuleRelMapper.deleteByStrategyId(mktStrategyConfId);
            if (mktStrategyConfDetail.getFilterRuleIdList() != null && mktStrategyConfDetail.getFilterRuleIdList().size() > 0) {
                for (Long FilterRuleId : mktStrategyConfDetail.getFilterRuleIdList()) {
                    MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                    mktStrategyFilterRuleRelDO.setRuleId(FilterRuleId);
                    mktStrategyFilterRuleRelDO.setStrategyId(mktStrategyConfId);
                    mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                    mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                    mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                }
            }

            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_STR_CONF_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfId", mktStrategyConfId);

            // 遍历策略下的所有规则
            if (mktStrategyConfDetail.getMktStrategyConfRuleList() != null) {
                ExecutorService executorService = Executors.newCachedThreadPool();
                try {
                    for (MktStrategyConfRule mktStrategyConfRule : mktStrategyConfDetail.getMktStrategyConfRuleList()) {
                        Map<String, Object> mktStrategyConfRuleMap;
                        mktStrategyConfRule.setMktCampaignId(mktStrategyConfDetail.getMktCampaignId());
                        mktStrategyConfRule.setMktCampaignName(mktStrategyConfDetail.getMktCampaignName());
                        mktStrategyConfRule.setStrategyConfId(mktStrategyConfId);
                        mktStrategyConfRule.setStrategyConfName(mktStrategyConfDetail.getMktStrategyConfName());
                        //判断规则是否是修改还是新增
                        if (mktStrategyConfRule.getMktStrategyConfRuleId() != null && mktStrategyConfRule.getMktStrategyConfRuleId() != 0) {
                            // 修改规则的信息 并返回
                            mktStrategyConfRuleMap = mktStrategyConfRuleService.updateMktStrategyConfRule(mktStrategyConfRule);
                        } else {
                            // 添加规则的信息 并返回
                            mktStrategyConfRuleMap = mktStrategyConfRuleService.saveMktStrategyConfRule(mktStrategyConfRule);
                            // 策略规则 Id
                            MktStrategyConfRuleDO mktStrategyConfRuleDO = (MktStrategyConfRuleDO) mktStrategyConfRuleMap.get("mktStrategyConfRuleDO");
                            // 建立策略配置和规则的关系
                            MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                            mktStrategyConfRuleRelDO.setCreateDate(new Date());
                            mktStrategyConfRuleRelDO.setCreateStaff(UserUtil.loginId());
                            mktStrategyConfRuleRelDO.setMktStrategyConfId(mktStrategyConfId);
                            mktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                            mktStrategyConfRuleRelDO.setUpdateStaff(UserUtil.loginId());
                            mktStrategyConfRuleRelDO.setUpdateDate(new Date());
                            mktStrategyConfRuleRelMapper.insert(mktStrategyConfRuleRelDO);
                        }
                        MktStrategyConfRuleDO mktStrategyConfRuleDO = (MktStrategyConfRuleDO) mktStrategyConfRuleMap.get("mktStrategyConfRuleDO");
                        // 线程池执行规则存入redis
                        executorService.submit(new TarGrpRule(mktStrategyConfDetail.getMktCampaignId(), mktStrategyConfId, mktStrategyConfRuleDO, redisUtils, tarGrpConditionMapper, injectionLabelMapper));
                    }
                    // 关闭线程池
                    if (!executorService.isShutdown()) {
                        executorService.shutdown();
                    }
                } catch (Exception e) {
                    // 关闭线程池
                    if (!executorService.isShutdown()) {
                        executorService.shutdown();
                    }
                }

            }

        } catch (Exception e) {
            logger.error("[op:MktStrategyConfServiceImpl] fail to update MktStrategyConfDetail = {}, Exception: ", JSON.toJSON(mktStrategyConfDetail), e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", ErrorCode.UPDATE_MKT_STR_CONF_FAILURE.getErrorMsg());
        }
        return mktStrategyConfMap;
    }

    /**
     * 查询配置配置信息
     *
     * @param mktStrategyConfId
     * @return
     */
    @Override
    public Map<String, Object> getMktStrategyConf(Long mktStrategyConfId) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<String, Object>();
        MktStrategyConfDetail mktStrategyConfDetail = new MktStrategyConfDetail();
        try {
            //查出获取所有的城市信息, 设成全局Map
            Map<Integer, String> cityMap = new HashMap<>();
            List<SysArea> sysAreaList = sysAreaMapper.selectAll();
            for (SysArea sysArea : sysAreaList) {
                cityMap.put(sysArea.getAreaId(), sysArea.getName());
            }

            //更具Id查询策略配置信息
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktStrategyConfId);
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDetail, mktStrategyConfDO);
            List<Integer> areaIdList = new ArrayList<>();
            String[] areaIds = mktStrategyConfDO.getAreaId().split("/");
            if (areaIds != null && !"".equals(areaIds[0])) {
                for (String areaId : areaIds) {
                    areaIdList.add(Integer.valueOf(areaId));
                }
                mktStrategyConfDetail.setAreaIdList(areaIdList);

            }
            // 策略下发渠道
            String[] channelIds = mktStrategyConfDO.getChannelsId().split("/");
            List<Long> channelList = new ArrayList<>();
            if(channelIds!=null && !"".equals(channelIds[0])){
                for (String channelId : channelIds) {
                    channelList.add(Long.valueOf(channelId));
                }
                mktStrategyConfDetail.setChannelList(channelList);
            }
            // 获取过滤规则集合
            List<Long> filterRuleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktStrategyConfId);
            mktStrategyConfDetail.setFilterRuleIdList(filterRuleIdList);

            //查询与策略匹配的所有规则
            List<MktStrategyConfRule> mktStrategyConfRuleList = new ArrayList<>();
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfId);
            List<MktStrategyConfRuleRel> mktStrategyConfRuleRelList = new ArrayList<>();
            for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                MktStrategyConfRule mktStrategyConfRule = new MktStrategyConfRule();
                CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRule, mktStrategyConfRuleDO);

                if (mktStrategyConfRuleDO.getProductId() != null) {
                    String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                    List<Long> productIdList = new ArrayList<>();
                    for (int i = 0; i < productIds.length; i++) {
                        if (productIds[i] != "" && !"".equals(productIds[i])) {
                            productIdList.add(Long.valueOf(productIds[i]));
                        }
                    }
                    mktStrategyConfRule.setProductIdlist(productIdList);
                }
                if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                    String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                    List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                    for (int i = 0; i < evtContactConfIds.length; i++) {
                        if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                            MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
                            mktCamChlConfDetail.setEvtContactConfId(Long.valueOf(evtContactConfIds[i]));
                            String evtContactConfName = mktCamChlConfMapper.selectforName(Long.valueOf(evtContactConfIds[i]));
                            mktCamChlConfDetail.setEvtContactConfName(evtContactConfName);
                            mktCamChlConfDetailList.add(mktCamChlConfDetail);
                        }
                    }
                    mktStrategyConfRule.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                }

                if (mktStrategyConfRuleDO.getMktCamChlResultId() != null) {
                    String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                    List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
                    for (int i = 0; i < mktCamChlResultIds.length; i++) {
                        if (mktCamChlResultIds[i] != null && !"".equals(mktCamChlResultIds[i])) {
                            MktCamChlResult mktCamChlResult = new MktCamChlResult();
                            mktCamChlResultMapper.selectByPrimaryKey(Long.valueOf(mktCamChlResultIds[i]));

                            mktCamChlResult.setMktCamChlResultId(Long.valueOf(mktCamChlResultIds[i]));
                            mktCamChlResultList.add(mktCamChlResult);
                        }
                    }
                    mktStrategyConfRule.setMktCamChlResultList(mktCamChlResultList);
                }

                mktStrategyConfRuleList.add(mktStrategyConfRule);
            }
            mktStrategyConfDetail.setMktStrategyConfRuleList(mktStrategyConfRuleList);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.GET_MKT_STR_CONF_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfDetail", mktStrategyConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfServiceImpl] fail to get MktStrategyConfDetail = {}, Exception:", JSON.toJSON(mktStrategyConfDetail), e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", ErrorCode.GET_MKT_STR_CONF_FAILURE.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfDetail", mktStrategyConfDetail);
            return mktStrategyConfMap;
        }
        return mktStrategyConfMap;
    }

    /**
     * 通过原策略id复制策略
     *
     * @param parentMktStrategyConfId
     * @param isPublish 是否为发布操作
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> copyMktStrategyConf(Long parentMktStrategyConfId, Boolean isPublish) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        // 通过原策略id 获取原策略基本信息
        try {
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(parentMktStrategyConfId);
            // 获取策略下规则信息
            List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(parentMktStrategyConfId);

            mktStrategyConfDO.setMktStrategyConfId(null);
            mktStrategyConfDO.setCreateDate(new Date());
            mktStrategyConfDO.setCreateStaff(UserUtil.loginId());
            mktStrategyConfDO.setUpdateDate(new Date());
            mktStrategyConfDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfMapper.insert(mktStrategyConfDO);
            Long childMktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();

            //获取策略对应的过滤规则
            List<Long> ruleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(parentMktStrategyConfId);
            // 与新的策略建立关联
            for (Long ruleId : ruleIdList) {
                MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                mktStrategyFilterRuleRelDO.setStrategyId(childMktStrategyConfId);
                mktStrategyFilterRuleRelDO.setRuleId(ruleId);
                mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
            }
            // 遍历规则
            for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
                // 复制获取规则
                Map<String, Object> mktStrategyConfRuleMap = mktStrategyConfRuleService.copyMktStrategyConfRule(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId(),true);
                Long mktStrategyConfRuleId = (Long) mktStrategyConfRuleMap.get("mktStrategyConfRuleId");
                // 简历策略和规则的关系
                MktStrategyConfRuleRelDO childMktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                childMktStrategyConfRuleRelDO.setMktStrategyConfId(childMktStrategyConfId);
                childMktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleId);
                childMktStrategyConfRuleRelDO.setCreateDate(new Date());
                childMktStrategyConfRuleRelDO.setCreateStaff(UserUtil.loginId());
                childMktStrategyConfRuleRelDO.setUpdateDate(new Date());
                childMktStrategyConfRuleRelDO.setUpdateStaff(UserUtil.loginId());
                mktStrategyConfRuleRelMapper.insert(childMktStrategyConfRuleRelDO);
            }
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", "复制策略成功！");
            mktStrategyConfMap.put("childMktStrategyConfId", childMktStrategyConfId);
        } catch (Exception e) {
            logger.error("[op:copyMktStrategyConf] copyMktStrategyConf parentMktStrategyConfId ={} 失败Exception = ", parentMktStrategyConfId, e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", "复制策略失败！");
        }
        return mktStrategyConfMap;
    }


    /**
     * 复制策略详情
     *
     * @param mktStrategyConfDetail
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> copyMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) throws Exception {
        SimpleDateFormat simpleDateFormat = null;
        Map<String, Object> mktStrategyConfDetailMap = null;
        try {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            logger.info("MktStrategyConfDetail-->>>开始：" + simpleDateFormat.format(new Date()));
            mktStrategyConfDetailMap = new HashMap<>();
            MktStrategyConfDetail mktStrategyConfDetailCopy = BeanUtil.create(mktStrategyConfDetail, new MktStrategyConfDetail());
            // 获取规则列表
            List<MktStrategyConfRule> mktStrategyConfRuleList = mktStrategyConfDetail.getMktStrategyConfRuleList();
            if (mktStrategyConfRuleList != null && mktStrategyConfRuleList.size() > 0) {
                List<MktStrategyConfRule> mktStrategyConfRuleCopyList = new ArrayList<>();
    /*            for (MktStrategyConfRule mktStrategyConfRule : mktStrategyConfRuleList) {
                    new MktStrategyConfRuleServiceImpl.CopyMktStrategyConfRuleTask(mktStrategyConfRule);
                    MktStrategyConfRule mktStrategyConfRuleCopy = (MktStrategyConfRule) mktStrategyConfRuleMap.get("mktStrategyConfRule");
                    mktStrategyConfRuleCopyList.add(mktStrategyConfRuleCopy);
                }*/

                Map<String, Object> ruleListMap = mktStrategyConfRuleService.copyMktStrategyConfRule(mktStrategyConfRuleList);
                List<MktStrategyConfRule> ruleList = (List<MktStrategyConfRule>) ruleListMap.get("mktStrategyConfRuleList");
                mktStrategyConfDetailCopy.setMktStrategyConfRuleList(ruleList);
            }
            mktStrategyConfDetailMap.put("mktStrategyConfDetail", mktStrategyConfDetailCopy);
            mktStrategyConfDetailMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfDetailMap.put("resultMsg", "复制策略成功！");
        } catch (Exception e) {
            logger.error("[op:copyMktStrategyConf] copyMktStrategyConf mktStrategyConfDetail={} 失败Exception = ", JSON.toJSONString(mktStrategyConfDetail), e);
            mktStrategyConfDetailMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfDetailMap.put("resultMsg", "复制策略失败！");
        }
        logger.info("MktStrategyConfDetail-->>>结束：" + simpleDateFormat.format(new Date()));
        return mktStrategyConfDetailMap;
    }

    @Override
    public Map<String, Object> listAllMktStrategyConf() {
        return null;
    }


    /**
     * 从模板获取策略信息(用future实现并发复制策略)
     *
     * @param preMktCampaignId
     * @return
     */
    @Override
    public Map<String, Object> getStrategyTemplate(Long preMktCampaignId) throws Exception {
        Map<String, Object> strategyTemplateMap = new HashMap<>();
        try {
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            Future<Map<String, Object>> strategyFuture = null;
            ExecutorService executorService = Executors.newCachedThreadPool();
            // 获取活动关联策略集合
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(preMktCampaignId);
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                strategyFuture = executorService.submit(new getStrategyTemplateTask(mktCamStrategyConfRelDO.getStrategyConfId()));
                threadList.add(strategyFuture);
            }
            List<MktStrategyConfDetail> mktStrategyConfDetailList = new ArrayList<>();
            for (Future<Map<String, Object>> strategyFutureNew : threadList) {
                MktStrategyConfDetail mktStrategyConfDetail = (MktStrategyConfDetail) strategyFutureNew.get().get("mktStrategyConfDetail");
                mktStrategyConfDetailList.add(mktStrategyConfDetail);
            }
            strategyTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            strategyTemplateMap.put("mktStrategyConfDetailList", mktStrategyConfDetailList);
        } catch (Exception e) {
            strategyTemplateMap.put("resultCode", CommonConstant.CODE_FAIL);
            logger.error("[op:MktStrategyConfServiceImpl] failed to get StrategyTemplate by preMktCampaignId = {} ,Exception = ", preMktCampaignId, e);
        }
        return strategyTemplateMap;
    }


    /**
     * 从模板获取策略信息
     */
    class getStrategyTemplateTask implements Callable<Map<String, Object>> {

        private Long preStrategyConfId;

        public getStrategyTemplateTask(Long preStrategyConfId) {
            this.preStrategyConfId = preStrategyConfId;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> mktStrategyConfMap = new HashMap<String, Object>();
            MktStrategyConfDetail mktStrategyConfDetail = new MktStrategyConfDetail();
            try {
                //查出获取所有的城市信息, 设成全局Map
                Map<Integer, String> cityMap = new HashMap<>();
                List<SysArea> sysAreaList = sysAreaMapper.selectAll();
                for (SysArea sysArea : sysAreaList) {
                    cityMap.put(sysArea.getAreaId(), sysArea.getName());
                }

                //更具Id查询策略配置信息
                MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(preStrategyConfId);
                CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDetail, mktStrategyConfDO);
                List<Integer> areaIdList = new ArrayList<>();
                String[] areaIds = mktStrategyConfDO.getAreaId().split("/");
                if (areaIds != null && !"".equals(areaIds[0])) {
                    for (String areaId : areaIds) {
                        areaIdList.add(Integer.valueOf(areaId));
                    }
                    mktStrategyConfDetail.setAreaIdList(areaIdList);

                }
                // 策略下发渠道
                String[] channelIds = mktStrategyConfDO.getChannelsId().split("/");
                List<Long> channelList = new ArrayList<>();
                if(channelIds!=null && !"".equals(channelIds[0])){
                    for (String channelId : channelIds) {
                        channelList.add(Long.valueOf(channelId));
                    }
                    mktStrategyConfDetail.setChannelList(channelList);
                }
                // 获取过滤规则集合
                List<Long> filterRuleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(preStrategyConfId);
                mktStrategyConfDetail.setFilterRuleIdList(filterRuleIdList);

                //查询与策略匹配的所有规则
                List<MktStrategyConfRule> mktStrategyConfRuleList = new ArrayList<>();
                List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(preStrategyConfId);
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                    MktStrategyConfRule mktStrategyConfRule = new MktStrategyConfRule();
                    CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRule, mktStrategyConfRuleDO);

                    if (mktStrategyConfRuleDO.getProductId() != null) {
                        String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                        List<Long> productIdList = new ArrayList<>();
                        for (int i = 0; i < productIds.length; i++) {
                            if (productIds[i] != "" && !"".equals(productIds[i])) {
                                productIdList.add(Long.valueOf(productIds[i]));
                            }
                        }
                        mktStrategyConfRule.setProductIdlist(productIdList);
                    }
                    if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                        String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                        for (int i = 0; i < evtContactConfIds.length; i++) {
                            if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                                MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
                                mktCamChlConfDetail.setEvtContactConfId(Long.valueOf(evtContactConfIds[i]));
                                String evtContactConfName = mktCamChlConfMapper.selectforName(Long.valueOf(evtContactConfIds[i]));
                                mktCamChlConfDetail.setEvtContactConfName(evtContactConfName);
                                mktCamChlConfDetailList.add(mktCamChlConfDetail);
                            }
                        }
                        mktStrategyConfRule.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                    }

                    if (mktStrategyConfRuleDO.getMktCamChlResultId() != null) {
                        String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                        List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
                        for (int i = 0; i < mktCamChlResultIds.length; i++) {
                            if (mktCamChlResultIds[i] != null && !"".equals(mktCamChlResultIds[i])) {
                                MktCamChlResult mktCamChlResult = new MktCamChlResult();
                                mktCamChlResultMapper.selectByPrimaryKey(Long.valueOf(mktCamChlResultIds[i]));

                                mktCamChlResult.setMktCamChlResultId(Long.valueOf(mktCamChlResultIds[i]));
                                mktCamChlResultList.add(mktCamChlResult);
                            }
                        }
                        mktStrategyConfRule.setMktCamChlResultList(mktCamChlResultList);
                    }

                    mktStrategyConfRuleList.add(mktStrategyConfRule);
                }
                mktStrategyConfDetail.setMktStrategyConfRuleList(mktStrategyConfRuleList);
                mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
                mktStrategyConfMap.put("mktStrategyConfDetail", mktStrategyConfDetail);
            } catch (Exception e) {
                logger.error("[op:MktStrategyConfServiceImpl] fail to get StrategyTemplateTask preStrategyConfId = {}, Exception:", preStrategyConfId, e);
                mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
                mktStrategyConfMap.put("mktStrategyConfDetail", mktStrategyConfDetail);
                return mktStrategyConfMap;
            }
            return mktStrategyConfMap;
        }
    }

}