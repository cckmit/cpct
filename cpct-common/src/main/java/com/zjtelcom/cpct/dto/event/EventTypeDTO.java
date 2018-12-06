package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.event.EventTypeDO;

import java.util.List;

public class EventTypeDTO extends EventTypeDO {

    private Long evtTypeId;//事件类型标识，主键标识
    private String contactEvtTypeCode;//记录事件类型的编码
    private String contactEvtName;//记录事件类型的名称
    private Long parEvtTypeId;//记录父级的事件类型标识
    private String evtTypeDesc;//事件类型描述
    private List<EventTypeDTO> children;//子级

    @Override
    public Long getEvtTypeId() {
        return evtTypeId;
    }

    @Override
    public void setEvtTypeId(Long evtTypeId) {
        this.evtTypeId = evtTypeId;
    }

    @Override
    public String getContactEvtTypeCode() {
        return contactEvtTypeCode;
    }

    @Override
    public void setContactEvtTypeCode(String contactEvtTypeCode) {
        this.contactEvtTypeCode = contactEvtTypeCode;
    }

    @Override
    public String getContactEvtName() {
        return contactEvtName;
    }

    @Override
    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
    }

    @Override
    public Long getParEvtTypeId() {
        return parEvtTypeId;
    }

    @Override
    public void setParEvtTypeId(Long parEvtTypeId) {
        this.parEvtTypeId = parEvtTypeId;
    }

    @Override
    public String getEvtTypeDesc() {
        return evtTypeDesc;
    }

    @Override
    public void setEvtTypeDesc(String evtTypeDesc) {
        this.evtTypeDesc = evtTypeDesc;
    }

    public List<EventTypeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<EventTypeDTO> children) {
        this.children = children;
    }
}

