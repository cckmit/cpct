package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * 问卷同步
 */
public interface SynQuestionService {


    Map<String,Object> synQuestion(Long questionnaireId);


    Map<String,Object> synchronizeBatchQuestion(String roleName);

    Map<String,Object> synchronizeBatchQuestionBank(String roleName);

}
