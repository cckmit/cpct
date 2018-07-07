package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.event.EventList;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtReq;

import java.util.List;
import java.util.Map;

/**
 * @Description EventService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface ContactEvtService {

    Map<String,Object> listEvents(ContactEvt contactEvt,Page pageInfo);

    Map<String,Object> createContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) throws Exception;

    Map<String,Object> createContactEvt(CreateContactEvtReq createContactEvtReq) throws Exception;

    Map<String,Object> delEvent(Long contactEvtId);

    Map<String,Object> closeEvent(Long contactEvtId,String statusCd);

    Map<String,Object> editEvent(Long contactEvtId) throws Exception;

    Map<String,Object> modContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq);

}
