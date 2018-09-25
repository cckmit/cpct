package com.zjtelcom.cpct.service.synchronize.template;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynTarGrpTemplateService {


    Map<String,Object> synchronizeSingleTemplate(Long templateId, String roleName);

    Map<String,Object> synchronizeBatchTemplate(String roleName);


}
