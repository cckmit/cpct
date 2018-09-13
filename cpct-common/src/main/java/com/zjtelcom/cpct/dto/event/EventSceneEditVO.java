package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class EventSceneEditVO implements Serializable {

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long eventSceneId;  //事件场景标识
    private String eventSceneName;//事件场景名称
    private String eventSceneDesc;//事件场景描述
    private Long eventSceneTypeId;//事件场景目录id


    public Long getEventSceneTypeId() {
        return eventSceneTypeId;
    }

    public void setEventSceneTypeId(Long eventSceneTypeId) {
        this.eventSceneTypeId = eventSceneTypeId;
    }

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

}
