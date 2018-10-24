package com.zjtelcom.cpct.constants;

/**
 * Description: CommonConstant
 * author: pengy
 * date: 2018/3/26 20:31
 */
public class CommonConstant {

    public static final String CODE_SUCCESS = "200"; //成功
    public static final String CODE_FAIL = "500"; //失败

    public static final String STATUSCD_EFFECTIVE = "1000";

    public static final String CONTENTTYPE = "application/vnd.ms-excel;charset=utf-8";

    public static final String STRATEGY_TRIAL_TO_REDIS_URL = "http://localhost:8080/es/strategyTrialToRedis";//策略试运算下发

    public static final String CPC_MATCH_FILE_TO_FTP = "http://134.96.252.170:30808/es/cpcMatchFileToFtp";//策略试运算下发上传文件到ftp


//    public static final String SEARCH_INFO_FROM_ES_URL = "http://134.96.252.170:30808/es/searchBatchInfo";//试运算接口
//
//    public static final String FIND_BATCH_HITS_LIST_URL = "http://134.96.252.170:30808/es/findBatchHitsList";//redis查询抽样试算结果清单
//
//    public static final String SEARCH_COUNT_INFO_URL = "http://134.96.252.170:30808/es/searchCountInfo";//策略试运算统计查询


    //本地测试用
    public static final String SEARCH_INFO_FROM_ES_URL = "http://localhost:8080/es/searchBatchInfo";//试运算接口

    public static final String FIND_BATCH_HITS_LIST_URL = "http://localhost:8080/es/findBatchHitsList";//redis查询抽样试算结果清单

    public static final String SEARCH_COUNT_INFO_URL = "http://localhost:8080/es/searchCountInfo";//策略试运算统计查询



}
