package com.zjtelcom.cpct_prd.dao.sys;

import com.zjtelcom.cpct.domain.system.SysStaffRole;

import java.util.List;

public interface SysStaffRolePrdMapper {

    int deleteByPrimaryKey(Long staffRoleId);

    int insert(SysStaffRole record);

    SysStaffRole selectByPrimaryKey(Long staffRoleId);

    List<SysStaffRole> selectAll();

    int updateByPrimaryKey(SysStaffRole record);

    int deleteByStaffId(Long staffId);

    SysStaffRole selectByStaffId(Long StaffId);
}