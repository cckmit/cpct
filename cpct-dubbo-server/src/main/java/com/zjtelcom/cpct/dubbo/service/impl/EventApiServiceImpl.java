package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.service.CpcService;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class EventApiServiceImpl implements EventApiService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${table.infallible}")
    private String defaultInfallibleTable;
    private static final Logger log = LoggerFactory.getLogger(EventApiServiceImpl.class);

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper; //协同渠道配置基本信息

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道

    @Autowired
    private MktCamScriptMapper mktCamScriptMapper; //营销脚本

    @Autowired
    private MktVerbalMapper mktVerbalMapper; //话术

    @Autowired
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired
    private EsHitsService esHitService;  //es存储

    @Autowired
    private RedisUtils redisUtils;  // redis方法


    @Autowired(required = false)
    private IContactTaskReceiptService iContactTaskReceiptService; //协同中心dubbo

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired(required = false)
    private ContactChannelMapper contactChannelMapper; //渠道信息

    @Autowired(required = false)
    private MktCamChlResultMapper mktCamChlResultMapper;

    @Autowired(required = false)
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired(required = false)
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;

    @Autowired
    private CpcService cpcService;





    @Override
    public Map<String, Object> CalculateCPC(Map<String, Object> map) {

        log.info("异步事件接入:" + map.get("reqId"));

        //初始化返回结果
        Map<String, Object> result = new HashMap();
        //构造后面线程要用的参数
        Map<String, String> params = new HashMap<>();

        //初始化es log
        JSONObject esJson = new JSONObject();

        //事件传入参数 开始--------------------
        //获取事件code（必填）
        String eventCode = (String) map.get("eventCode");
        //获取渠道编码（必填）
        String channelCode = (String) map.get("channelCode");
        //本地网
        String lanId = (String) map.get("lanId");
        //业务号码（必填）（资产号码）
        String accNbr = (String) map.get("accNbr");
        //集成编号（必填）（资产集成编码）
        String integrationId = (String) map.get("integrationId");
        //客户编码（必填）
        String custId = (String) map.get("custId");
        //销售员编码（必填）
        String reqId = (String) map.get("reqId");
        //采集时间(yyyy-mm-dd hh24:mm:ss)
        String evtCollectTime = (String) map.get("evtCollectTime");

        //自定义参数集合json字符串
        String evtContent = (String) map.get("evtContent");
        //事件传入参数 结束--------------------

        //构造下级线程使用参数
        params.put("eventCode", eventCode); //事件编码
        params.put("channelCode", channelCode); //渠道编码
        params.put("lanId", lanId); //本地网
        params.put("custId", custId); //客户编码
        params.put("reqId", reqId); //流水号
        params.put("evtCollectTime", evtCollectTime); //事件触发时间
        params.put("evtContent", evtContent); //事件采集项
        params.put("accNbr", accNbr); //资产号码
        params.put("integrationId", integrationId); //资产集成编码

        esJson.put("reqId", reqId);
        esJson.put("eventCode", eventCode);
        esJson.put("integrationId", integrationId);
        esJson.put("accNbr", accNbr);
        esJson.put("custId", custId);
        esJson.put("evtCollectTime", evtCollectTime);
        esJson.put("hit", false);
        esJson.put("success", false);
        esJson.put("msg", "事件接入，开始异步流程");
        esHitService.save(esJson, IndexList.EVENT_MODULE, reqId);

        //异步
        AsyncCPC asyncCPC = new AsyncCPC(params);
        asyncCPC.start();

        result.put("reqId", reqId);
        result.put("resultCode", "1");
        result.put("resultMsg", "success");

        return result;
    }


    /**
     * 异步调用协同中心回调接口
     */
    private class AsyncCPC extends Thread {

        private Map<String, String> params;

        public AsyncCPC(Map<String, String> params) {
            this.params = params;
        }

        public void run() {
            //这里为线程的操作
            //就可以使用注入之后Bean了
            async();

        }


        public Map async() {

            //初始化返回结果
            Map<String, Object> result = cpcService.cpc(params);
            //调用协同中心回调接口
            Map<String, Object> back = iContactTaskReceiptService.contactTaskReceipt(result);
            if (back != null) {
                if ("1".equals(back.get("resultCode"))) {
                    log.info("异步事件回调成功" + params.get("reqId"));
                    return null;
                }
            }
            log.info("异步事件回调失败" + params.get("reqId"));
            return result;
        }
    }

    /**
     * 同步计算
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> CalculateCPCSync(Map<String, Object> map) {

        log.info("同步事件接入:" + map.get("reqId"));

        //初始化返回结果
        Map<String, Object> result = new HashMap();

        Map<String, String> params = new HashMap<>();

        //初始化es log
        JSONObject esJson = new JSONObject();

        //事件传入参数 开始--------------------
        //获取事件code（必填）
        String eventCode = (String) map.get("eventCode");
        //获取渠道编码（必填）
        String channelCode = (String) map.get("channelCode");
        //本地网
        String lanId = (String) map.get("lanId");
        //业务号码（必填）（资产号码）
        String accNbr = (String) map.get("accNbr");
        //集成编号（必填）（资产集成编码）
        String integrationId = (String) map.get("integrationId");
        //客户编码（必填）
        String custId = (String) map.get("custId");
        //流水号（必填）
        String reqId = (String) map.get("reqId");
        //采集时间(yyyy-mm-dd hh24:mm:ss)
        String evtCollectTime = (String) map.get("evtCollectTime");
        //自定义参数集合json字符串
        String evtContent = (String) map.get("evtContent");
        //事件传入参数 结束--------------------

        //构造下级线程使用参数
        params.put("eventCode", eventCode); //事件编码
        params.put("channelCode", channelCode); //渠道编码
        params.put("lanId", lanId); //本地网
        params.put("custId", custId); //客户编码
        params.put("reqId", reqId); //流水号
        params.put("evtCollectTime", evtCollectTime); //事件触发时间
        params.put("evtContent", evtContent); //事件采集项
        params.put("accNbr", accNbr); //资产号码
        params.put("integrationId", integrationId); //资产集成编码

        esJson.put("reqId", reqId);
        esJson.put("eventCode", eventCode);
        esJson.put("integrationId", integrationId);
        esJson.put("accNbr", accNbr);
        esJson.put("custId", custId);
        esJson.put("evtCollectTime", evtCollectTime);
        esJson.put("hit", false);
        esJson.put("success", false);
        esJson.put("msg", "事件接入，开始异步流程");
        esHitService.save(esJson, IndexList.EVENT_MODULE, reqId);

        //调用计算方法
        try {
            result = cpcService.cpc(params);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("同步事件返回失败:" + map.get("reqId"), e.getMessage());
        }


        log.info("同步事件返回成功:" + map.get("reqId"));

        return result;

    }


    private JSONObject getLabelByDubbo(JSONObject param) {
        //查询标签实例数据
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        if ("0".equals(dubboResult.get("result_code").toString())) {
            JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
            //解析返回结果
            return body;
        } else {
//            System.out.println("查询标签失败:" + httpResult.getString("result_msg"));
            return new JSONObject();
        }
    }


    private List<String> subScript(String str) {
        List<String> result = new ArrayList<>();
        Pattern p = Pattern.compile("(?<=\\$\\{)([^$]+)(?=\\}\\$)");
        Matcher m = p.matcher(str);
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }

    private Map<String, Object> getLabelValue(JSONObject param) {
        //更换为dubbo因子查询-----------------------------------------------------
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        return dubboResult;
    }


    /**
     * 二次协同
     *
     * @param
     * @return
     */
    @Override
    public Map<String, Object> secondChannelSynergy(Map<String, Object> params) {

        System.out.println("二次协同入参：" + params.toString());

        Date now = new Date();

        //初始化返回结果
        Map<String, Object> result = new HashMap();
        Long activityInitId = Long.valueOf((String) params.get("activityId"));
        Long ruleInitId = Long.valueOf((String) params.get("ruleId"));
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByInitId(activityInitId);
        Long activityId = mktCampaignDO.getMktCampaignId();

        Long ruleId = null;
        // 通过活动Id查询所有规则
        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByCampaignId(mktCampaignDO.getMktCampaignId());
        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
            if(ruleInitId.equals(mktStrategyConfRuleDO.getInitId())){
                ruleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();
                break;
            }
        }
        String resultNbr = String.valueOf(params.get("resultNbr"));
        String accNbr = String.valueOf(params.get("accNbr"));
        String integrationId = String.valueOf(params.get("integrationId"));
        String custId = String.valueOf(params.get("custId"));
        String lanId = String.valueOf(params.get("lanId"));

        //判断字段是否合法
        if (activityId == null || activityId <= 0) {
            result.put("resultCode", "1000");
            result.put("resultMsg", "活动id为空");
            return result;
        }
        if (ruleId == null || ruleId <= 0) {
            result.put("resultCode", "1000");
            result.put("resultMsg", "规则id为空");
            return result;
        }
        if (resultNbr == null || "".equals(resultNbr)) {
            result.put("resultCode", "1000");
            result.put("resultMsg", "结果id为空");
            return result;
        }
        if (custId == null || "".equals(custId)) {
            result.put("resultCode", "1000");
            result.put("resultMsg", "客户编码为空");
            return result;
        }
        if (lanId == null || "".equals(lanId) || "null".equals(lanId)) {
            result.put("resultCode", "1000");
            result.put("resultMsg", "本地网编码为空");
            return result;
        }

        // 通过规则Id获取规则下的结果id
        List<Map<String, Object>> taskChlList = new ArrayList<>();
        MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleId);
        if (mktStrategyConfRuleDO != null) {
            String[] resultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
            if (resultIds != null && !"".equals(resultIds[0])) {
                for (String resultId : resultIds) {
                    MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(Long.valueOf(resultId));
                    if (resultNbr.equals(mktCamChlResultDO.getReason().toString())) {
                        // 查询推送渠道
                        List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOS = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultDO.getMktCamChlResultId());
                        if (mktCamChlResultConfRelDOS != null && mktCamChlResultConfRelDOS.size() > 0) {
                            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOS) {
                                Map<String, Object> taskChlMap = new HashMap<>();
                                MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(mktCamChlResultConfRelDO.getEvtContactConfId());
//                                taskChlMap.put("channelId", mktCamChlConfDO.getContactChlId());
                                Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDO.getContactChlId());
                                if (channel != null) {
                                    taskChlMap.put("channelId", channel.getContactChlCode());
                                    taskChlMap.put("channelConfId", channel.getContactChlId().toString());
                                } else {
                                    System.out.println("渠道查询出错，渠道为空");
                                    continue;
                                }
                                taskChlMap.put("pushType", mktCamChlConfDO.getPushType());
                                taskChlMap.put("pushTime", "");
                                // 获取属性
                                List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                                List<Map<String, Object>> taskChlAttrList = new ArrayList<>();
                                if (mktCamChlConfAttrDOList != null && mktCamChlConfAttrDOList.size() > 0) {
                                    boolean checkTime = true;
                                    for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                                        Map<String, Object> taskChlAttr = new HashMap<>();

                                        //渠道属性数据返回给协同中心
                                        if (mktCamChlConfAttrDO.getAttrId() == 500600010001L || mktCamChlConfAttrDO.getAttrId() == 500600010002L || mktCamChlConfAttrDO.getAttrId() == 500600010003L || mktCamChlConfAttrDO.getAttrId() == 500600010004L) {
                                            taskChlAttr = new HashMap<>();
                                            taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                                            taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                            taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttrDO.getAttrValue())));
                                            taskChlAttrList.add(taskChlAttr);

                                            continue;
                                        }

                                        if (mktCamChlConfAttrDO.getAttrId() == 500600010005L || mktCamChlConfAttrDO.getAttrId() == 500600010011L) {
                                            taskChlAttr = new HashMap<>();
                                            taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                                            taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                                            taskChlAttr.put("attrValue", mktCamChlConfAttrDO.getAttrValue());
                                            taskChlAttrList.add(taskChlAttr);

                                            continue;
                                        }

                                        //判断渠道生失效时间
                                        if (mktCamChlConfAttrDO.getAttrId() == 500600010006L) {
                                            if (!now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                                                checkTime = false;
                                            } else {
                                                taskChlAttr = new HashMap<>();
                                                taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                                                taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttrDO.getAttrValue())));
                                                taskChlAttrList.add(taskChlAttr);
                                            }

                                            continue;

                                        }
                                        if (mktCamChlConfAttrDO.getAttrId() == 500600010007L) {
                                            if (now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                                                checkTime = false;
                                            } else {
                                                taskChlAttr = new HashMap<>();
                                                taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                                                taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttrDO.getAttrValue())));
                                                taskChlAttrList.add(taskChlAttr);
                                            }

                                            continue;
                                        }

                                        Map<String, Object> taskChlAttrMap = new HashMap<>();
                                        taskChlAttrMap.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                                        taskChlAttrMap.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                                        taskChlAttrMap.put("attrValue", mktCamChlConfAttrDO.getAttrValue());
                                        // 接触账号/推送账号
                                        if (ConfAttrEnum.ACCOUNT.getArrId().equals(mktCamChlConfAttrDO.getAttrId())) {

                                            if (mktCamChlConfAttrDO.getAttrValue() != null) {
                                                JSONObject httpParams = new JSONObject();
                                                httpParams.put("queryNum", accNbr);
                                                httpParams.put("c3", "571"); //todo 这里要添加 本地网
                                                httpParams.put("queryId", integrationId);
                                                httpParams.put("type", "1");
                                                //待查询的标签列表
                                                httpParams.put("queryFields", mktCamChlConfAttrDO.getAttrValue());
                                                httpParams.put("centerType", "00");
                                                //dubbo接口查询标签
                                                JSONObject resJson = getLabelByDubbo(httpParams);
                                                if (resJson.containsKey(mktCamChlConfAttrDO.getAttrValue())) {
                                                    taskChlMap.put("contactAccount", resJson.get(mktCamChlConfAttrDO.getAttrValue()));
                                                } else {
                                                    //todo 不命中
                                                    taskChlMap.put("contactAccount", mktCamChlConfAttrDO.getAttrValue());
                                                }
                                            }
                                        } else if (ConfAttrEnum.ACCOUNT.getArrId().equals(mktCamChlConfAttrDO.getAttrId())) {
                                            taskChlMap.put("naireId", mktCamChlConfAttrDO.getAttrValue());
                                        }
                                        taskChlAttrList.add(taskChlAttrMap);
                                    }
                                    if (checkTime = false) {
                                        //生失效时间不通过 todo
//                                        taskChlAttrList = new ArrayList<>();
//                                        break;
                                    }
                                }
                                taskChlMap.put("taskChlAttrList", taskChlAttrList);


                                //查询推荐指引
                                List<String> scriptLabelList = new ArrayList<>();
                                String contactScript = null;
                                String mktVerbalStr = null;
                                CamScript camScript = mktCamScriptMapper.selectByConfId(mktCamChlConfDO.getEvtContactConfId());
                                if (camScript != null) {
                                    //获取脚本信息
                                    contactScript = camScript.getScriptDesc();
                                    if (contactScript != null) {
                                        scriptLabelList.addAll(subScript(contactScript));
                                    }
                                }

                                //查询痛痒点
                                List<MktVerbal> verbalList = mktVerbalMapper.findVerbalListByConfId(mktCamChlConfDO.getEvtContactConfId());
                                if (verbalList != null && verbalList.size() > 0) {
                                    taskChlMap.put("reason", verbalList.get(0).getScriptDesc()); // 痛痒点话术有多个
                                    for (MktVerbal mktVerbal : verbalList) {
                                        //查询痛痒点规则 todo
//                        List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());

                                        mktVerbalStr = verbalList.get(0).getScriptDesc();
                                        if (mktVerbalStr != null) {
                                            scriptLabelList.addAll(subScript(mktVerbalStr));
                                        }
                                    }
                                }

                                if (scriptLabelList.size() > 0) {

                                    JSONObject labelParam = new JSONObject();
                                    labelParam.put("queryNum", accNbr);
                                    labelParam.put("c3", lanId);
                                    labelParam.put("queryId", integrationId);
                                    labelParam.put("type", "1");
                                    StringBuilder queryFieldsSb = new StringBuilder();

                                    for (String labelCode : scriptLabelList) {
                                        if (queryFieldsSb.toString().contains(labelCode)) {
                                            continue;
                                        }
                                        queryFieldsSb.append(labelCode).append(",");
                                    }
                                    if (queryFieldsSb.length() > 0) {
                                        queryFieldsSb.deleteCharAt(queryFieldsSb.length() - 1);
                                    }

                                    labelParam.put("queryFields", queryFieldsSb.toString());
                                    Map<String, Object> queryResult = getLabelValue(labelParam);

                                    JSONObject body = new JSONObject((HashMap) queryResult.get("msgbody"));
                                    //获取查询结果
                                    for (Map.Entry<String, Object> entry : body.entrySet()) {
                                        //替换标签值内容
                                        if (contactScript != null) {
                                            contactScript = contactScript.replace("${" + entry.getKey() + "}$", entry.getValue().toString());
                                        }
                                        if (mktVerbalStr != null) {
                                            mktVerbalStr = mktVerbalStr.replace("${" + entry.getKey() + "}$", entry.getValue().toString());
                                        }
                                    }
                                }
                                //返回结果中添加脚本信息
                                if (contactScript != null) {
                                    if (subScript(contactScript).size() > 0) {
                                        System.out.println("推荐指引标签替换含有无值的标签");
                                    }
                                }
                                taskChlMap.put("contactScript", contactScript == null ? "" : contactScript);
                                //痛痒点
                                if (mktVerbalStr != null) {
                                    if (subScript(mktVerbalStr).size() > 0) {
                                        System.out.println("痛痒点标签替换含有无值的标签");
                                    }
                                }
                                taskChlMap.put("reason", mktVerbalStr == null ? "" : mktVerbalStr);

                                taskChlList.add(taskChlMap);
                            }
                        }
                    }
                }
            }
        }
        MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = mktStrategyConfRuleRelMapper.selectByRuleId(ruleId);

        try {
            //查询展示列标签
            MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);
            List<Map<String, Object>> iSaleDisplay = new ArrayList<>();
            iSaleDisplay = (List<Map<String, Object>>) redisUtils.get("EVT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay());
            if (iSaleDisplay == null) {
                iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getIsaleDisplay());
                redisUtils.set("EVT_ISALE_LABEL_" + mktCampaign.getIsaleDisplay(), iSaleDisplay);
            }
            List<Map<String, Object>> itgTriggers = new ArrayList<>();
            Map<String, Object> itgTrigger;

            StringBuilder querySb = new StringBuilder();

            if (iSaleDisplay != null && iSaleDisplay.size() > 0) {
                for (Map<String, Object> labelMap : iSaleDisplay) {
                    querySb.append((String) labelMap.get("labelCode")).append(",");
                }
                if (querySb.length() > 0) {
                    querySb.deleteCharAt(querySb.length() - 1);
                }
                JSONObject httpParams = new JSONObject();
                httpParams.put("queryNum", accNbr);
                httpParams.put("queryId", integrationId);
                httpParams.put("type", "1");
                httpParams.put("c3", lanId);
                //待查询的标签列表
                httpParams.put("queryFields", querySb.toString());

                //dubbo接口查询标签
                JSONObject resJson = getLabelByDubbo(httpParams);
                System.out.println("试运算标签查询" + resJson.toString());
                Map<String, Object> triggers;
                List<Map<String, Object>> triggerList1 = new ArrayList<>();
                List<Map<String, Object>> triggerList2 = new ArrayList<>();
                List<Map<String, Object>> triggerList3 = new ArrayList<>();
                List<Map<String, Object>> triggerList4 = new ArrayList<>();

                for (Map<String, Object> label : iSaleDisplay) {
                    if (resJson.containsKey((String) label.get("labelCode"))) {
                        triggers = new JSONObject();
                        triggers.put("key", label.get("labelCode"));
                        triggers.put("value", resJson.get((String) label.get("labelCode")));
                        triggers.put("display", 0); //todo 确定display字段
                        triggers.put("name", label.get("labelName"));
                        if ("1".equals(label.get("typeCode").toString())) {
                            triggerList1.add(triggers);
                        } else if ("2".equals(label.get("typeCode").toString())) {
                            triggerList2.add(triggers);
                        } else if ("3".equals(label.get("typeCode").toString())) {
                            triggerList3.add(triggers);
                        } else if ("4".equals(label.get("typeCode").toString())) {
                            triggerList4.add(triggers);
                        }
                    }
                }
                if (triggerList1.size() > 0) {
                    itgTrigger = new HashMap<>();
                    itgTrigger.put("triggerList", triggerList1);
                    itgTrigger.put("type", "固定信息");
                    itgTriggers.add(new JSONObject(itgTrigger));
                }
                if (triggerList2.size() > 0) {
                    itgTrigger = new JSONObject();
                    itgTrigger.put("triggerList", triggerList2);
                    itgTrigger.put("type", "营销信息");
                    itgTriggers.add(new JSONObject(itgTrigger));
                }
                if (triggerList3.size() > 0) {
                    itgTrigger = new JSONObject();
                    itgTrigger.put("triggerList", triggerList3);
                    itgTrigger.put("type", "费用信息");
                    itgTriggers.add(new JSONObject(itgTrigger));
                }
                if (triggerList4.size() > 0) {
                    itgTrigger = new JSONObject();
                    itgTrigger.put("triggerList", triggerList4);
                    itgTrigger.put("type", "协议信息");
                    itgTriggers.add(new JSONObject(itgTrigger));
                }
            }
            result.put("itgTriggers", JSONArray.parse(JSONArray.toJSON(itgTriggers).toString()));
        } catch (Exception e) {
            System.out.println("标签查询出错");
            e.printStackTrace();
        }

        result.put("activityId", activityInitId.toString());

        MktStrategyConfRuleRelDO mktStrategyConfRuleRel = mktStrategyConfRuleRelMapper.selectByRuleId(ruleInitId);
        if (mktStrategyConfRuleRelDO != null) {
            result.put("policyId", mktStrategyConfRuleRel.getMktStrategyConfId().toString());
        }

        result.put("ruleId", ruleInitId.toString());
        result.put("taskChlList", taskChlList);
        System.out.println("二次协同结果：" + new JSONObject(result).toString());
        if (taskChlList.size() > 0) {
            result.put("resultCode", "1");
        } else {
            result.put("resultCode", "1000");
        }

        return result;
    }



}