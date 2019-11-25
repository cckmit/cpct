package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.domain.campaign.MktDttsLog;

import java.util.Map;

public interface MktDttsLogService {

    Map<String,Object> saveMktDttsLog(MktDttsLog mktDttsLog);

    Map<String,Object> updateMktDttsLog(MktDttsLog mktDttsLog);

    Map<String,Object> getMktDttsLog(Long dttsLogId);

    Map<String,Object> getMktDttsLogList(Map<String, Object> params);
}
