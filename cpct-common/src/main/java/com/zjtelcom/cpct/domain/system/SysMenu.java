package com.zjtelcom.cpct.domain.system;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

public class SysMenu extends BaseEntity {

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单图标
     */
    private String menuImg;

    /**
     * 菜单级别（菜单类型）
     */
    private Long menuType;

    /**
     * 上级菜单id
     */
    private Long parentMenuId;

    /**
     * 菜单排序
     */
    private Integer menuNextId;

    /**
     * 菜单路由
     */
    private String menuUrl;

    /**
     * 同步地址
     */
    private String syncUrl;

    /**
     * 菜单描述
     */
    private String menuRemark;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

    /**
     * 上级菜单名称
     */
    private String parentMenuName;

    /**
     * 菜单级别名称
     */
    private String menuTypeName;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuImg() {
        return menuImg;
    }

    public void setMenuImg(String menuImg) {
        this.menuImg = menuImg;
    }

    public Long getMenuType() {
        return menuType;
    }

    public void setMenuType(Long menuType) {
        this.menuType = menuType;
    }

    public Long getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(Long parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public Integer getMenuNextId() {
        return menuNextId;
    }

    public void setMenuNextId(Integer menuNextId) {
        this.menuNextId = menuNextId;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getSyncUrl() {
        return syncUrl;
    }

    public void setSyncUrl(String syncUrl) {
        this.syncUrl = syncUrl;
    }

    public String getMenuRemark() {
        return menuRemark;
    }

    public void setMenuRemark(String menuRemark) {
        this.menuRemark = menuRemark;
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

    public String getParentMenuName() {
        return parentMenuName;
    }

    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
    }

    public String getMenuTypeName() {
        return menuTypeName;
    }

    public void setMenuTypeName(String menuTypeName) {
        this.menuTypeName = menuTypeName;
    }
}