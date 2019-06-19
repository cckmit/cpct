package com.zjtelcom.cpct.open.entity.event;

import java.util.Date;

public class OpenInterfaceCfg {

    private String actType;
    private Long interfaceCfgId;
    private Long evtSrcId;
    private String interfaceName;
    private String interfaceDesc;
    private String interfaceNbr;
    private String interfaceType;
    private String provider;
    private String caller;
    private String protocolType;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    //接口配置参数
    private OpenInterfaceCfgParam interfaceCfgParams;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getInterfaceCfgId() {
        return interfaceCfgId;
    }

    public void setInterfaceCfgId(Long interfaceCfgId) {
        this.interfaceCfgId = interfaceCfgId;
    }

    public Long getEvtSrcId() {
        return evtSrcId;
    }

    public void setEvtSrcId(Long evtSrcId) {
        this.evtSrcId = evtSrcId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceDesc() {
        return interfaceDesc;
    }

    public void setInterfaceDesc(String interfaceDesc) {
        this.interfaceDesc = interfaceDesc;
    }

    public String getInterfaceNbr() {
        return interfaceNbr;
    }

    public void setInterfaceNbr(String interfaceNbr) {
        this.interfaceNbr = interfaceNbr;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
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

    public OpenInterfaceCfgParam getInterfaceCfgParams() {
        return interfaceCfgParams;
    }

    public void setInterfaceCfgParams(OpenInterfaceCfgParam interfaceCfgParams) {
        this.interfaceCfgParams = interfaceCfgParams;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
