package com.zjtelcom.cpct.service.system;


import java.util.Map;

public interface SysMenuService {

    Map<String,Object> listMenu();

    Map<String,Object> listMenuByRoleId(Long roleId);

    /**
     * 新增菜单
     * @param params
     * @return
     */
    Map<String,Object> saveMenu(Map<String,String> params);

    /**
     * 修改菜单
     * @param params
     * @return
     */
    Map<String,Object> updateMenu(Map<String,String> params);

    /**
     * 删除菜单
     * @param params
     * @return
     */
    Map<String,Object> delMenu(Map<String,String> params);

    /**
     * 根据菜单id获取菜单及其子菜单
     * @param params
     * @return
     */
    Map<String,Object> listMenuById(Map<String,String> params);

    /**
     * 根据菜单等级获取菜单
     * @param params
     * @return
     */
    Map<String,Object> listMenuByLevel(Map<String,String> params);


}
