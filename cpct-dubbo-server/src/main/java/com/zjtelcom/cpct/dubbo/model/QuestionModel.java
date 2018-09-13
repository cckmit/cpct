package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;

import java.io.Serializable;
import java.util.List;

public class QuestionModel implements Serializable {
    private QuestionVO question;
    private List<QuestionDetailVO> questionDetailList;

    public QuestionVO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionVO question) {
        this.question = question;
    }

    public List<QuestionDetailVO> getQuestionDetailList() {
        return questionDetailList;
    }

    public void setQuestionDetailList(List<QuestionDetailVO> questionDetailList) {
        this.questionDetailList = questionDetailList;
    }
}
