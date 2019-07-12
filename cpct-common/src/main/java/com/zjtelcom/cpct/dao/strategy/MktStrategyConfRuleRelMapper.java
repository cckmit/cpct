package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface  MktStrategyConfRuleRelMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleRelId);

    int deleteByMktStrategyConfId(Long mktStrategyConfId);

    int deleteByMktStrategyConfRulId(Long mktStrategyConfRulId);

    int insert(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);

    int insertBatch(List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList);

    MktStrategyConfRuleRelDO selectByPrimaryKey(Long mktStrategyConfRuleRelId);

    List<MktStrategyConfRuleRelDO> selectByMktStrategyConfId(Long mktStrategyConfId);

    MktStrategyConfRuleRelDO selectByRuleId(Long ruleId);

    List<MktStrategyConfRuleRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO);

    List<Long> listTarGrpIdListByStrategyId(@Param("strategyId")Long strategyId);
}