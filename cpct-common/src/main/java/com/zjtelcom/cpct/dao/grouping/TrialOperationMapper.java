package com.zjtelcom.cpct.dao.grouping;



import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TrialOperationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TrialOperation record);

    TrialOperation selectByPrimaryKey(Long id);

    List<TrialOperation> selectAll();

    List<TrialOperation> findOperationListByRuleId(@Param("ruleId")Long ruleId);

    List<TrialOperation> findOperationListByStrategyId(@Param("strategyId")Long strategyId);

    int updateByPrimaryKey(TrialOperation record);

    List<TrialOperation> listOperationByUpdateTime(@Param("updateTime")Date updateTime);

    TrialOperation selectByBatchNum(@Param("batchNum") String batchNum);
}