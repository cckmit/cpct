package com.zjtelcom.cpct.service.impl.synchronize.question;

import com.zjtelcom.cpct.dao.question.MktQstQuestRelMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionDetailMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionnaireMapper;
import com.zjtelcom.cpct.domain.question.QuestRel;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynQuestionService;
import com.zjtelcom.cpct_prd.dao.question.MktQstQuestRelPrdMapper;
import com.zjtelcom.cpct_prd.dao.question.MktQuestionDetailPrdMapper;
import com.zjtelcom.cpct_prd.dao.question.MktQuestionPrdMapper;
import com.zjtelcom.cpct_prd.dao.question.MktQuestionnairePrdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

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


    /**
     * 同步问卷
     * @param questionnaireId
     * @return
     */
    @Override
    public Map<String, Object> synQuestion(Long questionnaireId) {
        Map<String,Object> result = new HashMap<>();
        Questionnaire questionnaire = questionnaireMapper.selectByPrimaryKey(questionnaireId);
        if (questionnaire==null){
            throw new SystemException("对应问卷不存在");
        }
        questionnairePrdMapper.insert(questionnaire);
        List<QuestRel> relList = questRelMapper.findRelListByQuestionnaireId(questionnaireId);
        for (QuestRel rel : relList){
            Question question = questionMapper.selectByPrimaryKey(rel.getQuestionId());
            if (question == null){
                continue;
            }
            questRelPrdMapper.insert(rel);
            questionPrdMapper.insert(question);
            List<QuestionDetail> detailList = questionDetailMapper.findDetailListByQuestionId(question.getQuestionId());
            for (QuestionDetail detail : detailList){
                questionDetailPrdMapper.insert(detail);
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","同步成功");
        return result;
    }
}
