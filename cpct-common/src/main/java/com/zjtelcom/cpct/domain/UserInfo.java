package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:HuangHua
 * @Descirption:
 * @Date: Created by huanghua on 2018/5/8.
 * @Modified By:
 */
public class UserInfo implements Serializable{

    private static final long serialVersionUID = 1L;

    private User user;

    private List<Role> roleList;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
