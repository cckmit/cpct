package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysStaff;

import java.util.List;

public interface SysStaffMapper {
    int deleteByPrimaryKey(Long staffId);

    int insert(SysStaff record);

    SysStaff selectByPrimaryKey(Long staffId);

    List<SysStaff> selectAll();

    int updateByPrimaryKey(SysStaff record);
}