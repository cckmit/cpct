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

    STATUS_CD("CAM-0001", "statusCd"),    //状态
    MKT_CAMPAIGN_CATEGORY("CAM-C-0037", "mktCampaignCategory"),  //活动类别
    TIGGER_TYPE("CAM-C-0009", "tiggerType"),  // 触发类型
    EXEC_TYPE("CAM-C-0034", "execType"), // 活动周期
    MKT_CAMPAIGN_TYPE("CAM-C-0033", "mktCampaignType"),  //活动分类
    TIME_TYPE("CAM-C-0014", "timeType");  // 周期类型

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