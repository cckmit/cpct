package com.zjtelcom.cpct_prd.dao.sys;

import com.zjtelcom.cpct.domain.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysMenuPrdMapper {

    int deleteByPrimaryKey(Long menuId);

    int insert(SysMenu record);

    SysMenu selectByPrimaryKey(Long menuId);

    List<SysMenu> selectAll();

    List<SysMenu> selectByRoleId(Long roleId);

    List<SysMenu> listMenuById(Long menuId);

    List<SysMenu> listMenuByLevel(Long level);

    int updateByPrimaryKey(SysMenu record);
}