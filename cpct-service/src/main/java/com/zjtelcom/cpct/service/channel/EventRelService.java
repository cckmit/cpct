package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.EventRel;
import com.zjtelcom.cpct.dto.event.ContactEvt;

import java.util.Map;

public interface EventRelService {

    Map<String,Object> getEventNoRelation(Long userId, ContactEvt contactEvt);

    Map<String,Object> createEventRelation(Long userId, EventRel addVO);

    Map<String,Object> delEventRelation(Long userId, EventRel delVO);

    Map<String,Object> getEventRelList(Long userId, ContactEvt contactEvt);

}
