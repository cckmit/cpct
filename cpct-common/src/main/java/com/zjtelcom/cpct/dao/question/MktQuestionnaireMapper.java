package com.zjtelcom.cpct.dao.question;


import com.zjtelcom.cpct.domain.question.Questionnaire;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MktQuestionnaireMapper {
    int deleteByPrimaryKey(Long naireId);

    int insert(Questionnaire record);

    Questionnaire selectByPrimaryKey(Long naireId);

    List<Questionnaire> selectAll();

    List<Questionnaire> findQuestionnaireListByParam(@Param("naireName")String naireName,@Param("naireType")String naireType);

//    Page<Questionnaire> findNairePageByParam(@Param("naireName")String naireName, @Param("naireType")String naireType);


    int updateByPrimaryKey(Questionnaire record);
}