package com.zjtelcom.cpct.open.service.mktCampaign;

import com.zjtelcom.cpct.domain.campaign.MktDttsLog;

import java.util.Date;
import java.util.Map;

public interface MktDttsLogService {

    Map<String,Object> saveMktDttsLog(String dttsType, String dttsState, Date beginTime, Date endTime, String dttsResult, String remark);

    Map<String,Object> updateMktDttsLog(MktDttsLog mktDttsLog);

    Map<String,Object> getMktDttsLog(Long dttsLogId);

    Map<String,Object> getMktDttsLogList(Map<String, Object> params);
}
