package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysRoleMenuMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(SysRoleMenu record);

    SysRoleMenu selectByPrimaryKey(Long authorityId);

    List<SysRoleMenu> selectAll();

    int updateByPrimaryKey(SysRoleMenu record);
}