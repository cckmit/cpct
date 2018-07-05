/**
 * @(#)MktStrategyConfRuleRelService.java, 2018/7/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.strategy;

import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;

import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/07/04 09:49
 * version: V1.0
 */
public interface MktStrategyConfRuleRelService {

    Map<String, Object> saveMktStrConfRuleRel(MktStrategyConfRuleRel mktStrategyConfRuleRel);

    Map<String, Object> updateMktStrConfRuleRel(MktStrategyConfRuleRel mktStrategyConfRuleRel);

    Map<String, Object> getMktStrConfRuleRel(Long mktStrategyConfRuleRelId);

    Map<String, Object> listAllMktStrConfRuleRel();

    Map<String, Object> listMktStrConfRuleRel(Long mktStrategyConfId);

    Map<String, Object> deleteMktStrConfRuleRel(Long mktStrategyConfRuleRelId);

    Map<String, Object> deleteMktStrConfRuleRelByConfId(Long mktStrategyConfId);
}