package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/28
 * @Description:同步事件目录
 */

public interface SynContactEvtTypeService {

    Map<String,Object> synchronizeSingleEventType(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEventType(String roleName);

    Map<String,Object> deleteSingleEventType(Long eventId,String roleName);
}
