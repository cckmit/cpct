package com.zjtelcom.cpct.dao.filter;

import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CloseRuleMapper {
//    int deleteByPrimaryKey(Long ruleId);

//    int insert(FilterRule record);

    CloseRule selectByPrimaryKey(Long ruleId);

//    List<FilterRule> selectAll();

    int updateByPrimaryKey(CloseRule record);

    List<CloseRule> qryFilterRule(CloseRule closeRule);

    int delFilterRule(CloseRule closeRule);

//    FilterRule getFilterRule(FilterRule filterRule);

    int createFilterRule(CloseRule closeRule);

//    int modFilterRule(FilterRule filterRule);

    List<CloseRule> selectByProduct(@Param("chooseProduct") String chooseProduct, @Param("executionChannel") String executionChannel, @Param("filterType") String filterType);

    List<Map<String,Object>> getCloseCampaign(@Param("map") HashMap<String, Object> map);

    void updateExpression(@Param("filterRule")String filterRule, @Param("expression")String expression);

    void insertTarGrp(CloseRule closeRule);
}
