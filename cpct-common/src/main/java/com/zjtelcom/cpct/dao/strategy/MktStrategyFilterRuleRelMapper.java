package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;

import java.util.List;

public interface MktStrategyFilterRuleRelMapper {
    int deleteByPrimaryKey(Long mktStrategyFilterRuleRelId);

    int deleteByStrategyId(Long strategyId);

    int insert(MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO);

    MktStrategyFilterRuleRelDO selectByPrimaryKey(Long mktStrategyFilterRuleRelId);

    List<Long> selectByStrategyId(Long strategyId);

    List<MktStrategyFilterRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO);
}