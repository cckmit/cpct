package com.zjtelcom.cpct.service.question;

import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.dto.question.QuestionEditVO;

import java.util.Map;

public interface QuestionService {

    Map<String,Object> createQuestion(Long userId, Question question);

    Map<String,Object> modQuestion(Long userId, QuestionEditVO editVO);

    Map<String,Object> delQuestion(Long userId,Long questionId);

}
