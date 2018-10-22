package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.SysStaff;
import java.util.List;

public interface SysStaffMapper {
    int deleteByPrimaryKey(Long staffId);

    int insert(SysStaff record);

    SysStaff selectByPrimaryKey(Long staffId);

    List<SysStaff> selectAll();

    int updateByPrimaryKey(SysStaff record);
}