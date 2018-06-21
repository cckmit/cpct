package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysParams;

import java.util.List;

public interface SysParamsMapper {
    int deleteByPrimaryKey(Long paramId);

    int insert(SysParams record);

    SysParams selectByPrimaryKey(Long paramId);

    List<SysParams> selectAll();

    int updateByPrimaryKey(SysParams record);
}