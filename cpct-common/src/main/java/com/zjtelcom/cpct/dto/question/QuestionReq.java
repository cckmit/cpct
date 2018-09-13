package com.zjtelcom.cpct.dto.question;

import com.zjtelcom.cpct.domain.question.Questionnaire;

import java.io.Serializable;
import java.util.List;

public class QuestionReq implements Serializable {
    private Questionnaire questionnaire;
    private List<InputQuestionAddVO> inputQuestionAddVOList;
    private List<MultiQuestionAddVO> multiQuestionAddVOList;
    private List<SingleQuestionAddVO> singleQuestionAddVOList;


    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<InputQuestionAddVO> getInputQuestionAddVOList() {
        return inputQuestionAddVOList;
    }

    public void setInputQuestionAddVOList(List<InputQuestionAddVO> inputQuestionAddVOList) {
        this.inputQuestionAddVOList = inputQuestionAddVOList;
    }

    public List<MultiQuestionAddVO> getMultiQuestionAddVOList() {
        return multiQuestionAddVOList;
    }

    public void setMultiQuestionAddVOList(List<MultiQuestionAddVO> multiQuestionAddVOList) {
        this.multiQuestionAddVOList = multiQuestionAddVOList;
    }

    public List<SingleQuestionAddVO> getSingleQuestionAddVOList() {
        return singleQuestionAddVOList;
    }

    public void setSingleQuestionAddVOList(List<SingleQuestionAddVO> singleQuestionAddVOList) {
        this.singleQuestionAddVOList = singleQuestionAddVOList;
    }
}
