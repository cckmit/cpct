package com.zjtelcom.cpct.dto.question;

import com.zjtelcom.cpct.domain.question.Questionnaire;

import java.io.Serializable;
import java.util.List;

public class QuestionReq implements Serializable {
    private Questionnaire questionnaire;
    private List<QuestionAddVO> addVOList;

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<QuestionAddVO> getAddVOList() {
        return addVOList;
    }

    public void setAddVOList(List<QuestionAddVO> addVOList) {
        this.addVOList = addVOList;
    }
}
