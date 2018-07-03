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
public enum ConfAttrEnum {

    START_DATE(1000L, "生效日期"),
    END_DATE(1001L, "失效日期"),
    START_TIME(1002L, "开始时间"),
    END_TIME(1003L, "结束时间"),
    QUESTION(1004L, "调查问卷"),
    RULE(1005L, "协同规则");

    private Long arrId;
    private String arrName;

    ConfAttrEnum() {
    }

    ConfAttrEnum(Long arrId, String arrName) {
        this.arrId = arrId;
        this.arrName = arrName;
    }

    public Long getArrId() {
        return arrId;
    }

    public void setArrId(Long arrId) {
        this.arrId = arrId;
    }

    public String getArrName() {
        return arrName;
    }

    public void setArrName(String arrName) {
        this.arrName = arrName;
    }

}