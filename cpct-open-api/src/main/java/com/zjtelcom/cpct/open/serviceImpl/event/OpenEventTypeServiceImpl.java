package com.zjtelcom.cpct.open.serviceImpl.event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtTypeMapper;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.common.FormatBeanUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.event.EventType;
import com.zjtelcom.cpct.open.service.event.OpenEventTypeService;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OpenEventTypeServiceImpl extends BaseService implements OpenEventTypeService{

    @Autowired
    private ContactEvtTypeMapper contactEvtTypeMapper;

    /*
    **根据事件类型id主键查询
    */
    @Override
    public Map<String, Object> getEventType(Long evtTypeId) {
        Map<String, Object> eventTypeMap = new HashMap<>();
        ContactEvtType contactEvtType = contactEvtTypeMapper.selectByPrimaryKey(evtTypeId);
        if(null != contactEvtType) {
            EventType eventType = BeanUtil.create(contactEvtType, new EventType());
            eventType.setId(String.valueOf(evtTypeId));
            eventType.setHref("/eventType/" + String.valueOf(evtTypeId));
            eventType.setEvtTypeNbr(contactEvtType.getContactEvtTypeCode());
            eventType.setEvtTypeName(contactEvtType.getContactEvtName());
            if(contactEvtType.getStatusDate() != null) {
                eventType.setStatusDate(DateUtil.getDatetime(contactEvtType.getStatusDate()));
            }
            eventTypeMap.put("params",eventType);
            return eventTypeMap;
        }else {
            throw new SystemException("事件类型id为" + evtTypeId + " 所对应的事件类型不存在!");
        }

    }

    /*
    **新增事件类型
    */
    @Override
    public Map<String, Object> saveEventType(EventType eventType) {
        Map<String, Object> eventTypeMap = new HashMap<>();
        ContactEvtType contactEvtType = BeanUtil.create(eventType, new ContactEvtType());

        contactEvtType.setContactEvtTypeCode(eventType.getEvtTypeNbr());
        contactEvtType.setContactEvtName(eventType.getEvtTypeName());

        contactEvtType.setCreateDate(DateUtil.getCurrentTime());
        contactEvtType.setUpdateDate(DateUtil.getCurrentTime());
        contactEvtType.setStatusDate(DateUtil.getCurrentTime());
        contactEvtType.setUpdateStaff(UserUtil.loginId());
        contactEvtType.setCreateStaff(UserUtil.loginId());
        contactEvtType.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);

        contactEvtTypeMapper.createContactEvtType(contactEvtType);

        QryContactEvtTypeReq qryContactEvtTypeReq = new QryContactEvtTypeReq();
        qryContactEvtTypeReq.setContactEvtTypeCode(eventType.getEvtTypeNbr());
        List<ContactEvtType> contactEvtTypes = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);

        eventTypeMap = getEventType(contactEvtTypes.get(0).getEvtTypeId());
        return eventTypeMap;
    }

    /*
    **修改事件类型
    */
    @Override
    public Map<String, Object> updateEventType(String evtTypeId, String params) {
        Map<String, Object> eventTypeMap = new HashMap<>();
        ContactEvtType contactEvtType = contactEvtTypeMapper.selectByPrimaryKey(Long.valueOf(evtTypeId));
        if (null == contactEvtType) {
            throw new SystemException("对应事件类型不存在");
        }
        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(contactEvtType));
        JSONArray array = (JSONArray) JSONArray.parse(params);
        for (int i = 0; i <array.size() ; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            if(((String)jsonObject.get("op")).equals("replace")) {
                String path = ((String) jsonObject.get("path")).substring(1);
                if (path.equals("evtTypeName")) {
                    json.put("contactEvtName", jsonObject.getString("value"));
                } else if (path.equals("evtTypeNbr")) {
                    json.put("contactEvtTypeCode", jsonObject.getString("value"));
                } else {
                    json.put(path, jsonObject.getString("value"));
                }
            }
        }
        ContactEvtType eventType = JSONObject.parseObject(json.toJSONString(), ContactEvtType.class);
        contactEvtTypeMapper.modContactEvtType(eventType);

        //查找
        eventTypeMap = getEventType(Long.valueOf(evtTypeId));
        return eventTypeMap;
    }


    /*
    **删除事件类型
    */
    @Override
    public Map<String, Object> deleteEventType(Long evtTypeId) {
        Map<String, Object> eventTypeMap = new HashMap<>();
        int i = contactEvtTypeMapper.deleteByPrimaryKey(evtTypeId);
        if(i == 0){
            throw new SystemException("对应事件类型不存在");
        }
        return eventTypeMap;
    }

    /*
    **查询事件类型列表
    */
    @Override
    public Map<String, Object> listEventPageType(Map<String, Object> params) {
        Map<String, Object> eventTypeMap = new HashMap<>();
        CommonUtil.setPage(params);

        QryContactEvtTypeReq qryContactEvtTypeReq = new QryContactEvtTypeReq();
        if(StringUtils.isNotBlank((String) params.get("evtTypeId"))){
            qryContactEvtTypeReq.setEvtTypeId(Long.valueOf((String)params.get("evtTypeId")));
        }
        if(StringUtils.isNotBlank((String) params.get("eventTypeCode"))){
            qryContactEvtTypeReq.setContactEvtTypeCode((String)params.get("eventTypeCode"));
        }
        if(StringUtils.isNotBlank((String) params.get("contactEvtName"))){
            qryContactEvtTypeReq.setContactEvtName((String)params.get("contactEvtName"));
        }
        if(StringUtils.isNotBlank((String) params.get("parEvtTypeId"))){
            qryContactEvtTypeReq.setParEvtTypeId(Long.valueOf((String)params.get("parEvtTypeId")));
        }
        if(StringUtils.isNotBlank((String) params.get("statusCd"))){
            qryContactEvtTypeReq.setStatusCd((String)params.get("statusCd"));
        }
        List<ContactEvtType> contactEvtTypes = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);

        if(contactEvtTypes ==null){
            throw new SystemException("对应事件类型不存在");
        }

        List<EventType> list = new ArrayList<>();
        for(ContactEvtType contactEvtType : contactEvtTypes) {
            EventType eventType = BeanUtil.create(contactEvtType, new EventType());
            eventType.setId(String.valueOf(contactEvtType.getEvtTypeId()));
            eventType.setHref("/eventType/" + String.valueOf(contactEvtType.getEvtTypeId()));
            eventType.setEvtTypeNbr(contactEvtType.getContactEvtTypeCode());
            eventType.setEvtTypeName(contactEvtType.getContactEvtName());
            if(contactEvtType.getStatusDate() != null) {
                eventType.setStatusDate(DateUtil.getDatetime(contactEvtType.getStatusDate()));
            }
            list.add(eventType);
        }
        Page pageInfo = new Page(new PageInfo(contactEvtTypes));
        eventTypeMap.put("params", list);
        eventTypeMap.put("size", String.valueOf(pageInfo.getTotal()));
        return eventTypeMap;
    }
}
