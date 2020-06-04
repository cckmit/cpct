package com.zjtelcom.cpct.service.impl.dubbo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheOfferEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheRelEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheOfferRelIndexQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheProdIndexQryService;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.rule.RuleResult;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.service.dubbo.labelResultService;
import com.zjtelcom.cpct.service.es.CoopruleService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.zjtelcom.cpct.enums.Operator.BETWEEN;

@Service
@Transactional
public class labelResultServiceImpl implements labelResultService,Runnable {
    private static final Logger log = LoggerFactory.getLogger(labelResultServiceImpl.class);

    private JSONObject esJson;
    private List<Map<String, String>> labelMapList;
    private DefaultContext<String, Object> context;
    private SysParams sysParams;
    private ExpressRunner runner;
    private List<LabelResult> labelResultList;
    private EsHitsService esHitService;  //es存储
    private SysParamsService sysParamsService;
    private EventRedisService eventRedisService;


    public labelResultServiceImpl(HashMap<String, Object> hashMap) {
        this.esJson = (JSONObject)hashMap.get("esJson");
        this.labelMapList = (List<Map<String, String>>)hashMap.get("labelMapList");
        this.context = (DefaultContext<String, Object>)hashMap.get("context");
        this.sysParams = (SysParams)hashMap.get("sysParams");
        this.runner = (ExpressRunner)hashMap.get("runner");
        this.labelResultList = (List<LabelResult>)hashMap.get("labelResultList");
        this.sysParamsService = (SysParamsService)hashMap.get("sysParamsService");
        this.esHitService = (EsHitsService)hashMap.get("esHitService");
        this.eventRedisService = (EventRedisService)hashMap.get("eventRedisService");
    }



//    @Autowired
//    private EsHitsService esHitService;  //es存储
//
//
//    @Autowired
//    private SysParamsService sysParamsService;
//
//
//    @Autowired
//    private EventRedisService eventRedisService;


    /**
     * 异步执行当个标签比较结果
     */
    @Override
    public void run() {
        // 从redis 中获取所有的时间类型标签集合
        Map<String, Object> labelCodeListRedis = eventRedisService.getRedis("LABEL_CODE_LIST");
        List<String> labelCodeList = new ArrayList<>();
        if (labelCodeListRedis != null) {
            labelCodeList = (List<String>) labelCodeListRedis.get("LABEL_CODE_LIST");
        }

        for (Map<String, String> labelMap : labelMapList) {
            String type = labelMap.get("operType");
            //保存标签的es log
            LabelResult lr = new LabelResult();
            lr.setOperType(type);
            lr.setLabelCode(labelMap.get("code"));
            lr.setLabelName(labelMap.get("name"));
            if ("PROM_LIST".equals(labelMap.get("code"))) {
                Long filterRuleId = Long.valueOf(labelMap.get("rightParam"));
                Map<String, Object> filterRuleRedis = eventRedisService.getRedis("FILTER_RULE_", filterRuleId);
                FilterRule filterRule = new FilterRule();
                if (filterRuleRedis != null) {
                    filterRule = (FilterRule) filterRuleRedis.get("FILTER_RULE_" + filterRuleId);
                }
                String checkProduct = filterRule.getChooseProduct();
                String s = sysParamsService.systemSwitch("PRODUCT_FILTER_SWITCH");
                if (s != null && s.equals("code")) {
                    checkProduct = filterRule.getChooseProductCode();
                }
                lr.setRightOperand(checkProduct);
            } else {
                lr.setRightOperand(labelMap.get("rightParam"));
            }

            lr.setClassName(labelMap.get("className"));

            //判断标签实例是否足够
            if (context.containsKey(labelMap.get("code"))) {
                lr.setRightParam(context.get(labelMap.get("code")).toString());
                if (sysParams != null && "1".equals(sysParams.getParamValue())) {
                    try {

                        if (labelCodeList != null && labelCodeList.contains(labelMap.get("code"))) {
                            ExpressRunner runnerQ = new ExpressRunner();

                            //将规则拼装为表达式
                            StringBuilder expressSb = new StringBuilder();
                            expressSb.append("if(");
                            expressSb.append(cpcExpression(labelMap));
                            runnerQ.addFunction("toNum", new StringToNumOperator("toNum"));
                            runnerQ.addFunction("checkProm", new PromCheckOperator("checkProm"));
                            runnerQ.addFunction("dateLabel", new ComperDateLabel("dateLabel"));
                            expressSb.append(") {return true} else {return false}");
                            RuleResult ruleResult = runnerQ.executeRule(expressSb.toString(), context, true, true);
                            if (null != ruleResult.getResult()) {
                                lr.setResult((Boolean) ruleResult.getResult());
                            } else {
                                lr.setResult(false);
                            }
                        } else {
                            RuleResult ruleResultOne = runner.executeRule(cpcLabel(labelMap.get("code"), type, labelMap.get("rightParam")), context, true, true);
                            if (null != ruleResultOne.getResult()) {
                                lr.setResult((Boolean) ruleResultOne.getResult());
                            } else {
                                lr.setResult(false);
                            }
                        }

                    } catch (Exception e) {
                        lr.setResult(false);
                    }
                }
            } else {
                lr.setRightParam("无值");
                lr.setResult(false);
            }
            labelResultList.add(lr);

        }
        esJson.put("labelResultList", JSONArray.toJSON(labelResultList));
        esHitService.save(esJson, IndexList.Label_MODULE);  //储存标签比较结果
    }

