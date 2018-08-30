package com.zjtelcom.cpct.service.synchronize;

import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtReq;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/28
 * @Description:同步事件接口
 */
public interface SynContactEvtService {



    Map<String,Object> synchronizeSingleEvent(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEvent(String roleName);

}
