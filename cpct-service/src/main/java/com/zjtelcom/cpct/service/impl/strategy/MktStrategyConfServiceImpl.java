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
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRegionRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.City;
import com.zjtelcom.cpct.domain.campaign.CityProperty;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRegionRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConf;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.service.thread.TarGrpRule;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.io.CopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // RedisUtils工具类
    @Autowired
    private RedisUtils redisUtils;


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
        mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
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
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfId", mktStrategyConfId);
/*
            //添加属性配置信息 与 下发城市关联
            List<City> cityList = mktStrategyConfDetail.getCityList();
            if (cityList != null && cityList.size() > 0) {
                for (City city : cityList) {
                    MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO = new MktStrategyConfRegionRelDO();
                    mktStrategyConfRegionRelDO.setMktStrategyConfId(mktStrategyConfId);
                    mktStrategyConfRegionRelDO.setApplyCityId(city.getApplyCity().getCityPropertyId());
                    String applyCountyIds = "";
                    String applyBranchIds = "";
                    String applyGriddingIds = "";
                    // 区县
                    for (int i = 0; i < city.getApplyCountys().size(); i++) {
                        if (i == 0) {
                            applyCountyIds += city.getApplyCountys().get(i).getCityPropertyId();
                        } else {
                            applyCountyIds += "/" + city.getApplyCountys().get(i).getCityPropertyId();
                        }
                    }
                    // 支局
                    for (int i = 0; i < city.getApplyBranchs().size(); i++) {
                        if (i == 0) {
                            applyBranchIds += city.getApplyBranchs().get(i).getCityPropertyId();
                        } else {
                            applyBranchIds += "/" + city.getApplyBranchs().get(i).getCityPropertyId();
                        }
                    }
                    // 网格
                    for (int i = 0; i < city.getApplyGriddings().size(); i++) {
                        if (i == 0) {
                            applyGriddingIds += city.getApplyGriddings().get(i).getCityPropertyId();
                        } else {
                            applyGriddingIds += "/" + city.getApplyGriddings().get(i).getCityPropertyId();
                        }
                    }
                    mktStrategyConfRegionRelDO.setApplyCounty(applyCountyIds);
                    mktStrategyConfRegionRelDO.setApplyBranch(applyBranchIds);
                    mktStrategyConfRegionRelDO.setApplyGridding(applyGriddingIds);
                    mktStrategyConfRegionRelDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyConfRegionRelDO.setCreateDate(new Date());
                    mktStrategyConfRegionRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyConfRegionRelDO.setUpdateDate(new Date());
                    mktStrategyConfRegionRelMapper.insert(mktStrategyConfRegionRelDO);
                }
            }
*/

            ExecutorService executorService = Executors.newCachedThreadPool();
            // 遍历策略下对应的规则
            try {
                for (MktStrategyConfRule mktStrategyConfRule : mktStrategyConfDetail.getMktStrategyConfRuleList()) {
                    MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
                    String productIds = "";
                    String evtContactConfIds = "";
                    if (mktStrategyConfRule.getProductIdlist() != null) {
                        for (int i = 0; i < mktStrategyConfRule.getProductIdlist().size(); i++) {
                            if (i == 0) {
                                productIds += mktStrategyConfRule.getProductIdlist().get(i);
                            } else {
                                productIds += "/" + mktStrategyConfRule.getProductIdlist().get(i);
                            }
                        }
                    }
                    if (mktStrategyConfRule.getMktCamChlConfList() != null) {
                        for (int i = 0; i < mktStrategyConfRule.getMktCamChlConfList().size(); i++) {
                            if (i == 0) {
                                evtContactConfIds += mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();
                            } else {
                                evtContactConfIds += "/" + mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();
                            }
                        }
                    }
                    CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleDO, mktStrategyConfRule);
                    // 添加规则的信息 并返回id -- mktStrategyConfRuleId
                    mktStrategyConfRuleDO.setProductId(productIds);
                    mktStrategyConfRuleDO.setEvtContactConfId(evtContactConfIds);
                    mktStrategyConfRuleDO.setCreateDate(new Date());
                    mktStrategyConfRuleDO.setCreateStaff(UserUtil.loginId());
                    mktStrategyConfRuleDO.setUpdateDate(new Date());
                    mktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyConfRuleMapper.insert(mktStrategyConfRuleDO);
                    // 策略规则 Id
                    Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();
                    // 建立策略配置和规则的关系
                    MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                    mktStrategyConfRuleRelDO.setMktStrategyConfId(mktStrategyConfId);
                    mktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleId);
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
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_FAILURE.getErrorMsg());

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

            // 更新策略配置基本，并返回策略Id -- mktStrategyConfId0
            mktStrategyConfMapper.updateByPrimaryKey(mktStrategyConfDO);
            // 策略Id
            Long mktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfId", mktStrategyConfId);
