package com.zjtelcom.cpct.dubbo.service;

import java.util.List;
import java.util.Map;


public interface SearchLabelService  {

    Map<String,String> labelListByCampaignId(List<Long> campaignId);

}