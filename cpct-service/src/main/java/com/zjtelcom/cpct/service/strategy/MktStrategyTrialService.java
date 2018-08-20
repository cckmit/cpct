/**
 * @(#)StrategyTrialService.java, 2018/8/10.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.strategy;

/**
 * Description:
 * author: linchao
 * date: 2018/08/10 17:12
 * version: V1.0
 */
public interface MktStrategyTrialService {

    /**
     * 策略试运算下发
     *
     * @param batchNum  批次号
     * @param mktStrategyConfRuleId  规则Id
     */
    void StrategyTrial(Long batchNum, Long mktStrategyConfRuleId);

}