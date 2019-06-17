package com.zjtelcom.cpct.dao.question;


import com.zjtelcom.cpct.domain.question.QuestDetailRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktQstQuestDetailRelMapper {
    int deleteByPrimaryKey(Long relConfId);

    int insert(QuestDetailRel record);

    QuestDetailRel selectByPrimaryKey(Long relConfId);

    List<QuestDetailRel> selectAll();

    int updateByPrimaryKey(QuestDetailRel record);
}