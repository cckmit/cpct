package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.rule.RuleResult;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulConditionMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.CamApiService;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.service.SearchLabelService;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.MONTH;

@Service
public class EventApiServiceImpl implements EventApiService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;

    private static final Logger log = LoggerFactory.getLogger(EventApiServiceImpl.class);

    @Autowired
    private ContactEvtMapper contactEvtMapper; //事件总表

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired
    private FilterRuleMapper filterRuleMapper; //过滤规则

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
    private EsHitService esHitService;  //es存储

    @Autowired
    private RedisUtils redisUtils;  // redis方法

    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;  // 事件采集项

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
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper; //事件规则

    @Autowired
    private EventMatchRulConditionMapper eventMatchRulConditionMapper;  //事件规则条件

    @Autowired(required = false)
    private SearchLabelService searchLabelService;  //查询活动下使用的所有标签

    @Autowired(required = false)
    private CamApiService camApiService; // 活动任务

    @Autowired
    private RestTemplate restTemplate;


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
            Map<String, Object> result = new HashMap();
            result = new EventTask().cpc(params);
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
            result = new EventTask().cpc(params);
        } catch (Exception e) {
            log.error("同步事件返回失败:" + map.get("reqId"), e.getMessage());
        }


        log.info("同步事件返回成功:" + map.get("reqId"));

        return result;

    }

    /**
     * 事件验证模块公共方法
     */
    class EventTask {

        public Map<String, Object> cpc(Map<String, String> map) {
            //记录开始时间
            long begin = System.currentTimeMillis();
            log.info("事件计算流程开始:" + map.get("eventCode") + "***" + map.get("reqId"));

            //初始化返回结果中的工单信息
            List<Map<String, Object>> activityList = new ArrayList<>();

            //初始化返回结果
            Map<String, Object> result = new HashMap();

            JSONObject timeJson = new JSONObject();
            timeJson.put("reqId", map.get("reqId"));
            timeJson.put("name", "事件");
            timeJson.put("time1", System.currentTimeMillis() - begin);

            //构造返回结果
            String custId = map.get("custId");
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);

            //初始化es log
            JSONObject esJson = new JSONObject();
            esJson.put("reqId", map.get("reqId"));

            //初始化入参出参的es log
            JSONObject paramsJson = new JSONObject();
            paramsJson.put("reqId", map.get("reqId"));
            paramsJson.put("intoParams", map);  //保存入参

            //es log
            esJson.put("reqId", map.get("reqId"));
            esJson.put("eventCode", map.get("eventCode"));
            esJson.put("integrationId", map.get("integrationId"));
            esJson.put("accNbr", map.get("accNbr"));
            esJson.put("custId", map.get("custId"));
            esJson.put("channel", map.get("channelCode"));
            esJson.put("lanId", map.get("lanId"));
            //如果没有接入时间  自己补上
            try {
                if (map.get("evtCollectTime") == null || "".equals(map.get("evtCollectTime"))) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    esJson.put("evtCollectTime", simpleDateFormat.format(new Date()));
                } else {
                    esJson.put("evtCollectTime", map.get("evtCollectTime"));
                }
            } catch (Exception e) {
                log.error("事件接入时间入参异常:" + map.get("reqId"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                esJson.put("evtCollectTime", simpleDateFormat.format(new Date()));
            }

            try {
                //事件验证开始↓↓↓↓↓↓↓↓↓↓↓↓↓
                //解析事件采集项
                JSONObject evtParams = JSONObject.parseObject(map.get("evtContent"));
                //获取C4的数据用于过滤
                String c4 = null;
                if(evtParams!=null){
                    c4 = (String) evtParams.get("C4");
                }

                //根据事件code查询事件信息
                ContactEvt event = (ContactEvt) redisUtils.get("EVENT_" + map.get("eventCode"));
                if (event == null) {
                    event = contactEvtMapper.getEventByEventNbr(map.get("eventCode"));
                    redisUtils.set("EVENT_" + map.get("eventCode"), event);
                }
                if (event == null) {
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", "未找到相关事件");
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    log.error("未找到相关事件:" + map.get("reqId"));

                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "未找到相关事件");
                    return result;
                }
                //获取事件id
                Long eventId = event.getContactEvtId();

                esJson.put("eventId", eventId);

                //验证事件状态
                if (!"1000".equals(event.getStatusCd())) {
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", "事件已关闭");
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    log.info("事件已关闭:" + map.get("reqId"));

                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "事件已关闭");
                    return result;
                }

                //验证事件采集项
                List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(eventId);
                List<Map<String, Object>> evtTriggers = new ArrayList<>();
                Map<String, Object> trigger;
                //事件采集项标签集合(事件采集项标签优先规则)
                Map<String, String> labelItems = new HashMap<>();

                StringBuilder stringBuilder = new StringBuilder();
                for (EventItem contactEvtItem : contactEvtItems) {
                    if (evtParams != null && evtParams.containsKey(contactEvtItem.getEvtItemCode())) {
                        //筛选出作为标签使用的事件采集项
                        if ("0".equals(contactEvtItem.getIsLabel())) {
                            labelItems.put(contactEvtItem.getEvtItemCode(), evtParams.getString(contactEvtItem.getEvtItemCode()));
                        }

                        trigger = new HashMap<>();
                        trigger.put("key", contactEvtItem.getEvtItemCode());
                        trigger.put("value", evtParams.get(contactEvtItem.getEvtItemCode()));
                        evtTriggers.add(trigger);
                    } else {
                        //记录缺少的事件采集项
                        stringBuilder.append(contactEvtItem.getEvtItemCode()).append("、");
                    }
                }

                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }

                //事件采集项返回参数
                if (stringBuilder.length() > 0) {

                    //保存es log
                    long cost = System.currentTimeMillis() - begin;
                    esJson.put("timeCost", cost);
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    log.error(map.get("reqId") + "事件采集项验证失败:" + map.get("reqId"));

                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
                    return result;
                }

                //!!!验证事件规则命中
                Map<String, Object> stringObjectMap = matchRulCondition(eventId, labelItems, map);
                if (!stringObjectMap.get("code").equals("success")) {

                    log.error("事件规则未命中:" + map.get("reqId"));

                    //判断不符合条件 直接返回不命中
                    result.put("CPCResultMsg", stringObjectMap.get("result"));
                    result.put("CPCResultCode", "1000");
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", stringObjectMap.get("result"));
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                    return result;
                }

                //添加内置标签
                setInlayLabel(labelItems);

                //获取事件推荐活动数
                int recCampaignAmount;
                String recCampaignAmountStr = event.getRecCampaignAmount();
                if (recCampaignAmountStr == null || "".equals(recCampaignAmountStr)) {
                    recCampaignAmount = 0;
                } else {
                    recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
                }

                timeJson.put("time2", System.currentTimeMillis() - begin);
                //事件下所有活动的规则预校验，返回初步可命中活动
                List<Map<String, Object>> resultByEvent = getResultByEvent(eventId, map.get("lanId"), map.get("channelCode"), map.get("reqId"), map.get("accNbr"), c4);

                timeJson.put("time3", System.currentTimeMillis() - begin);

                if (resultByEvent == null || resultByEvent.size() <= 0) {
                    log.info("预校验为空");
                    long cost = System.currentTimeMillis() - begin;
                    esJson.put("timeCost", cost);
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", "活动均未命中");
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    //事件采集项没有客户编码
                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "success");
                    result.put("taskList", activityList);

                    paramsJson.put("backParams", result);
                    timeJson.put("time7", System.currentTimeMillis() - begin);
                    esHitService.save(paramsJson, IndexList.PARAMS_MODULE);

                    log.info("事件计算流程结束:" + map.get("eventCode") + "***" + map.get("reqId") + "（" + (System.currentTimeMillis() - begin) + "）");

                    return result;
                }



                //判断有没有客户级活动
                Boolean hasCust = false;  //是否有客户级
                Boolean successCust = false;  //客户级查询是否成功
                for (Map<String, Object> activeMap : resultByEvent) {
                    if ((Integer) activeMap.get("levelConfig") == 1) {
                        hasCust = true;
                        break;
                    }
                }

                //查询事件下使用的所有标签
                DefaultContext<String, Object> context = new DefaultContext<String, Object>();
                Map<String, String> mktAllLabels = (Map<String, String>) redisUtils.get("EVT_ALL_LABEL_" + eventId);
                if (mktAllLabels == null) {
                    try {
                        mktAllLabels = searchLabelService.labelListByEventId(eventId);  //查询事件下使用的所有标签
                        if (null != mktAllLabels) {
                            redisUtils.set("EVT_ALL_LABEL_" + eventId, mktAllLabels);
                        } else {
                            log.info("获取事件下所有标签失败");
                            esJson.put("hit", false);
                            esJson.put("msg", "获取事件下所有标签失败");
                            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                            return Collections.EMPTY_MAP;
                        }
                    } catch (Exception e) {
                        esJson.put("hit", false);
                        esJson.put("msg", "获取事件下所有标签异常");
                        esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                        return Collections.EMPTY_MAP;
                    }
                }

                // 过滤事件采集相中的标签
                Map<String, String> mktAllLabel = new HashMap<>();
                Iterator<Map.Entry<String, String>> iterator = labelItems.entrySet().iterator();
                List<String> assetLabelList = ChannelUtil.StringToList(mktAllLabels.get("assetLabels"));
                List<String> promLabelList = ChannelUtil.StringToList(mktAllLabels.get("promLabels"));
                List<String> custLabelList = ChannelUtil.StringToList(mktAllLabels.get("custLabels"));
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    if(assetLabelList.contains(entry.getKey())){
                        assetLabelList.remove(entry.getKey());
                    } else if(promLabelList.contains(entry.getKey())){
                        promLabelList.remove(entry.getKey());
                    } else if(custLabelList.contains(entry.getKey())){
                        custLabelList.remove(entry.getKey());
                    }
                }
                if (assetLabelList != null && assetLabelList.size() > 0) {
                    mktAllLabel.put("assetLabels", ChannelUtil.StringList2String(assetLabelList));
                }
                if (promLabelList != null && promLabelList.size() > 0) {
                    mktAllLabel.put("promLabels", ChannelUtil.StringList2String(promLabelList));
                }
                if (custLabelList != null && custLabelList.size() > 0) {
                    mktAllLabel.put("custLabels", ChannelUtil.StringList2String(custLabelList));
                }
                log.info("assetLabelList = " +assetLabelList  + "   " + "promLabelList = " + promLabelList + "   " + "custLabelList = " + custLabelList);




                List<DefaultContext<String, Object>> resultMapList = new ArrayList<>();
                JSONArray accArray = new JSONArray();
                // 是客户级的
                if (hasCust) {
                    if (custId == null || "".equals(custId)) {
                        //保存es log
                        long cost = System.currentTimeMillis() - begin;
                        esJson.put("timeCost", cost);
                        esJson.put("hit", false);
                        esJson.put("success", true);
                        esJson.put("msg", "客户级活动，事件采集项未包含客户编码");
                        esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                        log.error("采集项未包含客户编码:" + map.get("reqId"));

                        //事件采集项没有客户编码
                        result.put("CPCResultCode", "1000");
                        result.put("CPCResultMsg", "采集项未包含客户编码");
                        return result;

                    } else {
                        JSONObject param = new JSONObject();
                        //查询标识
                        param.put("c3", map.get("lanId"));
                        param.put("queryId", map.get("custId"));
                        param.put("queryNum", "");
                        param.put("queryFields", "");
                        param.put("type", "4");

                        try {
                            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
                            if ("0".equals(dubboResult.get("result_code").toString())) {
                                accArray = new JSONArray((List<Object>) dubboResult.get("msgbody"));
                                successCust = true;
                            }

                            // 查询客户级的标签
                            Map<String, String> privateParams = new HashMap<>();
                            privateParams.put("isCust", "1");
                            privateParams.put("accNbr", map.get("accNbr"));
                            privateParams.put("integrationId", map.get("integrationId"));
                            privateParams.put("custId", map.get("custId"));

//                            Map<String, Object> custLabelMap = new HashMap<>();
                            if(!"".equals(mktAllLabel.get("custLabels")) && mktAllLabel.get("custLabels")!=null ){
                                getCustLabel(mktAllLabel, map, privateParams, context, esJson);
                            }

                            ExecutorService executorService = Executors.newCachedThreadPool();
                            // 客户级
                            List<Future<DefaultContext<String, Object>>> futureList = new ArrayList<>();

                            //多线程获取资产级标签，并加上客户级标签
                            for (Object o : accArray) {

                                Future<DefaultContext<String, Object>> future = executorService.submit(new getListMapLabelTask(o, mktAllLabel, map, context, esJson, labelItems));
                                futureList.add(future);
                            }

                            for (Future<DefaultContext<String, Object>> future : futureList) {
                                if (future.get() != null && !future.get().isEmpty()) {
                                    DefaultContext<String, Object> reultMap = future.get();
//                                    reultMap.putAll(context);
                                    resultMapList.add(reultMap);
                                }
                            }

                        } catch (Exception e) {
                            log.error("Exception = " + e);
                        }
                    }
                } else{
                    //资产级
                    Map<String, String> privateParams = new HashMap<>();
                    privateParams.put("isCust", "1"); //资产级
                    privateParams.put("accNbr", map.get("accNbr"));
                    privateParams.put("integrationId", map.get("integrationId"));// 资产集成编码
                    privateParams.put("custId", map.get("custId"));

                    DefaultContext<String, Object> reultMap = new DefaultContext<>();
                    Map<String, Object> assetLabelMap= getAssetAndPromLabel(mktAllLabel, map, privateParams, context, esJson, labelItems);
                    if (assetLabelMap != null) {
                        reultMap.putAll(assetLabelMap);
                        Map<String, Object> custLabelMap = getCustLabel(mktAllLabel, map, privateParams, context, esJson);
                        if (custLabelMap != null) {
                            reultMap.putAll(custLabelMap);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    resultMapList.add(reultMap);
                }

                timeJson.put("time4", System.currentTimeMillis() - begin);

                //初始化结果集
                List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                //初始化线程池
//                ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT_ACTIVE);
                ExecutorService executorService = Executors.newCachedThreadPool();


                //判断是否全部为资产级
                boolean isAllAsset = false;
                for (Map<String, Object> activeMap : resultByEvent) {
                    if ((Integer) activeMap.get("levelConfig") == 1){ // 1为客户级
                        isAllAsset = false;
                        break;
                    } else if ((Integer) activeMap.get("levelConfig") == 0){  // 0为资产级
                        isAllAsset = true;
                    }
                }
                // 全部为资产级时直接遍历活动
                if(isAllAsset){
                    for (Map<String, Object> activeMap : resultByEvent) {
                        //资产级
                        Map<String, String> privateParams = new HashMap<>();
                        privateParams.put("isCust", "1"); //是否是客户级
                        privateParams.put("accNbr", map.get("accNbr"));
                        privateParams.put("integrationId", map.get("integrationId"));
                        privateParams.put("custId", map.get("custId"));
                        privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                        //资产级
                        Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), resultMapList.get(0)));
                        //将线程处理结果添加到结果集
                        threadList.add(f);
                    }
                } else {
                    //遍历活动
                    for (Map<String, Object> activeMap : resultByEvent) {
                        //提交线程
                        if ((Integer) activeMap.get("levelConfig") == 1) { //判断是客户级还是资产级
                            //客户级
                            if (successCust) {
                                for (DefaultContext<String, Object> o : resultMapList) {
                                    log.info("o = " + o);
                                    //客户级下，循环资产级
                                    Map<String, String> privateParams = new HashMap<>();
                                    privateParams.put("isCust", "0"); //是客户级
                                    privateParams.put("accNbr", o.get("accNbr").toString());
                                    privateParams.put("integrationId", o.get("integrationId").toString());
                                    privateParams.put("custId", map.get("custId"));
                                    //活动优先级为空的时候默认0
                                    privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                                    Future<Map<String, Object>> f = executorService.submit(
                                            new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), o));
                                    //将线程处理结果添加到结果集
                                    threadList.add(f);
                                }
                            } else {
                                log.error("客户级资产查询出错:" + map.get("reqId"));
                                esJson.put("reqId", map.get("reqId"));
                                esJson.put("activityId", activeMap.get("mktCampaginId"));
                                esJson.put("hitEntity", map.get("accNbr")); //命中对象
                                esJson.put("hit", false);
                                esJson.put("msg", "客户级资产查询出错");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE,map.get("reqId") + activeMap.get("mktCampaginId") + map.get("accNbr"));
                            }
                        } else {
                            //资产级
                            for (DefaultContext<String, Object> o : resultMapList) {
                                String assetId = o.get("integrationId").toString();
                                // 判断资产编码是否与接入的一致
                                if (assetId.equals(map.get("integrationId"))) {
                                    Map<String, String> privateParams = new HashMap<>();
                                    privateParams.put("isCust", "1"); //是否是客户级
                                    privateParams.put("accNbr", map.get("accNbr"));
                                    privateParams.put("integrationId", map.get("integrationId"));
                                    privateParams.put("custId", map.get("custId"));
                                    privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                                    //资产级
                                    Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), o));
                                    //将线程处理结果添加到结果集
                                    threadList.add(f);
                                }
                            }
                        }
                    }
                }



                timeJson.put("time5", System.currentTimeMillis() - begin);

                //获取结果
                try {
                    for (Future<Map<String, Object>> future : threadList) {
                        if (!future.get().isEmpty()) {
                            activityList.addAll((List<Map<String, Object>>) (future.get().get("ruleList")));
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //发生异常关闭线程池
                    executorService.shutdown();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    //发生异常关闭线程池
                    executorService.shutdown();
                    return Collections.EMPTY_MAP;
                } finally {
                    //关闭线程池
                    executorService.shutdown();
                }

                timeJson.put("time6", System.currentTimeMillis() - begin);

                //判断事件推荐活动数，按照优先级排序
                if (activityList.size() > 0 && recCampaignAmount > 0 && recCampaignAmount < activityList.size()) {
                    Collections.sort(activityList, new Comparator<Map<String, Object>>() {
                        public int compare(Map o1, Map o2) {
                            if (!o1.containsKey("orderPriority")) {
                                o1.put("orderPriority", "0");
                            }
                            if (!o2.containsKey("orderPriority")) {
                                o2.put("orderPriority", "0");
                            }
                            return Integer.parseInt((String) o2.get("orderPriority")) - Integer.parseInt((String) o1.get("orderPriority"));
                        }
                    });

                    String orderPriorityLast = (String) activityList.get(recCampaignAmount - 1).get("orderPriority");

                    for (int i = recCampaignAmount; i < activityList.size(); i++) {
                        if (!orderPriorityLast.equals(activityList.get(i).get("orderPriority"))) {
                            //es log
                            esJson.put("msg", "推荐数：" + i + ",命中数：" + activityList.size());
                            //事件推荐活动数
                            activityList = activityList.subList(0, i);
                        }
                    }
                }

                //返回结果
                result.put("taskList", activityList); //协同回调结果

                if (activityList.size() > 0) {
                    //构造返回参数
                    result.put("CPCResultCode", "1");
                    result.put("CPCResultMsg", "success");

                    StringBuilder actStr = new StringBuilder();
                    for (Map<String, Object> actMap : activityList) {
                        actStr.append(actMap.get("activityId")).append(",");
                    }
                    esJson.put("hit", true);
                    esJson.put("hitDetail", actStr.toString());

                } else {
                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "success");

                    esJson.put("hit", false);

                }
                result.put("reqId", map.get("reqId"));
                result.put("custId", custId);

                paramsJson.put("backParams", result);
                timeJson.put("time7", System.currentTimeMillis() - begin);
                esHitService.save(paramsJson, IndexList.PARAMS_MODULE);

                //es log
                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("success", true);
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                timeJson.put("time8", System.currentTimeMillis() - begin);

                esHitService.save(timeJson, IndexList.TIME_MODULE, map.get("reqId"));

            } catch (Exception e) {
                log.info("策略中心计算异常");
                log.error("Exception = ", e);
                paramsJson.put("errorMsg", e.getMessage());
                esHitService.save(paramsJson, IndexList.PARAMS_MODULE);

                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("hit", false);
                esJson.put("success", true);
                esJson.put("msg", "策略中心计算异常");
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                //构造返回参数
                result.put("CPCResultCode", "1000");
                result.put("CPCResultMsg", "策略中心计算异常");
                result.put("reqId", map.get("reqId"));
                result.put("custId", custId);
                return result;
            }
            log.info("事件计算流程结束:" + map.get("eventCode") + "***" + map.get("reqId") + "（" + (System.currentTimeMillis() - begin) + "）");
            return result;
        }
    }




    /**
     * 活动级别验证
     */
    class ActivityTask implements Callable<Map<String, Object>> {
        private Long activityId;
        private String reqId;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private Map<String, String> labelItems;
        private List<Map<String, Object>> evtTriggers;
        private List<Map<String, Object>> strategyMapList;
        private DefaultContext<String, Object> context;

        ActivityTask(Map<String, String> params, Long activityId, Map<String, String> privateParams,
                     Map<String, String> labelItems, List<Map<String, Object>> evtTriggers, List<Map<String, Object>> strategyMapList, DefaultContext<String, Object> context) {
            this.activityId = activityId;
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
            this.evtTriggers = evtTriggers;
            this.strategyMapList = strategyMapList;
            this.reqId = params.get("reqId");
            this.context = context;
        }

        @Override
        public Map<String, Object> call() {
           Map<String, Object> activityTaskResultMap = camApiService.ActivityTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
            return activityTaskResultMap;
        }
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


    private boolean compareHourAndMinute(FilterRule filterRule) {
        Boolean result = false;
        Calendar cal = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(filterRule.getDayStart());
        start.set(cal.get(Calendar.YEAR), cal.get(MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Calendar end = Calendar.getInstance();
        end.setTime(filterRule.getDayEnd());
        end.set(cal.get(Calendar.YEAR), cal.get(MONTH), cal.get(Calendar.DAY_OF_MONTH));

        if ((cal.getTimeInMillis() - start.getTimeInMillis()) > 0
                && (cal.getTimeInMillis() - end.getTimeInMillis()) < 0) {
            result = true;
        }

        return result;
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



    public static String cpcLabel(Label label, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        express.append("if(");

        if ("7100".equals(type)) {
            express.append("!");
        }
        express.append("(");
        express.append(assLabel(label, type, rightParam));
        express.append(") {return true}");

        return express.toString();
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

        Long activityId = Long.valueOf((String) params.get("activityId"));
        Long ruleId = Long.valueOf((String) params.get("ruleId"));
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
                                        if (mktCamChlConfAttrDO.getAttrId() == 500600010001L
                                                || mktCamChlConfAttrDO.getAttrId() == 500600010002L
                                                || mktCamChlConfAttrDO.getAttrId() == 500600010003L
                                                || mktCamChlConfAttrDO.getAttrId() == 500600010004L) {
                                            taskChlAttr = new HashMap<>();
                                            taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                                            taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                            taskChlAttr.put("attrValue", simpleDateFormat.format(Long.valueOf(mktCamChlConfAttrDO.getAttrValue())));
                                            taskChlAttrList.add(taskChlAttr);

                                            continue;
                                        }

                                        if (mktCamChlConfAttrDO.getAttrId() == 500600010005L ||
                                                mktCamChlConfAttrDO.getAttrId() == 500600010011L) {
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

                                // 营销服务话术脚本
//                                CamScript camScript = mktCamScriptMapper.selectByConfId(mktCamChlConfDO.getEvtContactConfId());
//                                if (camScript != null) {
//                                    taskChlMap.put("contactScript", camScript.getScriptDesc());
//                                }
//
//                                // 痛痒点话术
//                                List<MktVerbal> verbalList = mktVerbalMapper.findVerbalListByConfId(mktCamChlConfDO.getEvtContactConfId());
//                                if (verbalList != null && verbalList.size() > 0) {
//                                    taskChlMap.put("reason", verbalList.get(0).getScriptDesc()); // 痛痒点话术有多个
//                                }


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

        result.put("activityId", activityId.toString());

        if (mktStrategyConfRuleRelDO != null) {
            result.put("policyId", mktStrategyConfRuleRelDO.getMktStrategyConfId().toString());
        }
        result.put("ruleId", ruleId.toString());
        result.put("taskChlList", taskChlList);
        System.out.println("二次协同结果：" + new JSONObject(result).toString());
        if (taskChlList.size() > 0) {
            result.put("resultCode", "1");
        } else {
            result.put("resultCode", "1000");
        }

        return result;
    }

    public static String assLabel(Label label, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        switch (type) {
            case "1000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" > ");
                express.append(rightParam);
                break;
            case "2000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" < ");
                express.append(rightParam);
                break;
            case "3000":
                express.append(label.getInjectionLabelCode()).append(")");
                express.append(" == ");
                express.append("\"").append(rightParam).append("\"");
                break;
            case "4000":
                express.append(label.getInjectionLabelCode()).append(")");
                express.append(" != ");
                express.append("\"").append(rightParam).append("\"");
                break;
            case "5000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" >= ");
                express.append(rightParam);
                break;
            case "6000":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                express.append(" <= ");
                express.append(rightParam);
                break;
            case "7100":
            case "7000":
                express.append(label.getInjectionLabelCode()).append(")");
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
            case "7200":
                express.append("toNum(").append(label.getInjectionLabelCode()).append("))");
                String[] strArray2 = rightParam.split(",");
                express.append(" >= ").append(strArray2[0]);
                express.append(" && ").append("(toNum(");
                express.append(label.getInjectionLabelCode()).append("))");
                express.append(" <= ").append(strArray2[1]);

        }
        return express.toString();
    }


    /**
     * 判断事件规则条件是否满足  返回map的code值为success则满足条件 否则返回result错误信息
     *
     * @param eventId    事件id
     * @param labelItems 需要作为标签值的标签  即不需要去查询ES的标签
     * @param map        事件接入的信息
     * @return
     */
    public Map<String, Object> matchRulCondition(Long eventId, Map<String, String> labelItems, Map<String, String> map) {
//        log.info("开始验证事件规则条件");
        Map<String, Object> result = new HashMap<>();
        result.put("code", "success");
        //查询事件规则
        ContactEvtMatchRul evtMatchRul = new ContactEvtMatchRul();
        evtMatchRul.setContactEvtId(eventId);
        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(evtMatchRul);
        //事件规则为空不用判断,直接返回
        if (contactEvtMatchRuls.isEmpty()) {
//            log.info("事件规则为空直接返回");
            return result;
        }
        //查询事件规则条件
        List<EventMatchRulCondition> eventMatchRulConditions = new ArrayList<>();
        for (ContactEvtMatchRul c : contactEvtMatchRuls) {
            List<EventMatchRulCondition> list = eventMatchRulConditionMapper.listEventMatchRulCondition(c.getEvtMatchRulId());
            eventMatchRulConditions.addAll(list);
        }
        if (eventMatchRulConditions.isEmpty()) {
            return result;
        }
        //查询事件规则条件对应的标签
        List<Label> labelList = new ArrayList<>();
        for (EventMatchRulCondition condition : eventMatchRulConditions) {
            //!!先查redis 没有再查数据库
            Label label = (Label) redisUtils.get("MATCH_RUL_CONDITION" + condition.getLeftParam());
            if (label == null) {
                label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                redisUtils.set("MATCH_RUL_CONDITION_" + condition.getLeftParam(), label);
            }
            labelList.add(label);
        }
        //判断那些标签需要查询ES
        List<Label> selectByEs = new ArrayList<>();   //需要查询ES获取标签值的标签集合
        List<Label> myLabelList = new ArrayList<>();  //使用事件接入的数据作为标签值的标签集合
        for (Label label : labelList) {
            if (labelItems.containsKey(label.getInjectionLabelCode())) {
                myLabelList.add(label);
            } else {
                selectByEs.add(label);
            }
        }

        //判断不需要查ES的标签是否符合规则引擎计算结果
        JSONObject eventItem = JSONObject.parseObject(map.get("evtContent"));
        for (Label label : myLabelList) {
            //添加到上下文
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
            context.put(label.getInjectionLabelCode(), eventItem.get(label.getInjectionLabelCode()));
            //事件命中规则信息
            EventMatchRulCondition condition = null;
            for (EventMatchRulCondition c : eventMatchRulConditions) {
                //得到标签对应的事件规则
                if (Long.valueOf(c.getLeftParam()).equals(label.getInjectionLabelId())) {
                    condition = c;
                    break;
                }
            }
            Map<String, String> stringStringMap = decideExpress(condition, label, context);
            //拼接规则引擎判断  有一个不满足则不满足
            if (!stringStringMap.get("code").equals("success")) {
                //判断返回为false直接结束命中
                result.put("result", stringStringMap.get("result"));
                result.put("code", "failed");
                return result;
            }
        }

        //有需要查询ES的标签
        if (!selectByEs.isEmpty()) {
            Map<String, Object> stringObjectMap = selectLabelByEs(selectByEs, map);
            if (stringObjectMap.get("result").equals("success")) {
                //查询成功
                JSONObject body = (JSONObject) stringObjectMap.get("body");
                //拼接规则引擎
                for (Label label : selectByEs) {
                    DefaultContext<String, Object> context = new DefaultContext<String, Object>();
                    //拼装参数
                    for (Map.Entry<String, Object> entry : body.entrySet()) {
                        if (entry.getKey().equals(label.getInjectionLabelCode())) {
                            context.put(entry.getKey(), entry.getValue());
                            log.info("规则计算标签：" + entry.getKey() + "  对应值：" + entry.getValue());
                            break;
                        }
                    }

                    //事件命中规则信息
                    EventMatchRulCondition condition = null;
                    for (EventMatchRulCondition c : eventMatchRulConditions) {
                        //得到标签对应的事件规则
                        if (Long.valueOf(c.getLeftParam()) == label.getInjectionLabelId()) {
                            condition = c;
                            break;
                        }
                    }

                    Map<String, String> stringStringMap = decideExpress(condition, label, context);
                    //拼接规则引擎判断  有一个不满足则不满足
                    if (!stringStringMap.get("code").equals("success")) {
                        //判断返回为false直接结束命中
                        result.put("result", stringStringMap.get("result"));
                        result.put("code", "failed");
                        return result;
                    }
                }
            } else {
                result.put("result", stringObjectMap.get("result"));
                result.put("code", "failed");
            }
        }
        return result;
    }


    /**
     * 查询标签因子实例数据
     *
     * @param selectByEs 需要ES查询的标签
     * @param map        事件接入的信息
     * @return
     */
    public Map<String, Object> selectLabelByEs(List<Label> selectByEs, Map<String, String> map) {
        Map<String, Object> dubboLabel = new HashMap<>();
        dubboLabel.put("result", "success");
        JSONObject param = new JSONObject();
        //查询标识
        param.put("queryNum", map.get("accNbr"));
        param.put("c3", map.get("lanId"));
        param.put("queryId", map.get("integrationId"));
        param.put("type", "1");
        StringBuilder queryFields = new StringBuilder();
        for (Label label : selectByEs) {
            queryFields.append(label.getInjectionLabelCode()).append(",");
        }
        if (queryFields.length() > 0) {
            queryFields.deleteCharAt(queryFields.length() - 1);
        }
        param.put("queryFields", queryFields.toString());
        //查询标签实例数据
        log.info("事件规则请求数据：" + JSON.toJSONString(param));
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
        log.info("事件规则请求ES返回：" + dubboResult.toString());
        if ("0".equals(dubboResult.get("result_code").toString())) {
            //查询成功
            JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
            dubboLabel.put("body", body);
        } else {
            //查询失败
            dubboLabel.put("result", "查询标签实例失败");
        }
        return dubboLabel;
    }


    /**
     * @param condition 事件规则条件
     * @param label     标签
     * @param context   规则需要比较的上下文内容
     */
    public Map<String, String> decideExpress(EventMatchRulCondition condition, Label label, DefaultContext<String, Object> context) {
        String type = condition.getOperType();
        Map<String, String> message = new HashMap<>();
        message.put("code", "success");
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("toNum", new StringToNumOperator("toNum"));

        try {
            String str = cpcLabel(label, type, condition.getRightParam());
            log.info("事件规则表达式" + str);
            RuleResult result = runner.executeRule(str, context, true, true);
            if (null == result.getResult()) {
                //计算为false
                message.put("code", "failed");
                message.put("result", "事件规则条件" + label.getInjectionLabelCode() + "的标签值" + context.get(label.getInjectionLabelCode()) + "不满足条件" + str.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    /**
     * 事件下活动校验
     *
     * @param eventId
     * @param lanId
     * @param channel
     * @return
     */
    private List<Map<String, Object>> getResultByEvent(Long eventId, String lanId, String channel, String reqId, String accNbr, String c4) {
        List<Map<String, Object>> mktCampaginIdList = mktCamEvtRelMapper.listActivityByEventId(eventId);
        // 初始化线程
        ExecutorService fixThreadPool = Executors.newFixedThreadPool(maxPoolSize);
        List<Future<Map<String, Object>>> futureList = new ArrayList<>();
        List<Map<String, Object>> mktCampaignMapList = new ArrayList<>();
        try {
            for (Map<String, Object> act : mktCampaginIdList) {
                Future<Map<String, Object>> future = fixThreadPool.submit(
                        new ListResultByEventTask(lanId, channel, reqId, accNbr, act, c4));
                futureList.add(future);
            }
            if (futureList != null && futureList.size() > 0) {
                for (Future<Map<String, Object>> future : futureList) {
                    Map<String, Object> mktCampaignMap = future.get();
                    if (mktCampaignMap != null && !mktCampaignMap.isEmpty()) {
                        mktCampaignMapList.add(mktCampaignMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[op:getResultByEvent] failed to getResultByEvent by eventId = {}, lanId = {}, channel = {}, Expection = ", eventId, lanId, channel, e);
        }
        return mktCampaignMapList;
    }

    /**
     * 多线程活动校验
     */
    class ListResultByEventTask implements Callable<Map<String, Object>> {
        private String lanId;
        private String channel;
        private String reqId;
        private String accNbr;
        private Map<String, Object> act;
        private String c4;

        public ListResultByEventTask(String lanId, String channel, String reqId, String accNbr, Map<String, Object> act, String c4) {
            this.lanId = lanId;
            this.channel = channel;
            this.reqId = reqId;
            this.accNbr = accNbr;
            this.act = act;
            this.c4 = c4;
        }

        @Override
        public Map<String, Object> call() throws Exception {

            Map<String, Object> mktCampaignMap = new HashMap<>();

            try {
                Long mktCampaginId = (Long) act.get("mktCampaginId");

                //初始化es log
                JSONObject esJson = new JSONObject();
                esJson.put("reqId", reqId);
                esJson.put("activityId", mktCampaginId);
                esJson.put("activityName", act.get("mktCampaginName"));
                esJson.put("activityCode", act.get("mktCampaginNbr"));
                esJson.put("hitEntity", accNbr); //命中对象

                List<String> strategyTypeList = new ArrayList<>();
                strategyTypeList.add("1000");
                strategyTypeList.add("2000");
                strategyTypeList.add("5000");
                //验证过滤规则时间,默认只查询5000类型的时间段过滤
                List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(mktCampaginId, strategyTypeList);
                for (FilterRule filterRule : filterRuleList) {
                    if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                        //获取名单
                        String userList = filterRule.getUserList();
                        if (userList != null && !"".equals(userList)) {
                            int index = userList.indexOf(accNbr);
                            if (index >= 0) {
                                esJson.put("hit", false);
                                esJson.put("msg", "红黑名单过滤规则验证被拦截");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                                return Collections.EMPTY_MAP;
                            }
                        }
                    } else if ("5000".equals(filterRule.getFilterType())) {
                        //时间段的格式
                        if (compareHourAndMinute(filterRule)) {
                            log.info("过滤时间段验证被拦截");
                            esJson.put("hit", false);
                            esJson.put("msg", "过滤时间段验证被拦截");
                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                            return Collections.EMPTY_MAP;
                        }
                    }
                }

                //查询活动信息
                MktCampaignDO mktCampaign = (MktCampaignDO) redisUtils.get("MKT_CAMPAIGN_" + mktCampaginId);
                if (mktCampaign == null) {
                    mktCampaign = mktCampaignMapper.selectByPrimaryKey(mktCampaginId);
                    redisUtils.set("MKT_CAMPAIGN_" + mktCampaginId, mktCampaign);
                }
                Date now = new Date();
                //验证活动生效时间
                Date beginTime = mktCampaign.getPlanBeginTime();
                Date endTime = mktCampaign.getPlanEndTime();
                if (now.before(beginTime) || now.after(endTime)) {
                    //当前时间不在活动生效时间内
                    esJson.put("hit", false);
                    esJson.put("msg", "当前时间不在活动生效时间内");
                    log.info("当前时间不在活动生效时间内");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }

                // 判断活动状态



                if (!StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(mktCampaign.getStatusCd())) {
                    esJson.put("hit", false);
                    esJson.put("msg", "活动状态未发布");
//                log.info("活动状态未发布");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }




                // 判断活动类型
                if (!StatusCode.REAL_TIME_CAMPAIGN.getStatusCode().equals(mktCampaign.getTiggerType())) {
                    esJson.put("hit", false);
                    esJson.put("msg", "活动类型不符");
                    log.info("活动类型不符");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }

                // 查询策略信息
                List<MktStrategyConfDO> mktStrategyConfDOS = mktStrategyConfMapper.selectByCampaignId(mktCampaginId);
                if (mktStrategyConfDOS == null) {
                    esJson.put("hit", false);
                    esJson.put("msg", "策略查询失败");
                    log.info("策略查询失败");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }
                List<Map<String, Object>> strategyMapList = new ArrayList<>();
                for (MktStrategyConfDO mktStrategyConf : mktStrategyConfDOS) {

                    //初始化es log
                    JSONObject esJsonStrategy = new JSONObject();
                    esJsonStrategy.put("reqId", reqId);
                    esJsonStrategy.put("activityId", mktCampaginId);
                    esJsonStrategy.put("hitEntity", accNbr); //命中对象
                    esJsonStrategy.put("strategyConfId", mktStrategyConf.getMktStrategyConfId());
                    esJsonStrategy.put("strategyConfName", mktStrategyConf.getMktStrategyConfName());

                    Map<String, Object> strategyMap = new HashMap<>();
                    //验证策略生效时间
                    if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                        //若当前时间在策略生效时间外
//                    log.info("当前时间不在策略生效时间内");

                        esJson.put("hit", false);
                        esJson.put("msg", "策略未命中");
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                        esJsonStrategy.put("hit", false);
                        esJsonStrategy.put("msg", "当前时间不在策略生效时间内");
                        esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                        continue;
                    }
                    //适用地市校验
                    if (mktStrategyConf.getAreaId() != null && !"".equals(mktStrategyConf.getAreaId())) {
                        String[] strArrayCity = mktStrategyConf.getAreaId().split("/");
                        boolean areaCheck = true;
                        for (String str : strArrayCity) {
                            if (c4 != null) {
                                if (c4.equals(str)) {
                                    areaCheck = false;
                                    break;
                                }
                            }else if (lanId != null) {
                                if (lanId.equals(str)) {
                                    areaCheck = false;
                                    break;
                                }
                            } else {
                                //适用地市获取异常 lanId
//                            log.info("适用地市获取异常");

                                esJson.put("hit", false);
                                esJson.put("msg", "策略未命中");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                                strategyMap.put("msg", "适用地市获取异常");
                                esJsonStrategy.put("hit", "false");
                                esJsonStrategy.put("msg", "适用地市获取异常");
                                esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                            }
                        }
                        if (areaCheck) {

                            esJson.put("hit", false);
                            esJson.put("msg", "策略未命中");
                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                            strategyMap.put("msg", "适用地市不符");
                            esJsonStrategy.put("hit", "false");
                            esJsonStrategy.put("msg", "适用地市不符");
                            esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                            continue;
                        }
                    } else {
                        //适用地市数据异常
//                    log.info("适用地市数据异常");

                        esJson.put("hit", false);
                        esJson.put("msg", "策略未命中");
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                        strategyMap.put("msg", "适用地市数据异常");
                        esJsonStrategy.put("hit", "false");
                        esJsonStrategy.put("msg", "适用地市数据异常");
                        esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                        continue;
                    }
                    //判断适用渠道
                    if (mktStrategyConf.getChannelsId() != null && !"".equals(mktStrategyConf.getChannelsId())) {
                        String[] strArrayChannelsId = mktStrategyConf.getChannelsId().split("/");
                        List<Long> channelsIdList = new ArrayList<>();
                        if (strArrayChannelsId != null && !"".equals(strArrayChannelsId[0])) {
                            for (String channelsId : strArrayChannelsId) {
                                channelsIdList.add(Long.valueOf(channelsId));
                            }
                        }
                        List<String> channelCodeList = contactChannelMapper.selectChannelCodeByPrimaryKey(channelsIdList);
                        boolean channelCheck = true;
                        for (String channelCode : channelCodeList) {
                            if (channel != null) {
                                if (channel.equals(channelCode)) {
                                    channelCheck = false;
                                    break;
                                }
                            } else {
                                //适用地市获取异常 lanId
//                            log.info("适用渠道获取异常");

                                esJson.put("hit", false);
                                esJson.put("msg", "策略未命中");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                                strategyMap.put("msg", "适用渠道获取异常");
                                esJsonStrategy.put("hit", "false");
                                esJsonStrategy.put("msg", "适用渠道获取异常");
                                esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                            }
                        }
                        if (channelCheck) {
//                        log.info("适用渠道不符");

                            esJson.put("hit", false);
                            esJson.put("msg", "策略未命中");
                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                            strategyMap.put("msg", "适用渠道不符");
                            esJsonStrategy.put("hit", "false");
                            esJsonStrategy.put("msg", "适用渠道不符");
                            esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                            continue;
                        }
                    } else {
                        //适用地市数据异常
                        log.info("适用渠道数据异常");

                        esJson.put("hit", false);
                        esJson.put("msg", "策略未命中");
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE,reqId + mktCampaginId + accNbr);

                        strategyMap.put("msg", "适用渠道数据异常");
                        esJsonStrategy.put("hit", "false");
                        esJsonStrategy.put("msg", "适用渠道数据异常");
                        esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                        continue;
                    }

                    // 获取规则
                List<Map<String, Object>> ruleMapList = new ArrayList<>();
                List<MktStrategyConfRuleDO> mktStrategyConfRuleList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConf.getMktStrategyConfId());
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleList) {
                    Map<String, Object> ruleMap = new HashMap<>();
                    String evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId();
//                    if (evtContactConfMapList != null && evtContactConfMapList.size() > 0) {
                    ruleMap.put("ruleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                    ruleMap.put("ruleName", mktStrategyConfRuleDO.getMktStrategyConfRuleName());
                    ruleMap.put("tarGrpId", mktStrategyConfRuleDO.getTarGrpId());
                    ruleMap.put("productId", mktStrategyConfRuleDO.getProductId());
                    ruleMap.put("evtContactConfId", mktStrategyConfRuleDO.getEvtContactConfId());
//                        ruleMap.put("evtContactConfMapList", evtContactConfMapList);
                    ruleMapList.add(ruleMap);
//                    }
                }
                    if (ruleMapList != null && ruleMapList.size() > 0) {
                        strategyMap.put("strategyConfId", mktStrategyConf.getMktStrategyConfId());
                        strategyMap.put("strategyConfName", mktStrategyConf.getMktStrategyConfName());
                        strategyMap.put("ruleMapList", ruleMapList);
                        strategyMapList.add(strategyMap);
                    }
                }
                if (strategyMapList != null && strategyMapList.size() > 0) {
                    mktCampaignMap.put("mktCampaginId", mktCampaginId);
                    mktCampaignMap.put("levelConfig", act.get("levelConfig"));
                    mktCampaignMap.put("campaignSeq", act.get("campaignSeq"));
                    mktCampaignMap.put("strategyMapList", strategyMapList);
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.info("预校验出错");
            }

            return mktCampaignMap;
        }
    }

    /**
     * 添加内置的标签并赋值
     * @param map
     */
    private void setInlayLabel(Map<String,String> map) {
        Calendar calendar = Calendar.getInstance();
        map.put("CPCP_IN_EVENT_YEAR",String.valueOf(calendar.get(Calendar.YEAR)));
        map.put("CPCP_IN_EVENT_MONTH",String.valueOf(calendar.get(Calendar.MONTH) + 1));
        map.put("CPCP_IN_EVENT_DAY",String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }


    //客户级标签
    private DefaultContext<String, Object> getCustLabel( Map<String, String> mktAllLabel, Map<String, String> params,Map<String, String> privateParams,
                                                         DefaultContext<String, Object> context,  JSONObject esJson){
        if (mktAllLabel.get("custLabels") != null && !"".equals(mktAllLabel.get("custLabels"))) {
            JSONObject paramCust = new JSONObject();
            paramCust.put("queryNum", "");
            paramCust.put("c3", params.get("lanId"));
            paramCust.put("queryId", privateParams.get("custId"));
            paramCust.put("type", "2");
            paramCust.put("queryFields", mktAllLabel.get("custLabels"));

            //客户级因子查询-----------------------------------------------------
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(paramCust));

            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));

                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    context.put(entry.getKey(), entry.getValue());
                }
            } else {
                esJson.put("hit", "false");
                esJson.put("msg", "查询客户标签失败");
//                esHitService.save(esJson, IndexList.ACTIVITY_MODULE,params.get("reqId") + activityId + params.get("accNbr"));
                esHitService.save(esJson, IndexList.EVENT_MODULE, params.get("reqId"));
                return null;
            }
        }
        return context;
    }


    // 处理资产级标签和销售品级标签
    private DefaultContext<String, Object> getAssetAndPromLabel(Map<String, String> mktAllLabel, Map<String, String> params,Map<String, String> privateParams,
                                                                DefaultContext<String, Object> context,  JSONObject esJson, Map<String, String> labelItems){
        String saleId = "";
        //资产级标签
        DefaultContext<String, Object> contextNew = new DefaultContext<String, Object>();
        if (mktAllLabel.get("assetLabels") != null && !"".equals(mktAllLabel.get("assetLabels"))) {
            JSONObject assParam = new JSONObject();
            assParam.put("queryNum", privateParams.get("accNbr"));
            assParam.put("c3", params.get("lanId"));
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
            } else {
                log.info("查询资产标签失败");
                esJson.put("hit", "false");
                esJson.put("msg", "查询资产标签失败");
                //esHitService.save(esJson, IndexList.ACTIVITY_MODULE,params.get("reqId") + activityId + params.get("accNbr"));
                esHitService.save(esJson, IndexList.EVENT_MODULE, params.get("reqId"));
                return null;
            }
        }

        //销售品级标签
        if (mktAllLabel.get("promLabels") != null && !"".equals(mktAllLabel.get("promLabels")) && !"".equals(saleId)) {
/*            if ("".equals(saleId)) {
                esJson.put("hit", false);
                esJson.put("msg", "主销售品数据错误");
                log.info("主销售品数据错误");
                //esHitService.save(esJson, IndexList.ACTIVITY_MODULE,params.get("reqId") + activityId + params.get("accNbr"));
                esHitService.save(esJson, IndexList.EVENT_MODULE, params.get("reqId"));
                return null;
            }*/

            JSONObject paramSale = new JSONObject();
            paramSale.put("queryNum", "");
            paramSale.put("c3", params.get("lanId"));
            paramSale.put("queryId", saleId);
            paramSale.put("type", "3");
            paramSale.put("queryFields", mktAllLabel.get("promLabels"));

            //因子查询
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(paramSale));
            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    contextNew.put(entry.getKey(), entry.getValue());
                }
            } else {
                esJson.put("hit", "false");
                esJson.put("msg", "查询销售品级标签失败");
                //esHitService.save(esJson, IndexList.ACTIVITY_MODULE,params.get("reqId") + activityId + params.get("accNbr"));
                esHitService.save(esJson, IndexList.EVENT_MODULE, params.get("reqId"));
                return null;
            }
        }
        contextNew.putAll(labelItems);   //添加事件采集项中作为标签使用的实例
        contextNew.putAll(context);      // 客户级标签
        contextNew.put("integrationId", privateParams.get("integrationId"));
        contextNew.put("accNbr", privateParams.get("accNbr"));
        return contextNew;
    }




    class getListMapLabelTask implements Callable<DefaultContext<String, Object>>{

        private Object o;
        private Map<String, String> mktAllLabel;
        private Map<String, String> map;
        private DefaultContext<String, Object> context;
        private JSONObject esJson;
        Map<String, String> labelItems;

        public getListMapLabelTask(Object o, Map<String, String> mktAllLabel, Map<String, String> map, DefaultContext<String, Object> context, JSONObject esJson, Map<String, String> labelItems) {
            this.o = o;
            this.mktAllLabel = mktAllLabel;
            this.map = map;
            this.context = context;
            this.esJson = esJson;
            this.labelItems = labelItems;
        }

        @Override
        public DefaultContext<String, Object> call() throws Exception {

            DefaultContext<String, Object> resultMap = new DefaultContext<>();
            Map<String, String> privateParams = new HashMap<>();
            privateParams.put("isCust", "0"); //是客户级
            privateParams.put("accNbr", ((Map) o).get("ACC_NBR").toString());
            privateParams.put("integrationId", ((Map) o).get("ASSET_INTEG_ID").toString());
            privateParams.put("custId", map.get("custId"));

            Map<String, Object> assetAndPromLabel = getAssetAndPromLabel(mktAllLabel, map, privateParams, context, esJson, labelItems);
            if (assetAndPromLabel != null) {
                resultMap.putAll(assetAndPromLabel);
            }
            return resultMap;
        }
    }
}