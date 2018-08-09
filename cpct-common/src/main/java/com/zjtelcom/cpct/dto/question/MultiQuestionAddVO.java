package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;
import java.util.List;

public class MultiQuestionAddVO implements Serializable {
    private String questionName;

    private String questionType = "2000";//1000 单选题；2000多选题

    private String answerType = "9000";//1000	日期输入框;2000	下拉选择框;3000	文本输入框;4000	单选框;5000	字符编辑框;6000	是与否控制框;7000	数值输入框;8000	多选框;9000	文本标签

    private List<QuestionDetailAddVO> questionDetailAddVOList;

    //关系表属性
    private Integer questionOrder;

    private Integer questionWeight;

    private Short isMark;

    private Short isMust;

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public List<QuestionDetailAddVO> getQuestionDetailAddVOList() {
        return questionDetailAddVOList;
    }

    public void setQuestionDetailAddVOList(List<QuestionDetailAddVO> questionDetailAddVOList) {
        this.questionDetailAddVOList = questionDetailAddVOList;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getQuestionWeight() {
        return questionWeight;
    }

    public void setQuestionWeight(Integer questionWeight) {
        this.questionWeight = questionWeight;
    }

    public Short getIsMark() {
        return isMark;
    }

    public void setIsMark(Short isMark) {
        this.isMark = isMark;
    }

    public Short getIsMust() {
        return isMust;
    }

    public void setIsMust(Short isMust) {
        this.isMust = isMust;
    }
}
