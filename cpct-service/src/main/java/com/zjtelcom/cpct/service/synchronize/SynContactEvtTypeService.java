package com.zjtelcom.cpct.service.synchronize;

import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;

import java.util.Map;

/**
 * @Description EventTypeService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface SynContactEvtTypeService {

    Map<String,Object> synchronizeSingleEventType(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEventType(String roleName);
}
