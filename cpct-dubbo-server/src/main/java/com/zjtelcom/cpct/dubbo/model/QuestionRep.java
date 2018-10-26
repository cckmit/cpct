package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dto.question.QuestionVO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionRep implements Serializable {
    private QuestionnaireVO questionnaire;
    private ArrayList<QuestionModel> questionVOList;

    public QuestionnaireVO getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(QuestionnaireVO questionnaire) {
        this.questionnaire = questionnaire;
    }

    public ArrayList<QuestionModel> getQuestionVOList() {
        return questionVOList;
    }

    public void setQuestionVOList(ArrayList<QuestionModel> questionVOList) {
        this.questionVOList = questionVOList;
    }
}
