package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;

public class EventSorceDO extends BaseEntity {

    private Long evtSrcId;//事件源标识
    private String evtSrcCode;//事件源编码
    private String evtSrcName;//事件源名称
    private String evtSrcDesc;//事件源描述
    private Long regionId;//记录适用区域标识，指定公共管理区域

    public Long getEvtSrcId() {
        return evtSrcId;
    }

    public void setEvtSrcId(Long evtSrcId) {
        this.evtSrcId = evtSrcId;
    }

    public String getEvtSrcCode() {
        return evtSrcCode;
    }

    public void setEvtSrcCode(String evtSrcCode) {
        this.evtSrcCode = evtSrcCode;
    }

    public String getEvtSrcName() {
        return evtSrcName;
    }

    public void setEvtSrcName(String evtSrcName) {
        this.evtSrcName = evtSrcName;
    }

    public String getEvtSrcDesc() {
        return evtSrcDesc;
    }

    public void setEvtSrcDesc(String evtSrcDesc) {
        this.evtSrcDesc = evtSrcDesc;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}
