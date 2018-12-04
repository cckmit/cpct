package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktStrategy;

import java.util.List;

public interface MktStrategyMapper {
    int deleteByPrimaryKey(Long strategyId);

    int insert(MktStrategy record);

    MktStrategy selectByPrimaryKey(Long strategyId);

    List<MktStrategy> selectAll();

    int updateByPrimaryKey(MktStrategy record);
}