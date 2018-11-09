package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktStrategyFilterRuleRelMapper {
    int deleteByPrimaryKey(Long mktStrategyFilterRuleRelId);

    int deleteByStrategyId(Long strategyId);

    int insert(MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO);

    MktStrategyFilterRuleRelDO selectByPrimaryKey(Long mktStrategyFilterRuleRelId);

    List<Long> selectByStrategyId(Long strategyId);

    List<MktStrategyFilterRuleRelDO> selectRuleByStrategyId(Long strategyId);

    List<MktStrategyFilterRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO);

    List<MktStrategyFilterRuleRelDO> selectByRuleId(Long ruleId);
}