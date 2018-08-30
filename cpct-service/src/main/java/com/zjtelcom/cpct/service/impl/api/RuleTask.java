package com.zjtelcom.cpct.service.impl.api;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class RuleTask implements Callable<List<Map<String, Object>>> {

    @Autowired
    private FilterRuleMapper filterRuleMapper; //过滤规则

    @Autowired
    private UserListMapper userListMapper; //过滤规则（红名单、黑名单数据）

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //活动策略关联

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired
    private PpmProductMapper ppmProductMapper; //销售品

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper; //协同渠道配置基本信息

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper; //规则存储公共表（此处查询协同渠道子策略规则和话术规则）

    @Autowired
    private MktCamScriptMapper mktCamScriptMapper; //营销脚本

    @Autowired
    private MktVerbalMapper mktVerbalMapper; //话术

    @Autowired
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;


    private Long strategyConfId; //策略配置id

    private Long tarGrpId;

    private String productStr;

    private String evtContactConfIdStr;

    private RedisUtils redisUtils;

    public RuleTask(Long strategyConfId, Long tarGrpId, String productStr, String evtContactConfIdStr) {
        this.strategyConfId = strategyConfId;
        this.tarGrpId = tarGrpId;
        this.productStr = productStr;
        this.evtContactConfIdStr = evtContactConfIdStr;
    }

    @Override
    public List<Map<String, Object>> call() {

        //初始化返回结果中的推荐信息列表
        List<Map<String, Object>> recommendList = new ArrayList<>();

        //  1.判断活动的过滤规则---------------------------
        //获取过滤规则
        //FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(ruleConfId);
        boolean ruleFilter = true;
        List<Long> FilterRuleList = mktStrategyFilterRuleRelMapper.selectByStrategyId(strategyConfId);
        for (Long FilterRuleId : FilterRuleList) {
            FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(FilterRuleId);
            //匹配事件过滤规则
            int flag = 0;
            flag = userListMapper.checkRule("", filterRule.getRuleId(), null);
            if (flag > 0) {
                ruleFilter = false;
            }
        }
        //若存在不符合的规则 结束当前规则循环
        if (!ruleFilter) {
            return null;
        }
        //String ruleConfIdStr = filterRuleConfDO.getFilterRuleIds();
/*        if (ruleConfIdStr != null) {
            String[] array = ruleConfIdStr.split(",");

            for (String str : array) {
                //获取具体规则
                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.parseLong(str));

                //匹配事件过滤规则
                int flag = 0;
                flag = userListMapper.checkRule("", filterRule.getRuleId(), null);
                if (flag > 0) {
                    ruleFilter = false;
                }
            }
            //若存在不符合的规则 结束当前规则循环
            if (!ruleFilter) {
                return null;
            }
        }*/

        //  2.判断活动的客户分群规则---------------------------
        //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

//        String key = "EVENT_RULE_" + mktCampaignId + "_" + mktStrategyConfId + "_" + mktStrategyConfRuleId;
        String key = "EVENT_RULE";
        //判断redis中是否存在
        String express = "";
        if (redisUtils.exists(key)) {
            express = (String) redisUtils.get(key);
        } else {
            //若redis中不存在key，则从数据库中查询并拼装表达式
            //查询分群规则list
            List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            //将规则拼装为表达式
            StringBuilder expressSb = new StringBuilder();
            expressSb.append("if(");
            //遍历所有规则
            for (int i = 0; i < tarGrpConditionDOs.size(); i++) {
                String type = tarGrpConditionDOs.get(i).getOperType();
                Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));
                expressSb.append("(");
                expressSb.append(label.getInjectionLabelCode());
                if ("1000".equals(type)) {
                    expressSb.append(">");
                } else if ("2000".equals(type)) {
                    expressSb.append("<");
                } else if ("3000".equals(type)) {
                    expressSb.append("==");
                } else if ("4000".equals(type)) {
                    expressSb.append("!=");
                } else if ("5000".equals(type)) {
                    expressSb.append(">=");
                } else if ("6000".equals(type)) {
                    expressSb.append("<=");
                }
                expressSb.append(tarGrpConditionDOs.get(i).getRightParam());
                expressSb.append(")");
                if (i + 1 != tarGrpConditionDOs.size()) {
                    expressSb.append("&&");
                }
            }
            expressSb.append(") {return true} else {return false}");

            express = expressSb.toString();
        }

        try {
            //规则引擎计算
            RuleResult ruleResult = runner.executeRule(express, context, true, true);

            //log输出 todo es存储log
//            logger.info("======================================");
//            logger.info("事件流水 = {}", ISI);
//            logger.info("事件ID = {}", eventNbr);
//            logger.info("活动ID = {}", activityId);
//            logger.info("express = {}", express);
//            logger.info("result = {}", ruleResult.getResult());
//            logger.info("tree = {}", ruleResult.getRule().toTree());
//            logger.info("trace = {}", ruleResult.getTraceMap());
//            logger.info("======================================");

            if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {
                //查询销售品列表
                String[] productArray = productStr.split(",");

                //初始化返回结果中的销售品条目
                List<Map<String, String>> productList = new ArrayList<>();
                for (String str : productArray) {
                    Map<String, String> product = new HashMap<>();
                    PpmProduct ppmProduct = ppmProductMapper.selectByPrimaryKey(Long.parseLong(str));

                    product.put("productCode", ppmProduct.getProductCode());
                    product.put("productFlag", ""); //todo 不明 案例上有 文档上没有
                    product.put("productAlias", ""); //todo 不明 案例上有 文档上没有
                    product.put("productName", ppmProduct.getProductName());
                    product.put("productType", ppmProduct.getProductType());
                    productList.add(product);
                }

                //获取协同渠道所有id
                String[] evtContactConfIdArray = evtContactConfIdStr.split(",");
                //初始化结果集
                List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                //初始化线程池
                ExecutorService executorService = Executors.newCachedThreadPool();
                //遍历协同渠道
                for (String str : evtContactConfIdArray) {

                    //协同渠道规则表id（自建表）
                    Long evtContactConfId = Long.parseLong(str);

                    //提交线程
                    Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList));
                    //将线程处理结果添加到结果集
                    threadList.add(f);
                }

                //获取结果
                try {
                    for (Future<Map<String, Object>> future : threadList) {
                        recommendList.add(future.get());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //发生异常关闭线程池
                    executorService.shutdown();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    //发生异常关闭线程池
                    executorService.shutdown();
                    return null;
                }

                //关闭线程池
                executorService.shutdown();
            } else {
                //判断失败 返回
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //todo 异常处理
        }

        return recommendList;
    }


}
