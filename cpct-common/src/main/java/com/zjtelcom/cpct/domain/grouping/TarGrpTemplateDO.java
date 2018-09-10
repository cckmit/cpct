package com.zjtelcom.cpct.domain.grouping;

import java.util.Date;

public class TarGrpTemplateDO {
    private Long tarGrpTemplateId;

    private String tarGrpTemplateName;

    private String tarGrpTemplateDesc;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    public Long getTarGrpTemplateId() {
        return tarGrpTemplateId;
    }

    public void setTarGrpTemplateId(Long tarGrpTemplateId) {
        this.tarGrpTemplateId = tarGrpTemplateId;
    }

    public String getTarGrpTemplateName() {
        return tarGrpTemplateName;
    }

    public void setTarGrpTemplateName(String tarGrpTemplateName) {
        this.tarGrpTemplateName = tarGrpTemplateName;
    }

    public String getTarGrpTemplateDesc() {
        return tarGrpTemplateDesc;
    }

    public void setTarGrpTemplateDesc(String tarGrpTemplateDesc) {
        this.tarGrpTemplateDesc = tarGrpTemplateDesc;
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
}