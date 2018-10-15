package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysRole;

import java.util.List;
import java.util.Map;

public interface SysRoleService {

    Map<String, Object> listRole(String RoleName,int page,int pageSize);

    Map<String, Object> saveRole(SysRole sysRole);

    Map<String, Object> updateRole(SysRole sysRole);

    Map<String, Object> getRole(Long id);

    Map<String, Object> delRole(Long id);

    Map<String, Object> saveAuthority(Map<String, Object> params);

    Map<String, Object> listRoleAll();

}
