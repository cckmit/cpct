package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 策略配置规则Mapper
 */
@Mapper
@Repository
public interface MktStrategyConfRuleMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRuleId);

    int insert(MktStrategyConfRuleDO mktStrategyConfRuleDO);

    void insertByBatch(List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList);

    MktStrategyConfRuleDO selectByPrimaryKey(Long mktStrategyConfRuleId);

    List<MktStrategyConfRuleDO> selectAll();

    String selectMktStrategyConfRuleName(Long mktStrategyConfRuleId);

    int updateByPrimaryKey(MktStrategyConfRuleDO mktStrategyConfRuleDO);

    List<MktStrategyConfRuleDO> selectByMktStrategyConfId(Long mktStrategyConfId);

    MktStrategyConfRuleDO selectByTarGrpId(@Param("tarGrpId") Long tarGrpId);


}