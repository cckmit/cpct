package com.zjtelcom.cpct.dao.grouping;



import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface TrialOperationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TrialOperation record);

    TrialOperation selectByPrimaryKey(Long id);

    List<TrialOperation> selectAll();

    List<TrialOperation> findOperationListByRuleId(@Param("ruleId")Long ruleId);

    List<TrialOperation> findOperationListByStrategyId(@Param("strategyId")Long strategyId,@Param("createStaff")Long createType);

    int updateByPrimaryKey(TrialOperation record);

    List<TrialOperation> listOperationByUpdateTime(@Param("campaignId")Long campaignId,@Param("updateTime")Date updateTime,@Param("list")String[] list);

    List<TrialOperation> listOperationByCreateTime(@Param("campaignId")Long campaignId,@Param("createTime")Date updateTime,@Param("list")String[] list);

    List<TrialOperation> listOperationCheck(@Param("list")String[] list);

    List<TrialOperation> listOperationByStatusCd(@Param("statusCd")String statusCd);

    TrialOperation selectByBatchNum(@Param("batchNum") String batchNum);

    List<TrialOperation> listOperationByCamIdAndStatusCd(@Param("campaignId")Long campaignId, @Param("statusCd")String statusCd);
}