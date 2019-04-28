package com.zjtelcom.cpct.dao.question;


import com.zjtelcom.cpct.domain.question.QuestRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktQstQuestRelMapper {
    int deleteByPrimaryKey(Long relId);

    int insert(QuestRel record);

    QuestRel selectByPrimaryKey(Long relId);

    List<QuestRel> selectAll();

    List<QuestRel> findRelListByQuestionnaireId(@Param("questionnaireId")Long questionnaireId);

    int updateByPrimaryKey(QuestRel record);

    int insertBatch(@Param("relList") List<QuestRel> questRelList);

    int deleteByNaireId(@Param("naireId") Long naireId);
}