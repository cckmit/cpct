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
import com.zjtelcom.cpct.service.synchronize.SynQuestionService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private SynQuestionService synQuestionService;

    @Value("${sync.value}")
    private String value;


    /**
     * 所有问卷列表
     * @return
     */
    @Override
    public Map<String, Object> listSimpleNaire() {
        Map<String,Object> result = new HashMap<>();
        List<Questionnaire> questionnaireList = questionnaireMapper.selectAll();
        List<QuestionnaireVO> voList = new ArrayList<>();
        for (Questionnaire questionnaire : questionnaireList){
            QuestionnaireVO questionnaireVO = BeanUtil.create(questionnaire,new QuestionnaireVO());
            voList.add(questionnaireVO);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        return result;
    }

    /**
     * 创建问卷
     * @param addVO
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> releaseQuestionnaire(QuestionnaireParam addVO) {
        return createQuestionnaire(addVO,false);
    }

    /**
     * 创建问卷
     * @param addVO
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> createQuestionnaire(QuestionnaireParam addVO,boolean isSave) {
        Map<String,Object> result = new HashMap<>();
        //添加调研问卷记录
        final Questionnaire questionnaire = BeanUtil.create(addVO,new Questionnaire());
        questionnaire.setCreateDate(DateUtil.getCurrentTime());
        questionnaire.setUpdateDate(DateUtil.getCurrentTime());
        questionnaire.setStatusDate(DateUtil.getCurrentTime());
        questionnaire.setUpdateStaff(UserUtil.loginId());
        questionnaire.setCreateStaff(UserUtil.loginId());
        if (isSave){
            questionnaire.setStatusCd("1000");//草稿
        }else {
            questionnaire.setStatusCd("2000");//已发布
        }
        questionnaire.setNairePoints(100);
        questionnaireMapper.insert(questionnaire);
        List<QuestRel> questRelList = getQuestRels(addVO, questionnaire);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","创建成功");

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synQuestionService.synQuestion("",questionnaire.getNaireId());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    /**
     * 编辑问卷
     * @param editvo
     * @return
     */
    @Override
    public Map<String, Object> modQuestionnaire(QuestionnaireParam editvo) {
        Map<String,Object> result = new HashMap<>();
        final Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(editvo.getNaireId());
        if (questionnaire==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","调研问卷不存在");
            return result;
        }
        //添加调研问卷记录
        BeanUtil.copy(editvo,questionnaire);
        questionnaire.setUpdateDate(DateUtil.getCurrentTime());
        questionnaire.setUpdateStaff(UserUtil.loginId());
        questionnaireMapper.updateByPrimaryKey(questionnaire);
        questRelMapper.deleteByNaireId(questionnaire.getNaireId());
        List<QuestRel> questRelList = getQuestRels(editvo, questionnaire);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synQuestionService.synQuestion("",questionnaire.getNaireId());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }


    private List<QuestRel> getQuestRels(QuestionnaireParam editvo, Questionnaire questionnaire) {
        List<QuestRel> questRelList = new ArrayList<>();
        for (Long id : editvo.getQuestionIdList()){
            QuestRel questRel = new QuestRel();
            questRel.setQuestionId(id);
            questRel.setNaireId(questionnaire.getNaireId());
            questRel.setCreateDate(DateUtil.getCurrentTime());
            questRel.setUpdateDate(DateUtil.getCurrentTime());
            questRel.setStatusDate(DateUtil.getCurrentTime());
            questRel.setUpdateStaff(UserUtil.loginId());
            questRel.setCreateStaff(UserUtil.loginId());
            questRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            questRelMapper.insert(questRel);
            questRelList.add(questRel);

        }
        return questRelList;
    }

    /**
     * 获取问卷
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> getQuestionnaire(Long questionnaireId) {
        Map<String,Object> result = new HashMap<>();
        QuestionRep resultRep = new QuestionRep();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","调研问卷不存在");
            return result;
        }
        QuestionnaireVO questionnaireVO = BeanUtil.create(questionnaire,new QuestionnaireVO());
        questionnaireVO.setNaireTypeName(ChannelUtil.getNaireType(questionnaire));
        resultRep.setQuestionnaire(questionnaireVO);
        List<QuestionModel> voList = new ArrayList<>();
        List<QuestRel> questRelList = questRelMapper.findRelListByQuestionnaireId(questionnaireId);
        for (QuestRel questRel : questRelList){
            Question question = questionMapper.selectByPrimaryKey(questRel.getQuestionId());
            if (question!=null){
                Map<String,Object> questionDetail = questionService.getQuestionDetail(question.getQuestionId());
                QuestionModel vo = (QuestionModel)questionDetail.get("data");
                voList.add(vo);
            }
        }
        resultRep.setQuestionVOList(voList);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resultRep);
        return result;
    }





    //--------------------------------------------------------------------------------------------------------------------------------------------------------
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
        return questionnaireRel(userId, req, result, questionnaire,false);
    }

    /**
     * 删除调研问卷
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> delQuestionnaire(Long questionnaireId) {
        Map<String,Object> result = new HashMap<>();
        final Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","调研问卷不存在");
            return result;
        }
        questionnaireMapper.deleteByPrimaryKey(questionnaire.getNaireId());
        questRelMapper.deleteByNaireId(questionnaire.getNaireId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synQuestionService.deleteQuestion("",questionnaire.getNaireId());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

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
        Integer page = MapUtil.getIntNum(param.get("page").toString());
        Integer pageSize =  MapUtil.getIntNum(param.get("pageSize").toString());

        PageHelper.startPage(page,pageSize);
        List<Questionnaire> questionnaireList = questionnaireMapper.findQuestionnaireListByParam(param);
        Page info = new Page(new PageInfo(questionnaireList));
        List<QuestionnaireVO> voList = new ArrayList<>();
        for (Questionnaire questionnaire: questionnaireList){
            QuestionnaireVO vo = BeanUtil.create(questionnaire,new QuestionnaireVO());
            vo.setNaireTypeName(ChannelUtil.getNaireType(questionnaire));
            Map<String,String> statusParam = sysParamsService.getParamsByValue("QUEST_001",questionnaire.getStatusCd());
            vo.setStatusSt(statusParam.get("PARAM_NAME"));
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
        return questionnaireRel(userId, questionReq, result, questionnaire,true);
    }

    private Map<String, Object> questionnaireRel(Long userId, QuestionReq questionReq, Map<String, Object> result, Questionnaire questionnaire,boolean isAdd) {
        Long naireId = questionnaire.getNaireId();
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
        if (isAdd){
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","创建成功");
            return result;
        }else {
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","编辑成功");
            return result;
        }
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
        QuestionnaireVO questionnaireVO = BeanUtil.create(questionnaire,new QuestionnaireVO());
        questionnaireVO.setNaireTypeName(ChannelUtil.getNaireType(questionnaire));
        resultRep.setQuestionnaire(questionnaireVO);
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
//        resultRep.setQuestionVOList(voList);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resultRep);
        return result;
    }
}
