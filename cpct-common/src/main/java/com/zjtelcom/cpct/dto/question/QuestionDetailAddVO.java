package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;

public class QuestionDetailAddVO implements Serializable {
    private Long questionId;

    private Integer qstDetailOrder;//题库明细顺序

    private String qstDetailValue;//题库明细答案

    private String qstDetailInputType;//题库明细输入类型

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
}
