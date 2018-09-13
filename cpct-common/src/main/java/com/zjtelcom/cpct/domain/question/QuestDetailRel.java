package com.zjtelcom.cpct.domain.question;

import java.util.Date;

public class QuestDetailRel {
    private Long relConfId;

    private Long aNaireId;

    private Long aQuestionId;

    private Long aQstDetailId;

    private String rstrType;

    private Long zNaireId;

    private Long zQuestionId;

    private Long zQstDetailId;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    public Long getRelConfId() {
        return relConfId;
    }

    public void setRelConfId(Long relConfId) {
        this.relConfId = relConfId;
    }

    public Long getaNaireId() {
        return aNaireId;
    }

    public void setaNaireId(Long aNaireId) {
        this.aNaireId = aNaireId;
    }

    public Long getaQuestionId() {
        return aQuestionId;
    }

    public void setaQuestionId(Long aQuestionId) {
        this.aQuestionId = aQuestionId;
    }

    public Long getaQstDetailId() {
        return aQstDetailId;
    }

    public void setaQstDetailId(Long aQstDetailId) {
        this.aQstDetailId = aQstDetailId;
    }

    public String getRstrType() {
        return rstrType;
    }

    public void setRstrType(String rstrType) {
        this.rstrType = rstrType;
    }

    public Long getzNaireId() {
        return zNaireId;
    }

    public void setzNaireId(Long zNaireId) {
        this.zNaireId = zNaireId;
    }

    public Long getzQuestionId() {
        return zQuestionId;
    }

    public void setzQuestionId(Long zQuestionId) {
        this.zQuestionId = zQuestionId;
    }

    public Long getzQstDetailId() {
        return zQstDetailId;
    }

    public void setzQstDetailId(Long zQstDetailId) {
        this.zQstDetailId = zQstDetailId;
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