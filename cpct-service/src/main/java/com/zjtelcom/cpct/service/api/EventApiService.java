package com.zjtelcom.cpct.service.api;

import com.zjtelcom.cpct.dto.api.EventApiResultDTO;
import com.zjtelcom.cpct.dto.api.EventReportDTO;

import java.util.Map;

public interface EventApiService {

    Map<String, Object> CalculateCPC(Map<String, Object> map) throws Exception;

    void cpc();

}
