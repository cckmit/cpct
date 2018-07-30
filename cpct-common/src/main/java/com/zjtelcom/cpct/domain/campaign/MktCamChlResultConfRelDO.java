package com.zjtelcom.cpct.domain.campaign;

import java.util.Date;

public class MktCamChlResultConfRelDO {
    private Long mktCamChlResultConfRelId;

    private Long mktCamChlResultId;

    private Long evtContactConfId;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    public Long getMktCamChlResultConfRelId() {
        return mktCamChlResultConfRelId;
    }

    public void setMktCamChlResultConfRelId(Long mktCamChlResultConfRelId) {
        this.mktCamChlResultConfRelId = mktCamChlResultConfRelId;
    }

    public Long getMktCamChlResultId() {
        return mktCamChlResultId;
    }

    public void setMktCamChlResultId(Long mktCamChlResultId) {
        this.mktCamChlResultId = mktCamChlResultId;
    }

    public Long getEvtContactConfId() {
        return evtContactConfId;
    }

    public void setEvtContactConfId(Long evtContactConfId) {
        this.evtContactConfId = evtContactConfId;
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