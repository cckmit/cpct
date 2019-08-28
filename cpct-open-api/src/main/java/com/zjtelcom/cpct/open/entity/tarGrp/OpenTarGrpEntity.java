package com.zjtelcom.cpct.open.entity.tarGrp;

import java.util.Date;
import java.util.List;

public class OpenTarGrpEntity {

    private String actType;
    private Long tarGrpId;
    private String tarGrpName;
    private String tarGrpDesc;
    private String tarGrpType;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private List<OpenTarGrpConditionEntity> tarGrpConditions;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public String getTarGrpName() {
        return tarGrpName;
    }

    public void setTarGrpName(String tarGrpName) {
        this.tarGrpName = tarGrpName;
    }

    public String getTarGrpDesc() {
        return tarGrpDesc;
    }

    public void setTarGrpDesc(String tarGrpDesc) {
        this.tarGrpDesc = tarGrpDesc;
    }

    public String getTarGrpType() {
        return tarGrpType;
    }

    public void setTarGrpType(String tarGrpType) {
        this.tarGrpType = tarGrpType;
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

    public List<OpenTarGrpConditionEntity> getTarGrpConditions() {
        return tarGrpConditions;
    }

    public void setTarGrpConditions(List<OpenTarGrpConditionEntity> tarGrpConditions) {
        this.tarGrpConditions = tarGrpConditions;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
