package com.zjtelcom.cpct.count.service.api;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2019/1/9
 * @Description:
 */
public interface ActivityService {

    Map<String,Object> changeActivityStatus(Map<String,Object> paramMap);   //修改活动状态

    Map<String,Object> getCampaignList(Map<String,Object> paramMap);       //加载活动列表或者删除需求函活动的关联关系

    Map<String,Object> generateRequestInfo(Map<String,Object> paramMap);   //框架活动生成子需求函
}
