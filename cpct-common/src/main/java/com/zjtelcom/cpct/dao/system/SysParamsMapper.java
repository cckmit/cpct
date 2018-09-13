package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Mapper
@Repository
public interface SysParamsMapper {
    int deleteByPrimaryKey(Long paramId);

    int insert(SysParams record);

    List<SysParams> selectByPrimaryKey(Long paramId);

    List<SysParams> selectAll(@Param("paramName") String paramName, @Param("configType") Long configType);

    int updateByPrimaryKey(SysParams record);

    List<Map<String,String>> listParamsByKey(String key);

    Map<String,String> getParamsByKey(@Param("keyWord") String keyWord,@Param("key") String key);

    Map<String,String> getParamsByValue(@Param("keyWord") String keyWord,@Param("value") String value);

    SysParams findParamsByValue(@Param("keyWord") String keyWord,@Param("value") String value);

    List<SysParams> listParamsByKeyForCampaign(String key);

}