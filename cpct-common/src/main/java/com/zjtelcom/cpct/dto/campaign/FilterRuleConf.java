package com.zjtelcom.cpct.dto.campaign;

import java.util.List;

public class FilterRuleConf {

    private Long filterRuleConfId;

    private List<FilterRule> filterRuleList;

    public Long getFilterRuleConfId() {
        return filterRuleConfId;
    }

    public void setFilterRuleConfId(Long filterRuleConfId) {
        this.filterRuleConfId = filterRuleConfId;
    }

    public List<FilterRule> getFilterRuleList() {
        return filterRuleList;
    }

    public void setFilterRuleList(List<FilterRule> filterRuleList) {
        this.filterRuleList = filterRuleList;
    }
}