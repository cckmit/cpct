package com.zjtelcom.cpct.controller.question;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.question.QuestionReq;
import com.zjtelcom.cpct.service.question.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${adminPath}/question")
public class QuestionController extends BaseController {

    @Autowired
    private QuestionnaireService questionnaireService;


//    @PostMapping
//    public Map<String, Object> createQuestionnaire(Long userId, QuestionReq questionReq) {
//
//    }



}
