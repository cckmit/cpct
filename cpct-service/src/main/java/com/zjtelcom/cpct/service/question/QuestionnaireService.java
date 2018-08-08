package com.zjtelcom.cpct.service.question;

import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dto.question.QuestionReq;

import java.util.Map;

public interface QuestionnaireService {

    Map<String,Object> getQuestionnaireList(Long userId,Map<String,Object> param);

    Map<String,Object> createQuestionnaire(Long userId,QuestionReq questionReq);

    Map<String,Object> listQuestionListByQuestionId(Long userId,Long questionnaireId);
}
