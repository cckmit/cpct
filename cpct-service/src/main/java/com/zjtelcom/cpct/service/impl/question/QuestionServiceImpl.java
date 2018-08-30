package com.zjtelcom.cpct.service.impl.question;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.question.MktQuestionDetailMapper;
import com.zjtelcom.cpct.dao.question.MktQuestionMapper;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.QuestionDetail;
import com.zjtelcom.cpct.dto.question.QuestionAddVO;
import com.zjtelcom.cpct.dto.question.QuestionDetailAddVO;
import com.zjtelcom.cpct.dto.question.QuestionEditVO;
import com.zjtelcom.cpct.service.question.QuestionService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {


    @Autowired
    private MktQuestionMapper questionMapper;
    @Autowired
    private MktQuestionDetailMapper questionDetailMapper;


    /**
     * 添加问卷问题及答案
     * @param userId
     * @param addVO
     * @return
     */
    @Override
    public Map<String, Object> addQuestion(Long userId, QuestionAddVO addVO) {
        Map<String,Object> result = new HashMap<>();
        Question question = BeanUtil.create(addVO,new Question());
        Map<String,Object> map = createQuestion(userId,question);
        if (!map.get("resultCode").equals(CODE_SUCCESS)){
            return map;
        }
        Long questionId = Long.valueOf(map.get("questionId").toString());
        if (addVO.getQuestionDetailAddVOList()!=null){
            for (QuestionDetailAddVO detailAddVO : addVO.getQuestionDetailAddVOList()){
                QuestionDetail detail = BeanUtil.create(detailAddVO,new QuestionDetail());
                detail.setQuestionId(questionId);
                detail.setCreateDate(DateUtil.getCurrentTime());
                detail.setUpdateDate(DateUtil.getCurrentTime());
                detail.setStatusDate(DateUtil.getCurrentTime());
                detail.setUpdateStaff(UserUtil.loginId());
                detail.setCreateStaff(UserUtil.loginId());
                detail.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                questionDetailMapper.insert(detail);
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        result.put("questionId",questionId);
        return result;
    }


    @Override
    public Map<String, Object> createQuestion(Long userId, Question addVO) {
        Map<String,Object> result = new HashMap<>();
        Question question = BeanUtil.create(addVO,new Question());
        question.setCreateDate(DateUtil.getCurrentTime());
        question.setUpdateDate(DateUtil.getCurrentTime());
        question.setStatusDate(DateUtil.getCurrentTime());
        question.setUpdateStaff(UserUtil.loginId());
        question.setCreateStaff(UserUtil.loginId());
        question.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        questionMapper.insert(question);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        result.put("questionId",question.getQuestionId());
        return result;
    }

    @Override
    public Map<String, Object> modQuestion(Long userId, QuestionEditVO editVO) {
        Map<String,Object> result = new HashMap<>();
        Question question = questionMapper.selectByPrimaryKey(editVO.getQuestionId());
        if (question==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","问题不存在");
            return result;
        }
        BeanUtil.copy(editVO,question);
        question.setUpdateDate(new Date());
        question.setUpdateStaff(userId);
        questionMapper.updateByPrimaryKey(question);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String, Object> delQuestion(Long userId, Long questionId) {
        return null;
    }
}
