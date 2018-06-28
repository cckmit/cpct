/*
 * 文件名：TryCalcServiceImpl.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月8日
 * 修改内容：
 */

package com.zjtelcom.cpct.service.impl;


import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.model.policy.CalcReqModel;
import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.common.IDacher;
import com.zjtelcom.cpct.constants.UseTypeConstants;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.model.EagleSourceTableDef;
import com.zjtelcom.cpct.model.EagleTag;
import com.zjtelcom.cpct.model.TriggerValue;
import com.zjtelcom.cpct.pojo.Company;
import com.zjtelcom.cpct.service.TryCalcService;
import com.zjtelcom.cpct.util.SqlUtil;
import com.zjtelcom.cpct.validator.ValidateResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;
import java.util.*;


/**
 * 试算服务接口实现
 * @author taowenwu
 * @version 1.0
 * @see TryCalcServiceImpl
 * @since
 */

@Service
public class TryCalcServiceImpl implements TryCalcService {

    private static final Logger LOG = Logger.getLogger(TryCalcServiceImpl.class);

//    @Autowired
//    private EagleTagMapper tagMapper;
//
//    @Autowired
//    private EagleTrycalcRecordMapper trycalcRecordMapper;

    /**
     * 校验标签是否存在于大数据，校验标签与主表之间有没有关系
     * 
     * @param serialNum 流水号
     * @param calcReqModel 试算请求对象
     * @return 校验结果
     * @see
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public ValidateResult validate(String serialNum, CalcReqModel calcReqModel) {
        LOG.debug("calcReqModel: " + JSON.toJSONString(calcReqModel));
        List<Map<String, Object>> policyList = calcReqModel.getPolicyList();
        IDacher<EagleTag> tagCache = CacheManager.getInstance().getCache(
            CacheConstants.TAG_CACHE_NAME);
        IDacher<Trigger> triggerCache = CacheManager.getInstance().getCache(
            CacheConstants.TRIGGER_CACHE_NAME);
        IDacher<TriggerValue> triggerValueCache = CacheManager.getInstance().getCache(
            CacheConstants.TRIGGER_VALUE_CACHE_NAME);

        StringBuilder resultMessage = new StringBuilder();
        boolean flag = true;
        String recommendType = null;
        List<Long> tagIdList = new ArrayList<>();
        ValidateResult validateResult = new ValidateResult();
        EagleSourceTableDef masterTable = null;
        Map<String, String> eagleMapping = new HashMap<>();

        //校验标签是否在某个域中存在
        for (Map<String, Object> policy : policyList) {
            recommendType = policy.get("recommendType").toString();
            String recommendName = policy.get("recommendName").toString();
            List<Company> companyList = new ArrayList<>();

            //获取单位信息
            if (policy.containsKey("place")
                && StringUtils.isNotEmpty(policy.get("place").toString())) {
                //组织单位就是页面上选择的地市，选择哪个地市，清单数据就从哪个地市中取
                String[] places = policy.get("place").toString().split(",");
                for (String place : places) {

                    TriggerValue realValue = triggerValueCache.queryOne(new StringBuilder("100").append(
                        "@").append(recommendType).append("@").append(place).toString());
                    if (null != realValue) {
                        String[] companyValue = realValue.getRealValue().split("-");
                        Company company = new Company();
                        company.setCompanyId(companyValue[0]);
                        company.setLevel(companyValue[1]);
                        companyList.add(company);
                    }
                }
            }

            List<Map<String, Object>> ruleList = (List<Map<String, Object>>)policy.get("rules");

            //拿到协同规则的标签，保证每个规则中都要有这些标签，用map保证不重复
            Map<String, Map<String, String>> keys = new HashMap<>(5);
            for (Map<String, Object> rule : ruleList) {
                //协同规则
                if (rule.containsKey("xietong") && null != rule.get("xietong")) {
                    List<Map<String, String>> xietongList = (List<Map<String, String>>)rule.get("xietong");
                    for (Map xietongTag : xietongList) {
                        String conditionId = xietongTag.get("conditionId").toString();
                        //协同规则上选择的标签都以查询字段出现
                        xietongTag.put("useType", UseTypeConstants.RESULT);
                        keys.put(conditionId, xietongTag);
                    }
                }
            }

            //筛选出规则中多出的标签（只在一个规则或N个规则中出现，但不是全部的规则），因为试算sql最后要结果合并，必须保证sql查询字段一致
            for (Map<String, Object> rule1 : ruleList) {
                List<Map<String, String>> rule1Triggers = (List<Map<String, String>>)rule1.get("triggers");

                for (Map rule1Trigger : rule1Triggers) {
                    String rule1Id = rule1.get("ruleId").toString();
                    List<Boolean> inAllRules = new ArrayList<>();

                    for (Map<String, Object> rule2 : ruleList) {
                        List<Map<String, String>> rule2Triggers = (List<Map<String, String>>)rule2.get("triggers");

                        for (Map<String, String> rule2Trigger : rule2Triggers) {
                            if (rule1Trigger.get("conditionId").equals(
                                rule2Trigger.get("conditionId"))) {
                                inAllRules.add(true);
                            }
                        }
                    }

                    //如果筛选出的个数跟规则个数相同说明这个标签在所有规则中都存在
                    if (inAllRules.size() == ruleList.size()) {
                        rule1Trigger.put("useType", UseTypeConstants.CONDITION_RESULT);
                    }
                    else {

                        LOG.debug(String.format(
                            "rule tag not InAllRules, ruleId: %s, trigger: %s", rule1Id,
                            JSON.toJSONString(rule1Trigger)));
                        rule1Trigger.put("useType", UseTypeConstants.CONDITION);
                    }
                }

            }

            List<Map<String, String>> xietong = new ArrayList<>(keys.values());
            LOG.debug("xietong: " + JSON.toJSONString(xietong));

            for (Map<String, Object> rule : ruleList) {
                rule.put("company", companyList);
                List<Map<String, String>> triggers = (List<Map<String, String>>)rule.get("triggers");
                resultMessage.append(rule.get("ruleName")).append("选择的：");
                flag = validateTag(tagCache, triggerCache, resultMessage, recommendType, triggers);

                //校验通过继续校验协同规则
                if (flag) {
                    resultMessage = new StringBuilder();
                    resultMessage.append(rule.get("ruleName")).append("协同规则选择的：");
                    flag = validateTag(tagCache, triggerCache, resultMessage, recommendType,
                        xietong);
                }

                resultMessage = resultMessage.delete(resultMessage.length() - 1,
                    resultMessage.length());
                resultMessage.append(" 在").append(recommendName).append("中不存在！");

                //校验不通过直接返回校验消息
                if (!flag) {
                    validateResult.setCode(ErrorCode.POLICY_TRY_ERROR.getErrorCode());
                    validateResult.setResult(flag);
                    validateResult.setMessage(resultMessage.toString());
                    return validateResult;
                }

                //协同规则作为结果字段查询
                if (CollectionUtils.isNotEmpty(xietong)) {
                    triggers.addAll(xietong);
                }
            }
        }

        resultMessage = new StringBuilder();
        for (Map<String, Object> policy : policyList) {
            List<Map<String, Object>> ruleList = (List<Map<String, Object>>)policy.get("rules");
            recommendType = policy.get("recommendType").toString();
            masterTable = SqlUtil.getMasterTable(recommendType);
            for (Map<String, Object> rule : ruleList) {
                List<Map<String, String>> triggers = (List<Map<String, String>>)rule.get("triggers");
                resultMessage.append(rule.get("ruleName")).append("选择的：");

                for (Map tigger : triggers) {
                    String conditionId = tigger.get("conditionId").toString();
//                    String eagleName = triggerCache.queryOne(conditionId).getEagleName();
//                    EagleTag tag = tagCache.queryOne(eagleName + "_" + recommendType);
//                    if (null != tag
//                        && !tag.getCtasTableDefinitionRowId().equals(
//                            masterTable.getCtasTableDefinitionRowId())) {
//                        tagIdList.add(tag.getTagRowId());
//                        eagleMapping.put(eagleName, tigger.get("conditionName").toString());
//                    }
                }

                //校验标签是否配置了主从关系
                List<EagleTag> tagList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(tagIdList)) {
                    //校验标签是否配置了主从关系
//                    tagList = tagMapper.queryByNotExistsTabRef(tagIdList, recommendType,
//                        masterTable.getCtasTableDefinitionRowId().toString());
                }
                if (CollectionUtils.isNotEmpty(tagList)) {

                    for (EagleTag tag : tagList) {
                        String conditionName = eagleMapping.get(tag.getSourceTableColumnName());
                        resultMessage.append(conditionName).append("，");
                    }
                    resultMessage = resultMessage.delete(resultMessage.length() - 1,
                        resultMessage.length());
                    resultMessage.append(" 未配置主从关系");
//                    validateResult.setCode(ResponseCode.POLICY_TRY_ERROR);
                    validateResult.setResult(false);
                    validateResult.setMessage(resultMessage.toString());

                    return validateResult;
                }
            }
        }

        //插入试算记录
        for (Map<String, Object> policy : policyList) {
//            EagleTrycalcRecord record = new EagleTrycalcRecord();
//            record.setActivityId(Integer.parseInt(calcReqModel.getActivityId()));
//            record.setActivityName(calcReqModel.getActivityName());
//            record.setSerialNum(serialNum);
//            String policyId = policy.get("policyId").toString();
//            record.setPolicyId(Integer.parseInt(policyId));
//            String recordData = JSON.toJSONString(calcReqModel);
//            if (recordData.length() >= 10000) {
//                recordData = recordData.substring(0, 10000);
//            }
//            record.setRecordData(recordData);
//            record.setCreateTime(new Date());
//            String nowDate = DateUtils.getNowDate("yyyyMMddHHmmssSSS");
//            record.setRecordId(Long.parseLong(nowDate));
//            policy.put("batchId", nowDate);
//            //试算中
//            record.setStatus("1");
//            trycalcRecordMapper.insert(record);
        }

        validateResult.setResult(flag);
        return validateResult;
    }

    private boolean validateTag(IDacher<EagleTag> tagCache, IDacher<Trigger> triggerCache,
                                StringBuilder resultMessage, String recommendType,
                                List<Map<String, String>> triggers) {
        boolean flag = true;
        Map<String, String> keys = new HashMap<>(10);
        if (null != triggers) {
            for (Map tigger : triggers) {

                String conditionId = tigger.get("conditionId").toString();
//                String eagleName = triggerCache.queryOne(conditionId).getEagleName();
//                String cacheKey = eagleName + "_" + recommendType;
//                EagleTag tag = tagCache.queryOne(cacheKey);
//                String conditionName = tigger.get("conditionName").toString();
//                if (null == tag) {
//                    LOG.debug("not in cache, key:" + cacheKey);
//                    if (!keys.containsKey(conditionName)) {
//                        resultMessage.append(conditionName).append("，");
//                        keys.put(conditionName, null);
//                    }
//                    flag = false;
//
//                }
            }
        }
        return flag;
    }
}
