package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

public interface MktOperatorLogService {

    Map<String, Object> selectByPrimaryKey(Map<String,String> params);

    int addMktOperatorLog(String name,Long campaignId, Integer type);
}
