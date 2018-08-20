package com.zjtelcom.cpct.service.impl.question;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.question.MktQstQuestRelMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionDetailMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionnaireMapper;
import com.zjtelcom.cpct.domain.question.QuestRel;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dto.question.*;
import com.zjtelcom.cpct.service.question.QuestionService;
import com.zjtelcom.cpct.service.question.QuestionnaireService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
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
     * 编辑调研问卷
     * @param userId
     * @param req
     * @return
     */
    @Override
    public Map<String, Object> modQuestionnaire(Long userId, QuestionReq req) {
        Map<String,Object> result = new HashMap<>();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(req.getQuestionnaire().getNaireId());
        if (questionnaire==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","调研问卷不存在");
            return result;
        }
        questionnaire.setNaireName(req.getQuestionnaire().getNaireName());
        questionnaire.setNaireDesc(req.getQuestionnaire().getNaireDesc());
        questionnaireMapper.updateByPrimaryKey(questionnaire);
        List<QuestRel> questRelList = questRelMapper.findRelListByQuestionnaireId(questionnaire.getNaireId());
        for (QuestRel questRel : questRelList){
            if (questRel!=null){
                questRelMapper.deleteByPrimaryKey(questRel.getRelId());
            }
        }
        return questionnaireRel(userId, req, result, questionnaire);
    }

    /**
     * 删除调研问卷
     * @param userId
     * @param req
     * @return
     */
    @Override
    public Map<String, Object> delQuestionnaire(Long userId, QuestionReq req) {
        Map<String,Object> result = new HashMap<>();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(req.getQuestionnaire().getNaireId());
        if (questionnaire==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","调研问卷不存在");
            return result;
        }
        questionnaireMapper.deleteByPrimaryKey(questionnaire.getNaireId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }

    /**
     * 获取调查问卷列表
     * @param userId
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getQuestionnaireList(Long userId, Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        String naireName = null;
        String naireType = null;
        Integer page = MapUtil.getIntNum(param.get("page").toString());
        Integer pageSize =  MapUtil.getIntNum(param.get("pageSize").toString());
        if (param.get("naireName")!=null){
            naireName = param.get("naireName").toString();
        }
        if (param.get("naireType")!=null){
            naireType = param.get("naireType").toString();
        }

        PageHelper.startPage(page,pageSize);
        List<Questionnaire> questionnaireList = questionnaireMapper.findQuestionnaireListByParam(naireName,naireType);
        Page info = new Page(new PageInfo(questionnaireList));
        List<QuestionnaireVO> voList = new ArrayList<>();
        for (Questionnaire questionnaire: questionnaireList){
            QuestionnaireVO vo = BeanUtil.create(questionnaire,new QuestionnaireVO());
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        result.put("page",info);
        return result;
    }

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
        return questionnaireRel(userId, questionReq, result, questionnaire);
    }

    private Map<String, Object> questionnaireRel(Long userId, QuestionReq questionReq, Map<String, Object> result, Questionnaire questionnaire) {
        Long naireId = questionnaire.getNaireId();
        for (InputQuestionAddVO inputQuestionAddVO : questionReq.getInputQuestionAddVOList()){
            //添加问题及答案
            QuestionAddVO questionAddVO = BeanUtil.create(inputQuestionAddVO,new QuestionAddVO());
            Map<String, Object> map = addQuestion(userId, naireId, questionAddVO);
            if (map != null) return map;
        }
        for (MultiQuestionAddVO multiQuestionAddVO : questionReq.getMultiQuestionAddVOList()){
            //添加问题及答案
            QuestionAddVO questionAddVO = BeanUtil.create(multiQuestionAddVO,new QuestionAddVO());
            Map<String, Object> map = addQuestion(userId, naireId, questionAddVO);
            if (map != null) return map;
        }
        for (SingleQuestionAddVO singleQuestionAddVO : questionReq.getSingleQuestionAddVOList()){
            //添加问题及答案
            QuestionAddVO questionAddVO = BeanUtil.create(singleQuestionAddVO,new QuestionAddVO());
            Map<String, Object> map = addQuestion(userId, naireId, questionAddVO);
            if (map != null) return map;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    private Map<String, Object> addQuestion(Long userId, Long naireId, QuestionAddVO questionAddVO) {
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
        return null;
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
