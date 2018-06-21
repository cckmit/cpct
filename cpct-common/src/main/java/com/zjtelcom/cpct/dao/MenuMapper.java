package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
  * 菜单dao层
  * @author Huang Hua
  * @date 2018年5月21日
  */
@Mapper
@Repository
public interface MenuMapper {

    /**菜单查询方法**/
    public List<Menu> query(Menu menu);

    /**菜单插入接口**/
    public void insert(Menu menu);

    /**菜单更新接口**/
    public void update(Menu menu);
}
