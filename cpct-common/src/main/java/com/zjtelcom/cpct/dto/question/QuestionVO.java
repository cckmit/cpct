package com.zjtelcom.cpct.dto.question;

import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;

import java.io.Serializable;
import java.util.List;

public class QuestionVO implements Serializable {
    private Question question;
    private List<QuestionDetail> questionDetailList;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<QuestionDetail> getQuestionDetailList() {
        return questionDetailList;
    }

    public void setQuestionDetailList(List<QuestionDetail> questionDetailList) {
        this.questionDetailList = questionDetailList;
    }
}
