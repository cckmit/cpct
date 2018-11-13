package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * @Description 同步事件源接口
 * @Author pengy
 * @Date: 2018/8/28
 */
public interface SynInterfaceCfgService {

    Map<String,Object> synchronizeSingleEventInterface(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEventInterface(String roleName);

    Map<String,Object> deleteSingleEventInterface(Long eventId,String roleName);

}
