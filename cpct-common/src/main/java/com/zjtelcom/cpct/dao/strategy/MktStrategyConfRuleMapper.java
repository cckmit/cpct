package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;

import java.util.List;

public interface MktStrategyConfRuleMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleId);

    int insert(MktStrategyConfRuleDO mktStrategyConfRuleDO);

    MktStrategyConfRuleDO selectByPrimaryKey(Long mktStrategyConfRuleId);

    List<MktStrategyConfRuleDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRuleDO mktStrategyConfRuleDO);

    List<MktStrategyConfRuleDO> selectByMktStrategyConfId(Long mktStrategyConfId);


}