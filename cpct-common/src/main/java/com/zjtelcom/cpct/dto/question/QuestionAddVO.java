package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;
import java.util.List;

public class QuestionAddVO implements Serializable {
    private String questionName;

    private String questionType;//1000 单选题；2000多选题

    private String questionDesc;

    private String answerType;//1000	日期输入框;2000	下拉选择框;3000	文本输入框;4000	单选框;5000	字符编辑框;6000	是与否控制框;7000	数值输入框;8000	多选框;9000	文本标签

    private String defaultAnswer;

    private List<QuestionDetailAddVO> questionDetailAddVOList;

    private String reset;

    //关系表属性
    private Integer questionOrder;

    private Integer questionWeight;

    private Short isMark;

    private Short isMust;



    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
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

    public String getQuestionDesc() {
        return questionDesc;
    }

    public void setQuestionDesc(String questionDesc) {
        this.questionDesc = questionDesc;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getDefaultAnswer() {
        return defaultAnswer;
    }

    public void setDefaultAnswer(String defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    public List<QuestionDetailAddVO> getQuestionDetailAddVOList() {
        return questionDetailAddVOList;
    }

    public void setQuestionDetailAddVOList(List<QuestionDetailAddVO> questionDetailAddVOList) {
        this.questionDetailAddVOList = questionDetailAddVOList;
    }
}
