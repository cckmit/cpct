package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;

import java.util.List;

/**
 * 策略配置规则Mapper
 */
public interface MktStrategyConfRuleMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleId);

    int insert(MktStrategyConfRuleDO mktStrategyConfRuleDO);

    void insertByBatch(List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList);

    MktStrategyConfRuleDO selectByPrimaryKey(Long mktStrategyConfRuleId);

    List<MktStrategyConfRuleDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRuleDO mktStrategyConfRuleDO);

    List<MktStrategyConfRuleDO> selectByMktStrategyConfId(Long mktStrategyConfId);


}