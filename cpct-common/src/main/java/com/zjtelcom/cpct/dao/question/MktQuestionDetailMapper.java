package com.zjtelcom.cpct.dao.question;

import com.zjtelcom.cpct.domain.question.QuestionDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MktQuestionDetailMapper {
    int deleteByPrimaryKey(Long qstDetailId);

    int insert(QuestionDetail record);

    QuestionDetail selectByPrimaryKey(Long qstDetailId);

    List<QuestionDetail> selectAll();

    List<QuestionDetail> findDetailListByQuestionId(@Param("questionId")Long questionId);

    int updateByPrimaryKey(QuestionDetail record);

    int deleteInBatch(@Param("idList")List<Long> idList);

    int deleteByQuestionId(@Param("questionId")Long questionId);
}