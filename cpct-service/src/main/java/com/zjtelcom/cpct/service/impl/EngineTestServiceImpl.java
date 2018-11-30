package com.zjtelcom.cpct.service.impl;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.EngineTestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class EngineTestServiceImpl extends BaseService implements EngineTestService {

    @Override
    public void test(Map<String, String> map) {

        try {
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
//            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

//            String label_1 = map.get("key_1");
//            String label_2 = map.get("key_2");
//            String label_3 = map.get("key_3");
//            String label_4 = map.get("key_4");
//            String label_5 = map.get("key_5");
//
//
            context.put("1001", 1000);
            context.put("1002", 1000);
            context.put("1003", 1000);
            context.put("1004", 1000);
            context.put("1005", 1000);
            context.put("1006", 1000);
            context.put("1007", 1000);
            context.put("1008", 1000);
            context.put("1009", 1000);


//            String express = "1>=6 && 1==1 && 1==1";
//            Object r = runner.execute(express, context, null, true, false);


//            String express= "if(label_1 >= 3 and label_2 == 1 and label_3 == 1){return true;}else if(label_4 <= 10){true} else {false}";
//            String express = "label_1 >= 3 and label_2 == 1 and label_3 == 1";
//            String express = map.get("expression");
            String express = "if(" + map.get("expression") + "){return true;}else {return false}";

//            Object r = runner.execute(express, context, null, true, true);
//            logger.info("结果 = {}", r);

            //此算法仅支持if或者when等逻辑判断（待确认）
            RuleResult ruleResult = runner.executeRule(express, context, true, true);
            logger.info("======================================");
            logger.info("事件流水 = {}", "ISI");
            logger.info("活动ID = {}", "activityId");
            logger.info("express = {}", express);
            logger.info("result = {}", ruleResult.getResult());
            logger.info("script = {}", ruleResult.getScript());
            logger.info("tree = {}", ruleResult.getRule().toTree());
            logger.info("trace = {}", ruleResult.getTraceMap());
            logger.info("======================================");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
