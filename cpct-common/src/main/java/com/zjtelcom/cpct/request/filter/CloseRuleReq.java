package com.zjtelcom.cpct.request.filter;

import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.FilterRule;

import java.io.Serializable;

/**
 * @Description 过滤规则前端请求req
 * @Author pengy
 * @Date 2018/7/3 11:49
 */
public class CloseRuleReq implements Serializable {

    private CloseRule closeRule;
    private Page pageInfo;

    public CloseRule getCloseRule() {
        return closeRule;
    }

    public void setCloseRule(CloseRule closeRule) {
        this.closeRule = closeRule;
    }

    public Page getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Page pageInfo) {
        this.pageInfo = pageInfo;
    }
}
