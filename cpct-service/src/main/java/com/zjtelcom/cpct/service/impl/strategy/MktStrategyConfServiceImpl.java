/**
 * @(#)MktStrategyConfServiceImpl.java, 2018/6/25.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.strategy;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRegionRelMapper;
import com.zjtelcom.cpct.domain.campaign.City;
import com.zjtelcom.cpct.domain.campaign.CityProperty;
import com.zjtelcom.cpct.domain.campaign.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.campaign.MktStrategyConfRegionRelDO;
import com.zjtelcom.cpct.dto.campaign.MktStrategyConfDetail;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/06/25 17:23
 * version: V1.0
 */
@Transactional
@Service
public class MktStrategyConfServiceImpl extends BaseService implements MktStrategyConfService {

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Autowired
    private MktStrategyConfRegionRelMapper mktStrategyConfRegionRelMapper;

    @Override
    public Map<String, Object> deleteMktStrategyConf(Long mktStrategyConfId) {
        return null;
    }

    /**
     * 添加策略配置信息
     *
     * @param mktStrategyConfDetail
     * @return
     */
    @Override
    public Map<String, Object> saveMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) {

        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        try {
            //添加属性配置信息
            MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDO, mktStrategyConfDetail);
            Long mktStrategyConfId = mktStrategyConfMapper.insert(mktStrategyConfDO);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfId", mktStrategyConfId);

            //添加属性配置信息 与 下发城市关联
            List<City> cityList = mktStrategyConfDetail.getCityList();
            for (City city : cityList) {
                MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO = new MktStrategyConfRegionRelDO();
                mktStrategyConfRegionRelDO.setMktStrategyConfId(mktStrategyConfId);
                mktStrategyConfRegionRelDO.setApplyCityId(city.getApplyCity().getCityPropertyId());
                String applyCountyIds = "";
                String applyBranchIds = "";
                String applyGriddingIds = "";
                for (int i = 0; i < city.getApplyCountys().size(); i++) {
                    if (i == 0) {
                        applyCountyIds += city.getApplyCountys().get(i).getCityPropertyId();
                    } else {
                        applyCountyIds = "/" + city.getApplyCountys().get(i).getCityPropertyId();
                    }
                }
                for (int i = 0; i < city.getApplyBranchs().size(); i++) {
                    if (i == 0) {
                        applyBranchIds += city.getApplyBranchs().get(i).getCityPropertyId();
                    } else {
                        applyBranchIds = "/" + city.getApplyBranchs().get(i).getCityPropertyId();
                    }
                }
                for (int i = 0; i < city.getApplyGriddings().size(); i++) {
                    if (i == 0) {
                        applyGriddingIds += city.getApplyGriddings().get(i).getCityPropertyId();
                    } else {
                        applyGriddingIds = "/" + city.getApplyGriddings().get(i).getCityPropertyId();
                    }
                }
                mktStrategyConfRegionRelDO.setApplyCounty(applyCountyIds);
                mktStrategyConfRegionRelDO.setApplyBranch(applyBranchIds);
                mktStrategyConfRegionRelDO.setApplyGridding(applyGriddingIds);
                mktStrategyConfRegionRelMapper.insert(mktStrategyConfRegionRelDO);
            }
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfServiceImpl] fail to save MktStrategyConfDetail = {}, Exception: ", JSON.toJSON(mktStrategyConfDetail), e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_FAILURE.getErrorMsg());
        }
        return mktStrategyConfMap;
    }

    @Override
    public Map<String, Object> updateMktStrategyConf(MktStrategyConfDetail mktStrategyConfDetail) {
        Map<String, Object> mktStrategyConfMap = new HashMap<String, Object>();
        try {
            //修改属性配置信息
            MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDO, mktStrategyConfDetail);
            Long mktStrategyConfId = mktStrategyConfMapper.updateByPrimaryKey(mktStrategyConfDO);

            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", ErrorCode.UPDATE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
            mktStrategyConfMap.put("mktStrategyConfId", mktStrategyConfId);

            //修改属性配置信息 与 下发城市关联
            List<City> cityList = mktStrategyConfDetail.getCityList();
            for (City city : cityList) {
                MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO = new MktStrategyConfRegionRelDO();
                mktStrategyConfRegionRelDO.setMktStrategyConfId(mktStrategyConfId);
                mktStrategyConfRegionRelDO.setApplyCityId(city.getApplyCity().getCityPropertyId());
                String applyCountyIds = "";
                String applyBranchIds = "";
                String applyGriddingIds = "";
                for (int i = 0; i < city.getApplyCountys().size(); i++) {
                    if (i == 0) {
                        applyCountyIds += city.getApplyCountys().get(i).getCityPropertyId();
                    } else {
                        applyCountyIds = "/" + city.getApplyCountys().get(i).getCityPropertyId();
                    }
                }
                for (int i = 0; i < city.getApplyBranchs().size(); i++) {
                    if (i == 0) {
                        applyBranchIds += city.getApplyBranchs().get(i).getCityPropertyId();
                    } else {
                        applyBranchIds = "/" + city.getApplyBranchs().get(i).getCityPropertyId();
                    }
                }
                for (int i = 0; i < city.getApplyGriddings().size(); i++) {
                    if (i == 0) {
                        applyGriddingIds += city.getApplyGriddings().get(i).getCityPropertyId();
                    } else {
                        applyGriddingIds = "/" + city.getApplyGriddings().get(i).getCityPropertyId();
                    }
                }
                mktStrategyConfRegionRelDO.setApplyCounty(applyCountyIds);
                mktStrategyConfRegionRelDO.setApplyBranch(applyBranchIds);
                mktStrategyConfRegionRelDO.setApplyGridding(applyGriddingIds);
                mktStrategyConfRegionRelMapper.updateByPrimaryKey(mktStrategyConfRegionRelDO);
            }
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
    public Map<String, Object> getMktStrategyConf(Long mktStrategyConfId) {
        Map<String, Object> mktStrategyConfMap = new HashMap<String, Object>();
        MktStrategyConfDetail mktStrategyConfDetail = new MktStrategyConfDetail();
        try {
            //TODO 查出获取所有的城市信息
            Map<Long, String> cityMap = new HashMap<>();


            //更具Id查询策略配置信息
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktStrategyConfId);
            CopyPropertiesUtil.copyBean2Bean(mktStrategyConfDetail, mktStrategyConfDO);

            List<MktStrategyConfRegionRelDO> mktStrategyConfRegionRelDOList = mktStrategyConfRegionRelMapper.selectByMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
            List<City> cityList = new ArrayList<>();
            for (MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO : mktStrategyConfRegionRelDOList) {
                City city = new City();
                // 获取城市
                CityProperty cityProperty = new CityProperty();
                cityProperty.setCityPropertyId(mktStrategyConfRegionRelDO.getApplyCityId());
                cityProperty.setCityPropertyName(cityMap.get(mktStrategyConfRegionRelDO.getApplyCityId()));
                city.setApplyCity(cityProperty);

                // 获取区县
                String applyCountys[] = mktStrategyConfRegionRelDO.getApplyCounty().split("/");
                for (int i = 0; i < applyCountys.length; i++) {
                    CityProperty countyProperty = new CityProperty();
                    countyProperty.setCityPropertyId(Long.valueOf(applyCountys[i]));
                    countyProperty.setCityPropertyName(cityMap.get(Long.valueOf(applyCountys[i])));
                    city.getApplyCountys().add(countyProperty);
                }

                // 获取支局
                String applyBranchs[] = mktStrategyConfRegionRelDO.getApplyBranch().split("/");
                for (int i = 0; i < applyBranchs.length; i++) {
                    CityProperty branchProperty = new CityProperty();
                    branchProperty.setCityPropertyId(Long.valueOf(applyBranchs[i]));
                    branchProperty.setCityPropertyName(cityMap.get(Long.valueOf(applyBranchs[i])));
                    city.getApplyCountys().add(branchProperty);
                }

                // 获取网格
                String applyGridding[] = mktStrategyConfRegionRelDO.getApplyGridding().split("/");
                for (int i = 0; i < applyGridding.length; i++) {
                    CityProperty griddingProperty = new CityProperty();
                    griddingProperty.setCityPropertyId(Long.valueOf(applyGridding[i]));
                    griddingProperty.setCityPropertyName(cityMap.get(Long.valueOf(applyGridding[i])));
                    city.getApplyCountys().add(griddingProperty);
                }
                mktStrategyConfDetail.getCityList().add(city);
            }
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