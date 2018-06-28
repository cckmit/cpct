package com.zjtelcom.cpct.dto.event;


import com.zjtelcom.cpct.domain.event.EventSceneTypeDO;

import java.util.List;


public class EventSceneTypeDTO extends EventSceneTypeDO {

    private static final long serialVersionUID = 1137091414159855130L;

    private Long evtSceneTypeId;//事件场景目录标识
    private String evtSceneTypeNbr;//事件场景目录编码
    private String evtSceneTypeName;//事件场景目录名称
    private Long parEvtSceneTypeId;//父级事件场景目录标识
    private String evtSceneTypeDesc;//事件场景目录描述
    private List<EventSceneTypeDTO> eventSceneTypeDTOList;//子级

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

    public List<EventSceneTypeDTO> getEventSceneTypeDTOList() {
        return eventSceneTypeDTOList;
    }

    public void setEventSceneTypeDTOList(List<EventSceneTypeDTO> eventSceneTypeDTOList) {
        this.eventSceneTypeDTOList = eventSceneTypeDTOList;
    }
}