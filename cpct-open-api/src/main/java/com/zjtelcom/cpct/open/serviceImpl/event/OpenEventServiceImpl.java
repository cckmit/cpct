//package com.zjtelcom.cpct.open.serviceImpl.event;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.zjtelcom.cpct.common.Page;
//import com.zjtelcom.cpct.constants.CommonConstant;
//import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
//import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
//import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
//import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
//import com.zjtelcom.cpct.dto.event.ContactEvt;
//import com.zjtelcom.cpct.dto.event.ContactEvtItem;
//import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
//import com.zjtelcom.cpct.exception.SystemException;
//import com.zjtelcom.cpct.open.base.common.CommonUtil;
//import com.zjtelcom.cpct.open.base.common.FormatBeanUtil;
//import com.zjtelcom.cpct.open.base.service.BaseService;
//import com.zjtelcom.cpct.open.entity.event.Event;
//import com.zjtelcom.cpct.open.entity.event.EventItem;
//import com.zjtelcom.cpct.open.entity.event.EventMatchRul;
//import com.zjtelcom.cpct.open.entity.event.EventSource;
//import com.zjtelcom.cpct.open.service.event.OpenEventService;
//import com.zjtelcom.cpct.util.BeanUtil;
//import com.zjtelcom.cpct.util.DateUtil;
//import com.zjtelcom.cpct.util.UserUtil;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Transactional
//public class OpenEventServiceImpl extends BaseService implements OpenEventService{
//
//    @Autowired
//    private ContactEvtMapper contactEvtMapper;
//    @Autowired
//    private ContactEvtItemMapper contactEvtItemMapper;
//    @Autowired
//    private ContactEvtMatchRulMapper contactEvtMatchRulMapper;
//
//    /*
//    **根据事件id主键查询
//    */
//    @Override
//    public Map<String, Object> getEvent(Long contactEvtId) {
//        Map<String, Object> eventMap = new HashMap<>();
//        ContactEvt contactEvt = contactEvtMapper.getEventById(contactEvtId);
//        if(null != contactEvt) {
//            Event event = BeanUtil.create(contactEvt, new Event());
//
//            event.setId(String.valueOf(contactEvtId));
//            event.setHref("/event/" + String.valueOf(contactEvtId));
//            event.setEventId(contactEvt.getContactEvtId());
//            event.setEventName(contactEvt.getContactEvtName());
//            event.setEventDesc(contactEvt.getContactEvtDesc());
//            event.setEventNbr(contactEvt.getContactEvtCode());
//            event.setEventTrigType(contactEvt.getEvtTrigType());
//
//            //事件采集项
//            List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvtId);
//            List<EventItem> eventItems = new ArrayList<>();
//            for(ContactEvtItem contactEvtItem : contactEvtItems) {
//                EventItem eventItem = BeanUtil.create(contactEvtItem, new EventItem());
//                eventItems.add(eventItem);
//            }
//            event.setEventItem(eventItems);
//
//            //事件规则
//
//            ContactEvtMatchRul contactEvtMatchRul = new ContactEvtMatchRul();
//            contactEvtMatchRul.setContactEvtId(contactEvtId);
//            List<ContactEvtMatchRul> contactEvtMatchRuls= contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRul);
//            List<EventMatchRul> eventMatchRuls = new ArrayList<>();
//            for(ContactEvtMatchRul contactEvtMatchRulOne : contactEvtMatchRuls) {
//                EventMatchRul eventMatchRul = BeanUtil.create(contactEvtMatchRulOne, new EventMatchRul());
//                eventMatchRul.setEventId(contactEvtMatchRulOne.getContactEvtId());
//                if(contactEvtMatchRulOne.getStatusDate() != null) {
//                    eventMatchRul.setStatusDate(DateUtil.getDatetime(contactEvtMatchRulOne.getStatusDate()));
//                }
//                eventMatchRuls.add(eventMatchRul);
//            }
//            event.setEventMatchRul(eventMatchRuls);
//
//            eventMap.put("params", event);
//            return eventMap;
//        }else {
//            throw new SystemException("事件id为" + contactEvtId + " 所对应的事件不存在!");
//        }
//    }
//
//    /*
//    **新增事件
//    */
//    @Override
//    public Map<String, Object> saveEvent(Event event) {
//        Map<String, Object> eventMap = new HashMap<>();
//        ContactEvt contactEvt = BeanUtil.create(event, new ContactEvt());
//
//        contactEvt.setContactEvtName(event.getEventName());
//        contactEvt.setContactEvtDesc(event.getEventDesc());
//        contactEvt.setContactEvtCode(event.getEventNbr());
//        contactEvt.setEvtTrigType(event.getEventTrigType());
//
//        contactEvt.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//        contactEvt.setUpdateDate(DateUtil.getCurrentTime());
//        contactEvt.setCreateDate(DateUtil.getCurrentTime());
//        contactEvt.setStatusDate(DateUtil.getCurrentTime());
//        contactEvt.setUpdateStaff(UserUtil.loginId());
//        contactEvt.setCreateStaff(UserUtil.loginId());
//        contactEvtMapper.createContactEvt(contactEvt);
//
//        //事件采集
//        List<EventItem> eventItems = event.getEventItem();
//        for (EventItem eventItem : eventItems) {
//            ContactEvtItem contactEvtItem = BeanUtil.create(eventItem ,new ContactEvtItem());
//            contactEvtItem.setContactEvtId(contactEvt.getContactEvtId());
//            contactEvtItem.setCreateDate(DateUtil.getCurrentTime());
//            contactEvtItem.setUpdateDate(DateUtil.getCurrentTime());
//            contactEvtItem.setStatusDate(DateUtil.getCurrentTime());
//            contactEvtItem.setUpdateStaff(UserUtil.loginId());
//            contactEvtItem.setCreateStaff(UserUtil.loginId());
//            contactEvtItem.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//            contactEvtItemMapper.insertContactEvtItem(contactEvtItem);
//        }
//
//        //事件匹配规则
//        List<EventMatchRul> eventMatchRuls = event.getEventMatchRul();
//        for (EventMatchRul eventMatchRul : eventMatchRuls) {
//            ContactEvtMatchRul contactEvtMatchRul = BeanUtil.create(eventMatchRul, new ContactEvtMatchRul());
//            contactEvtMatchRul.setContactEvtId(contactEvt.getContactEvtId());
//            contactEvtMatchRul.setCreateDate(DateUtil.getCurrentTime());
//            contactEvtMatchRul.setUpdateDate(DateUtil.getCurrentTime());
//            contactEvtMatchRul.setUpdateStaff(UserUtil.loginId());
//            contactEvtMatchRul.setCreateStaff(UserUtil.loginId());
//            contactEvtMatchRul.setStatusDate(DateUtil.getCurrentTime());
//
//            contactEvtMatchRulMapper.createContactEvtMatchRul(contactEvtMatchRul);
//        }
//
//        eventMap = getEvent(contactEvt.getContactEvtId());
//        return eventMap;
//    }
//
//    /*
//    **修改事件
//    */
//    @Override
//    public Map<String, Object> updateEvent(String eventId, String params) {
//        Map<String, Object> eventMap = new HashMap<>();
//
//        JSONArray array = (JSONArray) JSONArray.parse(params);
//        for (int i = 0; i <array.size() ; i++) {
//            //目前只考虑修改值的情况
//            JSONObject jsonObject = (JSONObject) array.get(i);
//            String op = (String) jsonObject.get("op");
//            String path = (String) jsonObject.get("path");
//
//            if(path.indexOf("/", 1) < 0){
//                ContactEvt contactEvt = contactEvtMapper.getEventById(Long.valueOf(eventId));
//                if(contactEvt == null) {
//                    throw new SystemException("对应事件不存在");
//                }
//                path = path.substring(1);
//                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(contactEvt));
//                if(path.equals("eventName")) {
//                    json.put("contactEvtName", jsonObject.getString("value"));
//                }else if(path.equals("eventDesc")){
//                    json.put("contactEvtDesc", jsonObject.getString("value"));
//                }else if(path.equals("eventNbr")){
//                    json.put("contactEvtCode", jsonObject.getString("value"));
//                }else if(path.equals("eventTrigType")){
//                    json.put("evtTrigType", jsonObject.getString("value"));
//                }else{
//                    json.put(path, jsonObject.getString("value"));
//                }
//                ContactEvt event = JSONObject.parseObject(json.toJSONString(), ContactEvt.class);
//                contactEvtMapper.modContactEvt(event);
//            }
//            else {
//                //事件采集项
//                if (path.substring(1, path.indexOf("/", 1)).equals("eventItem")) {
//                    ContactEvtItem contactEvtItem = contactEvtItemMapper.selectByPrimaryKey(Long.valueOf(path.substring(path.indexOf("/", 1) + 1)));
//                    if (contactEvtItem != null) {
//                        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(contactEvtItem));
//                        json.putAll((Map<String,Object>)jsonObject.get("value"));
//                        ContactEvtItem eventItem = JSONObject.parseObject(json.toJSONString(), ContactEvtItem.class);
//                        contactEvtItemMapper.modEventItem(eventItem);
//                    }else {
//                        throw new SystemException("对应事件采集项不存在");
//                    }
//                }
//                //事件规则
//                else if(path.substring(1, path.indexOf("/", 1)).equals("eventMatchRul")) {
//                    EventMatchRulDO eventMatchRulDO  = contactEvtMatchRulMapper.selectByPrimaryKey(Long.valueOf(path.substring(path.indexOf("/", 1) + 1)));
//                    if(eventMatchRulDO != null) {
//                        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(eventMatchRulDO));
//                        json.putAll((Map<String,Object>)jsonObject.get("value"));
//                        EventMatchRulDO evtMatchRul = JSONObject.parseObject(json.toJSONString(), EventMatchRulDO.class);
//                        contactEvtMatchRulMapper.updateByPrimaryKey(evtMatchRul);
//                    }else {
//                        throw new SystemException("对应事件规则不存在");
//                    }
//                }
//            }
//        }
//
//        eventMap = getEvent(Long.valueOf(eventId));
//        return eventMap;
//    }
//
//    /*
//    **删除事件
//    */
//    @Override
//    public Map<String, Object> deleteEvent(Long contactEvtId) {
//        Map<String, Object> eventMap = new HashMap<>();
//        int i = contactEvtMapper.delEvent(contactEvtId);
//        if(i == 0){
//            throw new SystemException("对应事件不存在");
//        }else {
//            //删除事件采集项
//            List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvtId);
//            for(int j = 0;j<contactEvtItems.size();j++) {
//                contactEvtItemMapper.deleteByPrimaryKey(contactEvtItems.get(j).getEvtItemId());
//            }
//            //删除事件规则
//            ContactEvtMatchRul contactEvtMatchRul = new ContactEvtMatchRul();
//            contactEvtMatchRul.setContactEvtId(contactEvtId);
//            List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRul);
//            for(int j = 0;j<contactEvtMatchRuls.size();j++) {
//                contactEvtMatchRulMapper.deleteByPrimaryKey(contactEvtMatchRuls.get(j).getEvtMatchRulId());
//            }
//        }
//        return eventMap;
//    }
//
//    /*
//    **查询事件列表
//    */
//    @Override
//    public Map<String, Object> listEventPage(Map<String, Object> params) {
//        Map<String, Object> eventMap = new HashMap<>();
//        CommonUtil.setPage(params);
//
//        ContactEvt contactEvt = new ContactEvt();
//        if(StringUtils.isNotBlank((String) params.get("eventId"))){
//            contactEvt.setContactEvtId(Long.valueOf((String)params.get("eventId")));
//        }
//        if(StringUtils.isNotBlank((String) params.get("eventName"))){
//            contactEvt.setContactEvtName((String)params.get("eventName"));
//        }
//        if(StringUtils.isNotBlank((String) params.get("eventNbr"))){
//            contactEvt.setContactEvtCode((String)params.get("eventNbr"));
//        }
//        if(StringUtils.isNotBlank((String) params.get("statusCd"))){
//            contactEvt.setStatusCd((String)params.get("statusCd"));
//        }
//        List<ContactEvt> contactEvts = contactEvtMapper.findEventsByKey(contactEvt);
//
//        if(contactEvts == null){
//            throw new SystemException("对应的事件不存在!");
//        }
//
//        List<Event> list = new ArrayList<>();
//        for(ContactEvt contactEvtOne : contactEvts) {
//            Event event = BeanUtil.create(contactEvtOne, new Event());
//            event.setId(String.valueOf(contactEvtOne.getContactEvtId()));
//            event.setHref("/event/" + String.valueOf(contactEvtOne.getContactEvtId()));
//            event.setEventId(contactEvtOne.getContactEvtId());
//            event.setEventName(contactEvtOne.getContactEvtName());
//            event.setEventDesc(contactEvtOne.getContactEvtDesc());
//            event.setEventNbr(contactEvtOne.getContactEvtCode());
//            event.setEventTrigType(contactEvtOne.getEvtTrigType());
//            list.add(event);
//        }
//
//        Page pageInfo = new Page(new PageInfo(contactEvts));
//        eventMap.put("params", list);
//        eventMap.put("size", String.valueOf(pageInfo.getTotal()));
//        return eventMap;
//    }
//
//}
