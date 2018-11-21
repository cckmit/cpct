package com.zjtelcom.cpct.service.campaign;

import java.util.Map;

public interface MktCamCycleTimingService {

    Map<String, Object> findCampaignCycleHour();

    Map<String, Object> findCampaignCycleDay();

    Map<String, Object> findCampaignCycleWeek();

    Map<String, Object> findCampaignCycleMonth();
}
