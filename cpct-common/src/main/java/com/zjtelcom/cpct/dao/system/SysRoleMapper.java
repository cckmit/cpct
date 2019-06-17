package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysRoleMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    List<SysRole> selectByParams(@Param("roleName") String roleName);

    List<Map<String,Object>> selectAll();

    int updateByPrimaryKey(SysRole record);

    List<SysRole> selectByStaffId(@Param("staffId") Long staffId);

}