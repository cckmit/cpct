package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.dto.system.SysStaffDTO;

import java.util.Map;


public interface SysStaffService {

    Map<String,Object> listStaff(Map<String,String> params);

    Map<String,Object> saveStaff(SysStaffDTO sysStaffDTO) throws Exception;

    Map<String,Object> updateStaff(SysStaffDTO sysStaffDTO) throws Exception;

    Map<String,Object> changeStatus(Long id,Long status);

    Map<String,Object> getStaff(Long id);

    Map<String,Object> updatePassword(Long id,String password);

    Map<String,Object> lastLogin(String staffCode);

    Map<String, Object> queryUserByName(String userName);

}
