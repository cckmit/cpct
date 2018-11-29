package com.zjtelcom.cpct_prd.dao.question;


import com.zjtelcom.cpct.domain.question.QuestRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MktQstQuestRelPrdMapper {
    int deleteByPrimaryKey(Long relId);

    int insert(QuestRel record);

    QuestRel selectByPrimaryKey(Long relId);

    List<QuestRel> selectAll();

    List<QuestRel> findRelListByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    int updateByPrimaryKey(QuestRel record);

    int insertBatch(@Param("list") List<QuestRel> questRelList);

    int deleteByNaireId(@Param("naireId") Long naireId);
}