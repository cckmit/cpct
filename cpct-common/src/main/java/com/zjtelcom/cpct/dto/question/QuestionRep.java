package com.zjtelcom.cpct.dto.question;

import com.zjtelcom.cpct.domain.question.Questionnaire;

import java.io.Serializable;
import java.util.List;

public class QuestionRep implements Serializable {
    private Questionnaire questionnaire;
    private List<QuestionVO> questionVOList;

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<QuestionVO> getQuestionVOList() {
        return questionVOList;
    }

    public void setQuestionVOList(List<QuestionVO> questionVOList) {
        this.questionVOList = questionVOList;
    }
}
