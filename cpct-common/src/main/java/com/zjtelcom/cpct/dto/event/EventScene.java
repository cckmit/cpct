package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description 事件场景
 * @Author pengy
 * @Date 2018/6/26 15:49
 */
public class EventScene extends BaseEntity{

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long eventSceneId;  //事件场景标识
    private String eventSceneNbr;//事件场景编码
    private String eventSceneName;//事件场景名称
    private String eventSceneDesc;//事件场景描述
    private Long eventId;//事件标识
    private Long extEventSceneId;//外部事件场景标识
    private String contactEvtCode;//事件编码

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getEventSceneId() {
        return eventSceneId;
    }

    public void setEventSceneId(Long eventSceneId) {
        this.eventSceneId = eventSceneId;
    }

    public String getEventSceneNbr() {
        return eventSceneNbr;
    }

    public void setEventSceneNbr(String eventSceneNbr) {
        this.eventSceneNbr = eventSceneNbr;
    }

    public String getEventSceneName() {
        return eventSceneName;
    }

    public void setEventSceneName(String eventSceneName) {
        this.eventSceneName = eventSceneName;
    }

    public String getEventSceneDesc() {
        return eventSceneDesc;
    }

    public void setEventSceneDesc(String eventSceneDesc) {
        this.eventSceneDesc = eventSceneDesc;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getExtEventSceneId() {
        return extEventSceneId;
    }

    public void setExtEventSceneId(Long extEventSceneId) {
        this.extEventSceneId = extEventSceneId;
    }

    public String getContactEvtCode() {
        return contactEvtCode;
    }

    public void setContactEvtCode(String contactEvtCode) {
        this.contactEvtCode = contactEvtCode;
    }
}
