package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Role;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 角色dao层
  * @author Huang Hua
  * @date 2018年5月21日
  */
@Mapper
@Repository
public interface RoleMapper {

    /**角色方法**/
    public List<Role> query(Role role);

    /**角色插入接口**/
    public void insert(Role role);

    /**角色更新接口**/
    public void update(Role role);
}
