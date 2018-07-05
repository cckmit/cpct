package com.zjtelcom.cpct.domain.system;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户登录是体类
 */
public class UserInfo implements Serializable{


    private static final long serialVersionUID = -5647574388727658088L;

    private Long staffId;

    /**
     * 员工账号
     */
    private String staffAccount;

    private String staffCode;

    private String staffName;

    private Long channelId;

    private Long cityId;

    private List<SysRole> roleList;

    private List<SysMenu> menuList;


    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffAccount() {
        return staffAccount;
    }

    public void setStaffAccount(String staffAccount) {
        this.staffAccount = staffAccount;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public List<SysRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SysRole> roleList) {
        this.roleList = roleList;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public List<SysMenu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<SysMenu> menuList) {
        this.menuList = menuList;
    }
}
