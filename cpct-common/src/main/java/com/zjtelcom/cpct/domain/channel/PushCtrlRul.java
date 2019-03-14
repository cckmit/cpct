package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class PushCtrlRul extends BaseEntity {
    private Long pushCtrlRulId;

    private Long contactChlId;

    private Long priority;

    private Long custCount;

    private String noPustDate;

    private Long periodType;

    private Long ctrlMark;

    private String timeSet;



    public Long getPushCtrlRulId() {
        return pushCtrlRulId;
    }

    public void setPushCtrlRulId(Long pushCtrlRulId) {
        this.pushCtrlRulId = pushCtrlRulId;
    }

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getCustCount() {
        return custCount;
    }

    public void setCustCount(Long custCount) {
        this.custCount = custCount;
    }

    public String getNoPustDate() {
        return noPustDate;
    }

    public void setNoPustDate(String noPustDate) {
        this.noPustDate = noPustDate;
    }

    public Long getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Long periodType) {
        this.periodType = periodType;
    }

    public Long getCtrlMark() {
        return ctrlMark;
    }

    public void setCtrlMark(Long ctrlMark) {
        this.ctrlMark = ctrlMark;
    }

    public String getTimeSet() {
        return timeSet;
    }

    public void setTimeSet(String timeSet) {
        this.timeSet = timeSet;
    }


}