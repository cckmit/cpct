package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;

import java.util.List;

public interface  MktStrategyConfRuleRelMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleRelId);

    int deleteByMktStrategyConfId(Long mktStrategyConfId);

    int deleteByMktStrategyConfRulId(Long mktStrategyConfRulId);

    int insert(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);

    MktStrategyConfRuleRelDO selectByPrimaryKey(Long mktStrategyConfRuleRelId);

    List<MktStrategyConfRuleRelDO> selectByMktStrategyConfId(Long mktStrategyConfId);

    MktStrategyConfRuleRelDO selectByRuleId(Long ruleId);

    List<MktStrategyConfRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);
}