    public String cpcExpression(Map<String, String> labelMap) {
        StringBuilder express = new StringBuilder();
        // 从redis 中获取所有的时间类型标签集合
        Map<String, Object> labelCodeListRedis = eventRedisService.getRedis("LABEL_CODE_LIST");
        List<String> labelCodeList = new ArrayList<>();
        if (labelCodeListRedis != null) {
            labelCodeList = (List<String>) labelCodeListRedis.get("LABEL_CODE_LIST");
        }
        String code = labelMap.get("code");
        String type = labelMap.get("operType");
        String rightParam = labelMap.get("rightParam");

        if ("PROM_LIST".equals(code)) {
            express.append("(checkProm(").append(code).append(",").append(type).append(",").append(rightParam);
            express.append("))");
        } else if (labelCodeList.contains(code)) {
            // todo 时间类型标签
            String updateStaff = String.valueOf(labelMap.get("updateStaff"));
            if ("200".equals(updateStaff)) {
                if (type.equals(BETWEEN.getValue().toString())) {
                    String[] split = rightParam.split(",");
                    String time1 = DateUtil.getPreDay(Integer.parseInt(split[0]));
                    String time2 = DateUtil.getPreDay(Integer.parseInt(split[1]));
                    rightParam = time1 + "," + time2;
                } else {
                    rightParam = DateUtil.getPreDay(Integer.parseInt(rightParam));
                }
            }
            express.append("(dateLabel(").append(code).append(",").append(type).append(",").append("\"" + rightParam + "\"");
            express.append("))");
        } else {
            if ("7100".equals(type)) {
                express.append("!");
            }
            express.append("((");
            express.append(assLabel(code, type, rightParam));
            express.append(")");
        }
        return express.toString();
    }

