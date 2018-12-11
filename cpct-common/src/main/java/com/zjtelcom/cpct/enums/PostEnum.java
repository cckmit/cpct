package com.zjtelcom.cpct.enums;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/12/10 19:23
 * @version: V1.0
 */
public enum PostEnum {

    ADMIN("cpcp0001", "超级管理员"),
    CHANNEL_MANAGER("cpcp0002", "渠道管理员"),
    REVIEW_CAMPAIGN("cpcp0003","活动审核"),
    PLANNING_CAMPAIGN("cpcp0004","活动策划"),
    UNDERTAKE_CAMPAIGN("cpcp0005","活动承接");

    private String postCode;
    private String postName;

    PostEnum(String postCode, String postName) {
        this.postCode = postCode;
        this.postName = postName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public static String getNameByCode(String postCode) {
        for (PostEnum roleEnum : PostEnum.values()) {
            if (postCode != null && postCode.equals(roleEnum.postCode)) {
                return roleEnum.postName;
            }
        }
        return null;
    }
}