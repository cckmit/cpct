package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.system.SysStaffDTO;

import java.util.List;
import java.util.Map;


public interface SysStaffService {

    List<SysStaff> listStaff(String staffCode,String staffName,Long status,Integer page,Integer pageSize);

    int saveStaff(SysStaffDTO sysStaffDTO) throws Exception;

    int updateStaff(SysStaffDTO sysStaffDTO) throws Exception;

    int changeStatus(Long id,Long status);

    SysStaff getStaff(Long id);

    int updatePassword(Long id,String password);

    int lastLogin(String staffCode);

    Map<String, Object> queryUserByName(String userName);

}
