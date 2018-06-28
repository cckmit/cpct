package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;

public class EventSceneType extends BaseEntity{

    private Long evtSceneTypeId;
    private String evtSceneTypeNbr;
    private String evtSceneTypeName;
    private Long parEvtSceneTypeId;
    private String evtSceneTypeDesc;

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