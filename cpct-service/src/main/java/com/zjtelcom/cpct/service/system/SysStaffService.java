package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysStaff;

import java.util.List;


public interface SysStaffService {

    List<SysStaff> listStaff(String staffCode,String staffName,Long status);

    int saveStaff(SysStaff sysStaff);

    int updateStaff(SysStaff sysStaff);

    int changeStatus(Long id,Long status);

    SysStaff getStaff(Long id);

}
