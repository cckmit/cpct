package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/28
 * @Description:同步事件
 */
public interface SynContactEvtService {



    Map<String,Object> synchronizeSingleEvent(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEvent(String roleName);

    Map<String,Object> deleteSingleEvent(Long eventId,String roleName);

}
