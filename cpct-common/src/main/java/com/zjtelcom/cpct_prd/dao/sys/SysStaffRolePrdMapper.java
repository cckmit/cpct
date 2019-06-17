package com.zjtelcom.cpct_prd.dao.sys;

import com.zjtelcom.cpct.domain.system.SysStaffRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysStaffRolePrdMapper {

    int deleteByPrimaryKey(Long staffRoleId);

    int insert(SysStaffRole record);

    SysStaffRole selectByPrimaryKey(Long staffRoleId);

    List<SysStaffRole> selectAll();

    int updateByPrimaryKey(SysStaffRole record);

    int deleteByStaffId(Long staffId);

    SysStaffRole selectByStaffId(Long StaffId);
}