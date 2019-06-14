package com.zjtelcom.cpct_prd.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyCloseRuleRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktStrategyCloseRuleRelPrdMapper {

    List<MktStrategyCloseRuleRelDO> selectByRuleId(Long ruleId);

    int deleteByPrimaryKey(Long mktStrategyFilterRuleRelId);

    int deleteByStrategyId(Long strategyId);

    int insert(MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO);

    int insertBatch(List<MktStrategyCloseRuleRelDO> mktStrategyFilterRuleRelDOList);

    MktStrategyCloseRuleRelDO selectByPrimaryKey(Long mktStrategyFilterRuleRelId);

    List<Long> selectByStrategyId(Long strategyId);

    List<MktStrategyCloseRuleRelDO> selectRuleByStrategyId(Long strategyId);

    List<MktStrategyCloseRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO);
    
}
