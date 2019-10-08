package com.zjtelcom.cpct.dubbo.out.impl;


import com.zjtelcom.cpct.dubbo.out.TarGrpService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TarGrpServiceImpl implements TarGrpService {

    @Autowired
    private TarGrpTemplateService tarGrpTemplateService;

    /**
     * 分群定时批量下发
     */
    @Override
    public Map<String, Object> tarGrpTemplateScheduledBatchIssue() {
        return tarGrpTemplateService.tarGrpTemplateScheduledBatchIssue();
    }
}
