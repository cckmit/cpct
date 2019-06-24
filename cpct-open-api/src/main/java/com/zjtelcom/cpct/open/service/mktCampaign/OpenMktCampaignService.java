package com.zjtelcom.cpct.open.service.mktCampaign;

import com.zjtelcom.cpct.open.base.service.BaseDao;

import java.util.Map;

public interface OpenMktCampaignService extends BaseDao {

    Map<String,Object> updateMktCampaign(Object object);
}