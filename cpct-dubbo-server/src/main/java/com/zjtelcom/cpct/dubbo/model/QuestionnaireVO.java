package com.zjtelcom.cpct.dubbo.model;

import java.util.Date;

public class QuestionnaireVO {
    private Long naireId;

    private String naireName;

    private String naireType;//1000	营销问卷;2000	维挽问卷

    private String markType;

    private Integer nairePoints;

    private String naireDesc;

    private String startTip;

    private String endTip;

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    public Long getNaireId() {
        return naireId;
    }

    public void setNaireId(Long naireId) {
        this.naireId = naireId;
    }

    public String getNaireName() {
        return naireName;
    }

    public void setNaireName(String naireName) {
        this.naireName = naireName;
    }

    public String getNaireType() {
        return naireType;
    }

    public void setNaireType(String naireType) {
        this.naireType = naireType;
    }

    public String getMarkType() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }

    public Integer getNairePoints() {
        return nairePoints;
    }

    public void setNairePoints(Integer nairePoints) {
        this.nairePoints = nairePoints;
    }

    public String getNaireDesc() {
        return naireDesc;
    }

    public void setNaireDesc(String naireDesc) {
        this.naireDesc = naireDesc;
    }

    public String getStartTip() {
        return startTip;
    }

    public void setStartTip(String startTip) {
        this.startTip = startTip;
    }

    public String getEndTip() {
        return endTip;
    }

    public void setEndTip(String endTip) {
        this.endTip = endTip;
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