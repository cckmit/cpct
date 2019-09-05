package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.rule.RuleResult;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.TarGrpApiService;

import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/04/22 14:11
 * @version: V1.0
 */
@Service
public class TarGrpApiServiceImpl implements TarGrpApiService {

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired
    private EsHitsService esHitService;  //es存储

    @Autowired
    private RedisUtils redisUtils;  // redis方法

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired
    private FilterRuleMapper filterRuleMapper; //过滤规则

    /**
     * 获取命中的客户分群
     *
     * @param paramsMap
     * @return
     */
    @Override
    public Map<String, Object> getCpcTargrp(Map<String, Object> paramsMap) {

        Map<String, String> mktAllLabel = new HashMap<>();
        // 获取客户分群Id集合
        List<String> targrpIdList = (List<String>) paramsMap.get("targrpIdList");
        // 获取所有客户分群标签
        List<Map<String, String>> mktAllLabelList = new ArrayList();
        for (String tarGrpId : targrpIdList) {
            //查询客户分群下所有标签
            // List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("TAR_GRP_LABEL_" + tarGrpId);
            List<Map<String, String>> labelMapList = null;
            if (labelMapList == null) {
                try {
                    labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(Long.valueOf(tarGrpId));
                } catch (Exception e) {
                    return Collections.EMPTY_MAP;
                }
                redisUtils.set("TAR_GRP_LABEL_" + tarGrpId, labelMapList);
            }
            mktAllLabelList.addAll(labelMapList);
        }
        List<String> mktAllLabelStrList = new ArrayList<>();
        for (Map<String, String> mktAllLabelMap : mktAllLabelList) {
            if (!mktAllLabelStrList.contains(mktAllLabelMap.get("code"))) {
                mktAllLabelStrList.add(mktAllLabelMap.get("code"));
            }
        }

        // 所有资产的所有标签
        mktAllLabel.put("assetLabels", ChannelUtil.StringList2String(mktAllLabelStrList));

        List<Map<String, Object>> assetParamList = (List<Map<String, Object>>) paramsMap.get("assetParamList");
        List<Future<Map<String, Object>>> futureList = new ArrayList<>();
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        boolean result = true;
        Map<String, Object> resultMap = new HashMap<>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            for (Map<String, Object> assetParamMap : assetParamList) {
                Future<Map<String, Object>> targrpLabelFuture = executorService.submit(new TargrpLabelTask(targrpIdList, mktAllLabel, assetParamMap));
                futureList.add(targrpLabelFuture);
            }

            for (Future<Map<String, Object>> future : futureList) {
                try {
                    List<Map<String, Object>> assetResultList = new ArrayList<>();
                    if (future.get() != null && !future.get().isEmpty()) {
                        String resultCode = (String) ((Map<String, Object>) future.get()).get("resultCode");
                        if ("2000".equals(resultCode)){
                            Map<String, Object> resultDataMap = (Map<String, Object>) (((Map<String, Object>) future.get()).get("resultData"));
                            resultMapList.add(resultDataMap);
                            result = false;
                        }
                    } else {
                        result = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resultMap.put("resultCode", "0");
                    resultMap.put("resultMsg", "分群校验异常：线程处理异常");
                    return resultMap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("resultCode", "0");
            resultMap.put("resultMsg", "分群校验异常");
            return resultMap;
        } finally {
            executorService.shutdown();
        }
        if(result){
            resultMap.put("resultCode", "1000");
        } else {
            resultMap.put("resultCode", "2000");
            resultMap.put("assetParamList", resultMapList);
        }
        return resultMap;
    }


    class TargrpLabelTask implements Callable<Map<String, Object>> {

        private List<String> targrpIdList;
        private Map<String, String> mktAllLabel;
        private Map<String, Object> assetParamMap;

        public TargrpLabelTask(List<String> targrpIdList, Map<String, String> mktAllLabel, Map<String, Object> assetParamMap) {
            this.targrpIdList = targrpIdList;
            this.mktAllLabel = mktAllLabel;
            this.assetParamMap = assetParamMap;
        }

        @Override
        public Map<String, Object> call() throws Exception {

            //从hyh接口获取标签实例
            Map<String, String> privateParams = new HashMap<>();
            privateParams.put("isCust", "1"); //资产级
            privateParams.put("accNbr", (String) assetParamMap.get("accNbr"));
            privateParams.put("integrationId", (String) assetParamMap.get("integrationId"));// 资产集成编码
            privateParams.put("lanId", (String) assetParamMap.get("lanId"));


            DefaultContext<String, Object> context = getAssetLabel(mktAllLabel, privateParams);

            boolean tarGrpResult = true;
            // 遍历策略查询
            for (String tarGrpId : targrpIdList) {
                //查询客户分群下所有标签
                List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("TAR_GRP_LABEL_" + tarGrpId);
                if (labelMapList == null) {
                    try {
                        labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(Long.valueOf(tarGrpId));
                    } catch (Exception e) {
                        System.out.println("分群标签数据库查询失败");
                        tarGrpResult = false;
                    }
                    redisUtils.set("TAR_GRP_LABEL_" + tarGrpId, labelMapList);
                }
                if (labelMapList == null || labelMapList.size() <= 0) {
//                    return Collections.EMPTY_MAP;
                    System.out.println("分群标签为空");
                    tarGrpResult = false;
                }

                //拼装redis key
                ExpressRunner runner = new ExpressRunner();
                runner.addFunction("toNum", new StringToNumOperator("toNum"));

                //判断表达式在缓存中有没有
                // String express = (String) redisUtils.get("EXPRESS_TAR_GRP_" + tarGrpId);

                String express = null;
                if (express == null || "".equals(express)) {
//                    List<LabelResult> labelResultList = new ArrayList<>();
                    try {
//                        LabelResult lr;
                        //将规则拼装为表达式
                        StringBuilder expressSb = new StringBuilder();
                        expressSb.append("if(");
                        //遍历所有规则
                        for (Map<String, String> labelMap : labelMapList) {
                            String type = labelMap.get("operType");

                            if ("PROM_LIST".equals(labelMap.get("code"))) {
                                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.valueOf(labelMap.get("rightParam")));
                                labelMap.put("rightParam", filterRule.getChooseProduct());
                            }

                            //保存标签的es log
//                            lr = new LabelResult();
                            //拼接表达式：主表达式
                            expressSb.append(cpcExpression(labelMap.get("code"), type, labelMap.get("rightParam")));

                            //判断标签实例是否足够
//                            if (context.containsKey(labelMap.get("code"))) {
//                                try {
//                                    RuleResult ruleResultOne = runner.executeRule(cpcLabel(labelMap.get("code"), type, labelMap.get("rightParam")), context, true, true);
//                                    if (null != ruleResultOne.getResult()) {
////                                        lr.setResult((Boolean) ruleResultOne.getResult());
//                                    } else {
////                                        lr.setResult(false);
//                                        tarGrpResult = false;
//                                        break;
//                                    }
//                                } catch (Exception e) {
////                                    lr.setResult(false);
//                                    tarGrpResult = false;
//                                    break;
//                                }
//                            } else {
////                                lr.setRightParam("无值");
////                                lr.setResult(false);
//                                tarGrpResult = false;
//                                break;
//                            }

                            if (!context.containsKey(labelMap.get("code"))) {
                                tarGrpResult = false;
                                break;
                            }

                            expressSb.append("&&");
//                            labelResultList.add(lr);
                        }

                        expressSb.delete(expressSb.length() - 2, expressSb.length());
                        expressSb.append(") {return true} else {return false}");
                        express = expressSb.toString();

                    } catch (Exception e) {
//                        return Collections.EMPTY_MAP;
                        tarGrpResult = false;
                    }
                    //表达式存入redis
                    redisUtils.set("EXPRESS_TAR_GRP_" + tarGrpId, express);
                }

                if(tarGrpResult) {
                    //规则引擎计算
                    RuleResult ruleResult = null;
                    ExpressRunner runnerQ = new ExpressRunner();
                    runnerQ.addFunction("toNum", new StringToNumOperator("toNum"));
                    runnerQ.addFunction("checkProm", new PromCheckOperator("checkProm"));
                    runnerQ.addFunction("dateLabel", new ComperDateLabel("dateLabel"));
                    try {
                        ruleResult = runnerQ.executeRule(express, context, true, true);
                    } catch (Exception e) {
//                    return Collections.EMPTY_MAP;
                        tarGrpResult = false;
                    }

                    if (ruleResult != null && ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {
                        //命中
                    } else {
                        tarGrpResult = false;
                        break;
                    }
                }
            }
            Map<String, Object> resultMap = new HashMap<>();
            Map<String, Object> resultDataMap = new HashMap<>();
            if (!tarGrpResult) {
                resultDataMap.put("accNbr", (String) assetParamMap.get("accNbr"));
                resultDataMap.put("integrationId", (String) assetParamMap.get("integrationId"));// 资产集成编码
                resultDataMap.put("lanId", (String) assetParamMap.get("lanId"));
                resultMap.put("resultCode", "2000");
                resultMap.put("resultData", resultDataMap);
            } else {
                resultMap.put("resultCode", "1000");
            }
            return resultMap;
        }
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

    public String cpcExpression(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();

        // 从redis 中获取所有的时间类型标签集合
        List<String> labelCodeList = (List<String>) redisUtils.get("LABEL_CODE_LIST");
        if (labelCodeList == null) {
            labelCodeList = injectionLabelMapper.selectLabelCodeByType("1100");// 1100 代表为时间类型的标签
            if (labelCodeList != null) {
                redisUtils.set("LABEL_CODE_LIST", labelCodeList);
            }
        }


        if ("PROM_LIST".equals(code)) {
            express.append("(checkProm(").append(code).append(",").append(type).append(",").append(rightParam);
            express.append("))");
        } else if (labelCodeList.contains(code)) {
            // todo 时间类型标签
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
            Date dateRight = dateFormat.parse(rightParam);
            // 左参跟右参对比
            int countDay = dateLeft.compareTo(dateRight);
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
            String[] checkProductArr = new String[list.length-2];
            for (int i = 2; i < list.length; i++) {
                checkProductArr[i-2] = list[i].toString();
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


    // 处理资产级标签和销售品级标签
    private DefaultContext<String, Object> getAssetLabel(Map<String, String> mktAllLabel, Map<String, String> privateParams) {
        String saleId = "";
        //资产级标签
        DefaultContext<String, Object> contextNew = new DefaultContext<String, Object>();
        if (mktAllLabel.get("assetLabels") != null && !"".equals(mktAllLabel.get("assetLabels"))) {
            JSONObject assParam = new JSONObject();
            assParam.put("queryNum", privateParams.get("accNbr"));
            assParam.put("c3", privateParams.get("lanId"));
            assParam.put("queryId", privateParams.get("integrationId"));
            assParam.put("type", "1");
            assParam.put("queryFields", mktAllLabel.get("assetLabels"));

            //因子查询-----------------------------------------------------
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(assParam));
            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                //ES log 标签实例
                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    contextNew.put(entry.getKey(), entry.getValue());

                    if ("PROM_INTEG_ID".equals(entry.getKey())) {
                        saleId = entry.getValue().toString();
                    }
                }
            }
        }

        contextNew.put("integrationId", privateParams.get("integrationId"));
        contextNew.put("accNbr", privateParams.get("accNbr"));
        return contextNew;
    }

}