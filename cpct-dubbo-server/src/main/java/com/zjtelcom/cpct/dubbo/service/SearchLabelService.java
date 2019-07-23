package com.zjtelcom.cpct.service.channel;

import java.util.List;
import java.util.Map;


public interface SearchLabelService {

    Map<String,String> labelListByCampaignId(List<Long> campaignId);

    Map<String, String> labelListByEventId(Long eventId);

}