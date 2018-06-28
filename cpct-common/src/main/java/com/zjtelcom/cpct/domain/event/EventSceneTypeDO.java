package com.zjtelcom.cpct.domain.event;


import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;

public class EventSceneTypeDO extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -1161582235420131662L;

    private Long evtSceneTypeId;//事件场景目录标识
    private String evtSceneTypeNbr;//事件场景目录编码
    private String evtSceneTypeName;//事件场景目录名称
    private Long parEvtSceneTypeId;//父级事件场景目录标识
    private String evtSceneTypeDesc;//事件场景目录描述

    public Long getEvtSceneTypeId() {
        return evtSceneTypeId;
    }

    public void setEvtSceneTypeId(Long evtSceneTypeId) {
        this.evtSceneTypeId = evtSceneTypeId;
    }

    public String getEvtSceneTypeNbr() {
        return evtSceneTypeNbr;
    }

    public void setEvtSceneTypeNbr(String evtSceneTypeNbr) {
        this.evtSceneTypeNbr = evtSceneTypeNbr;
    }

    public String getEvtSceneTypeName() {
        return evtSceneTypeName;
    }

    public void setEvtSceneTypeName(String evtSceneTypeName) {
        this.evtSceneTypeName = evtSceneTypeName;
    }

    public Long getParEvtSceneTypeId() {
        return parEvtSceneTypeId;
    }

    public void setParEvtSceneTypeId(Long parEvtSceneTypeId) {
        this.parEvtSceneTypeId = parEvtSceneTypeId;
    }

    public String getEvtSceneTypeDesc() {
        return evtSceneTypeDesc;
    }

    public void setEvtSceneTypeDesc(String evtSceneTypeDesc) {
        this.evtSceneTypeDesc = evtSceneTypeDesc;
    }

}