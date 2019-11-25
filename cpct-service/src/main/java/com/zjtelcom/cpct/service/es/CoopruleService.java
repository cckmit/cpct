package com.zjtelcom.cpct.service.es;

import java.util.List;
import java.util.Map;

public interface CoopruleService {

    void validateProduct(List<Map<String, Object>> taskList, String campaignType, String integrationId, String loginId, String lantId);
}
