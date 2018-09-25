package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;
import java.util.List;

public class QuestionModel implements Serializable {
    private Long questionId;

    private String questionName;

    private String questionType;//1000 单选题；2000多选题

    private String questionOrder;

    private String questionTypeName;

    private String questionDesc;

    private String answerType;//1000	日期输入框;2000	下拉选择框;3000	文本输入框;4000	单选框;5000	字符编辑框;6000	是与否控制框;7000	数值输入框;8000	多选框;9000	文本标签

    private String answerTypeName;

    private String optionState;

    private String time; // 时间选择器绑定值

    private String textarea;  //文本框绑定值

    private List<String> checkList; // 多选框绑定值

    private List<QuestionDetailVO> questionDetailList;


    public String getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(String questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<String> checkList) {
        this.checkList = checkList;
    }

    public String getOptionState() {
        return optionState;
    }

    public void setOptionState(String optionState) {
        this.optionState = optionState;
    }

    public String getTextarea() {
        return textarea;
    }

    public void setTextarea(String textarea) {
        this.textarea = textarea;
    }


    public String getQuestionTypeName() {
        return questionTypeName;
    }

    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    public String getAnswerTypeName() {
        return answerTypeName;
    }

    public void setAnswerTypeName(String answerTypeName) {
        this.answerTypeName = answerTypeName;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public List<QuestionDetailVO> getQuestionDetailList() {
        return questionDetailList;
    }

    public void setQuestionDetailList(List<QuestionDetailVO> questionDetailList) {
        this.questionDetailList = questionDetailList;
    }
}
