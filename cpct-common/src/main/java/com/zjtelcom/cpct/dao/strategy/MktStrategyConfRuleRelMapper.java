package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;

import java.util.List;

public interface MktStrategyConfRuleRelMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleRelId);

    int insert(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);

    MktStrategyConfRuleRelDO selectByPrimaryKey(Long mktStrategyConfRuleRelId);

    List<MktStrategyConfRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);
}