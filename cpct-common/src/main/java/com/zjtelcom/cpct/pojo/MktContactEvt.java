package com.zjtelcom.cpct.pojo;


import java.io.Serializable;
import java.util.Date;


public class MktContactEvt implements Serializable {
    private Long contactEvtId;

    private Long interfaceCfgId;

    private String contactEvtCode;

    private String contactEvtName;

    private String evtMappedAddr;

    private String evtMappedIp;

    private String evtProcotolType;

    private String evtMappedFunName;

    private String contactEvtDesc;

    private Long contactChlId;

    private Long contactEvtTypeId;

    private String evtTrigType;

    private String coopType;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    private String actType;

    public Long getContactEvtId() {
        return contactEvtId;
    }

    public void setContactEvtId(Long contactEvtId) {
        this.contactEvtId = contactEvtId;
    }

    public Long getInterfaceCfgId() {
        return interfaceCfgId;
    }

    public void setInterfaceCfgId(Long interfaceCfgId) {
        this.interfaceCfgId = interfaceCfgId;
    }

    public String getContactEvtCode() {
        return contactEvtCode;
    }

    public void setContactEvtCode(String contactEvtCode) {
        this.contactEvtCode = contactEvtCode;
    }

    public String getContactEvtName() {
        return contactEvtName;
    }

    public void setContactEvtName(String contactEvtName) {
        this.contactEvtName = contactEvtName;
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

    public String getContactEvtDesc() {
        return contactEvtDesc;
    }

    public void setContactEvtDesc(String contactEvtDesc) {
        this.contactEvtDesc = contactEvtDesc;
    }

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public Long getContactEvtTypeId() {
        return contactEvtTypeId;
    }

    public void setContactEvtTypeId(Long contactEvtTypeId) {
        this.contactEvtTypeId = contactEvtTypeId;
    }

    public String getEvtTrigType() {
        return evtTrigType;
    }

    public void setEvtTrigType(String evtTrigType) {
        this.evtTrigType = evtTrigType;
    }

    public String getCoopType() {
        return coopType;
    }

    public void setCoopType(String coopType) {
        this.coopType = coopType;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }
}