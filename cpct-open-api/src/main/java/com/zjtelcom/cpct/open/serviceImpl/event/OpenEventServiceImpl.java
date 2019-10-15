package com.zjtelcom.cpct.open.serviceImpl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.EventRelMapper;
import com.zjtelcom.cpct.dao.event.*;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.domain.channel.EventRel;
import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import com.zjtelcom.cpct.dto.event.*;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.event.*;
import com.zjtelcom.cpct.open.service.event.OpenEventService;
import com.zjtelcom.cpct.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class OpenEventServiceImpl extends BaseService implements OpenEventService {

    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper;
    @Autowired
    private EventMatchRulMapper eventMatchRulMapper;
    @Autowired
    private ContactEvtTypeMapper contactEvtTypeMapper;
    @Autowired
    private EventRelMapper eventRelMapper;

    /*
     ** 新增事件
     */
    @Override
    public Map<String,Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> singleEvent = new HashMap<>();
        List<Map<String, Object>> events = new ArrayList<>();

        CreateEvtJtReq createEvtJtReq = (CreateEvtJtReq)object;
        List<OpenEvent> openEventList = createEvtJtReq.getEventDetails();
        for(OpenEvent openEvent : openEventList) {
            singleEvent.put("eventNbr",openEvent.getEventNbr());
            singleEvent.put("eventName",openEvent.getEventName());
            events.add(singleEvent);
            if(openEvent.getActType() != null) {
                if (openEvent.getActType() != null){
                    if (!openEvent.getActType().equals("ADD")) {
                        resultObject.put("events", events);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "新增失败,事件的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                }
            }
            //新增事件
            ContactEvt contactEvt = BeanUtil.create(openEvent, new ContactEvt());
            contactEvt.setContactEvtName(openEvent.getEventName());
            contactEvt.setContactEvtDesc(openEvent.getEventDesc());
            contactEvt.setContactEvtCode(openEvent.getEventNbr());
            contactEvt.setContactEvtTypeId(openEvent.getEvtTypeId());
            contactEvt.setEvtTrigType(openEvent.getEventTrigType());
            contactEvt.setExtEventId(1000L);
            if(openEvent.getInterfaceCfgId() == null) {
                contactEvt.setInterfaceCfgId(0L);
            }
            if(openEvent.getStatusCd() == null) {
                contactEvt.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            }
            if(openEvent.getStatusDate() == null) {
                contactEvt.setStatusDate(new Date());
            }
            if(openEvent.getCreateStaff() == null) {
                contactEvt.setCreateStaff(1L);
            }
            if(openEvent.getCreateDate() == null) {
                contactEvt.setCreateDate(new Date());
            }
            if(openEvent.getUpdateStaff() == null) {
                contactEvt.setUpdateStaff(1L);
            }
            if(openEvent.getUpdateDate() == null) {
                contactEvt.setUpdateDate(new Date());
            }
            contactEvtMapper.createContactEvt(contactEvt);

            //新增事件采集项
            List<OpenEventItem> openEventItems = openEvent.getEventItems();
            if(openEventItems != null && openEventItems.size() > 0) {
                for (OpenEventItem openEventItem : openEventItems) {
                    if (!openEventItem.getActType().equals("ADD")) {
                        resultObject.put("events", events);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "新增失败,事件采集项的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    ContactEvtItem contactEvtItem = BeanUtil.create(openEventItem, new ContactEvtItem());
                    contactEvtItem.setEvtItemId(null);
                    contactEvtItem.setContactEvtId(contactEvt.getContactEvtId());
                    if(openEventItem.getIsNullable() != null) {
                        contactEvtItem.setIsNullable(Long.valueOf(openEventItem.getIsNullable()));
                    }
                    if(contactEvtItem.getStatusCd() == null) {
                        contactEvtItem.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    }
                    contactEvtItem.setRemark(openEventItem.getEvtItemName());
                    contactEvtItem.setIsMainParam("1");
                    contactEvtItem.setIsLabel("0");
                    contactEvtItem.setStatusDate(openEventItem.getCreateDate());
                    contactEvtItem.setUpdateStaff(openEventItem.getCreateStaff());
                    contactEvtItem.setUpdateDate(openEventItem.getCreateDate());
                    contactEvtItemMapper.insert(contactEvtItem);
                }
            }

            //新增事件匹配规则
            List<OpenEventMatchRul> openEventMatchRuls = openEvent.getEventMatchRuls();
            if(openEventMatchRuls != null && openEventMatchRuls.size() > 0) {
                for (OpenEventMatchRul openEventMatchRul : openEventMatchRuls) {
                    if(openEventMatchRul.getActType() != null) {
                        if (!openEventMatchRul.getActType().equals("ADD")) {
                            resultObject.put("events", events);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "新增失败,事件匹配规则的数据操作类型字段的值不是ADD");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                    }
                    EventMatchRulDTO eventMatchRulDTO = BeanUtil.create(openEventMatchRul, new EventMatchRulDTO());
                    eventMatchRulDTO.setEventId(contactEvt.getContactEvtId());
                    eventMatchRulDTO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    eventMatchRulDTO.setStatusDate(openEventMatchRul.getCreateDate());
                    eventMatchRulDTO.setCreateStaff(openEventMatchRul.getCreateStaff());
                    eventMatchRulDTO.setCreateDate(openEventMatchRul.getCreateDate());
                    eventMatchRulDTO.setUpdateStaff(openEventMatchRul.getCreateStaff());
                    eventMatchRulDTO.setUpdateDate(openEventMatchRul.getCreateDate());
                    eventMatchRulMapper.createEventMatchRul(eventMatchRulDTO);
                }
            }else {
                EventMatchRulDTO eventMatchRulDTO = new EventMatchRulDTO();
                eventMatchRulDTO.setEventId(contactEvt.getContactEvtId());
                eventMatchRulDTO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                eventMatchRulDTO.setStatusDate(openEvent.getStatusDate());
                eventMatchRulDTO.setCreateStaff(openEvent.getCreateStaff());
                eventMatchRulDTO.setCreateDate(openEvent.getCreateDate());
                eventMatchRulDTO.setUpdateStaff(openEvent.getUpdateStaff());
                eventMatchRulDTO.setUpdateDate(openEvent.getUpdateDate());
                eventMatchRulMapper.createEventMatchRul(eventMatchRulDTO);
            }

            //新增事件类型
            OpenEventType openEventType = openEvent.getEventType();
            if(openEventType != null) {
                if(!openEventType.getActType().equals("ADD")) {
                    resultObject.put("events",events);
                    resultMap.put("resultCode","1");
                    resultMap.put("resultMsg","新增失败,事件类型的数据操作类型字段的值不是ADD");
                    resultMap.put("resultObject",resultObject);
                    return resultMap;
                }
                ContactEvtType contactEvtType = BeanUtil.create(openEventType, new ContactEvtType());
                contactEvtType.setContactEvtTypeCode(openEventType.getEvtTypeNbr());
                contactEvtType.setContactEvtName(openEventType.getEvtTypeName());
                contactEvtType.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                contactEvtType.setStatusDate(openEventType.getCreateDate());
                contactEvtType.setCreateStaff(openEventType.getCreateStaff());
                contactEvtType.setCreateDate(openEventType.getCreateDate());
                contactEvtType.setUpdateStaff(openEventType.getCreateStaff());
                contactEvtType.setUpdateDate(openEventType.getCreateDate());
                contactEvtTypeMapper.createContactEvtType(contactEvtType);
            }

            //新增事件关系
            List<OpenEventRel> openEventRels = openEvent.getEventRels();
            if(openEventRels != null && openEventRels.size() > 0) {
                for (OpenEventRel openEventRel : openEventRels) {
                    EventRel eventRel = BeanUtil.create(openEventRel, new EventRel());
                    eventRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    eventRel.setStatusDate(openEventRel.getStatusDate());
                    eventRel.setCreateDate(openEventRel.getStatusDate());
                    eventRel.setUpdateDate(openEventRel.getStatusDate());
                    eventRelMapper.insert(eventRel);
                }
            }
        }
        resultObject.put("events",events);
        resultMap.put("resultCode","0");
        resultMap.put("resultMsg","新增成功");
        resultMap.put("resultObject",resultObject);
        return resultMap;
    }

    /*
     ** 修改事件
     */
    @Override
    public Map<String,Object> updateEvent(ModEvtJt modEvtJt) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> singleEvent = new HashMap<>();
        List<Map<String, Object>> events = new ArrayList<>();

        List<OpenEvent> openEventList = modEvtJt.getEventDetails();
        for(OpenEvent openEvent : openEventList) {
            singleEvent.put("eventNbr",openEvent.getEventNbr());
            singleEvent.put("eventName",openEvent.getEventName());
            events.add(singleEvent);
            if(!openEvent.getActType().equals("MOD")) {
                resultObject.put("events",events);
                resultMap.put("resultCode","1");
                resultMap.put("resultMsg","处理失败,事件的数据操作类型字段的值不是MOD");
                resultMap.put("resultObject",resultObject);
                return resultMap;
            }
            ContactEvt event = contactEvtMapper.getEventById(openEvent.getEventId());
            if(event == null) {
                resultObject.put("events",events);
                resultMap.put("resultCode","1");
                resultMap.put("resultMsg","处理失败,对应事件不存在");
                resultMap.put("resultObject",resultObject);
                return resultMap;
            }
            //修改事件
            ContactEvt contactEvt = BeanUtil.create(openEvent, new ContactEvt());
            contactEvt.setContactEvtId(openEvent.getEventId());
            contactEvt.setContactEvtName(openEvent.getEventName());
            contactEvt.setContactEvtDesc(openEvent.getEventDesc());
            contactEvt.setContactEvtCode(openEvent.getEventNbr());
            contactEvt.setContactEvtTypeId(openEvent.getEvtTypeId());
            contactEvt.setEvtTrigType(openEvent.getEventTrigType());
            contactEvt.setExtEventId(1000L);
            contactEvt.setStatusCd(event.getStatusCd());
            contactEvt.setStatusDate(event.getStatusDate());
            contactEvt.setUpdateStaff(openEvent.getCreateStaff());
            contactEvt.setUpdateDate(openEvent.getCreateDate());
            contactEvtMapper.modContactEvtJt(contactEvt);

            //修改事件采集项
            List<OpenEventItem> openEventItems = openEvent.getEventItems();
            if(openEventItems.size() > 0) {
                for (OpenEventItem openEventItem : openEventItems) {
                    EventItem eventItem = BeanUtil.create(openEventItem, new EventItem());
                    eventItem.setContactEvtId(contactEvt.getContactEvtId());
                    eventItem.setIsNullable(Long.valueOf(openEventItem.getIsNullable()));
                    eventItem.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    if (openEventItem.getActType().equals("MOD")) {
                        EventItem eventItemSingle = contactEvtItemMapper.viewEventItem(openEventItem.getEvtItemId());
                        if (eventItemSingle == null) {
                            resultObject.put("events", events);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应事件采集项不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        eventItem.setStatusCd(openEventItem.getStatusCd());
                        eventItem.setStatusDate(openEventItem.getStatusDate());
                        eventItem.setUpdateStaff(openEventItem.getCreateStaff());
                        eventItem.setUpdateDate(openEventItem.getCreateDate());
                        contactEvtItemMapper.modEventItem(eventItem);
                    }else if(openEventItem.getActType().equals("ADD")) {
                        eventItem.setStatusDate(openEventItem.getCreateDate());
                        eventItem.setCreateStaff(openEventItem.getCreateStaff());
                        eventItem.setCreateDate(openEventItem.getCreateDate());
                        eventItem.setUpdateStaff(openEventItem.getCreateStaff());
                        eventItem.setUpdateDate(openEventItem.getCreateDate());
                        contactEvtItemMapper.insertContactEvtItem(eventItem);
                    }else if(openEventItem.getActType().equals("DEL")) {
                        contactEvtItemMapper.deleteByPrimaryKey(openEventItem.getEvtItemId());
                    }
                }
            }

            //修改事件匹配规则
            List<OpenEventMatchRul> openEventMatchRuls = openEvent.getEventMatchRuls();
            if(openEventMatchRuls.size() > 0) {
                for (OpenEventMatchRul openEventMatchRul : openEventMatchRuls) {
                    EventMatchRulDTO eventMatchRulDTO = BeanUtil.create(openEventMatchRul, new EventMatchRulDTO());
                    eventMatchRulDTO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    if (openEventMatchRul.getActType().equals("MOD")) {
                        EventMatchRulDO eventMatchRul = contactEvtMatchRulMapper.selectByPrimaryKey(openEventMatchRul.getEvtMatchRulId());
                        if (eventMatchRul == null) {
                            resultObject.put("events", events);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应事件匹配规则不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        eventMatchRulDTO.setStatusCd(openEventMatchRul.getStatusCd());
                        eventMatchRulDTO.setStatusDate(openEventMatchRul.getStatusDate());
                        eventMatchRulDTO.setUpdateStaff(openEventMatchRul.getCreateStaff());
                        eventMatchRulDTO.setUpdateDate(openEventMatchRul.getCreateDate());
                        eventMatchRulMapper.modEventMatchRul(eventMatchRulDTO);
                    } else if (openEventMatchRul.getActType().equals("ADD")) {
                        eventMatchRulDTO.setStatusDate(openEventMatchRul.getCreateDate());
                        eventMatchRulDTO.setCreateStaff(openEventMatchRul.getCreateStaff());
                        eventMatchRulDTO.setCreateDate(openEventMatchRul.getCreateDate());
                        eventMatchRulDTO.setUpdateStaff(openEventMatchRul.getCreateStaff());
                        eventMatchRulDTO.setUpdateDate(openEventMatchRul.getCreateDate());
                        eventMatchRulMapper.createEventMatchRul(eventMatchRulDTO);
                    } else if (openEventMatchRul.getActType().equals("DEL")) {
                        eventMatchRulMapper.delEventMatchRul(eventMatchRulDTO);
                    }
                }
            }

            //修改事件类型
            OpenEventType openEventType = openEvent.getEventType();
            if(openEventType != null) {
                ContactEvtType contactEvtType = BeanUtil.create(openEventType, new ContactEvtType());
                contactEvtType.setContactEvtTypeCode(openEventType.getEvtTypeNbr());
                contactEvtType.setContactEvtName(openEventType.getEvtTypeName());
                contactEvtType.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                if(openEventType.getActType().equals("MOD")) {
                    ContactEvtType eventType = contactEvtTypeMapper.selectByPrimaryKey(openEventType.getEvtTypeId());
                    if (eventType == null) {
                        resultObject.put("events", events);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,对应事件类型不存在");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    contactEvtType.setStatusCd(openEventType.getStatusCd());
                    contactEvtType.setStatusDate(openEventType.getStatusDate());
                    contactEvtType.setUpdateStaff(openEventType.getCreateStaff());
                    contactEvtType.setUpdateDate(openEventType.getCreateDate());
                    contactEvtTypeMapper.modContactEvtType(contactEvtType);
                }else if(openEventType.getActType().equals("ADD")) {
                    contactEvtType.setStatusDate(openEventType.getCreateDate());
                    contactEvtType.setCreateStaff(openEventType.getCreateStaff());
                    contactEvtType.setCreateDate(openEventType.getCreateDate());
                    contactEvtType.setUpdateStaff(openEventType.getCreateStaff());
                    contactEvtType.setUpdateDate(openEventType.getCreateDate());
                    contactEvtTypeMapper.createContactEvtType(contactEvtType);
                }else if(openEventType.getActType().equals("DEL")) {
                    contactEvtTypeMapper.deleteByPrimaryKey(contactEvtType.getEvtTypeId());
                }
            }

            //新增事件关系
            List<OpenEventRel> openEventRels = openEvent.getEventRels();
            if(openEventRels.size() > 0) {
                for (OpenEventRel openEventRel : openEventRels) {
                    EventRel eventRelSingle = eventRelMapper.selectByPrimaryKey(openEventRel.getComplexEvtRelaId());
                    if (eventRelSingle == null) {
                        resultObject.put("events", events);
                        resultMap.put("resultCode", "");
                        resultMap.put("resultMsg", "处理失败,对应事件关系不存在");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    EventRel eventRel = BeanUtil.create(openEventRel, new EventRel());
                    eventRel.setStatusCd(openEventRel.getStatusCd());
                    eventRel.setStatusDate(openEventRel.getStatusDate());
                    eventRel.setCreateDate(eventRelSingle.getCreateDate());
                    eventRel.setUpdateDate(openEventRel.getStatusDate());
                    eventRelMapper.updateByPrimaryKey(eventRel);
                }
            }
        }
        resultObject.put("events",events);
        resultMap.put("resultCode","0");
        resultMap.put("resultMsg","处理成功");
        resultMap.put("resultObject",resultObject);
        return resultMap;
    }

    /*
     ** 根据事件id主键查询
     */
    @Override
    public Map<String,Object> queryById(String id) {
        return null;
    }

    /*
     ** 删除事件
     */
    @Override
    public Map<String,Object> deleteById(String id) {
        return null;
    }

    /*
     ** 查询事件列表
     */
    @Override
    public Map<String,Object> queryListByMap(Map<String, Object> map) {
        return null;
    }

    @Override
    public Map<String,Object> updateByParams(String id,Object object) {
        return null;
    }
}
