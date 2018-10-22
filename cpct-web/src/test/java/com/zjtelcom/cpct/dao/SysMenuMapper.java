package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.SysMenu;
import java.util.List;

public interface SysMenuMapper {
    int deleteByPrimaryKey(Long menuId);

    int insert(SysMenu record);

    SysMenu selectByPrimaryKey(Long menuId);

    List<SysMenu> selectAll();

    int updateByPrimaryKey(SysMenu record);
}