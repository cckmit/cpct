package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;

import java.util.Map;

/**
 * @Description 事件匹配规则service
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface ContactEvtMatchRulService {

    Map<String,Object> listEventMatchRuls(ContactEvtMatchRul contactEvtMatchRul);

    Map<String,Object> createContactEvtMatchRul(ContactEvtMatchRul contactEvtMatchRul);

}
