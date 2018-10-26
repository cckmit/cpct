package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.question.MktQstQuestRelMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionDetailMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionnaireMapper;
import com.zjtelcom.cpct.domain.question.QuestRel;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dubbo.model.*;
import com.zjtelcom.cpct.dubbo.service.QuestionService;
import com.zjtelcom.cpct.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired(required = false)
    private MktQuestionnaireMapper questionnaireMapper;
    @Autowired(required = false)
    private QuestionService questionService;
    @Autowired(required = false)
    private MktQstQuestRelMapper questRelMapper;
    @Autowired(required = false)
    private MktQuestionMapper questionMapper;
    @Autowired(required = false)
    private MktQuestionDetailMapper questionDetailMapper;

    /**
     * 问卷详情
     * @param questionnaireId
     * @return
     */
    @Override
    public RetQuestion getQuestionnaireDetail(Long questionnaireId) {
        RetQuestion ret = new RetQuestion();
        Map<String,Object> result = new HashMap<>();
        QuestionRep resultRep = new QuestionRep();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire==null){
            ret.setData(resultRep);
            ret.setResultMsg("调研问卷不存在");
            ret.setResultCode(CODE_FAIL);
            return ret;
        }
        QuestionnaireVO questionnaireVO = BeanUtil.create(questionnaire,new QuestionnaireVO());
        resultRep.setQuestionnaire(questionnaireVO);
        List<QuestRel> questRelList = questRelMapper.findRelListByQuestionnaireId(questionnaireId);


        List<QuestionModel> voList = new ArrayList<>();
        for (QuestRel questRel : questRelList){
            Question question = questionMapper.selectByPrimaryKey(questRel.getQuestionId());
            if (question!=null){
                QuestionModel vo = new QuestionModel();
                QuestionVO questionVO = BeanUtil.create(question,new QuestionVO());
                vo.setQuestion(questionVO);
                List<QuestionDetail> questionDetailList = questionDetailMapper.findDetailListByQuestionId(question.getQuestionId());
                List<QuestionDetailVO> detailVOS = new ArrayList<>();
                for (QuestionDetail detail : questionDetailList){
                    QuestionDetailVO detailVO = BeanUtil.create(detail,new QuestionDetailVO());
                    detailVOS.add(detailVO);
                }
                ArrayList<QuestionDetailVO> detailVOArrayList = new ArrayList(detailVOS);
                vo.setQuestionDetailList(detailVOArrayList);
            }
        }
        ArrayList<QuestionModel> detailVOArrayList = new ArrayList(voList);
        resultRep.setQuestionVOList(detailVOArrayList);
        ret.setResultCode(CODE_SUCCESS);
        ret.setData(resultRep);
        ret.setResultMsg(null);
        return ret;
    }
}
