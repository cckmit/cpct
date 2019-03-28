package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.ArrayList;

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
