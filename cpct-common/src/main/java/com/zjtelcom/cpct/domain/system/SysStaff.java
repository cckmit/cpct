package com.zjtelcom.cpct.domain.system;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.Date;

public class SysStaff extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7723594431088897364L;

    private Long staffId;

    private String staffCode;

    /**
     * 员工账号
     */
    private String staffAccount;

    private String staffName;

    private String password;

    private String staffPhone;

    private String staffTelephone;

    private String staffEmail;

    private Long channelId;

    private Long cityId;

    private Long status;

    private Long loginStatus;

    private Date lastLogin;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

    /**
     * 员工角色
     */
    private String roleName;

    /**
     * 员工角色
     */
    private Long roleId;

    /**
     * 所属渠道
     */
    private String channelName;

    /**
     * 所属地市
     */
    private String cityName;

    /**
     * 登录状态
     */
    private String loginName;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getStaffTelephone() {
        return staffTelephone;
    }

    public void setStaffTelephone(String staffTelephone) {
        this.staffTelephone = staffTelephone;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public Long getCreateStaff() {
        return createStaff;
    }

    @Override
    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public Long getUpdateStaff() {
        return updateStaff;
    }

    @Override
    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public String getStaffAccount() {
        return staffAccount;
    }

    public void setStaffAccount(String staffAccount) {
        this.staffAccount = staffAccount;
    }

    public Long getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Long loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}