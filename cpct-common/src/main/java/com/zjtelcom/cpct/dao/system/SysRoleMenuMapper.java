package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysRoleMenu;

import java.util.List;

public interface SysRoleMenuMapper {
    int deleteByPrimaryKey(Long authorityId);

    int insert(SysRoleMenu record);

    SysRoleMenu selectByPrimaryKey(Long authorityId);

    List<SysRoleMenu> selectAll();

    int updateByPrimaryKey(SysRoleMenu record);
}