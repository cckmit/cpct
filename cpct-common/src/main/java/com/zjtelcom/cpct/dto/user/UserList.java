package com.zjtelcom.cpct.dto.user;


import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;

public class UserList extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1675201743240220394L;
    private Long userId;
    private String userName;
    private String userPhone;
    private String filterType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
}