package com.zjtelcom.cpct.service.impl.synchronize.question;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.question.MktQstQuestRelMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionDetailMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionnaireMapper;
import com.zjtelcom.cpct.domain.question.QuestRel;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynQuestionService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.question.MktQstQuestRelPrdMapper;
import com.zjtelcom.cpct_prd.dao.question.MktQuestionDetailPrdMapper;
import com.zjtelcom.cpct_prd.dao.question.MktQuestionPrdMapper;
import com.zjtelcom.cpct_prd.dao.question.MktQuestionnairePrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * 题库关联：       mkt_question  mkt_question_detail
 * 调研问卷关联：   mkt_questionnaire  mkt_qst_quest_rel(可以关联到题库和题库详情)
 */
@Service
public class SynQuestionServiceImpl  extends BaseService implements SynQuestionService {
    @Autowired
    private MktQuestionnaireMapper questionnaireMapper;
    @Autowired
    private MktQuestionnairePrdMapper questionnairePrdMapper;
    @Autowired
    private MktQuestionMapper questionMapper;
    @Autowired
    private MktQuestionPrdMapper questionPrdMapper;
    @Autowired
    private MktQuestionDetailMapper questionDetailMapper;
    @Autowired
    private MktQuestionDetailPrdMapper questionDetailPrdMapper;
    @Autowired
    private MktQstQuestRelMapper questRelMapper;
    @Autowired
    private MktQstQuestRelPrdMapper questRelPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    //调查问卷题库表
    private String question="mkt_questionnaire";
    //调查问卷关联表
    private String questionRel="mkt_qst_quest_rel";
    //题库表
    private String questionBank="mkt_question";
    //题库详情表
    private String questionBankDetail="mkt_question_detail";

