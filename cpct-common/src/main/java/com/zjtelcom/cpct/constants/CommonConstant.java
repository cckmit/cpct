package com.zjtelcom.cpct.constants;

/**
 * Description: CommonConstant
 * author: pengy
 * date: 2018/3/26 20:31
 */
public class CommonConstant {

    public static final String CODE_SUCCESS = "0"; //成功
    public static final String CODE_FAIL = "1"; //失败

    public static final String STATUSCD_EFFECTIVE = "1000";

    public static final String CONTENTTYPE = "application/vnd.ms-excel;charset=utf-8";

    public static final String SEARCH_INFO_FROM_ES_URL = "http://localhost/es/searchBatchInfo";//试运算接口

    public static final String FIND_BATCH_HITS_LIST_URL = "http://localhost/es/findBatchHitsList";//redis查询抽样试算结果清单

    public static final String STRATEGY_TRIAL_TO_REDIS_URL = "http://localhost/es/findBatchHitsList";//策略试运算下发


}
