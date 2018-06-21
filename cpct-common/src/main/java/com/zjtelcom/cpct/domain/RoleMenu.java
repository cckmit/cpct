package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.Date;

/**
  * 角色菜单表实体类
  * @author Huang Hua
  * @date 2018年5月19日
  */
public class RoleMenu implements Serializable{

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private int id;

    /**
     * 角色ID
     */
    private int roleId;

    /**
     * 菜单ID
     */
    private int menuId;

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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
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

    @Override
    public String toString(){
        return "id:" + this.getId() + ";" + "role id:" + this.getRoleId() + ";" + "menu id:" + this.getMenuId();
    }
}
