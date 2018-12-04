package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.SysRole;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    List<SysRole> selectAll();

    int updateByPrimaryKey(SysRole record);
}