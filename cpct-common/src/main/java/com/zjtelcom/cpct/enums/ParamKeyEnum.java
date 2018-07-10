/**
 * @(#)ConfAttrEnum.java, 2018/7/3.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.enums;

/**
 * Description:
 * author: linchao
 * date: 2018/07/03 10:21
 * version: V1.0
 */
public enum ParamKeyEnum {

    STATUS_CD("statusCd", "状态"),
    REL_TYPE("relType", "活动类别"),
    TIGGER_TYPE("tiggerType", "触发类型"),
    EXEC_TYPE("execType", "活动周期"),
    MKT_CAMPAIGN_TYPE("mktCampaignType", "活动分类"),;

    private String paramKey;
    private String paramName;

    ParamKeyEnum() {
    }

    ParamKeyEnum(String paramKey, String paramName) {
        this.paramKey = paramKey;
        this.paramName = paramName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}