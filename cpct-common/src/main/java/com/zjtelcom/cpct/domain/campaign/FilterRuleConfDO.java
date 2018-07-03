package com.zjtelcom.cpct.domain.campaign;

import java.util.Date;

public class FilterRuleConfDO {
    private Long filterRuleConfId;

    private String filterRuleIds;

    private String createStaff;

    private Date createDate;

    private String updateStaff;

    private Date updateDate;

    public Long getFilterRuleConfId() {
        return filterRuleConfId;
    }

    public void setFilterRuleConfId(Long filterRuleConfId) {
        this.filterRuleConfId = filterRuleConfId;
    }

    public String getFilterRuleIds() {
        return filterRuleIds;
    }

    public void setFilterRuleIds(String filterRuleIds) {
        this.filterRuleIds = filterRuleIds;
    }

    public String getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(String createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(String updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}