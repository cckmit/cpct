package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;

import java.util.Map;

/**
 * @Description EventTypeService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface ContactEvtTypeService {

    Map<String,Object> qryContactEvtTypeLists(QryContactEvtTypeReq qryContactEvtTypeReq);

    Map<String,Object> qryContactEvtTypeList(QryContactEvtTypeReq qryContactEvtTypeReq);

    Map<String,Object> createContactEvtType(ContactEvtType contactEvtType);

    Map<String,Object> getEventTypeDTOById(Long evtTypeId);

    Map<String,Object> modContactEvtType(ContactEvtType contactEvtType);

    Map<String,Object> delContactEvtType(ContactEvtType contactEvtType);

    Map<String,Object> listEventType();
}
