package com.zjtelcom.cpct.service.system;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface MsgTemplateService {
    Map<String,Object> addMsgTemplate(Map<String, Object> msgContent) throws Exception;

    Map<String, Object> delMsgTemplate(Map<String, Object> params);

    Map<String,Object> updateMsgTemplate(Map<String, Object> msgContent);

    Map<String,Object> getMsgTemplateById(Map<String, Object> idParams);

    Map<String,Object> getAllMsgTemplate();

    Map<String,Object> getPageMsgTemplate(Map<String, Object> pageParams);
}
