package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysUserRole;

import java.util.List;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(Long staffRoleId);

    int insert(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long staffRoleId);

    List<SysUserRole> selectAll();

    int updateByPrimaryKey(SysUserRole record);
}