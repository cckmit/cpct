package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.system.SysStaffDTO;

import java.util.List;
import java.util.Map;


public interface SysStaffService {

    Map<String,Object> listStaff(String staffAccount,String staffName,Long status,Integer page,Integer pageSize);

    Map<String,Object> saveStaff(SysStaffDTO sysStaffDTO) throws Exception;

    Map<String,Object> updateStaff(SysStaffDTO sysStaffDTO) throws Exception;

    Map<String,Object> changeStatus(Long id,Long status);

    Map<String,Object> getStaff(Long id);

    Map<String,Object> updatePassword(Long id,String password);

    Map<String,Object> lastLogin(String staffCode);

    Map<String, Object> queryUserByName(String userName);

}
