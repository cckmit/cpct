package com.zjtelcom.cpct.controller.question;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.question.QuestionEditVO;
import com.zjtelcom.cpct.dto.question.QuestionReq;
import com.zjtelcom.cpct.service.question.QuestionService;
import com.zjtelcom.cpct.service.question.QuestionnaireService;
import com.zjtelcom.cpct.util.FastJsonUtils;
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

    @PostMapping("delQuestionnaire")
    @CrossOrigin
    public Map<String, Object> delQuestionnaire(@RequestBody QuestionReq req) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.delQuestionnaire(1L,req);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to delQuestionnaire ", e);
        }
        return maps;

    }

    @PostMapping("modQuestionnaire")
    @CrossOrigin
    public Map<String, Object> modQuestionnaire(@RequestBody QuestionReq req) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = questionnaireService.modQuestionnaire(1L,req);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
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

    @PostMapping("createQuestionnaire")
    @CrossOrigin
    public Map<String, Object> createQuestionnaire(@RequestBody QuestionReq questionReq) {
        Map<String, Object> maps = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            maps = questionnaireService.createQuestionnaire(userId,questionReq);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to createQuestionnaire ", e);
        }
        return maps;
    }


    @PostMapping("listQuestionListByQuestionId")
    @CrossOrigin
    public Map<String, Object> listQuestionListByQuestionId(@RequestBody HashMap<String,Long> param) {
        Map<String, Object> maps = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long questionnaireId = param.get("questionnaireId");
            maps = questionnaireService.listQuestionListByQuestionId(userId,questionnaireId);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to listQuestionListByQuestionId ", e);
        }
        return maps;
    }






}