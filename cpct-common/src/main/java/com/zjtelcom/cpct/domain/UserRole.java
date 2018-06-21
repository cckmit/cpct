package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.Date;

/**
  * 用户角色表实体类
  * @author nhf
  * @date 2018年5月19日
  */
public class UserRole implements Serializable{

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private int id;

    /**
     * 用户ID
     */
    private int userId;

    /**
     * 角色ID
     */
    private int roleId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}
