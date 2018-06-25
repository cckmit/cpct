package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysMenu;

import java.util.List;

public interface SysMenuService {

    List<SysMenu> listMenu();

    List<SysMenu> listMenuByRoleId(Long roleId);

}
