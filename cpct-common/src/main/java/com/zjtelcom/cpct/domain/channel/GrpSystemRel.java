package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class GrpSystemRel {
    private Long grpSystemRelId;

    private String systemInfoId;

    private String statusCd;

    private Date statusDt;

    private Date startDt;

    private Date endDt;

    private String version;

    private Long offerVrulGrpId;

    private Date createDt;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

    public Long getGrpSystemRelId() {
        return grpSystemRelId;
    }

    public void setGrpSystemRelId(Long grpSystemRelId) {
        this.grpSystemRelId = grpSystemRelId;
    }

    public String getSystemInfoId() {
        return systemInfoId;
    }

    public void setSystemInfoId(String systemInfoId) {
        this.systemInfoId = systemInfoId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getStatusDt() {
        return statusDt;
    }

    public void setStatusDt(Date statusDt) {
        this.statusDt = statusDt;
    }

    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getOfferVrulGrpId() {
        return offerVrulGrpId;
    }

    public void setOfferVrulGrpId(Long offerVrulGrpId) {
        this.offerVrulGrpId = offerVrulGrpId;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }
}