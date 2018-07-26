package com.zjtelcom.cpct.dao.question;

import com.zjtelcom.cpct.domain.question.QuestionDetail;

import java.util.List;

public interface MktQuestionDetailMapper {
    int deleteByPrimaryKey(Long qstDetailId);

    int insert(QuestionDetail record);

    QuestionDetail selectByPrimaryKey(Long qstDetailId);

    List<QuestionDetail> selectAll();

    int updateByPrimaryKey(QuestionDetail record);
}