package com.zjtelcom.cpct.dubbo.out.impl;

import com.zjtelcom.cpct.dubbo.out.TarGrpCheckApiService;
import com.zjtelcom.cpct.service.api.TarGrpCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TarGrpCheckApiServiceImpl implements TarGrpCheckApiService {

    @Autowired
    private TarGrpCheckService tarGrpCheckService;

    @Override
    public Map<String, Object> cpcTarGrpCheck(Map<String, Object> paramsMap) {
        return tarGrpCheckService.cpcTarGrpCheck(paramsMap);
    }
}
