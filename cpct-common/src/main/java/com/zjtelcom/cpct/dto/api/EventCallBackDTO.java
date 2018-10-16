package com.zjtelcom.cpct.dto.api;


import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 事件触发回调dto
 */
@Data
public class EventCallBackDTO implements Serializable {


    /**
     * 客户编码
     */
    private String custId;

    /**
     * 计算结果编码：
     * 1  有cpc计算结果
     * 1000 没有匹配推荐
     */
    private String CPCResultCode;

    /**
     * 结果描述
     */
    private String CPCResultMsg;

    /**
     * 事件流水号
     */
    private String ISI;

    /**
     * 工单
     */
    private List<Map<String,String>> orderList;

    /**
     * 工单流水 （父参数：orderList）
     */
    private String orderISI;

    /**
     * 活动id （父参数：orderList）
     */
    private String activityId;

    /**
     * 活动名称 （父参数：orderList）
     */
    private String activityName;

    /**
     * 活动类型（0：营销活动  1：服务活动） （父参数：orderList）
     */
    private String activityType;

    /**
     * 推荐信息 （父参数：orderList）
     */
    private List<Map<String,String>> recommendList;

    /**
     * 集成编号 （父参数：recommendList）
     */
    private String integrationId;

    /**
     * 内容话术 （父参数：recommendList）
     */
    private String verbal;

    /**
     * 痛痒点话术 （父参数：recommendList）
     */
    private String keyNote;

    /**
     * 推荐指引 （父参数：recommendList）
     */
    private String reason;

    /**
     * 温馨提示（红色） （父参数：recommendList）
     */
    private String reminder;

    /**
     * 知识库连接（红色） （父参数：recommendList）
     */
    private String knowledgeBase;

    /**
     * 首页预判 （父参数：recommendList）
     */
    private String estimate;

    /**
     * 渠道 （父参数：recommendList）
     */
    private String channelId;

    /**
     * 比价内容 （父参数：recommendList）
     */
    private String compareContent;

    /**
     * 推送方式 1：弹框 2：短信 3:接口 （父参数：recommendList）
     */
    private String pushType;

    /**
     * 推送内容 （父参数：recommendList）
     */
    private String pushContent;

    /**
     * 推荐产品id（内部id，用来定位本次推荐对应的派单策略），
     * 一个对推荐产品id对应一个销售品条目，包括多个销售品信息 （黄色） （父参数：recommendList）
     */
    private Integer recommendProductId;

    /**
     * 策略编码 （父参数：recommendList）
     */
    private String policyId;

    /**
     * 规则编码 （父参数：recommendList）
     */
    private String ruleId;

    /**
     * 销售品条目 （父参数：recommendList）
     */
    private List<Map<String,String>> productList;

    /**
     * 销售品编码 （父参数：productList）
     */
    private String productCode;

    /**
     * 销售品名称 （父参数：productList）
     */
    private String productName;

    /**
     * 销售品类型 （父参数：productList）
     */
    private String productType;

    /**
     * 销售品标签 （父参数：productList）
     */
    private String productFlag;

    /**
     * 资产下的标签信息 （父参数：recommendList）
     */
    private List<Map<String,Object>> itgTriggers;

    /**
     * 标签类型 （父参数：itgTriggers）
     */
    private String type;

    /**
     * 标签标识 （父参数：itgTriggers）
     */
    private String key;

    /**
     * 标签值 （父参数：itgTriggers）
     */
    private String value;

    /**
     * 0 展现 1 不展现，默认 0 （父参数：itgTriggers）
     */
    private String display;

    /**
     * 标签名称 （父参数：itgTriggers）
     */
    private String name;

    /**
     * triggerList （父参数：itgTriggers）
     */
    private String triggerList;

    /**
     * 客户标签列表
     */
    private List<Map<String,String>> triggers;


//    /**
//     * 标签标识 （父参数：triggers）
//     */
//    private String key;
//
//    /**
//     * 标签值 （父参数：triggers）
//     */
//    private String value;




}
