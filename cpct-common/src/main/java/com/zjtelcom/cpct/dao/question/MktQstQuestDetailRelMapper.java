package com.zjtelcom.cpct.dao.question;


import com.zjtelcom.cpct.domain.question.QuestDetailRel;

import java.util.List;

public interface MktQstQuestDetailRelMapper {
    int deleteByPrimaryKey(Long relConfId);

    int insert(QuestDetailRel record);

    QuestDetailRel selectByPrimaryKey(Long relConfId);

    List<QuestDetailRel> selectAll();

    int updateByPrimaryKey(QuestDetailRel record);
}