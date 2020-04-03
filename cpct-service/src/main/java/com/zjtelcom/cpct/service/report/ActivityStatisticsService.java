package com.zjtelcom.cpct.service.report;

import java.util.Map;

public interface ActivityStatisticsService {

    Map<String,Object> getStoreForUser(Map<String, Object> params);

    Map<String,Object> getStore(Map<String, Object> params);

    Map<String,Object> getChannel(Map<String, Object> params);
    //getRptEventOrder
    Map<String,Object> getRptEventOrder(Map<String, Object> params);
    //getRptBatchOrder
    Map<String,Object> getRptBatchOrder(Map<String, Object> params);

    Map<String,Object> queryRptBatchOrderTest(Map<String, Object> params);

    Map<String,Object> getMktCampaignDetails(Map<String, Object> params);

    Map<String,Object> getActivityStatisticsByName(Map<String, Object> params);

    void MoreThan3MonthsOffline();

    Map<String,Object> delectConsumerlogByDate(Map<String, Object> params);

    Map<String,Object> getSalesClerk(Map<String, Object> params);

    Map<String,Object> queryEventOrderByReport(Map<String, String> params);

    Map<String,Object> queryEventOrderChlListByReport(Map<String, String> params);
}
