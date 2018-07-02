package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.event.EventList;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import java.util.List;
import java.util.Map;

/**
 * @Description EventService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface ContactEvtService {

    List<EventList> listEvents(String contactEvtName);

    Map<String,Object> createContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) throws Exception;

    void delEvent(Long contactEvtId);

    void closeEvent(Long contactEvtId);

    EventDTO editEvent(Long contactEvtId);

    Map<String,Object> modContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq);

}
