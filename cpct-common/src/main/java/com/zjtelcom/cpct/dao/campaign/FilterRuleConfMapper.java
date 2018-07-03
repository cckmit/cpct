package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.FilterRuleConfDO;

import java.util.List;

public interface FilterRuleConfMapper {
    int deleteByPrimaryKey(Long filterRuleConfId);

    int insert(FilterRuleConfDO filterRuleConfDO);

    FilterRuleConfDO selectByPrimaryKey(Long filterRuleConfId);

    List<FilterRuleConfDO> selectAll();

    int updateByPrimaryKey(FilterRuleConfDO filterRuleConfDO);
}