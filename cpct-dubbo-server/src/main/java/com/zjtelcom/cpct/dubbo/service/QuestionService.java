package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dubbo.model.LabelCatalogModel;
import com.zjtelcom.cpct.dubbo.model.Ret;
import com.zjtelcom.cpct.dubbo.model.RetQuestion;

import java.util.Map;

public interface QuestionService {

    RetQuestion getQuestionnaireDetail(Long questionnaireId);



}
