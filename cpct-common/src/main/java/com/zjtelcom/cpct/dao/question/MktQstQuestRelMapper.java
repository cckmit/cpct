package com.zjtelcom.cpct.dao.question;


import com.zjtelcom.cpct.domain.question.QuestRel;

import java.util.List;

public interface MktQstQuestRelMapper {
    int deleteByPrimaryKey(Long relId);

    int insert(QuestRel record);

    QuestRel selectByPrimaryKey(Long relId);

    List<QuestRel> selectAll();

    int updateByPrimaryKey(QuestRel record);
}