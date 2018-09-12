package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;

public class UserListModel implements Serializable {
    private String userName;
    private String userPhone;
    private Long ruleId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }
}
