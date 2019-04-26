package com.zjtelcom.cpct.dao.system;

import com.zjtelcom.cpct.domain.system.SysStaffRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysStaffRoleMapper {

    int deleteByPrimaryKey(Long staffRoleId);

    int insert(SysStaffRole record);

    SysStaffRole selectByPrimaryKey(Long staffRoleId);

    List<SysStaffRole> selectAll();

    int updateByPrimaryKey(SysStaffRole record);

    int deleteByStaffId(Long staffId);

    SysStaffRole selectByStaffId(Long StaffId);
}