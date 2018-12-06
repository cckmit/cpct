package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;

public class ContactEvtType extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -8725283412954441694L;
    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long evtTypeId;//事件类型标识，主键标识
    private String contactEvtTypeCode;//记录事件类型的编码
    private String contactEvtName;//记录事件类型的名称
    private Long parEvtTypeId;//记录父级的事件类型标识
    private String evtTypeDesc;//事件类型描述

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getEvtTypeId() {
        return evtTypeId;
    }

    public void setEvtTypeId(Long evtTypeId) {
        this.evtTypeId = evtTypeId;
    }

    public String getContactEvtTypeCode() {
        return contactEvtTypeCode;
    }

    public void setContactEvtTypeCode(String contactEvtTypeCode) {
        this.contactEvtTypeCode = contactEvtTypeCode;
    }

    public String getContactEvtName() {
        return contactEvtName;
    }

    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
    }

    public Long getParEvtTypeId() {
        return parEvtTypeId;
    }

    public void setParEvtTypeId(Long parEvtTypeId) {
        this.parEvtTypeId = parEvtTypeId;
    }

    public String getEvtTypeDesc() {
        return evtTypeDesc;
    }

    public void setEvtTypeDesc(String evtTypeDesc) {
        this.evtTypeDesc = evtTypeDesc;
    }
}

