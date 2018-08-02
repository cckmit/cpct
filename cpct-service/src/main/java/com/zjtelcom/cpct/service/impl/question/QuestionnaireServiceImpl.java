package com.zjtelcom.cpct.service.impl.question;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.question.MktQstQuestRelMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionDetailMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionnaireMapper;
import com.zjtelcom.cpct.domain.question.QuestRel;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dto.question.QuestionAddVO;
import com.zjtelcom.cpct.dto.question.QuestionRep;
import com.zjtelcom.cpct.dto.question.QuestionReq;
import com.zjtelcom.cpct.dto.question.QuestionVO;
import com.zjtelcom.cpct.service.question.QuestionService;
import com.zjtelcom.cpct.service.question.QuestionnaireService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private MktQuestionnaireMapper questionnaireMapper;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private MktQstQuestRelMapper questRelMapper;
    @Autowired
    private MktQuestionMapper questionMapper;
    @Autowired
    private MktQuestionDetailMapper questionDetailMapper;


    /**
     * 问卷配置关联
     * @param userId
     * @param questionReq
     * @return
     */
    @Override
    public Map<String, Object> createQuestionnaire(Long userId, QuestionReq questionReq) {
        Map<String,Object> result = new HashMap<>();
        //添加调研问卷记录
        Questionnaire questionnaire = BeanUtil.create(questionReq.getQuestionnaire(),new Questionnaire());
        questionnaire.setCreateDate(DateUtil.getCurrentTime());
        questionnaire.setUpdateDate(DateUtil.getCurrentTime());
        questionnaire.setStatusDate(DateUtil.getCurrentTime());
        questionnaire.setUpdateStaff(UserUtil.loginId());
        questionnaire.setCreateStaff(UserUtil.loginId());
        questionnaire.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        questionnaireMapper.insert(questionnaire);
        Long naireId = questionnaire.getNaireId();
        for (QuestionAddVO questionAddVO : questionReq.getAddVOList()){
            //添加问题及答案
            Map<String,Object> map = questionService.addQuestion(userId,questionAddVO);
            if (!map.get("resultCode").equals(CODE_SUCCESS)){
                return map;
            }
            Long questionId = Long.valueOf(map.get("questionId").toString());
            //添加问卷问题关联关系表记录
            QuestRel questRel = BeanUtil.create(questionAddVO,new QuestRel());
            questRel.setQuestionId(questionId);
            questRel.setNaireId(naireId);
            questRel.setCreateDate(DateUtil.getCurrentTime());
            questRel.setUpdateDate(DateUtil.getCurrentTime());
            questRel.setStatusDate(DateUtil.getCurrentTime());
            questRel.setUpdateStaff(UserUtil.loginId());
            questRel.setCreateStaff(UserUtil.loginId());
            questRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            questRelMapper.insert(questRel);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }


    /**
     * 根据调研问卷id获取问题详情列表
     * @param userId
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> listQuestionListByQuestionId(Long userId, Long questionnaireId) {
        Map<String,Object> result = new HashMap<>();
        QuestionRep resultRep = new QuestionRep();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","调研问卷不存在");
            return result;
        }
        resultRep.setQuestionnaire(questionnaire);
        List<QuestionVO> voList = new ArrayList<>();
        List<QuestRel> questRelList = questRelMapper.findRelListByQuestionnaireId(questionnaireId);
        for (QuestRel questRel : questRelList){
            Question question = questionMapper.selectByPrimaryKey(questRel.getQuestionId());
            if (question!=null){
                QuestionVO vo = new QuestionVO();
                vo.setQuestion(question);
                List<QuestionDetail> questionDetailList = questionDetailMapper.findDetailListByQuestionId(question.getQuestionId());
                vo.setQuestionDetailList(questionDetailList);
                voList.add(vo);
            }
        }
        resultRep.setQuestionVOList(voList);
        result.put("resutlCode",CODE_SUCCESS);
        result.put("resultMsg",resultRep);
        return result;
    }
}
