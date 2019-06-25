package com.zjtelcom.cpct.open.entity.event;

import java.util.Date;
import java.util.List;

public class OpenEvent {

    //通用数据操作类型, KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private String actType;
    private Long eventId;
    private Long interfaceCfgId;
    private String eventNbr;
    private String eventName;
    private String evtMappedAddr;
    private String evtMappedIp;
    private String evtProcotolType;
    private String evtMappedFunName;
    private String eventDesc;
    //事件类型标识
    private Long evtTypeId;
    //事件触发类型
    private String eventTrigType;
    //外部事件标识
    private Long extEventId;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    //事件类型
    private OpenEventType eventType;
    //事件关系
    private List<OpenEventRel> eventRels;
    //事件采集项
    private List<OpenEventItem> eventItems;
    //事件匹配规则
    private List<OpenEventMatchRul> eventMatchRuls;
    //事件源接口配置
    private OpenInterfaceCfg interfaceCfg;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getInterfaceCfgId() {
        return interfaceCfgId;
    }

    public void setInterfaceCfgId(Long interfaceCfgId) {
        this.interfaceCfgId = interfaceCfgId;
    }

    public String getEventNbr() {
        return eventNbr;
    }

    public void setEventNbr(String eventNbr) {
        this.eventNbr = eventNbr;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEvtMappedAddr() {
        return evtMappedAddr;
    }

    public void setEvtMappedAddr(String evtMappedAddr) {
        this.evtMappedAddr = evtMappedAddr;
    }

    public String getEvtMappedIp() {
        return evtMappedIp;
    }

    public void setEvtMappedIp(String evtMappedIp) {
        this.evtMappedIp = evtMappedIp;
    }

    public String getEvtProcotolType() {
        return evtProcotolType;
    }

    public void setEvtProcotolType(String evtProcotolType) {
        this.evtProcotolType = evtProcotolType;
    }

    public String getEvtMappedFunName() {
        return evtMappedFunName;
    }

    public void setEvtMappedFunName(String evtMappedFunName) {
        this.evtMappedFunName = evtMappedFunName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Long getEvtTypeId() {
        return evtTypeId;
    }

    public void setEvtTypeId(Long evtTypeId) {
        this.evtTypeId = evtTypeId;
    }

    public String getEventTrigType() {
        return eventTrigType;
    }

    public void setEventTrigType(String eventTrigType) {
        this.eventTrigType = eventTrigType;
    }

    public Long getExtEventId() {
        return extEventId;
    }

    public void setExtEventId(Long extEventId) {
        this.extEventId = extEventId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public OpenEventType getEventType() {
        return eventType;
    }

    public void setEventType(OpenEventType eventType) {
        this.eventType = eventType;
    }

    public List<OpenEventRel> getEventRels() {
        return eventRels;
    }

    public void setEventRels(List<OpenEventRel> eventRels) {
        this.eventRels = eventRels;
    }

    public List<OpenEventItem> getEventItems() {
        return eventItems;
    }

    public void setEventItems(List<OpenEventItem> eventItems) {
        this.eventItems = eventItems;
    }

    public List<OpenEventMatchRul> getEventMatchRuls() {
        return eventMatchRuls;
    }

    public void setEventMatchRuls(List<OpenEventMatchRul> eventMatchRuls) {
        this.eventMatchRuls = eventMatchRuls;
    }

    public OpenInterfaceCfg getInterfaceCfg() {
        return interfaceCfg;
    }

    public void setInterfaceCfg(OpenInterfaceCfg interfaceCfg) {
        this.interfaceCfg = interfaceCfg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
