package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.SysArea;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/09 14:58
 * version: V1.0
 */
public interface SysAreaService {

    Map<String, Object> listSysArea();

    Map<String, Object> listSysCity();

    Map<String, Object> listAllAreaTrea();

    Map<String, Object> listStrAreaTree(String lanId);

    Map<String, Object> getCityTable(List<Integer> areaIds);

    Map<String, Object> saveCityTORedis();

    Map<String, Object> listCityByParentId(Integer parentCityId);

    Map<String, Object> listCityAndParentByParentId(Integer parentCityId);

    String getAreaString(List<SysArea> sysAreaList);

}