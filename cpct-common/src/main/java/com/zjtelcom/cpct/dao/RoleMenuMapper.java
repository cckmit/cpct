package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 角色菜单dao层
 * @author Huang Hua
 * @date 2018年5月21日
 */
@Mapper
@Repository
public interface RoleMenuMapper {

    /**权限--菜单查询方法**/
    public List<RoleMenu> query(RoleMenu roleMenu);

    /**权限--菜单更新方法**/
    public void update(RoleMenu roleMenu);

    /**权限--菜单插入方法**/
    public void insert(RoleMenu roleMenu);
}
