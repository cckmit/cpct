/**
 * @(#)SysAreaServiceImpl.java, 2018/7/9.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.City;
import com.zjtelcom.cpct.domain.campaign.CityProperty;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.enums.AreaLeveL;
import com.zjtelcom.cpct.service.system.SysAreaService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/09 14:59
 * version: V1.0
 */
@Service
public class SysAreaServiceImpl implements SysAreaService {

    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String, Object> listSysArea() {
        List<SysArea> sysAreas = sysAreaMapper.selectAll();
        Map<String, Object> map = new HashMap<>();
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("sysAreas", sysAreas);
        return map;
    }

    @Override
    public Map<String, Object> listSysCity() {
        // 获取等级为1的地市级别的城市
        List<SysArea> sysCitys = sysAreaMapper.selectByAreaLevel(AreaLeveL.CITY.getAreaLevel());
        Map<String, Object> map = new HashMap<>();
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("sysCitys", sysCitys);
        return map;
    }

    /**
     * 通过父节点获取子节点地区
     *
     * @param parentCityId
     * @return
     */
    @Override
    public Map<String, Object> listCityByParentId(Integer parentCityId) {
        Map<String, Object> map = new HashMap<>();
        List<SysArea> sysAreaList = sysAreaMapper.selectByParnetArea(parentCityId);
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("sysAreaList", sysAreaList);
        return map;
    }

    @Override
    public Map<String, Object> listAllAreaTrea() {
        Map<String, Object> areaMap = new HashMap<>();
        List<SysArea> sysAreaList = new ArrayList<>();
        //获取省级
        List<SysArea> provinceAreas = sysAreaMapper.selectByAreaLevel(AreaLeveL.PROVINCE.getAreaLevel());
        for (SysArea provinceArea : provinceAreas) {
            SysArea sysArea = (SysArea) redisUtils.get("CITY_" + provinceArea.getAreaId().toString());
            if (sysArea == null) {
                // 将城市数据存入到redis
                saveCityTORedis();
                // 重新从redis中获取
                sysArea = (SysArea) redisUtils.get("CITY_" + provinceArea.getAreaId().toString());
            }
            sysAreaList.add(ChannelUtil.setOrgArea(sysArea));
        }
        areaMap.put("sysAreaList", sysAreaList);
        return areaMap;
    }





    @Override
    public Map<String, Object> getCityTable(List<Integer> areaIds) {
        Map<String, Object> map = new HashMap<>();
        Map<Integer, List<City>> cityMap = new HashMap<>();
        List<City> cityList = new ArrayList<>();

        List<SysArea> sysAreaListAll = new ArrayList<>();
        // 遍历所有勾选的下发地市Id
        for (Integer aresId : areaIds) {
            // 获取下发地市的信息
            SysArea sysArea = (SysArea) redisUtils.get("CITY_" + aresId.toString());
            if (!isContains(sysAreaListAll, sysArea)) {
                sysAreaListAll.add(sysArea);
            }
            getChildArea(sysArea, sysAreaListAll);
            getParentArea(sysArea.getParentArea(), sysAreaListAll);
        }


        for (SysArea sysArea : sysAreaListAll) {
            if (sysArea.getAreaLevel().equals(AreaLeveL.CITY.getAreaLevel())) {
                City city = new City();
                //遍历获取地市级别城市
                CityProperty cityProperty = new CityProperty();
                cityProperty.setCityPropertyId(Long.valueOf(sysArea.getAreaId()));
                cityProperty.setCityPropertyName(sysArea.getName());
                city.setApplyCity(cityProperty);

                List<CityProperty> applyCountys = new ArrayList<>();
                List<CityProperty> applyGriddings = new ArrayList<>();
                List<CityProperty> applyBranchs = new ArrayList<>();
                //遍历获取该地市下区县级别城市
                for (SysArea sysCountys : sysAreaListAll) {
                    if (sysCountys.getAreaLevel().equals(AreaLeveL.COUNTYS.getAreaLevel()) && sysCountys.getParentArea().equals(sysArea.getAreaId())) {
                        CityProperty countysProperty = new CityProperty();
                        countysProperty.setCityPropertyId(Long.valueOf(sysCountys.getAreaId()));
                        countysProperty.setCityPropertyName(sysCountys.getName());
                        applyCountys.add(countysProperty);
                        // 遍历获取该区县下的支局
                        for (SysArea sysBranchs : sysAreaListAll) {
                            if (sysBranchs.getAreaLevel().equals(AreaLeveL.BRANCHS.getAreaLevel()) && sysBranchs.getParentArea().equals(sysCountys.getAreaId())) {
                                CityProperty branchsProperty = new CityProperty();
                                branchsProperty.setCityPropertyId(Long.valueOf(sysBranchs.getAreaId()));
                                branchsProperty.setCityPropertyName(sysBranchs.getName());
                                applyBranchs.add(branchsProperty);
                                // 遍历获取该支局下的网格

                                for (SysArea sysGriddings : sysAreaListAll) {
                                    if (sysGriddings.getAreaLevel().equals(AreaLeveL.GRIDDINGS.getAreaLevel()) && sysGriddings.getParentArea().equals(sysBranchs.getAreaId())) {
                                        CityProperty griddingsProperty = new CityProperty();
                                        griddingsProperty.setCityPropertyId(Long.valueOf(sysGriddings.getAreaId()));
                                        griddingsProperty.setCityPropertyName(sysGriddings.getName());
                                        applyGriddings.add(griddingsProperty);
                                    }
                                }
                            }
                        }
                    }
                }
                city.setApplyGriddings(applyGriddings);
                city.setApplyBranchs(applyBranchs);
                city.setApplyCountys(applyCountys);
                cityList.add(city);
            }

        }
        map.put("cityList", cityList);
        return map;
    }

