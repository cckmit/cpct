package com.zjtelcom.cpct.dao.question;



import com.zjtelcom.cpct.domain.question.Question;

import java.util.List;

public interface MktQuestionMapper {
    int deleteByPrimaryKey(Long questionId);

    int insert(Question record);

    Question selectByPrimaryKey(Long questionId);

    List<Question> selectAll();

    int updateByPrimaryKey(Question record);
}