package com.zjtelcom.cpct.service.es;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.elastic.model.CampaignHitParam;

import java.util.List;
import java.util.Map;

public interface EsHitsService {

    void add() throws Exception;

    void save(JSONObject jsonObject, String indexName);

    void save(JSONObject jsonObject, String indexName, String _id);

    Map<String,Object> searchCampaignHitsInfo(CampaignHitParam param);

    Map<String,Object> searchCampaignHitsTotal(CampaignHitParam param);

    Map<String,Object> searchLabelInfoByRuleId(String ruleId, String isi, String hitEntity);

    List<Map<String, Object> > search(List<String> assetList);

    Map<String,Object> getCustomer(Map<String, String> params);

    Map<String,Object> getCustomerByCustId(Map<String, String> params);

}
