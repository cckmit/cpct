package com.zjtelcom.cpct_prd.dao.sys;


import com.zjtelcom.cpct.domain.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysRolePrdMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    List<SysRole> selectByParams(@Param("roleId") Long roleId,
                                 @Param("roleName") String roleName);

    List<Map<String,Object>> selectAll();

    int updateByPrimaryKey(SysRole record);

    List<SysRole> selectByStaffId(@Param("staffId") Long staffId);

}