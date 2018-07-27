package com.zjtelcom.cpct.service.question;

import com.zjtelcom.cpct.domain.question.Questionnaire;

import java.util.Map;

public interface QuestionnaireService {

    Map<String,Object> createQuestionnaire(Long userId, Questionnaire questionnaire);

}
