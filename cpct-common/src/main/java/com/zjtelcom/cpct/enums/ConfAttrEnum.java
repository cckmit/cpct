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

    START_TIME_1(500600010001L, "开始时间"),
    END_TIME_1(500600010002L, "结束时间"),
    START_TIME_2(500600010003L, "开始时间"),
    END_TIME_2(500600010004L, "结束时间"),
    MESSAGE_CODE(500600010005L, "短信发送码"),
    START_DATE(500600010006L, "生效日期"),
    END_DATE(500600010007L, "失效日期"),
    QUESTION(500600010008L, "调查问卷"),
    RULE(500600010009L, "协同规则"),
    PUSH_TYPE(500600010010L, "推送方式"),
    IVR_NUMBER(500600010011L, "IVR外显号码"),
    ACCOUNT(500600010012L, "接触账号");

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