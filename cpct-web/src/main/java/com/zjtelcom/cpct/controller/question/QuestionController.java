package com.zjtelcom.cpct.controller.question;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.dto.question.QuestionAddVO;
import com.zjtelcom.cpct.dto.question.QuestionEditVO;
import com.zjtelcom.cpct.dto.question.QuestionnaireParam;
import com.zjtelcom.cpct.service.question.QuestionService;
import com.zjtelcom.cpct.service.question.QuestionnaireService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/question")
public class QuestionController extends BaseController {

    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private QuestionService questionService;






    /**
     * 获取问卷列表（不分页）
     * @return
     */
    @PostMapping("listSimpleNaire")
    @CrossOrigin
    public Map<String, Object> listSimpleNaire() {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.listSimpleNaire();
        } catch (Exception e) {
            logger.error("[op:QuestionController] fail to listSimpleNaire ", e);
        }
        return maps;

    }

    /**
     * 删除题库问题
     * @param params
     * @return
     */
    @PostMapping("delQuestion")
    @CrossOrigin
    public Map<String, Object> delQuestion(@RequestBody HashMap<String,Long> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionService.delQuestion(1L,params.get("questionId"));
        } catch (Exception e) {
            logger.error("[op:QuestionController] fail to delQuestion ", e);
        }
        return maps;

    }


    /**
     * 获取题库问题详
     * @param params
     * @return
     */
    @PostMapping("getQuestionDetail")
    @CrossOrigin
    public Map<String, Object> getQuestionDetail(@RequestBody HashMap<String,Long> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionService.getQuestionDetail(params.get("questionId"));
        } catch (Exception e) {
            logger.error("[op:QuestionController] fail to addQuestion ", e);
        }
        return maps;

    }
    /**
     * 获取题库列表
     * @param params
     * @return
     */
    @PostMapping("listQuestion")
    @CrossOrigin
    public Map<String, Object> listQuestion(@RequestBody HashMap<String,Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            Question question = new Question();
            question.setQuestionName(MapUtil.getString(params.get("questionName")));
            question.setQuestionType(MapUtil.getString(params.get("questionType")));
            question.setAnswerType(MapUtil.getString(params.get("answerType")));
            Integer page = MapUtil.getIntNum(params.get("page"));
            Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
            maps = questionService.listQuestion(question,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:QuestionController] fail to delQuestionnaire ", e);
        }
        return maps;
    }

    /**
     * 添加题库问题
     * @param addVO
     * @return
     */
    @PostMapping("addQuestion")
    @CrossOrigin
    public Map<String, Object> addQuestion(@RequestBody QuestionAddVO addVO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionService.addQuestion(1L,addVO);
        } catch (Exception e) {
            logger.error("[op:QuestionController] fail to addQuestion ", e);
        }
        return maps;
    }

    /**
     * 编辑题库问题
     * @param editVO
     * @return
     */
    @PostMapping("modQuestion")
    @CrossOrigin
    public Map<String, Object> modQuestion(@RequestBody QuestionEditVO editVO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionService.modQuestion(1L,editVO);
        } catch (Exception e) {
            logger.error("[op:QuestionController] fail to modQuestion ", e);
        }
        return maps;
    }


    /**
     * 保存问卷-新
     * @param addVO
     * @return
     */
    @PostMapping("createQuestionnaire")
    @CrossOrigin
    public Map<String, Object> createQuestionnaire(@RequestBody  QuestionnaireParam addVO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.createQuestionnaire(addVO,true);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
        }
        return maps;
    }

    /**
     * 发布问卷-新
     * @param addVO
     * @return
     */
    @PostMapping("releaseQuestionnaire")
    @CrossOrigin
    public Map<String, Object> releaseQuestionnaire(@RequestBody QuestionnaireParam addVO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.releaseQuestionnaire(addVO);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
        }
        return maps;
    }


    /**
     * 编辑问卷-新
     * @param editvo
     * @return
     */
    @PostMapping("modQuestionnaire")
    @CrossOrigin
    public Map<String, Object> modQuestionnaire(@RequestBody QuestionnaireParam editvo) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.modQuestionnaire(editvo);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to modQuestionnaire ", e);
        }
        return maps;
    }

    /**
     * 获取问卷-新
     * @param
     * @return
     */
    @PostMapping("getQuestionnaire")
    @CrossOrigin
    public Map<String, Object> getQuestionnaire(@RequestBody HashMap<String,Long> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.getQuestionnaire(params.get("questionnaireId"));
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to modQuestionnaire ", e);
        }
        return maps;
    }

    @PostMapping("delQuestionnaire")
    @CrossOrigin
    public Map<String, Object> delQuestionnaire(@RequestBody HashMap<String,Long> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.delQuestionnaire(params.get("questionnaireId"));
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to delQuestionnaire ", e);
        }
        return maps;

    }

    @PostMapping("getQuestionnaireList")
    @CrossOrigin
    public Map<String, Object> getQuestionnaireList(@RequestBody HashMap<String, Object> param){
        Map<String, Object> maps = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            maps = questionnaireService.getQuestionnaireList(userId,param);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
        }
        return maps;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------


//    @PostMapping("modQuestionnaire")
//    @CrossOrigin
//    public Map<String, Object> modQuestionnaire(@RequestBody QuestionReq req) {
//        Map<String, Object> maps = new HashMap<>();
//        try {
//            maps = questionnaireService.modQuestionnaire(1L,req);
//        } catch (Exception e) {
//            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
//        }
//        return maps;
//    }



//    @PostMapping("createQuestionnaire")
//    @CrossOrigin
//    public Map<String, Object> createQuestionnaire(@RequestBody QuestionReq questionReq) {
//        Map<String, Object> maps = new HashMap<>();
//        Long userId = UserUtil.loginId();
//        try {
//            maps = questionnaireService.createQuestionnaire(userId,questionReq);
//        } catch (Exception e) {
//            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
//        }
//        return maps;
//    }



//    @PostMapping("listQuestionListByQuestionId")
//    @CrossOrigin
//    public Map<String, Object> listQuestionListByQuestionId(@RequestBody HashMap<String,Long> param) {
//        Map<String, Object> maps = new HashMap<>();
//        Long userId = UserUtil.loginId();
//        try {
//            Long questionnaireId = param.get("questionnaireId");
//            maps = questionnaireService.listQuestionListByQuestionId(userId,questionnaireId);
//        } catch (Exception e) {
//            logger.error("[op:TarGrpController] fail to listQuestionListByQuestionId ", e);
//        }
//        return maps;
//    }






}
