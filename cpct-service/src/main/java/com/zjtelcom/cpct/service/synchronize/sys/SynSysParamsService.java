package com.zjtelcom.cpct.service.synchronize.sys;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynSysParamsService {

    Map<String,Object> synchronizeSingleParam(Long paramId, String roleName);

    Map<String,Object> synchronizeBatchParam(String roleName);
}
