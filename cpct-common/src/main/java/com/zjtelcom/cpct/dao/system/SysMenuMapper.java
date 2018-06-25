package com.zjtelcom.cpct.dao.system;

import com.zjtelcom.cpct.domain.system.SysMenu;

import java.util.List;

public interface SysMenuMapper {

    int deleteByPrimaryKey(Long menuId);

    int insert(SysMenu record);

    SysMenu selectByPrimaryKey(Long menuId);

    List<SysMenu> selectAll();

    List<SysMenu> selectByRoleId(Long roleId);

    int updateByPrimaryKey(SysMenu record);
}