package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysParamsMapper {
    int deleteByPrimaryKey(Long paramId);

    int insert(SysParams record);

    SysParams selectByPrimaryKey(Long paramId);

    List<SysParams> selectAll(@Param("paramName") String paramName, @Param("configType") Long configType);

    int updateByPrimaryKey(SysParams record);

    List<SysParams> listParamsByKey(String key);

}