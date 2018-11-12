package com.zjtelcom.cpct.service.synchronize.sys;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynSysRoleService {

    Map<String,Object> synchronizeSingleRole(Long roleId, String roleName);

    Map<String,Object> synchronizeBatchRole(String roleName);

    Map<String,Object> deleteSingleRole(Long roleId, String roleName);
}
