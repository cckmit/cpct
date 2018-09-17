package com.zjtelcom.cpct.service.impl.synchronize.template;

import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
@Service
@Transactional
public class SynTarGrpTemplateServiceImpl implements SynTarGrpTemplateService{
    @Override
    public Map<String, Object> synchronizeSingleTemplate(Long templateId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchTemplate(String roleName) {
        return null;
    }
}
