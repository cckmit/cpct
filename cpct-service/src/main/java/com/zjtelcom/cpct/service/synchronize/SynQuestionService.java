package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * 问卷同步
 */
public interface SynQuestionService {


    Map<String,Object> synQuestion(String roleName,Long questionnaireId);

    Map<String,Object> synchronizeBatchQuestion(String roleName);

    Map<String,Object> deleteQuestion(String roleName,Long questionnaireId);

    Map<String,Object> synchronizeBatchQuestionBank(String roleName);

    Map<String,Object> synQuestionBank(String roleName,Long questionnaireId);

    Map<String,Object> deleteQuestionBank(String roleName,Long questionnaireId);


}
