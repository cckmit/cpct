package com.zjtelcom.cpct.domain.question;

import java.io.Serializable;
import java.util.Date;

public class QuestionDetail implements Serializable {
    private Long qstDetailId;

    private Long questionId;

    private Integer qstDetailOrder;//题库明细顺序

    private String qstDetailValue;//题库明细答案

    private String qstDetailInputType;//题库明细输入类型 :1000	输入	;2000 选择

    private String statusCd;

    private Date statusDate;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    public Long getQstDetailId() {
        return qstDetailId;
    }

    public void setQstDetailId(Long qstDetailId) {
        this.qstDetailId = qstDetailId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getQstDetailOrder() {
        return qstDetailOrder;
    }

    public void setQstDetailOrder(Integer qstDetailOrder) {
        this.qstDetailOrder = qstDetailOrder;
    }

    public String getQstDetailValue() {
        return qstDetailValue;
    }

    public void setQstDetailValue(String qstDetailValue) {
        this.qstDetailValue = qstDetailValue;
    }

    public String getQstDetailInputType() {
        return qstDetailInputType;
    }

    public void setQstDetailInputType(String qstDetailInputType) {
        this.qstDetailInputType = qstDetailInputType;
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