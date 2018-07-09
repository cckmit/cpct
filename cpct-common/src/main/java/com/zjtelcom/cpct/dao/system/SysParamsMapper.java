package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysParamsMapper {
    int deleteByPrimaryKey(Long paramId);

    int insert(SysParams record);

    SysParams selectByPrimaryKey(Long paramId);

    List<SysParams> selectAll(@Param("paramName") String paramName, @Param("configType") Long configType);

    int updateByPrimaryKey(SysParams record);

    Map<String,String> listParamsByKey(String key);

    Map<String,String> getParamsByKey(@Param("keyWord") String keyWord,@Param("key") String key);

    Map<String,String> getParamsByValue(@Param("keyWord") String keyWord,@Param("value") String value);



}