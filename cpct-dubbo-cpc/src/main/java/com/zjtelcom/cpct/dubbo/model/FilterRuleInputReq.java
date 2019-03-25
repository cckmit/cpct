package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.List;

public class FilterRuleInputReq implements Serializable {
    private Long filterRuleId;
    private List<UserListModel> userList;

    public Long getFilterRuleId() {
        return filterRuleId;
    }

    public void setFilterRuleId(Long filterRuleId) {
        this.filterRuleId = filterRuleId;
    }

    public List<UserListModel> getUserList() {
        return userList;
    }

    public void setUserList(List<UserListModel> userList) {
        this.userList = userList;
    }
}
