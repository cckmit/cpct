package com.zjtelcom.cpct.domain.campaign;

import java.io.Serializable;
import java.util.Date;

public class MktCamChlResultDO  implements Serializable {
    /**
     * 二次协同渠道结果标识
     */
    private Long mktCamChlResultId;
    /**
     * 二次协同渠道结果名称
     */
    private String mktCamChlResultName;
    /**
     * 结果类型：0-结果， 1-工单
     */
    private String resultType;
    /**
     * 营销结果Id
     */
    private Long result;
    /**
     * 原因Id
     */
    private Long reason;
    /**
     * 创建人
     */
    private Long createStaff;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新人
     */
    private Long updateStaff;
    /**
     * 更新时间
     */
    private Date updateDate;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Long getMktCamChlResultId() {
        return mktCamChlResultId;
    }

    public void setMktCamChlResultId(Long mktCamChlResultId) {
        this.mktCamChlResultId = mktCamChlResultId;
    }

    public String getMktCamChlResultName() {
        return mktCamChlResultName;
    }

    public void setMktCamChlResultName(String mktCamChlResultName) {
        this.mktCamChlResultName = mktCamChlResultName;
    }

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }

    public Long getReason() {
        return reason;
    }

    public void setReason(Long reason) {
        this.reason = reason;
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