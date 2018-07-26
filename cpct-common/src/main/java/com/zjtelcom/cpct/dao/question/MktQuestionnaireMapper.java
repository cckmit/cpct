package com.zjtelcom.cpct.dao.question;


import com.zjtelcom.cpct.domain.question.Questionnaire;

import java.util.List;

public interface MktQuestionnaireMapper {
    int deleteByPrimaryKey(Long naireId);

    int insert(Questionnaire record);

    Questionnaire selectByPrimaryKey(Long naireId);

    List<Questionnaire> selectAll();

    int updateByPrimaryKey(Questionnaire record);
}