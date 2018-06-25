package com.zjtelcom.cpct.service.system;

public interface SysStaffRoleService {

    int saveStaffRole(Long staffId,Long roleId);

    int delStaffRoleByStaffId(Long staffId);


}
