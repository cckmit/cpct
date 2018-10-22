package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.SysStaffRole;
import java.util.List;

public interface SysStaffRoleMapper {
    int deleteByPrimaryKey(Long staffRoleId);

    int insert(SysStaffRole record);

    SysStaffRole selectByPrimaryKey(Long staffRoleId);

    List<SysStaffRole> selectAll();

    int updateByPrimaryKey(SysStaffRole record);
}