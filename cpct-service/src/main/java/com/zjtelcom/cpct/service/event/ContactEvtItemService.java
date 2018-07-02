package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.dto.event.ContactEvtItem;

import java.util.Map;

/**
 * @Description 事件采集项service
 * @Author pengy
 * @Date 2018/7/1 22:39
 */
public interface ContactEvtItemService {

    Map<String,Object> listEventItem(Long contactEvtId);

    Map<String,Object> delEventItem(ContactEvtItem contactEvtItem);

    Map<String,Object> viewEventItem(ContactEvtItem contactEvtItem);

    Map<String,Object> createEventItem(ContactEvtItem contactEvtItem);

    Map<String,Object> modEventItem(ContactEvtItem contactEvtItem);

}
