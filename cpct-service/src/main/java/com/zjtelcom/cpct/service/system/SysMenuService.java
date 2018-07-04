package com.zjtelcom.cpct.service.system;


import java.util.Map;

public interface SysMenuService {

    Map<String,Object> listMenu();

    Map<String,Object> listMenuByRoleId(Long roleId);

}
