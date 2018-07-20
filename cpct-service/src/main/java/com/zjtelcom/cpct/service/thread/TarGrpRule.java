/**
 * @(#)TarGrpRuleThread.java, 2018/7/18.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.thread;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/07/18 13:59
 * version: V1.0
 */
@Scope("prototype")
public class TarGrpRule extends Thread {


    private Long mktCampaignId;

    private Long mktStrategyConfId;

    private MktStrategyConfRuleDO mktStrategyConfRuleDO;

    private RedisUtils redisUtils;

    private TarGrpConditionMapper tarGrpConditionMapper;

    private InjectionLabelMapper injectionLabelMapper;

    public TarGrpRule(Long mktCampaignId, Long mktStrategyConfId, MktStrategyConfRuleDO mktStrategyConfRuleDO, RedisUtils redisUtils, TarGrpConditionMapper tarGrpConditionMapper, InjectionLabelMapper injectionLabelMapper) {
        this.mktCampaignId = mktCampaignId;
        this.mktStrategyConfId = mktStrategyConfId;
        this.mktStrategyConfRuleDO = mktStrategyConfRuleDO;
        this.redisUtils = redisUtils;
        this.tarGrpConditionMapper = tarGrpConditionMapper;
        this.injectionLabelMapper = injectionLabelMapper;
    }

    @Override
    public void run() {
        // 策略配置规则Id
        Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();
        //  2.判断活动的客户分群规则---------------------------
        //查询分群规则list
        Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
        List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
        if(tarGrpId != null && tarGrpId!=0){
            //将规则拼装为表达式
            StringBuilder express = new StringBuilder();
            if(tarGrpConditionDOs!=null && tarGrpConditionDOs.size()>0){
                express.append("if(");
                //遍历所有规则
                for (int i = 0; i < tarGrpConditionDOs.size(); i++) {
                    String type = tarGrpConditionDOs.get(i).getOperType();
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
                    express.append("(");
                    express.append(label.getInjectionLabelCode());
                    if ("1000".equals(type)) {
                        express.append(">");
                    } else if ("2000".equals(type)) {
                        express.append("<");
                    } else if ("3000".equals(type)) {
                        express.append("==");
                    } else if ("4000".equals(type)) {
                        express.append("!=");
                    } else if ("5000".equals(type)) {
                        express.append(">=");
                    } else if ("6000".equals(type)) {
                        express.append("<=");
                    }
                    express.append(tarGrpConditionDOs.get(i).getRightParam());
                    express.append(")");
                    if (i + 1 != tarGrpConditionDOs.size()) {
                        express.append("&&");
                    }
                }
            }
            express.append(") {return true} else {return false}");
            // 将表达式存入Redis
            String key = "EVENT_RULE_" + mktCampaignId + "_" + mktStrategyConfId + "_" + mktStrategyConfRuleId;
            System.out.println("key>>>>>>>>>>" + key +">>>>>>>>express->>>>:" + JSON.toJSONString(express));
            redisUtils.set(key, express);
        }
    }


   /* private Long tarGrpId;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    public TarGrpRule(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public void execute() {
        new TarGrpRuleThread(tarGrpId).start();
    }

    private class TarGrpRuleThread extends thread {
        private Long tarGrpId;

        public TarGrpRuleThread(Long tarGrpId) {
            this.tarGrpId = tarGrpId;
        }

        public void run(){
            //  2.判断活动的客户分群规则---------------------------
            //查询分群规则list
            List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            //将规则拼装为表达式
            StringBuilder express = new StringBuilder();
            express.append("if(");
            //遍历所有规则
            for (int i = 0; i < tarGrpConditionDOs.size(); i++) {
                String type = tarGrpConditionDOs.get(i).getOperType();
                Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
                express.append("(");
                express.append(label.getInjectionLabelCode());
                if ("1000".equals(type)) {
                    express.append(">");
                } else if ("2000".equals(type)) {
                    express.append("<");
                } else if ("3000".equals(type)) {
                    express.append("==");
                } else if ("4000".equals(type)) {
                    express.append("!=");
                } else if ("5000".equals(type)) {
                    express.append(">=");
                } else if ("6000".equals(type)) {
                    express.append("<=");
                }
                express.append(tarGrpConditionDOs.get(i).getRightParam());
                express.append(")");
                if (i + 1 != tarGrpConditionDOs.size()) {
                    express.append("&&");
                }
            }
            express.append(") {return true} else {return false}");
        }

    }
*/
}


