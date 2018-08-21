package com.zjtelcom.cpct.elastic.service;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.elastic.model.CampaignHitParam;

import java.util.Map;

public interface EsService {

    void add();

    void save(JSONObject jsonObject,String indexName);

    Map<String,Object> searchCampaignHitsInfo(CampaignHitParam param);
}
