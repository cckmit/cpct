package com.zjtelcom.cpct.dao.filter;

import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Mapper
@Repository
public interface FilterRuleMapper {
    int deleteByPrimaryKey(Long ruleId);

    int insert(FilterRule record);

    FilterRule selectByPrimaryKey(Long ruleId);

    List<FilterRule> selectAll();

    int updateByPrimaryKey(FilterRule record);

    List<FilterRule> qryFilterRule(FilterRule filterRule);

    int delFilterRule(FilterRule filterRule);

    FilterRule getFilterRule(FilterRule filterRule);

    int createFilterRule(FilterRule filterRule);

    int modFilterRule(FilterRule filterRule);

    List<FilterRuleModel> selectFilterRuleByStrategyId(@Param("strategyId") Long strategyId);

    ArrayList<FilterRuleModel> selectFilterRuleByStrategyIdArrayList(@Param("strategyId") Long strategyId);

    List<FilterRule> selectFilterRuleList(@Param("strategyId") Long strategyId);

    List<FilterRule> selectFilterRuleListByStrategyId(@Param("strategyId") Long strategyId,@Param("strategyTypeList") List<String> strategyTypeList);

    void updateExpression2(@Param("filterRule")String filterRule, @Param("expression")String expression);

    List<FilterRule> qryFilterRuleExcludeType(FilterRule filterRule);

    List<FilterRule> selectFilterRuleByRuleName(@Param("ruleName") String ruleName);
}