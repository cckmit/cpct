package com.zjtelcom.cpct.service.synchronize.sys;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynSysStaffService {

    Map<String,Object> synchronizeSingleStaff(Long staffId, String roleName);

    Map<String,Object> synchronizeBatchStaff(String roleName);

}
