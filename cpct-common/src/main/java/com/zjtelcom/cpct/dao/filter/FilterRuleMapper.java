package com.zjtelcom.cpct.dao.filter;

import com.zjtelcom.cpct.dto.filter.FilterRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
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

}