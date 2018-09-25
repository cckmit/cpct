package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;
import java.util.List;

public class QuestionEditVO implements Serializable {
    private Long questionId;

    private String questionName;

    private String questionType;//1000 单选题；2000多选题

    private String questionDesc;

    private String answerType;

    private String defaultAnswer;

    private List<QuestionDetailAddVO> questionDetailAddVOList;

    public List<QuestionDetailAddVO> getQuestionDetailAddVOList() {
        return questionDetailAddVOList;
    }

    public void setQuestionDetailAddVOList(List<QuestionDetailAddVO> questionDetailAddVOList) {
        this.questionDetailAddVOList = questionDetailAddVOList;
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

    public String getDefaultAnswer() {
        return defaultAnswer;
    }

    public void setDefaultAnswer(String defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }
}
