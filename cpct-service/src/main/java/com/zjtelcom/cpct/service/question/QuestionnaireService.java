package com.zjtelcom.cpct.service.question;

import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dto.question.QuestionReq;
import com.zjtelcom.cpct.dto.question.QuestionnaireParam;

import java.util.Map;

public interface QuestionnaireService {


    Map<String,Object> createQuestionnaire(QuestionnaireParam addVO);

    Map<String,Object> modQuestionnaire(QuestionnaireParam editvo);

    Map<String,Object> getQuestionnaire(Long questionnaireId);

    Map<String,Object> getQuestionnaireList(Long userId,Map<String,Object> param);

    Map<String,Object> createQuestionnaire(Long userId,QuestionReq questionReq);

    Map<String,Object> listQuestionListByQuestionId(Long userId,Long questionnaireId);

    Map<String,Object> modQuestionnaire(Long userId,QuestionReq req);

    Map<String,Object> delQuestionnaire(Long questionnaireId);


}
