package com.zjtelcom.cpct.dao.system;

import com.zjtelcom.cpct.domain.system.SysStaffRole;

import java.util.List;

public interface SysStaffRoleMapper {

    int deleteByPrimaryKey(Long staffRoleId);

    int insert(SysStaffRole record);

    SysStaffRole selectByPrimaryKey(Long staffRoleId);

    List<SysStaffRole> selectAll();

    int updateByPrimaryKey(SysStaffRole record);

    int deleteByStaffId(Long staffId);
}