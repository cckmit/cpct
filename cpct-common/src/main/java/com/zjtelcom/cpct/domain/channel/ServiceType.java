package com.zjtelcom.cpct.domain.channel;

import java.io.Serializable;
import java.util.Date;

public class ServiceType implements Serializable {
    private Long serviceTypeId;

    private String serviceTypeNbr;

    private String serviceTypeName;

    private Long parServiceTypeId;

    private Long extServiceTypeId;

    private String serviceTypeDesc;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeNbr() {
        return serviceTypeNbr;
    }

    public void setServiceTypeNbr(String serviceTypeNbr) {
        this.serviceTypeNbr = serviceTypeNbr;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public Long getParServiceTypeId() {
        return parServiceTypeId;
    }

    public void setParServiceTypeId(Long parServiceTypeId) {
        this.parServiceTypeId = parServiceTypeId;
    }

    public Long getExtServiceTypeId() {
        return extServiceTypeId;
    }

    public void setExtServiceTypeId(Long extServiceTypeId) {
        this.extServiceTypeId = extServiceTypeId;
    }

    public String getServiceTypeDesc() {
        return serviceTypeDesc;
    }

    public void setServiceTypeDesc(String serviceTypeDesc) {
        this.serviceTypeDesc = serviceTypeDesc;
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
}