/*

            // 修改属性配置信息 与 下发城市关联
            List<City> cityList = mktStrategyConfDetail.getCityList();
            if (cityList != null) {
                for (City city : cityList) {
                    MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO = new MktStrategyConfRegionRelDO();
                    mktStrategyConfRegionRelDO.setMktStrategyConfId(mktStrategyConfId);
                    mktStrategyConfRegionRelDO.setApplyCityId(city.getApplyCity().getCityPropertyId());
                    String applyCountyIds = "";
                    String applyBranchIds = "";
                    String applyGriddingIds = "";
                    // 区县
                    if (city.getApplyCountys() != null) {
                        for (int i = 0; i < city.getApplyCountys().size(); i++) {
                            if (i == 0) {
                                applyCountyIds += city.getApplyCountys().get(i).getCityPropertyId();
                            } else {
                                applyCountyIds += "/" + city.getApplyCountys().get(i).getCityPropertyId();
                            }
                        }
                    }
                    // 支局
                    if (city.getApplyBranchs() != null) {
                        for (int i = 0; i < city.getApplyBranchs().size(); i++) {
                            if (i == 0) {
                                applyBranchIds += city.getApplyBranchs().get(i).getCityPropertyId();
                            } else {
                                applyBranchIds += "/" + city.getApplyBranchs().get(i).getCityPropertyId();
                            }
                        }
                    }

                    // 网格
                    if (city.getApplyGriddings() != null) {
                        for (int i = 0; i < city.getApplyGriddings().size(); i++) {
                            if (i == 0) {
                                applyGriddingIds += city.getApplyGriddings().get(i).getCityPropertyId();
                            } else {
                                applyGriddingIds += "/" + city.getApplyGriddings().get(i).getCityPropertyId();
                            }
                        }
                    }

                    mktStrategyConfRegionRelDO.setApplyCounty(applyCountyIds);
                    mktStrategyConfRegionRelDO.setApplyBranch(applyBranchIds);
                    mktStrategyConfRegionRelDO.setApplyGridding(applyGriddingIds);
                    mktStrategyConfRegionRelDO.setUpdateStaff(UserUtil.loginId());
                    mktStrategyConfRegionRelDO.setUpdateDate(new Date());
                    mktStrategyConfRegionRelMapper.updateByPrimaryKey(mktStrategyConfRegionRelDO);
                }
            }
*/


            // 遍历策略下的所有规则
            if (mktStrategyConfDetail.getMktStrategyConfRuleList() != null) {
                ExecutorService executorService = Executors.newCachedThreadPool();
                try {
                    for (MktStrategyConfRule mktStrategyConfRule : mktStrategyConfDetail.getMktStrategyConfRuleList()) {
                        MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
                        String productIds = "";
                        String evtContactConfIds = "";
                        if (mktStrategyConfRule.getProductIdlist() != null) {
                            for (int i = 0; i < mktStrategyConfRule.getProductIdlist().size(); i++) {
                                if (i == 0) {
                                    productIds += mktStrategyConfRule.getProductIdlist().get(i);
                                } else {
                                    productIds += "/" + mktStrategyConfRule.getProductIdlist().get(i);
                                }
                            }
                        }
                        if (mktStrategyConfRule.getMktCamChlConfList() != null) {
                            for (int i = 0; i < mktStrategyConfRule.getMktCamChlConfList().size(); i++) {
                                if (i == 0) {
                                    evtContactConfIds += mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();
                                } else {
                                    evtContactConfIds += "/" + mktStrategyConfRule.getMktCamChlConfList().get(i).getEvtContactConfId();
                                }
                            }
                        }
                        CopyPropertiesUtil.copyBean2Bean(mktStrategyConfRuleDO, mktStrategyConfRule);
                        // 添加规则的信息 并返回id -- mktStrategyConfRuleId
                        mktStrategyConfRuleDO.setProductId(productIds);
                        mktStrategyConfRuleDO.setEvtContactConfId(evtContactConfIds);
                        mktStrategyConfRuleDO.setCreateDate(new Date());
                        mktStrategyConfRuleDO.setCreateStaff(UserUtil.loginId());
                        mktStrategyConfRuleDO.setUpdateDate(new Date());
                        mktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());                        //判断规则是否是修改还是新增
                        if (mktStrategyConfRule.getMktStrategyConfRuleId() != null && mktStrategyConfRule.getMktStrategyConfRuleId() != 0) {
                            // 修改规则的信息 并返回id -- mktStrategyConfRuleId
                            mktStrategyConfRuleMapper.updateByPrimaryKey(mktStrategyConfRuleDO);
                        } else {
                            // 添加新增的规则
                            mktStrategyConfRuleMapper.insert(mktStrategyConfRuleDO);
                            // 策略规则 Id
                            Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();
                            // 建立策略配置和规则的关系
                            MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                            mktStrategyConfRuleRelDO.setCreateDate(new Date());
                            mktStrategyConfRuleRelDO.setCreateStaff(UserUtil.loginId());
                            mktStrategyConfRuleRelDO.setMktStrategyConfId(mktStrategyConfId);
                            mktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleId);
                            mktStrategyConfRuleRelDO.setUpdateStaff(UserUtil.loginId());
                            mktStrategyConfRuleRelDO.setUpdateDate(new Date());
                            mktStrategyConfRuleRelMapper.insert(mktStrategyConfRuleRelDO);
                        }
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
            // 删除被删掉的规则以及与策略的关联
/*
            for (Long mktStrategyConfRuleId : mktStrategyConfDetail.getDelRuleIds()) {
                //删掉规则与策略的关联
                mktStrategyConfRuleRelMapper.deleteByMktStrategyConfId(mktStrategyConfRuleId);
                //删掉规则规则信息
                mktStrategyConfRuleMapper.deleteByPrimaryKey(mktStrategyConfRuleId);
            }
*/

        } catch (Exception e) {
            logger.error("[op:MktStrategyConfServiceImpl] fail to update MktStrategyConfDetail = {}, Exception: ", JSON.toJSON(mktStrategyConfDetail), e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", ErrorCode.UPDATE_MKT_CAMPAIGN_FAILURE.getErrorMsg());
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
            for (String areaId : areaIds) {
                areaIdList.add(Integer.valueOf(areaId));
            }
            mktStrategyConfDetail.setAreaIdList(areaIdList);

/*
            List<MktStrategyConfRegionRelDO> mktStrategyConfRegionRelDOList = mktStrategyConfRegionRelMapper.selectByMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
            List<City> cityList = new ArrayList<>();
            for (MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO : mktStrategyConfRegionRelDOList) {
                City city = new City();
                // 获取城市
                CityProperty cityProperty = new CityProperty();
                cityProperty.setCityPropertyId(mktStrategyConfRegionRelDO.getApplyCityId());
                cityProperty.setCityPropertyName(cityMap.get(mktStrategyConfRegionRelDO.getApplyCityId()));
                city.setMktStrategyConfRegionRelId(mktStrategyConfRegionRelDO.getMktStrategyConfRegionRelId());
                city.setApplyCity(cityProperty);

                // 获取区县
                String applyCountys[] = mktStrategyConfRegionRelDO.getApplyCounty().split("/");
                List<CityProperty> applyCountyList = new ArrayList<>();
                for (int i = 0; i < applyCountys.length; i++) {
                    CityProperty countyProperty = new CityProperty();
                    countyProperty.setCityPropertyId(Long.valueOf(applyCountys[i]));
                    countyProperty.setCityPropertyName(cityMap.get(Integer.valueOf(applyCountys[i])));
                    applyCountyList.add(countyProperty);
                    city.setApplyCountys(applyCountyList);
                }

                // 获取支局
                String applyBranchs[] = mktStrategyConfRegionRelDO.getApplyBranch().split("/");
                List<CityProperty> applyBranchList = new ArrayList<>();
                for (int i = 0; i < applyBranchs.length; i++) {
                    CityProperty branchProperty = new CityProperty();
                    branchProperty.setCityPropertyId(Long.valueOf(applyBranchs[i]));
                    branchProperty.setCityPropertyName(cityMap.get(Integer.valueOf(applyBranchs[i])));
                    applyBranchList.add(branchProperty);
                    city.setApplyBranchs(applyBranchList);
                }

                // 获取网格
                String applyGridding[] = mktStrategyConfRegionRelDO.getApplyGridding().split("/");
                List<CityProperty> applyGriddingList = new ArrayList<>();
                for (int i = 0; i < applyGridding.length; i++) {
                    CityProperty griddingProperty = new CityProperty();
                    griddingProperty.setCityPropertyId(Long.valueOf(applyGridding[i]));
                    griddingProperty.setCityPropertyName(cityMap.get(Long.valueOf(applyGridding[i])));
                    applyGriddingList.add(griddingProperty);
                    city.setApplyGriddings(applyGriddingList);
                }
                cityList.add(city);
            }
            mktStrategyConfDetail.setCityList(cityList);
*/

            // 策略下发渠道
            String[] channelIds = mktStrategyConfDO.getChannelsId().split("/");
            List<Long> channelList = new ArrayList<>();
            for (String channelId : channelIds) {
                channelList.add(Long.valueOf(channelId));
            }
            mktStrategyConfDetail.setChannelList(channelList);

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
                    List<MktCamChlConf> mktCamChlConfList = new ArrayList<>();
                    for (int i = 0; i < evtContactConfIds.length; i++) {
                        if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                            MktCamChlConf mktCamChlConf = new MktCamChlConf();
                            mktCamChlConf.setEvtContactConfId(Long.valueOf(evtContactConfIds[i]));
                            String evtContactConfName = mktCamChlConfMapper.selectforName(Long.valueOf(evtContactConfIds[i]));
                            mktCamChlConf.setEvtContactConfName(evtContactConfName);
                            mktCamChlConfList.add(mktCamChlConf);
                        }
                    }
                    mktStrategyConfRule.setMktCamChlConfList(mktCamChlConfList);
                }

                mktStrategyConfRuleList.add(mktStrategyConfRule);
            }
            mktStrategyConfDetail.setMktStrategyConfRuleList(mktStrategyConfRuleList);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.GET_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfDetail", mktStrategyConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfServiceImpl] fail to get MktStrategyConfDetail = {}, Exception:", JSON.toJSON(mktStrategyConfDetail), e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.GET_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfDetail", mktStrategyConfDetail);
            return mktStrategyConfMap;
        }
        return mktStrategyConfMap;
    }

    @Override
    public Map<String, Object> listAllMktStrategyConf() {
        return null;
    }

}