    /**
     * 同步问卷 这里的同步每次修改主环境代码都是把问卷相关的问卷关联全删除 再全新增
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> synQuestion(String roleName,Long questionnaireId) {
        Map<String,Object> result = new HashMap<>();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire==null){
            throw new SystemException("对应问卷不存在");
        }
        //查出问卷关联题库的关联信息
        List<QuestRel> relListByQuestionnaireId = questRelMapper.findRelListByQuestionnaireId(questionnaire.getNaireId());

        Questionnaire questionnaire1 = questionnairePrdMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire1==null){
            questionnairePrdMapper.insert(questionnaire);
            //增加题库关联信息
            if(!relListByQuestionnaireId.isEmpty()){
                questRelPrdMapper.insertBatch(relListByQuestionnaireId);
            }
        }else{
            questionnairePrdMapper.updateByPrimaryKey(questionnaire);
            questRelPrdMapper.deleteByNaireId(questionnaireId);
            if(!relListByQuestionnaireId.isEmpty()){
                questRelPrdMapper.insertBatch(relListByQuestionnaireId);
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","同步成功");
        return result;
    }


    /**
     * 全量同步调查问卷
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchQuestion(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //问卷
        batchQuestion(roleName,question);
        //问卷关联
        batchQuestionRel(roleName,questionRel);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 全量同步问卷题库
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchQuestionBank(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //题库同步
        batchQuestionBank(roleName,questionBank);
        //题库详情
        batchQuestionBankDetail(roleName,questionBankDetail);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return  maps;
    }


    /**
     * 同步单个问卷题库  这里的同步每次修改主环境代码都是把题库相关的题库详情全删除 再全新增
     * @param roleName
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> synQuestionBank(String roleName, Long questionnaireId) {
        Map<String,Object> maps = new HashMap<>();
        Question question = questionMapper.selectByPrimaryKey(questionnaireId);
        if(question==null){
            throw new SystemException("对应题库信息不存在");
        }
        //查出题库关联的题库详情
        List<QuestionDetail> detailListByQuestionId = questionDetailMapper.findDetailListByQuestionId(question.getQuestionId());

        Question question1 = questionPrdMapper.selectByPrimaryKey(questionnaireId);
        if(question1==null){
            questionPrdMapper.insert(question);
            if(!detailListByQuestionId.isEmpty()){
               for (QuestionDetail questionDetail:detailListByQuestionId){
                   questionDetailPrdMapper.insert(questionDetail);
               }
            }
        }else{
            questionPrdMapper.updateByPrimaryKey(question);
            if(!detailListByQuestionId.isEmpty()){
                questionDetailPrdMapper.deleteByQuestionId(questionnaireId);
                for (QuestionDetail questionDetail:detailListByQuestionId){
                    questionDetailPrdMapper.insert(questionDetail);
                }
            }
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除单个问卷题库
     * @param roleName
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> deleteQuestionBank(String roleName, Long questionnaireId) {
        Map<String,Object> maps = new HashMap<>();
        questionPrdMapper.deleteByPrimaryKey(questionnaireId);
        questionDetailPrdMapper.deleteByQuestionId(questionnaireId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除单个调研问卷
     * @param roleName
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> deleteQuestion(String roleName, Long questionnaireId) {
        Map<String,Object> maps = new HashMap<>();
        questionnairePrdMapper.deleteByPrimaryKey(questionnaireId);
        questRelPrdMapper.deleteByNaireId(questionnaireId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


    /**
     * 题库
     * @param roleName
     */
    public void batchQuestionBank(String roleName,String tableName){
        List<Question> prdList = questionMapper.selectAll();
        List<Question> realList = questionPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<Question> addList=new ArrayList<Question>();
        List<Question> updateList=new ArrayList<Question>();
        List<Question> deleteList=new ArrayList<Question>();
        for(Question c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getQuestionId()-realList.get(i).getQuestionId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(Question c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getQuestionId()-prdList.get(i).getQuestionId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(Question c:addList){
            questionPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getQuestionId(), SynchronizeType.add.getType());
        }

        //开始修改
        for(Question c:updateList){
            questionPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getQuestionId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(Question c:deleteList){
            questionPrdMapper.deleteByPrimaryKey(c.getQuestionId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getQuestionId(), SynchronizeType.delete.getType());
        }
    }



    /**
     * 题库详情
     * @param roleName
     */
    public void batchQuestionBankDetail(String roleName,String tableName){
        List<QuestionDetail> prdList = questionDetailMapper.selectAll();
        List<QuestionDetail> realList = questionDetailPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<QuestionDetail> addList=new ArrayList<QuestionDetail>();
        List<QuestionDetail> updateList=new ArrayList<QuestionDetail>();
        List<QuestionDetail> deleteList=new ArrayList<QuestionDetail>();
        for(QuestionDetail c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getQstDetailId()-realList.get(i).getQstDetailId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(QuestionDetail c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getQstDetailId()-prdList.get(i).getQstDetailId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(QuestionDetail c:addList){
            questionDetailPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getQstDetailId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(QuestionDetail c:updateList){
            questionDetailPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getQstDetailId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(QuestionDetail c:deleteList){
            questionDetailPrdMapper.deleteByPrimaryKey(c.getQuestionId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getQstDetailId(), SynchronizeType.delete.getType());
        }
    }


    /**
     * 调查问卷
     * @param roleName
     * @param tableName
     */
    public void batchQuestion(String roleName,String tableName){
        List<Questionnaire> prdList = questionnaireMapper.selectAll();
        List<Questionnaire> realList = questionnairePrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<Questionnaire> addList=new ArrayList<Questionnaire>();
        List<Questionnaire> updateList=new ArrayList<Questionnaire>();
        List<Questionnaire> deleteList=new ArrayList<Questionnaire>();
        for(Questionnaire c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getNaireId()-realList.get(i).getNaireId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(Questionnaire c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getNaireId()-prdList.get(i).getNaireId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(Questionnaire c:addList){
            questionnairePrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getNaireId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(Questionnaire c:updateList){
            questionnairePrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getNaireId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(Questionnaire c:deleteList){
            questionnairePrdMapper.deleteByPrimaryKey(c.getNaireId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getNaireId(), SynchronizeType.delete.getType());
        }
    }



    /**
     * 问卷题库相关信息
     * @param roleName
     * @param tableName
     */
    public void batchQuestionRel(String roleName,String tableName){
        List<QuestRel> prdList = questRelMapper.selectAll();
        List<QuestRel> realList = questRelPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<QuestRel> addList=new ArrayList<QuestRel>();
        List<QuestRel> updateList=new ArrayList<QuestRel>();
        List<QuestRel> deleteList=new ArrayList<QuestRel>();
        for(QuestRel c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getRelId()-realList.get(i).getRelId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(QuestRel c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getRelId()-prdList.get(i).getRelId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(QuestRel c:addList){
            questRelPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getRelId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(QuestRel c:updateList){
            questRelPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getRelId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(QuestRel c:deleteList){
            questRelPrdMapper.deleteByPrimaryKey(c.getQuestionId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getRelId(), SynchronizeType.delete.getType());
        }
    }



}
