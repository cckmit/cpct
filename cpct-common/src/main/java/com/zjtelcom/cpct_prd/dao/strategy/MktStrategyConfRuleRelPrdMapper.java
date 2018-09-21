package com.zjtelcom.cpct_prd.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;

import java.util.List;

public interface MktStrategyConfRuleRelPrdMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleRelId);

    int deleteByMktStrategyConfId(Long mktStrategyConfId);

    int deleteByMktStrategyConfRulId(Long mktStrategyConfRulId);

    int insert(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);

    MktStrategyConfRuleRelDO selectByPrimaryKey(Long mktStrategyConfRuleRelId);

    List<MktStrategyConfRuleRelDO> selectByMktStrategyConfId(Long mktStrategyConfId);

    List<MktStrategyConfRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);
}