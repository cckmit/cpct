package com.zjtelcom.cpct.open.entity.event;

import java.util.Date;

public class OpenEventRel {

    private Long complexEvtRelaId;
    private Long aEvtId;
    private String aEvtNbr;
    private Long zEvtId;
    private String zEvtNbr;
    private Long sort;
    private String statusCd;
    private Date statusDate;
    private String remark;

    public Long getComplexEvtRelaId() {
        return complexEvtRelaId;
    }

    public void setComplexEvtRelaId(Long complexEvtRelaId) {
        this.complexEvtRelaId = complexEvtRelaId;
    }

    public Long getaEvtId() {
        return aEvtId;
    }

    public void setaEvtId(Long aEvtId) {
        this.aEvtId = aEvtId;
    }

    public String getaEvtNbr() {
        return aEvtNbr;
    }

    public void setaEvtNbr(String aEvtNbr) {
        this.aEvtNbr = aEvtNbr;
    }

    public Long getzEvtId() {
        return zEvtId;
    }

    public void setzEvtId(Long zEvtId) {
        this.zEvtId = zEvtId;
    }

    public String getzEvtNbr() {
        return zEvtNbr;
    }

    public void setzEvtNbr(String zEvtNbr) {
        this.zEvtNbr = zEvtNbr;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
