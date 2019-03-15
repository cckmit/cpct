package com.zjtelcom.cpct_prd.dao.strategy;


import com.zjtelcom.cpct.dto.strategy.MktStrategy;

import java.util.List;

public interface MktStrategyPrdMapper {
    int deleteByPrimaryKey(Long strategyId);

    int insert(MktStrategy mktStrategy);

    MktStrategy selectByPrimaryKey(Long strategyId);

    List<MktStrategy> selectAll();

    int updateByPrimaryKey(MktStrategy mktStrategy);

    List<MktStrategy> queryList(MktStrategy mktStrategy);
}