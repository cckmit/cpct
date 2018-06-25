package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysRole;

import java.util.List;

public interface SysRoleService {

    List<SysRole> listRole(Long roleId,String RoleName);

    int saveRole(SysRole sysRole);

    int updateRole(SysRole sysRole);

    SysRole getRole(Long id);

    int delRole(Long id);

    void saveAuthority(Long roleId,List<Long> list);

}
