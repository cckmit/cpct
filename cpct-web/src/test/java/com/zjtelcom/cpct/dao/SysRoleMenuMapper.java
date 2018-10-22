package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.SysRoleMenu;
import java.util.List;

public interface SysRoleMenuMapper {
    int deleteByPrimaryKey(Long authorityId);

    int insert(SysRoleMenu record);

    SysRoleMenu selectByPrimaryKey(Long authorityId);

    List<SysRoleMenu> selectAll();

    int updateByPrimaryKey(SysRoleMenu record);
}