package com.zjtelcom.cpct.domain.channel;

import java.util.Date;

public class RequestInstRel {
    private Long requestInstRelId;

    private Long requestInfoId;

    private String requestObjType;

    private Long requestObjId;

    private String statusCd;

    private Long createStaff;

    private Long updateStaff;

    private Date statusDate;

    private Date createDate;

    private Date updateDate;

    private String remark;



    public Long getRequestInstRelId() {
        return requestInstRelId;
    }

    public void setRequestInstRelId(Long requestInstRelId) {
        this.requestInstRelId = requestInstRelId;
    }

    public Long getRequestInfoId() {
        return requestInfoId;
    }

    public void setRequestInfoId(Long requestInfoId) {
        this.requestInfoId = requestInfoId;
    }

    public Long getRequestObjId() {
        return requestObjId;
    }

    public void setRequestObjId(Long requestObjId) {
        this.requestObjId = requestObjId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public String getRequestObjType() {
        return requestObjType;
    }

    public void setRequestObjType(String requestObjType) {
        this.requestObjType = requestObjType;
    }
}