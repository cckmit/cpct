package com.zjtelcom.cpct.open.entity.event;

import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class Event extends BaseEntity{

    Long eventId;
    String eventNbr;
    String eventName;
    String evtMappedAddr;
    String evtMappedIp;
    String evtProcotolType;
    String evtMappedFunName;
    String eventDesc;
    String eventTrigType;
    String remark;
    List<EventItem> eventItem;
    List<EventMatchRul> eventMatchRul;

}
