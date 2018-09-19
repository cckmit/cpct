package com.zjtelcom.cpct_prd.dao.grouping;



import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TrialOperationPrdMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TrialOperation record);

    TrialOperation selectByPrimaryKey(Long id);

    List<TrialOperation> selectAll();

    List<TrialOperation> findOperationListByRuleId(@Param("ruleId") Long ruleId);

    List<TrialOperation> findOperationListByStrategyId(@Param("strategyId") Long strategyId);

    int updateByPrimaryKey(TrialOperation record);
}