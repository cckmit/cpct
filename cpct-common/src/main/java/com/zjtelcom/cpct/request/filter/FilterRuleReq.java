package com.zjtelcom.cpct.request.filter;

import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dto.filter.FilterRule;

import java.io.Serializable;

/**
 * @Description 过滤规则前端请求req
 * @Author pengy
 * @Date 2018/7/3 11:49
 */
public class FilterRuleReq implements Serializable {

    private static final long serialVersionUID = -1177863356722254234L;
    private FilterRule filterRule;
    private Page pageInfo;

    public FilterRule getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(FilterRule filterRule) {
        this.filterRule = filterRule;
    }

    public Page getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Page pageInfo) {
        this.pageInfo = pageInfo;
    }
}
