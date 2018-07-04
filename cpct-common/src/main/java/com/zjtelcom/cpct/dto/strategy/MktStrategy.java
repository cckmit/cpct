package com.zjtelcom.cpct.dto.strategy;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 营销维挽策略
 */
public class MktStrategy extends BaseEntity {

    /**
     * 通用数据操作类型
     */
    private String actType;

    /**
     * 营销策略标识
     */
    private Long strategyId;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 策略类型
     */
    private String strategyType;

    /**
     * 策略内容
     */
    private String strategyDesc;

    /**
     * 计算表达式
     */
    private String ruleExpression;

}
