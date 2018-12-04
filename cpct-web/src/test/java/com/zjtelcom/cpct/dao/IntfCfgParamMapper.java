package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.IntfCfgParam;

import java.util.List;

public interface IntfCfgParamMapper {
    int deleteByPrimaryKey(Long intfCfgParamId);

    int insert(IntfCfgParam record);

    IntfCfgParam selectByPrimaryKey(Long intfCfgParamId);

    List<IntfCfgParam> selectAll();

    int updateByPrimaryKey(IntfCfgParam record);
}