    // 递归获取所有子节点城市
    private List<SysArea> getChildArea(SysArea sysArea, List<SysArea> sysAreaListAll) {
        if (sysArea.getAreaLevel().equals(AreaLeveL.GRIDDINGS.getAreaLevel()) || sysArea.getChildAreaList() == null) {
            if (!isContains(sysAreaListAll, sysArea)) {
                sysAreaListAll.add(sysArea);
            }
            return sysAreaListAll;
        } else {
            //遍历所有子集，并将结果全部放入sysAreaList
            for (SysArea sysChildArea : sysArea.getChildAreaList()) {
                if (!isContains(sysAreaListAll, sysChildArea)) {
                    sysAreaListAll.add(sysChildArea);
                }
                getChildArea(sysChildArea, sysAreaListAll);
            }
            return sysAreaListAll;
        }
    }

    // 递归获取所有父节点城市
    private List<SysArea> getParentArea(Integer parentArea, List<SysArea> sysAreaListAll) {
        SysArea sysArea = (SysArea) redisUtils.get("CITY_" + parentArea.toString());
        if (sysArea != null) {
            if (sysArea.getAreaLevel().equals(AreaLeveL.PROVINCE.getAreaLevel())) {
                return sysAreaListAll;
            } else {
                if (!isContains(sysAreaListAll, sysArea)) {
                    sysAreaListAll.add(sysArea);
                }
                return getParentArea(sysArea.getParentArea(), sysAreaListAll);
            }
        }
        return null;
    }


    /**
     * 初始化城市数据到redis
     *
     * @return
     */
    @Override
    public Map<String, Object> saveCityTORedis() {
        Map<String, Object> map = new HashMap<>();
        try {
            List<SysArea> sysAreaList = new ArrayList<>();
            List<SysArea> sysAreas = sysAreaMapper.selectAll();
            for (SysArea sysArea : sysAreas) {
                getByParentArea(sysArea.getAreaId(), sysArea);
                // 存放单个下发地市信息
                redisUtils.set("CITY_" + sysArea.getAreaId().toString(), sysArea);
            }
            map.put("resultCode", CommonConstant.CODE_SUCCESS);
            map.put("resultMsg", "success");
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", "failure");
        }
        return map;
    }


    private SysArea getByParentArea(Integer parentArea, SysArea sysParentArea) {
        List<SysArea> sysAreas = sysAreaMapper.selectByParnetArea(parentArea);
        if (sysAreas != null && sysAreas.size() > 0) {
            sysParentArea.setChildAreaList(sysAreas);
            ChannelUtil.setOrgArea(sysParentArea);
            for (SysArea sysArea : sysAreas) {
                ChannelUtil.setOrgArea(sysArea);
                getByParentArea(sysArea.getAreaId(), sysArea);
            }
        }
        return sysParentArea;
    }

    /**
     * 判断集合中是否包含
     *
     * @param sysAreaListAll
     * @param sysArea
     * @return
     */
    private boolean isContains(List<SysArea> sysAreaListAll, SysArea sysArea) {
        boolean isContains = false;
        if (sysAreaListAll == null || sysAreaListAll.size() == 0 || sysArea == null) {
            return false;
        }
        for (int i = 0; i < sysAreaListAll.size(); i++) {
            if (sysAreaListAll.get(i).getAreaId().equals(sysArea.getAreaId())) {
                return true;
            }
        }
        return isContains;
    }
}