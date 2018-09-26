package com.zjtelcom.cpct.service.synchronize.sys;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynSysMenuService {

    Map<String,Object> synchronizeSingleMenu(Long menuId, String roleName);

    Map<String,Object> synchronizeBatchMenu(String roleName);
}
