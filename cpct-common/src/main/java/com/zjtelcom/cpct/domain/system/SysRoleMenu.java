package com.zjtelcom.cpct.domain.system;

import com.zjtelcom.cpct.BaseEntity;

public class SysRoleMenu extends BaseEntity {
    private Long authorityId;

    private Long roleId;

    private Long menuId;

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
}