    public static String assLabel(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();

        switch (type) {
            case "1000":
                express.append("toNum(").append(code).append("))");
                express.append(" > ");
                express.append(rightParam);
                break;
            case "2000":
                express.append("toNum(").append(code).append("))");
                express.append(" < ");
                express.append(rightParam);
                break;
            case "3000":
                express.append("toNum(").append(code).append("))");
                express.append(" == ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "4000":
                express.append("toNum(").append(code).append("))");
                express.append(" != ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "5000":
                express.append("toNum(").append(code).append("))");
                express.append(" >= ");
                express.append(rightParam);
                break;
            case "6000":
                express.append("toNum(").append(code).append("))");
                express.append(" <= ");
                express.append(rightParam);
                break;
            case "7100":    //不包含于
            case "7000":    //包含于
                express.append(code).append(")");
                express.append(" in ");
                String[] strArray = rightParam.split(",");
                express.append("(");
                for (int j = 0; j < strArray.length; j++) {
                    express.append("\"").append(strArray[j]).append("\"");
                    if (j != strArray.length - 1) {
                        express.append(",");
                    }
                }
                express.append(")");
                break;
            case "7200":  //区间于
                express.append("toNum(").append(code).append("))");
                String[] strArray2 = rightParam.split(",");
                express.append(" >= ").append(strArray2[0]);
                express.append(" && ").append("(toNum(");
                express.append(code).append("))");
                express.append(" <= ").append(strArray2[1]);
                break;
        }

        return express.toString();
    }

    public static String cpcLabel(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        express.append("if(");

        if ("7100".equals(type)) {
            express.append("!");
        }
        express.append("(");
        express.append(assLabel(code, type, rightParam));
        express.append(") {return true}");

        return express.toString();
    }

    /**
     * 规则引擎过滤：字符串转数字 （用于大小区间比较）
     * <p>
     * 参数：待比较字段
     */
    class StringToNumOperator extends Operator {
        public StringToNumOperator(String name) {
            this.name = name;
        }

        public Object executeInner(Object[] list) throws Exception {
            String str = (String) list[0];
            if (NumberUtils.isNumber(str)) {
                return NumberUtils.toDouble(str);
            } else {
                return str;
            }
        }
    }

    /**
     * 规则引擎过滤：销售品标签过滤
     * <p>
     * 参数：已办理销售品、类型、过滤规则配置销售品
     */
    class PromCheckOperator extends Operator {
        public PromCheckOperator(String name) {
            this.name = name;
        }

        public Object executeInner(Object[] list) throws Exception {
            boolean productCheck = false;
            //获取用户已办理销售品
            String productStr = (String) list[0];
            //获取过滤类型
            String type = list[1].toString();
            //过滤规则配置的销售品
            String[] checkProductArr = new String[list.length - 2];
            for (int i = 2; i < list.length; i++) {
                checkProductArr[i - 2] = list[i].toString();
            }
            //获取需要过滤的销售品
            if (checkProductArr != null && checkProductArr.length > 0) {
                //    String[] checkProductArr = checkProduct.split(",");
                if (productStr != null && !"".equals(productStr)) {
                    if ("7000".equals(type)) {  //存在于
                        productCheck = false;
                        for (String product : checkProductArr) {
                            int index = productStr.indexOf(product);
                            if (index >= 0) {
                                productCheck = true;
                                break;
                            }
                        }
                    } else if ("7100".equals(type)) { //不存在于
                        productCheck = true;
                        for (String product : checkProductArr) {
                            int index = productStr.indexOf(product);
                            if (index >= 0) {
                                productCheck = false;
                                break;
                            }
                        }
                    }
                } else {

                    //存在于校验
                    if ("7100".equals(type)) {
                        productCheck = true;
                    } else if ("7000".equals(type)) {
                        productCheck = false;
                    }
                }
            }

            return productCheck;

        }
    }


    // 时间类型标签比较
    class ComperDateLabel extends Operator {

        public ComperDateLabel(String name) {
            this.name = name;
        }

        @Override
        public Object executeInner(Object[] list) throws Exception {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            boolean result = false;

            String date = list[0].toString();
            String operType = list[1].toString();
            String rightParam = list[2].toString();
            // 左参数转成时间
            Date dateLeft = dateFormat.parse(date);
            // 右参数转成时间
            Integer countDay = null;
            if (rightParam.contains(",")) {
                String[] rightParamArry = rightParam.split(",");
                String rightParam1 = rightParamArry[0];
                String rightParam2 = rightParamArry[1];
                Date dateRight1 = dateFormat.parse(rightParam1);
                countDay = dateLeft.compareTo(dateRight1);
            } else {
                Date dateRight = dateFormat.parse(rightParam);
                countDay = dateLeft.compareTo(dateRight);
            }
            // 左参跟右参对比
            if ("1000".equals(operType) && countDay > 0) {            //  > 大于
                result = true;
            } else if ("2000".equals(operType) && countDay < 0) {     // < 小于
                result = true;
            } else if ("3000".equals(operType) && countDay == 0) {     // = 等于
                result = true;
            } else if ("4000".equals(operType) && countDay != 0) {     // != 不等
                result = true;
            } else if ("5000".equals(operType) && (countDay == 0 || countDay > 0)) {     // >= 大于等于
                result = true;
            } else if ("6000".equals(operType) && (countDay == 0 || countDay < 0)) {     // <= 小于等于
                result = true;
            } else if ("7200".equals(operType) && (countDay == 0 || countDay > 0)) {  // 区间与 ,右参有2个参数
                String[] rightParamArry = rightParam.split(",");  // 区间与中的两个参数
                String rightParam1 = rightParamArry[0];
                String rightParam2 = rightParamArry[1];
                Date dateRight1 = dateFormat.parse(rightParam1);
                Date dateRight2 = dateFormat.parse(rightParam2);
                int count1 = dateLeft.compareTo(dateRight1);  // 数据与区间的前一个数据比较
                int count2 = dateLeft.compareTo(dateRight2);  // 数据与区间的后一个数据比较

                if ((count1 == 0 || count1 > 0) && (count2 == 0 || count2 < 0)) {
                    result = true;
                }
            }
            return result;
        }
    }
}
