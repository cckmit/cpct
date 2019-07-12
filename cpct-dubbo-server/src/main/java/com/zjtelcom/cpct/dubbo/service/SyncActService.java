package com.zjtelcom.cpct.dubbo.service;

import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;

/**
 * @Auther: anson
 * @Date: 2018/12/5
 * @Description:
 */
public interface SyncActService {

    ResponseHeaderModel syncActivity(Long mktCampaignId);
}
