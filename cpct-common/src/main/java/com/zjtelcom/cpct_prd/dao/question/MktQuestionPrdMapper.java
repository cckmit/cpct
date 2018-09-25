package com.zjtelcom.cpct_prd.dao.question;



import com.zjtelcom.cpct.domain.question.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MktQuestionPrdMapper {
    int deleteByPrimaryKey(Long questionId);

    int insert(Question record);

    Question selectByPrimaryKey(Long questionId);

    List<Question> selectAll();

    int updateByPrimaryKey(Question record);

    List<Question> selectByParam(@Param("map") Question question);
}