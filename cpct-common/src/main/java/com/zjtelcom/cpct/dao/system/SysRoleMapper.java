package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    List<SysRole> selectAll(@Param("roleId") Long roleId,
                            @Param("roleName") String roleName);

    int updateByPrimaryKey(SysRole record);
}