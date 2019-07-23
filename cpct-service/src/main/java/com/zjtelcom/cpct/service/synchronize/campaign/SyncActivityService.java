package com.zjtelcom.cpct.service.synchronize.campaign;

import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/12/5
 * @Description:
 */
public interface SyncActivityService {

    ResponseHeaderModel syncActivity(Long mktCampaignId);

    Map<String,Object> syncTotalActivity();
}
