package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.biz.asset.model.dto.AssetDto;
import com.ctzj.biz.asset.model.dto.AssetPromDto;
import com.ctzj.bss.customer.data.carrier.outbound.api.CtgCacheAssetService;
import com.ctzj.bss.customer.data.carrier.outbound.model.ResponseResult;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheIdMappingEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheProdEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheEntityApi.ICacheRelEntityQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheOfferRelIndexQryService;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheProdIndexQryService;
import com.ctzj.smt.bss.cache.service.api.model.CacheResultObject;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.ctzj.smt.bss.customer.model.dataobject.OfferProdInstRel;
import com.ctzj.smt.bss.customer.model.dataobject.ProdInst;
import com.ctzj.smt.bss.customer.model.dataobject.RowIdMapping;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.rule.RuleResult;
import com.telin.dubbo.service.QueryBindByAccCardService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulConditionMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.CamApiSerService;
import com.zjtelcom.cpct.dubbo.service.CamApiService;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.service.dubbo.CamCpcService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.es.es.service.EsService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Filter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.MONTH;

@Service
public class EventApiServiceImpl implements EventApiService {

    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${table.infallible}")
    private String defaultInfallibleTable;
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
    private EsHitsService esHitService;  //es存储

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
    private SysParamsMapper sysParamsMapper;  //查询系统参数

    @Autowired(required = false)
    private SearchLabelService searchLabelService;  //查询活动下使用的所有标签

    @Autowired(required = false)
    private CamApiService camApiService; // 活动任务

    @Autowired(required = false)
    private CamApiSerService camApiSerService; // 服务活动任务

    @Autowired(required = false)
    private EsService esService;

    @Autowired(required = false)
    private CtgCacheAssetService ctgCacheAssetService;// 销售品过滤方法

    @Autowired(required = false)
    private QueryBindByAccCardService queryBindByAccCardService; // 通过号码查询绑定状态

    @Autowired(required = false)
    private ICacheProdIndexQryService iCacheProdIndexQryService;

    @Autowired(required = false)
    private ICacheProdEntityQryService iCacheProdEntityQryService;

    @Autowired(required = false)
    private ICacheOfferRelIndexQryService iCacheOfferRelIndexQryService;

    @Autowired(required = false)
    private ICacheRelEntityQryService iCacheRelEntityQryService;

    @Autowired(required = false)
    private ICacheIdMappingEntityQryService iCacheIdMappingEntityQryService;

    /*@Autowired(required = false)
    private CamCpcService camCpcService;*/

    /*@Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;*/

    private final static String USED_FLOW = "used_flow";

    private final static String TOTAL_FLOW = "total_flow";

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
            e.printStackTrace();
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

            //构造返回结果

            String custId = map.get("custId");
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);
            result.put("taskList", activityList);
            Map<String, Object> evtContent = (Map<String, Object>) JSON.parse(map.get("evtContent"));
            List<Map<String, Object>> triggersList = new ArrayList<>();
            if (evtContent != null && !evtContent.isEmpty()) {
                for (Map.Entry entry : evtContent.entrySet()) {
                    Map<String, Object> trigger = new HashMap<>();
                    trigger.put("key", entry.getKey());
                    trigger.put("value", entry.getValue());
                    triggersList.add(trigger);
                }
                result.put("triggers", triggersList);
            }

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
                //事件验证开始↓↓↓↓↓↓↓↓↓↓↓↓↓l
                //解析事件采集项
                JSONObject evtParams = JSONObject.parseObject(map.get("evtContent"));
                //获取C4的数据用于过滤
                String c4 = null;
                if (evtParams != null) {
                    c4 = (String) evtParams.get("C4");
                }

                //根据事件code查询事件信息
                Object eventC =  redisUtils.get("EVENT_" + map.get("eventCode"));
                ContactEvt event = null;
                if (eventC!=null){
                    event = (ContactEvt)eventC;
                }
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

                //判断是否有流量事件,EVTS000001001,EVTS000001002
                // CPCP_USED_FLOW为已使用流量， CPCP_LEFT_FLOW为剩余流量, CPCP_NEED_FLOW 需要流量
                String eventCode = (String) map.get("eventCode");
                if ("EVTS000001001".equals(eventCode) || "EVTS000001002".equals(eventCode) && evtParams != null && evtParams.get("CPCP_USED_FLOW") != null && evtParams.get("CPCP_LEFT_FLOW") != null) {
                    String cpcpNeedFlow = getCpcpNeedFlow((String) evtParams.get("CPCP_USED_FLOW"), (String) evtParams.get("CPCP_LEFT_FLOW"));
                    labelItems.put("CPCP_NEED_FLOW", cpcpNeedFlow);

                }

                // 满意度调查事件，定义采集项
                if ("EVTD000000091".equals(eventCode)) {
                    // 联系号码-事件采集项
                    String contactNumber = (String) evtParams.get("CPCP_CONTACT_NUMBER");
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("phone", contactNumber);
                    paramsMap.put("type", "1");
                    labelItems.put("CPCP_PUSH_NUMBER", contactNumber);
                    // 判断该联系号码是否绑定微厅
                    Map<String, Object> resultMap = queryBindByAccCardService.queryBindByAccCard(paramsMap);
                    if (resultMap != null && resultMap.get("data") != null && ((List<Map>) resultMap.get("data")).size() > 0) {
                        // 绑定微厅
                        List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) resultMap.get("data");
                        for (Map dataMap : dataMapList) {
                            if (dataMap.get("tel_status") != null && (Integer) dataMap.get("tel_status") == 0) {
                                labelItems.put("CPCP_PUSH_CHANNEL", "1"); // 1-微厅, 2-短厅, 3-IVR
                                break;
                            }
                        }

                    } else {
                        // 判断资产号码是否绑定微厅
                        paramsMap.put("phone", map.get("accNbr"));
                        Map<String, Object> accResultMap = queryBindByAccCardService.queryBindByAccCard(paramsMap);
                        if (accResultMap != null && accResultMap.get("data") != null && ((List<Map>) accResultMap.get("data")).size() > 0) {
                            // 绑定微厅
                            List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) accResultMap.get("data");
                            for (Map dataMap : dataMapList) {
                                if (dataMap.get("tel_status") != null && (Integer) dataMap.get("tel_status") == 0) {
                                    labelItems.put("CPCP_PUSH_CHANNEL", "1"); // 1-微厅, 2-短厅, 3-IVR
                                    break;
                                }
                            }
                        } else {
                            // 若未绑定微厅，查看联系号码是否为C网用户
                            // 价格判断是否为手机号码
                            //    log.info("111---contactNumber --->" + contactNumber);
                            boolean isMobile = isMobile(contactNumber);
                            boolean isCUser = false;
                            //    log.info("222---isMobile --->" + isMobile);
                            if (isMobile) {
                                CacheResultObject<Set<String>> prodInstIdResult = iCacheProdIndexQryService.qryProdInstIndex3(contactNumber, "100000");
                                //        log.info("333---是否为C网用户-----prodInstIdResult --->" + JSON.toJSONString(prodInstIdResult));
                                if (prodInstIdResult != null && prodInstIdResult.getResultObject() != null && prodInstIdResult.getResultObject().size() > 0) {
                                    labelItems.put("CPCP_PUSH_CHANNEL", "2"); // 1-微厅, 2-短厅, 3-IVR
                                    isCUser = true;
                                }
                            }
                            if (!isCUser) {
                                //查看资产号码是否为C网用户
                                JSONObject param = new JSONObject();
                                //查询标识
                                param.put("queryNum", map.get("accNbr"));
                                param.put("c3", map.get("lanId"));
                                param.put("queryId", map.get("integrationId"));
                                param.put("type", "1");
                                param.put("queryFields", "PRD_NAME");
                                param.put("centerType", "00");

                                //因子查询-----------------------------------------------------
                                boolean isCdma = false;
                                Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
                                if ("0".equals(dubboResult.get("result_code").toString())) {
                                    JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                                    //           log.info("444---body --->" + JSON.toJSONString(body));
                                    //ES log 标签实例
                                    for (Map.Entry<String, Object> entry : body.entrySet()) {
                                        if ("PRD_NAME".equals(entry.getKey()) && "CDMA".equals(entry.getValue().toString())) {
                                            labelItems.put("CPCP_PUSH_CHANNEL", "2"); // 1-微厅, 2-短厅, 3-IVR
                                            isCdma = true;
                                        }
                                    }
                                } else {
                                    log.info("查询资产标签失败-判断C网用户");
                                    esJson.put("hit", "false");
                                    esJson.put("msg", "查询资产标签失败-判断C网用户");
                                    return null;
                                }
                                // 若不为C网用户，则“推送渠道”为IVR外呼
                                if (!isCdma) {
                                    labelItems.put("CPCP_PUSH_CHANNEL", "3"); // 1-微厅, 2-短厅, 3-IVR
                                }
                            }
                        }
                    }
                    //  log.info("555---labelItems --->" + JSON.toJSONString(labelItems));
                }

                // 计费短信合并功能 CPCP_JIFEI_CONTENT
                if (evtParams != null) {
                    cpcpJifeiContent(labelItems, evtParams);
                }

                //获取事件推荐活动数
                int recCampaignAmount;
                String recCampaignAmountStr = event.getRecCampaignAmount();
                if (recCampaignAmountStr == null || "".equals(recCampaignAmountStr)) {
                    recCampaignAmount = 0;
                } else {
                    recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
                }
                List<Map<String, Object>> resultByEvent = null;


                try {
                    //事件下所有活动的规则预校验，返回初步可命中活动
                    resultByEvent = getResultByEvent(eventId, map.get("eventCode"), map.get("lanId"), map.get("channelCode"), map.get("reqId"), map.get("accNbr"), c4, map.get("custId"));
                } catch (Exception e) {
                    esJson.put("hit", false);
                    esJson.put("success", true);
                    esJson.put("msg", "预校验返回异常");
                    esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                    e.printStackTrace();
                }

                // 固定必中规则提取
                // List<Map<String, Object>> resultByEvent2 = getBitslapByEvent(eventId, resultByEvent);

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
                    result.put("CPCResultMsg", "succes 预校验为空");
                    result.put("taskList", activityList);

                    paramsJson.put("backParams", result);
                    esHitService.save(paramsJson, IndexList.PARAMS_MODULE, map.get("reqId"));

                    log.info("事件计算流程结束:" + map.get("eventCode") + "***" + map.get("reqId") + "（" + (System.currentTimeMillis() - begin) + "）");

                    return result;
                }


                //判断有没有客户级活动
                Boolean hasCust = false;  //是否有客户级
                Boolean hasPackage = false;  //是否有套餐
                Boolean successCust = false;  //客户级查询是否成功
                Boolean successPackage = false;  //套餐级查询是否成功
                for (Map<String, Object> activeMap : resultByEvent) {
                    if (((Map) activeMap.get("mktCampaignMap")) != null && !((Map) activeMap.get("mktCampaignMap")).isEmpty()) {
                        if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 1) {  // 客户级
                            hasCust = true;
                        } else if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 2) { // 套餐级
                            hasPackage = true;
                        }
                    }
                }

                //查询事件下使用的所有标签
                DefaultContext<String, Object> context = new DefaultContext<String, Object>();
                Map<String, String> mktAllLabels = (Map<String, String>) redisUtils.get("EVT_ALL_LABEL_" + eventId);
                if (mktAllLabels == null) {
                    try {

                        mktAllLabels = new HashMap<>();

//                        mktAllLabels = searchLabelService.labelListByEventId(eventId);  //查询事件下使用的所有标签
//                        if (null != mktAllLabels) {
//                            redisUtils.set("EVT_ALL_LABEL_" + eventId, mktAllLabels);
//                        } else {
//                            log.info("获取事件下所有标签失败");
//                            esJson.put("hit", false);
//                            esJson.put("msg", "获取事件下所有标签失败");
//                            esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
//                            return Collections.EMPTY_MAP;
//                        }
                    } catch (Exception e) {
                        esJson.put("hit", false);
                        esJson.put("msg", "获取事件下所有标签异常");
                        esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                        return Collections.EMPTY_MAP;
                    }
                }

                // 过滤事件采集项中的标签
                Map<String, String> mktAllLabel = new HashMap<>();
                Iterator<Map.Entry<String, String>> iterator = labelItems.entrySet().iterator();
                List<String> assetLabelList = new ArrayList<>();
                List<String> promLabelList = new ArrayList<>();
                List<String> custLabelList = new ArrayList<>();
                if (mktAllLabels.get("assetLabels") != null && !"".equals(mktAllLabels.get("assetLabels"))) {
                    assetLabelList = ChannelUtil.StringToList(mktAllLabels.get("assetLabels"));
                    // ASSI_PROM_INTEG_ID标签
                    assetLabelList.add("ASSI_PROM_INTEG_ID");
                }
                if (mktAllLabels.get("promLabels") != null && !"".equals(mktAllLabels.get("promLabels"))) {
                    promLabelList = ChannelUtil.StringToList(mktAllLabels.get("promLabels"));
                }
                if (mktAllLabels.get("custLabels") != null && !"".equals(mktAllLabels.get("custLabels"))) {
                    custLabelList = ChannelUtil.StringToList(mktAllLabels.get("custLabels"));
                }

                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    if (assetLabelList.contains(entry.getKey())) {
                        assetLabelList.remove(entry.getKey());
                    } else if (promLabelList.contains(entry.getKey())) {
                        promLabelList.remove(entry.getKey());
                    } else if (custLabelList.contains(entry.getKey())) {
                        custLabelList.remove(entry.getKey());
                    }
                }

                List<String> labelList = new ArrayList<>();
                labelList.addAll(assetLabelList);
                labelList.addAll(promLabelList);
                labelList.addAll(custLabelList);
                // 添加落地网格AREA_ID标签
                labelList.add("AREA_ID");


                if (assetLabelList != null && assetLabelList.size() > 0) {
                    mktAllLabel.put("assetLabels", ChannelUtil.StringList2String(labelList));
                }

                List<DefaultContext<String, Object>> resultMapList = new ArrayList<>();
                List<Map<String, Object>> accNbrMapList = new ArrayList<>();
                JSONArray accArray = new JSONArray();
                // 是客户级的
                if (hasCust || hasPackage) {
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

                    } else if (hasCust) {
                        JSONObject param = new JSONObject();
                        //查询标识
                        param.put("c3", map.get("lanId"));
                        param.put("queryId", map.get("custId"));
                        param.put("queryNum", "");
                        param.put("queryFields", "");
                        param.put("type", "4");
                        param.put("centerType", "00");
                        ExecutorService executorService = Executors.newCachedThreadPool();
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
                                    resultMapList.add(reultMap);
                                }
                            }

                        } catch (Exception e) {
                            log.error("Exception = " + e);
                        } finally {
                            executorService.shutdown();
                        }
                        // 判断是否存在套餐级
                        if (hasPackage) {
                            accNbrMapList = getAccNbrList(map.get("accNbr"));
                            successPackage = true;
                        }

                    } else if (hasPackage) {
                        accNbrMapList = getAccNbrList(map.get("accNbr"));
                        successPackage = true;
                        // 查询标签
                        ExecutorService executorService = Executors.newCachedThreadPool();
                        try {
                            // 客户级
                            List<Future<DefaultContext<String, Object>>> futureList = new ArrayList<>();

                            //多线程获取资产级标签，并加上客户级标签
                            for (Object o : accNbrMapList) {
                                Future<DefaultContext<String, Object>> future = executorService.submit(new getListMapLabelTask(o, mktAllLabel, map, context, esJson, labelItems));
                                futureList.add(future);
                            }

                            for (Future<DefaultContext<String, Object>> future : futureList) {
                                if (future.get() != null && !future.get().isEmpty()) {
                                    DefaultContext<String, Object> reultMap = future.get();
                                    resultMapList.add(reultMap);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            executorService.shutdown();
                        }
                    }
                } else {
                    //资产级
                    Map<String, String> privateParams = new HashMap<>();
                    privateParams.put("isCust", "1"); //资产级
                    privateParams.put("accNbr", map.get("accNbr"));
                    privateParams.put("integrationId", map.get("integrationId"));// 资产集成编码
                    privateParams.put("custId", map.get("custId"));

                    DefaultContext<String, Object> reultMap = new DefaultContext<>();
                    Map<String, Object> assetLabelMap = getAssetAndPromLabel(mktAllLabel, map, privateParams, context, esJson, labelItems);
                    if (assetLabelMap != null) {
                        reultMap.putAll(assetLabelMap);
                    } else {
                        return null;
                    }
                    resultMapList.add(reultMap);
                }


                //初始化结果集
                List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                //初始化线程池
//                ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT_ACTIVE);
                ExecutorService executorService = Executors.newCachedThreadPool();

                // 启动线程走清单流程
                List<String> mktCampaginIdList = new ArrayList<>();
                List<String>initIdList = new ArrayList<>();
                for (Map<String, Object> activeMap : resultByEvent) {
                    if (activeMap.get("mktCampaignCustMap") != null && !((Map<String, Object>) activeMap.get("mktCampaignCustMap")).isEmpty()) {
                        mktCampaginIdList.add(((Map<String, Object>) activeMap.get("mktCampaignCustMap")).get("mktCampaginId").toString());
                        initIdList.add(((Map<String, Object>) activeMap.get("mktCampaignCustMap")).get("initId").toString());
                    }
                }
                if (mktCampaginIdList != null && mktCampaginIdList.size() > 0) {
                    Future<Map<String, Object>> f = executorService.submit(new getCustListTask(mktCampaginIdList, initIdList, eventId, map.get("lanId"), custId, map, evtTriggers));
                    threadList.add(f);
                }


                //判断是否全部为资产级
                boolean isAllAsset = false;
                for (Map<String, Object> activeMap : resultByEvent) {
                    if (((Map<String, Object>) activeMap.get("mktCampaignMap")) != null && !((Map<String, Object>) activeMap.get("mktCampaignMap")).isEmpty()) {
                        if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 1) { // 1为客户级
                            isAllAsset = false;
                            break;
                        } else if ((Integer) ((Map<String, Object>) activeMap.get("mktCampaignMap")).get("levelConfig") == 0) {  // 0为资产级
                            isAllAsset = true;
                        }
                    }
                }
                // 全部为资产级时直接遍历活动
                if (isAllAsset) {
                    for (Map<String, Object> resultMap : resultByEvent) {
                        //资产级
                        Map<String, Object> activeMap = (Map<String, Object>) resultMap.get("mktCampaignMap");
                        if (activeMap != null && !activeMap.isEmpty()) {
                            Map<String, String> privateParams = new HashMap<>();
                            privateParams.put("isCust", "1"); //是否是客户级
                            privateParams.put("accNbr", map.get("accNbr"));
                            privateParams.put("integrationId", map.get("integrationId"));
                            privateParams.put("custId", map.get("custId"));
                            privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                            //资产级
                            Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), (String) activeMap.get("type"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), resultMapList.get(0)));
                            //将线程处理结果添加到结果集
                            threadList.add(f);
                        }
                    }
                } else {
                    //遍历活动
                    for (Map<String, Object> resultMap : resultByEvent) {
                        //提交线程
                        Map<String, Object> activeMap = (Map<String, Object>) resultMap.get("mktCampaignMap");
                        if (activeMap != null && !activeMap.isEmpty()) {
                            if ((Integer) activeMap.get("levelConfig") == 1) { //判断是客户级
                                //客户级
                                if (successCust) {
                                    for (DefaultContext<String, Object> o : resultMapList) {
                                        //客户级下，循环资产级
                                        Map<String, String> privateParams = new HashMap<>();
                                        privateParams.put("isCust", "0"); //是客户级
                                        privateParams.put("accNbr", o.get("accNbr").toString());
                                        privateParams.put("integrationId", o.get("integrationId").toString());
                                        privateParams.put("custId", map.get("custId"));
                                        //活动优先级为空的时候默认0
                                        privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                                        Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), (String) activeMap.get("type"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), o));
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
                                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, map.get("reqId") + activeMap.get("mktCampaginId") + map.get("accNbr"));
                                }
                            } else if ((Integer) activeMap.get("levelConfig") == 2) { // 判断是套餐级别
                                //套餐级
                                if (successPackage) {
                                    for (DefaultContext<String, Object> o : resultMapList) {
                                        for (Map<String, Object> accNbrMap : accNbrMapList) {
                                            if (o.get("accNbr").toString().equals(accNbrMap.get("ACC_NBR"))) {
                                                //客户级下，循环资产级
                                                Map<String, String> privateParams = new HashMap<>();
                                                privateParams.put("isCust", "0"); //是客户级
                                                privateParams.put("accNbr", o.get("accNbr").toString());
                                                privateParams.put("integrationId", o.get("integrationId").toString());
                                                privateParams.put("custId", map.get("custId"));
                                                //活动优先级为空的时候默认0
                                                privateParams.put("orderPriority", activeMap.get("campaignSeq") == null ? "0" : activeMap.get("campaignSeq").toString());
                                                Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), (String) activeMap.get("type"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), o));
                                                //将线程处理结果添加到结果集
                                                threadList.add(f);
                                            }
                                        }
                                    }
                                } else {
                                    log.error("套餐级资产查询出错:" + map.get("reqId"));
                                    esJson.put("reqId", map.get("reqId"));
                                    esJson.put("activityId", activeMap.get("mktCampaginId"));
                                    esJson.put("hitEntity", map.get("accNbr"));  //命中对象
                                    esJson.put("hit", false);
                                    esJson.put("msg", "客户级资产查询出错");
                                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, map.get("reqId") + activeMap.get("mktCampaginId") + map.get("accNbr"));

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
                                        Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, (Long) activeMap.get("mktCampaginId"), (String) activeMap.get("type"), privateParams, labelItems, evtTriggers, (List<Map<String, Object>>) activeMap.get("strategyMapList"), o));
                                        //将线程处理结果添加到结果集
                                        threadList.add(f);
                                    }
                                }
                            }
                        }
                    }
                }


                //获取结果
                Boolean flag = true;
                try {
                    Map<String, Object> nonPassedMsg = new HashMap<>();
                    for (Future<Map<String, Object>> future : threadList) {
                        /*if (future.get() != null && !future.get().isEmpty()) {
                            activityList.addAll((List<Map<String, Object>>) (future.get().get("ruleList")));
                        }*/
                        if (future.get() != null && !future.get().isEmpty()) {
                            Map<String, Object> map1 = future.get();
                            for (String s : map1.keySet()) {
                                if (s.contains("cam_")) {
                                    flag = false;
                                    nonPassedMsg.put(s, map1.get(s));
                                }
                            }
                            if (map1.get("nonPassedMsg") != null) {
                                Object nonPassedMsg1 = map1.get("nonPassedMsg");
                                nonPassedMsg.putAll((Map<String, Object>)nonPassedMsg1);
                                // flag = false;
                            }
                            if (flag) {
                                // 命中活动
                                if (future.get() != null && future.get().get("ruleList") != null ) {
                                    activityList.addAll((List<Map<String, Object>>) (future.get().get("ruleList")));
                                }
                            }
                        }
                        /*else {
                            // 翼支付未命中原因
                            nonPassedMsg.putAll(future.get());
                        }*/
                    }
                    result.put("nonPassedMsg", JSON.toJSONString(nonPassedMsg));
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


                // 判断事件推荐活动数，按照优先级排序
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
                esHitService.save(paramsJson, IndexList.PARAMS_MODULE, map.get("reqId"));

                //es log
                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("success", true);
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

            } catch (Exception e) {
                log.info("策略中心计算异常");
                log.error("Exception = ", e);
                esJson.put("errorMsg", e.getMessage());
                esHitService.save(paramsJson, IndexList.PARAMS_MODULE, map.get("reqId"));

                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("hit", false);
                esJson.put("success", true);
                esJson.put("msg", "策略中心计算异常"+e.getMessage()+e.toString());
                esHitService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                //构造返回参数
                result.put("CPCResultCode", "1000");
                result.put("CPCResultMsg", "策略中心计算异常"+e.getMessage()+e.toString());
                result.put("reqId", map.get("reqId"));
                result.put("custId", custId);
                return result;
            }
            log.info("事件计算流程结束:" + map.get("eventCode") + "***" + map.get("reqId") + "（" + (System.currentTimeMillis() - begin) + "）");
            return result;
        }
    }

    /*private List<Map<String,Object>> getBitslapByEvent(Long eventId, List<Map<String,Object>> resultByEvent) {
        // 遍历出被过滤掉的活动
        List<Map<String, Object>> mktCampaginIdList = mktCamEvtRelMapper.listActivityByEventId(eventId);
        for (Map<String, Object> passMap : resultByEvent) {
            Map<String,Object> map = passMap.get("mktCampaignMap") == null ? new HashMap<>() : (Map<String, Object>)passMap.get("mktCampaignMap");
            if (map != null && map.get("mktCampaginId") != null && map.get("mktCampaginId") != "") {
                String passCamId = map.get("mktCampaginId").toString();
                for (Map<String, Object> countMap : mktCampaginIdList) {
                    String countCamId = countMap.get("mktCampaginId").toString();
                    if (passCamId.equals(countCamId)) {
                        mktCampaginIdList.remove(countMap);
                        break;
                    }
                }
            }
        }
        // 遍历活动找出配有必中标签的活动
        boolean flag = true;
        if (!mktCampaginIdList.isEmpty()) {
            ListIterator it = mktCampaginIdList.listIterator();
            while (it.hasNext()) {
                flag = true;
                Map<String, Object> map = (Map<String, Object>) it.next();
                String mktCampaginId = map.get("mktCampaginId").toString();
                List<Map<String, String>> mapList = tarGrpConditionMapper.selectAllLabelByCamId(Long.valueOf(mktCampaginId));
                if (!mapList.isEmpty()) {
                    for (Map<String, String> ruleMap : mapList) {
                        String labelCode = ruleMap.get("labelCode");
                        String ruleId = ruleMap.get("ruleId");
                        if (labelCode.equals(defaultInfallibleTable)) {
                            flag = false;
                            map.put("camPass", false);
                            map.put("willBeInRuleId", ruleId);
                        }
                    }
                }
                if (flag) {
                    mktCampaginIdList.remove(map);
                }
            }
        }
        return mktCampaginIdList;
    }*/


    /**
     * 活动级别验证
     */
    class ActivityTask implements Callable<Map<String, Object>> {
        private Long activityId;
        private String type;
        private String reqId;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private Map<String, String> labelItems;
        private List<Map<String, Object>> evtTriggers;
        private List<Map<String, Object>> strategyMapList;
        private DefaultContext<String, Object> context;

        ActivityTask(Map<String, String> params, Long activityId, String type, Map<String, String> privateParams, Map<String, String> labelItems, List<Map<String, Object>> evtTriggers, List<Map<String, Object>> strategyMapList, DefaultContext<String, Object> context) {
            this.activityId = activityId;
            this.type = type;
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
            Map<String, Object> activityTaskResultMap = new HashMap<>();
            if(StatusCode.SERVICE_CAMPAIGN.getStatusCode().equals(type) || StatusCode.SERVICE_SALES_CAMPAIGN.getStatusCode().equals(type)){
                log.info("服务活动进入camApiSerService.ActivitySerTask");
                //activityTaskResultMap = camCpcService.ActivityCpcTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
                activityTaskResultMap = camApiSerService.ActivitySerTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
            } else {
                log.info("进入camApiService.ActivityTask");
                //activityTaskResultMap = camCpcService.ActivityCpcTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
                activityTaskResultMap = camApiService.ActivityTask(params, activityId, privateParams, labelItems, evtTriggers, strategyMapList, context);
            }
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

        if ((cal.getTimeInMillis() - start.getTimeInMillis()) > 0 && (cal.getTimeInMillis() - end.getTimeInMillis()) < 0) {
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
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "4000":
                express.append(label.getInjectionLabelCode()).append(")");
                express.append(" != ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
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
        param.put("centerType", "00");
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
    private List<Map<String, Object>> getResultByEvent(Long eventId, String eventCode, String lanId, String channel, String reqId, String accNbr, String c4, String custId) {
        List<Map<String, Object>> mktCampaginIdList = mktCamEvtRelMapper.listActivityByEventId(eventId);
        // 初始化线程
        ExecutorService fixThreadPool = Executors.newFixedThreadPool(maxPoolSize);
        List<Future<Map<String, Object>>> futureList = new ArrayList<>();
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        try {
            for (Map<String, Object> act : mktCampaginIdList) {
                act.put("eventCode", eventCode);
                Future<Map<String, Object>> future = fixThreadPool.submit(new ListResultByEventTask(lanId, channel, reqId, accNbr, act, c4, custId));
                futureList.add(future);
            }
            if (futureList != null && futureList.size() > 0) {
                for (Future<Map<String, Object>> future : futureList) {
                    try {
                        Map<String, Object> resultMap = future.get();
                        if (resultMap != null && !resultMap.isEmpty()) {
                            resultMapList.add(resultMap);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            log.error("[op:getResultByEvent] failed to getResultByEvent by eventId = {}, lanId = {}, channel = {}, Expection = ", eventId, lanId, channel, e);
        } finally {
            fixThreadPool.shutdown();
        }
        return resultMapList;
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
        private String custId;

        public ListResultByEventTask(String lanId, String channel, String reqId, String accNbr, Map<String, Object> act, String c4, String custId) {
            this.lanId = lanId;
            this.channel = channel;
            this.reqId = reqId;
            this.accNbr = accNbr;
            this.act = act;
            this.c4 = c4;
            this.custId = custId;
        }

        @Override
        public Map<String, Object> call() throws Exception {

            Map<String, Object> resultMap = new ConcurrentHashMap<>();

            Map<String, Object> mktCampaignMap = new ConcurrentHashMap<>();

            Map<String, Object> mktCampaignCustMap = new ConcurrentHashMap<>();
            log.info("活动预校验开始************");

            try {
                Long mktCampaginId = (Long) act.get("mktCampaginId");
                //初始化es log
                JSONObject esJson = new JSONObject();
                esJson.put("reqId", reqId);
                esJson.put("activityId", mktCampaginId);
                esJson.put("activityName", act.get("mktCampaginName"));
                esJson.put("activityCode", act.get("mktCampaginNbr"));
                esJson.put("hitEntity", accNbr); //命中对象
                esJson.put("eventCode", act.get("eventCode"));


                if ("QD000015".equals(channel)) {
                    log.info("活动预校验：channel-----QD000015");
                    List<String> strategyTypeList = new ArrayList<>();
                    strategyTypeList.add("1000");
                    strategyTypeList.add("2000");
                    strategyTypeList.add("5000");

                    boolean iSRed = false;
                    //验证过滤规则时间,默认只查询5000类型的时间段过滤
                    List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(mktCampaginId, strategyTypeList);
                    for (FilterRule filterRule : filterRuleList) {
                        if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                            iSRed = true;
                            break;
                        }
                    }
                    JSONArray accArrayF = new JSONArray();
                    if (iSRed) {
                        JSONObject param = new JSONObject();
                        //查询标识
                        param.put("c3", lanId);
                        param.put("queryId", custId);
                        param.put("queryNum", "");
                        param.put("queryFields", "");
                        param.put("type", "4");
                        param.put("centerType", "00");
                        Map<String, Object> dubboResult_F = new HashMap<>();
                        try {
                            dubboResult_F = yzServ.queryYz(JSON.toJSONString(param));
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("queryYz查询失败："+e,JSON.toJSONString(dubboResult_F));
                        }
                        try {
                            if ("0".equals(dubboResult_F.get("result_code").toString())) {
                                accArrayF = new JSONArray((List<Object>) dubboResult_F.get("msgbody"));
                            }
                        } catch (Exception e) {
                            log.info("dubboResult_F.get(\"result_code\").toString()异常了~~~");
                            e.printStackTrace();
                        }
                    }

                    try {
                        for (FilterRule filterRule : filterRuleList) {
                            if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                                //获取名单
                                String userList = filterRule.getUserList();
                                if (userList != null && !"".equals(userList)) {
                                    for (Object ob : accArrayF) {
                                        int index = userList.indexOf(((Map) ob).get("ACC_NBR").toString());
                                        if (index >= 0) {
                                            esJson.put("hit", false);
                                            esJson.put("msg", "红黑名单过滤规则验证被拦截");
                                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                                            return Collections.EMPTY_MAP;
                                        }
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
                    } catch (Exception e) {
                        log.info("filterRuleList 预校验过滤出错！");
                        e.printStackTrace();
                    }
                } else {
                    try {
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
                    } catch (Exception e) {
                        log.info("filterRuleList else ！预校验过滤出错");
                        e.printStackTrace();
                    }
                }


                //查询活动信息
                MktCampaignDO mktCampaign = null;
                try {
                    Object campaign =  redisUtils.get("MKT_CAMPAIGN_" + mktCampaginId);
                    if (campaign!=null){
                        mktCampaign = (MktCampaignDO) campaign;
                    }
                    log.info(JSON.toJSONString(mktCampaign));
                } catch (Exception e) {
                    log.info("(MktCampaignDO) redisUtils.get(\"MKT_CAMPAIGN_\" + mktCampaginId)出现异常 缓存没取到？");
                    e.printStackTrace();
                }
                if (mktCampaign == null) {
                    mktCampaign = mktCampaignMapper.selectByPrimaryKey(mktCampaginId);
                    redisUtils.set("MKT_CAMPAIGN_" + mktCampaginId, mktCampaign);
                }
                Date now = null;
                try {
                    now = new Date();
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
                } catch (Exception e) {
                    log.info("验证活动生效时间失败");
                    e.printStackTrace();
                }

                log.info("预校验还没出错，拿到活动信息");

                // 判断活动状态


                if (!(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(mktCampaign.getStatusCd())
                        || StatusCode.STATUS_CODE_ADJUST.getStatusCode().equals(mktCampaign.getStatusCd()))) {
                    esJson.put("hit", false);
                    esJson.put("msg", "活动状态未发布");
//                log.info("活动状态未发布");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }


                // 判断触发活动类型
                if (!StatusCode.REAL_TIME_CAMPAIGN.getStatusCode().equals(mktCampaign.getTiggerType())
                        && !StatusCode.MIXTURE_CAMPAIGN.getStatusCode().equals(mktCampaign.getTiggerType())) {
                    esJson.put("hit", false);
                    esJson.put("msg", "活动触发类型不符");
                    log.info("活动触发类型不符");
                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE);
                    return Collections.EMPTY_MAP;
                }

                // 判断活动类型
                if (!StatusCode.AUTONOMICK_CAMPAIGN.getStatusCode().equals(mktCampaign.getMktCampaignCategory())) {
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

                    Map<String, Object> strategyMap = new ConcurrentHashMap<>();
                    //验证策略生效时间
                    if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                        //若当前时间在策略生效时间外
//                    log.info("当前时间不在策略生效时间内");

                        esJson.put("hit", false);
                        esJson.put("msg", "策略未命中");
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

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
                            if (c4 != null && c4.equals(str)) {
                                areaCheck = false;
                                break;
                            } else if (lanId != null) {
                                if (lanId.equals(str)) {
                                    areaCheck = false;
                                    break;
                                }
                            } else {
                                //适用地市获取异常 lanId
//                            log.info("适用地市获取异常");

                                esJson.put("hit", false);
                                esJson.put("msg", "策略未命中");
                                esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                                strategyMap.put("msg", "适用地市获取异常");
                                esJsonStrategy.put("hit", "false");
                                esJsonStrategy.put("msg", "适用地市获取异常");
                                esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                            }
                        }
                        if (areaCheck) {

                            esJson.put("hit", false);
                            esJson.put("msg", "策略未命中");
                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

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
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

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
                        try {
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
                                    esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                                    strategyMap.put("msg", "适用渠道获取异常");
                                    esJsonStrategy.put("hit", "false");
                                    esJsonStrategy.put("msg", "适用渠道获取异常");
                                    esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                                }
                            }
                        } catch (Exception e) {
                            log.error("预校验渠道获取异常");
                            e.printStackTrace();
                        }
                        if (channelCheck) {
//                        log.info("适用渠道不符");

                            esJson.put("hit", false);
                            esJson.put("msg", "策略未命中");
                            esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

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
                        esHitService.save(esJson, IndexList.ACTIVITY_MODULE, reqId + mktCampaginId + accNbr);

                        strategyMap.put("msg", "适用渠道数据异常");
                        esJsonStrategy.put("hit", "false");
                        esJsonStrategy.put("msg", "适用渠道数据异常");
                        esHitService.save(esJsonStrategy, IndexList.STRATEGY_MODULE);
                        continue;
                    }
                    log.info("预校验还没出错1");
                    // 获取规则
                    List<Map<String, Object>> ruleMapList = new ArrayList<>();
                    List<MktStrategyConfRuleDO> mktStrategyConfRuleList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConf.getMktStrategyConfId());
                    try {
                        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleList) {
                            Map<String, Object> ruleMap = new ConcurrentHashMap<>();
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
                    } catch (Exception e) {
                        log.error("预校验获取规则查询失败");
                        e.printStackTrace();
                    }
                    if (ruleMapList != null && ruleMapList.size() > 0) {
                        strategyMap.put("strategyConfId", mktStrategyConf.getMktStrategyConfId());
                        strategyMap.put("strategyConfName", mktStrategyConf.getMktStrategyConfName());
                        strategyMap.put("ruleMapList", ruleMapList);
                        strategyMapList.add(strategyMap);
                    }
                }

                log.info("预校验还没出错2");
                List<String> mktCamCodeList = null;
                try {
                    mktCamCodeList = (List<String>) redisUtils.get("MKT_CAM_API_CODE_KEY");
                } catch (Exception e) {
                    log.info("(List<String>) redisUtils.get(\"MKT_CAM_API_CODE_KEY\") 出现异常 ！请检查~");
                    e.printStackTrace();
                }
                if (mktCamCodeList == null) {
                    List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("MKT_CAM_API_CODE");
                    mktCamCodeList = new ArrayList<String>();
                    for (SysParams sysParams : sysParamsList) {
                        mktCamCodeList.add(sysParams.getParamValue());
                    }
                    redisUtils.set("MKT_CAM_API_CODE_KEY", mktCamCodeList);
                }

                if (strategyMapList != null && strategyMapList.size() > 0) {
                    // 判断initId是否在清单列表里面
                    if (mktCamCodeList.contains(mktCampaign.getInitId().toString())) {
                   // if (mktCamCodeList.contains(mktCampaign.getMktCampaignId().toString())) {
                        mktCampaignCustMap.put("initId", mktCampaign.getInitId());
                        mktCampaignCustMap.put("mktCampaginId", mktCampaginId);
                        mktCampaignCustMap.put("levelConfig", act.get("levelConfig"));
                        mktCampaignCustMap.put("campaignSeq", act.get("campaignSeq"));
                        mktCampaignCustMap.put("strategyMapList", strategyMapList);
                    } else {
                        mktCampaignMap.put("mktCampaginId", mktCampaginId);
                        mktCampaignMap.put("levelConfig", act.get("levelConfig"));
                        mktCampaignMap.put("campaignSeq", act.get("campaignSeq"));
                        mktCampaignMap.put("strategyMapList", strategyMapList);
                        mktCampaignMap.put("type", mktCampaign.getMktCampaignType().toString()); // 活动类型 5000
                    }
                }
                // 实时
                resultMap.put("mktCampaignMap", mktCampaignMap);
                // 清单
                resultMap.put("mktCampaignCustMap", mktCampaignCustMap);

            } catch (Exception e) {
                log.info("预校验出错",e.getCause());
                log.info("预校验出错",e.toString());
                log.info("预校验出错",e.getMessage());
                e.printStackTrace();
            }
            return resultMap;
        }
    }

    /**
     * 添加内置的标签并赋值
     *
     * @param map
     */
    private void setInlayLabel(Map<String, String> map) {
        Calendar calendar = Calendar.getInstance();
        map.put("CPCP_IN_EVENT_YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
        map.put("CPCP_IN_EVENT_MONTH", String.valueOf(calendar.get(Calendar.MONTH) + 1));
        map.put("CPCP_IN_EVENT_DAY", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }


    // 处理资产级标签和销售品级标签
    private DefaultContext<String, Object> getAssetAndPromLabel(Map<String, String> mktAllLabel, Map<String, String> params, Map<String, String> privateParams, DefaultContext<String, Object> context, JSONObject esJson, Map<String, String> labelItems) {
        //资产级标签
        DefaultContext<String, Object> contextNew = new DefaultContext<String, Object>();
        if (mktAllLabel.get("assetLabels") != null && !"".equals(mktAllLabel.get("assetLabels"))) {
            JSONObject assParam = new JSONObject();
            assParam.put("queryNum", privateParams.get("accNbr"));
            assParam.put("c3", params.get("lanId"));
            assParam.put("queryId", privateParams.get("integrationId"));
            assParam.put("type", "1");
            assParam.put("queryFields", mktAllLabel.get("assetLabels"));
            assParam.put("centerType", "00");

            //因子查询-----------------------------------------------------
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(assParam));
//            Map<String, Object> dubboResult = new HashMap<>();
//            Map<String, Object> msgbody = new HashMap<>();
//            dubboResult.put("result_code", "0");
//            dubboResult.put("msgbody", msgbody);
            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                //ES log 标签实例
                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    contextNew.put(entry.getKey(), entry.getValue());
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

        contextNew.putAll(labelItems);   //添加事件采集项中作为标签使用的实例
        contextNew.putAll(context);      // 客户级标签
        contextNew.put("integrationId", privateParams.get("integrationId"));
        contextNew.put("accNbr", privateParams.get("accNbr"));
        return contextNew;
    }


    class getListMapLabelTask implements Callable<DefaultContext<String, Object>> {

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
            Map<String, String> privateParams = new ConcurrentHashMap<>();
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


    /**
     * 获取客户清单 Task
     */
    class getCustListTask implements Callable<Map<String, Object>> {

        private List<String> campaignList;
        private List<String> initIdList;
        private Long eventId;
        private String landId;
        private String custId;
        private Map<String, String> map;
        private List<Map<String, Object>> evtTriggers;

        public getCustListTask(List<String> campaignList, List<String> initIdList, Long eventId, String landId, String custId, Map<String, String> map, List<Map<String, Object>> evtTriggers) {
            this.campaignList = campaignList;
            this.initIdList = initIdList;
            this.eventId = eventId;
            this.landId = landId;
            this.custId = custId;
            this.map = map;
            this.evtTriggers = evtTriggers;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {

                List<MktCamEvtRel> resultByEvent = mktCamEvtRelMapper.qryBycontactEvtId(eventId);

                //判断有没有客户级活动
                Boolean hasCust = false;  //是否有客户级
                Boolean hasProm = false;  //是否有套餐级
                for (MktCamEvtRel mktCamEvtRel : resultByEvent) {
                    if (campaignList.contains(mktCamEvtRel.getMktCampaignId().toString()) && !hasCust && mktCamEvtRel.getLevelConfig() == 1) {
                        hasCust = true;
                    } else if(campaignList.contains(mktCamEvtRel.getMktCampaignId().toString()) && !hasProm && mktCamEvtRel.getLevelConfig() == 2){
                        hasProm = true;
                    }
                }
                List<String> custRuleIdList = new ArrayList<>();
                List<String> assetRuleIdList = new ArrayList<>();
                List<String> packageRuleIdList = new ArrayList<>();
                List<Map<String, Object>> mapList = mktCamEvtRelMapper.selectRuleIdsByEventId(eventId);
                for (Map<String, Object> ruleMap : mapList) {
                    if (campaignList.contains(ruleMap.get("campaignId").toString())) {
                        if ((Integer) ruleMap.get("levelConfig") == 1) {  // 客户级
                            custRuleIdList.add(ruleMap.get("ruleId").toString());
                        } else if((Integer) ruleMap.get("levelConfig") == 2){ // 套餐级
                            packageRuleIdList.add(ruleMap.get("ruleId").toString());
                        } else {
                            assetRuleIdList.add(ruleMap.get("ruleId").toString());  // 资产级
                        }
                    }
                }

                JSONObject param = new JSONObject();
                //查询标识
                param.put("c3", landId);
                param.put("queryId", custId);
                param.put("queryNum", "");
                param.put("queryFields", "");
                param.put("type", "4");
                param.put("centerType", "00");

                Map<String, Object> custParamMap = new HashMap<>();
                Map<String, Object> assetParamMap = new HashMap<>();
                Map<String, Object> promParamMap = new HashMap<>();
                JSONArray accArray = new JSONArray();
                List<String> custIdList = new ArrayList<>();
                List<String> assetIdList = new ArrayList<>();
                List<String> promIdList = new ArrayList<>();
                if (custId != null && !"".equals(custId) && hasCust) {
                    Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
                    if ("0".equals(dubboResult.get("result_code").toString())) {
                        accArray = new JSONArray((List<Object>) dubboResult.get("msgbody"));
                        for (Object o : accArray) {
                            custIdList.add(((Map) o).get("ASSET_INTEG_ID").toString());
                        }
                    }
                } else {
                    assetIdList.add(map.get("integrationId"));
                }
                if(hasProm){
                    List<Map<String, Object>> accNbrMapList = getAccNbrList(map.get("accNbr"));
                    for (Map<String, Object> accNbrMap : accNbrMapList) {
                        String assetIntegId = (String) accNbrMap.get("ASSET_INTEG_ID");
                        promIdList.add(assetIntegId);
                    }
                }
                custParamMap.put("assetList", custIdList);
                custParamMap.put("ruleList", custRuleIdList);

                assetParamMap.put("assetList", assetIdList);
                assetParamMap.put("ruleList", assetRuleIdList);

                promParamMap.put("assetList", promIdList);
                promParamMap.put("ruleList", packageRuleIdList);

                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("prom", promParamMap);
                paramMap.put("cust", custParamMap);
                paramMap.put("asset", assetParamMap);
                paramMap.put("campaignList", initIdList);

                // 获取本地网的中文名
                String landName = AreaNameEnum.getNameByLandId(Long.valueOf(landId));

                Map<String, Object> paramResultMap = esService.queryCustomer4Event(paramMap);

                List<Map<String, Object>> resultList = new ArrayList<>();
                // 解析
                if ("200".equals(paramResultMap.get("resultCode"))) {
                    List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) paramResultMap.get("data");
                    if (resultMapList != null && resultMapList.size() > 0) {
                        for (Map<String, Object> resultMap1 : resultMapList) {
                            Map<String, Object> result = new HashMap();
                            List<Map<String, Object>> taskChlList = (List<Map<String, Object>>) ((Map) resultMap1.get("CPC_VALUE")).get("taskChlList");
                            int count = 0;  // 统计符合渠道的个数
                            List<Map<String, Object>> taskChlListNew = new ArrayList<>();
                            for (Map<String, Object> taskChlMap : taskChlList) {
                                if (map.get("channelCode").equals(taskChlMap.get("channelId"))) {
                                    count++;
                                    taskChlListNew.add(taskChlMap);
                                }
                            }
                            if (count > 0) {
                                ((Map) resultMap1.get("CPC_VALUE")).put("taskChlList", taskChlListNew);
                            }

                            // 销售品过滤
                            String custProdFilter = (String) redisUtils.get("CUST_PROD_FILTER");
                            //String custProdFilter = null;
                            if (custProdFilter == null) {
                                List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("CUST_PROD_FILTER");
                                if (sysParamsList != null && sysParamsList.size() > 0) {
                                    custProdFilter = sysParamsList.get(0).getParamValue();
                                    redisUtils.set("CUST_PROD_FILTER", custProdFilter);
                                }
                            }

                            if (custProdFilter != null && "2".equals(custProdFilter)) {
                                List<Map<String, Object>> taskMapNewList = new ArrayList<>();

                                Map<String, Object> taskMap = (Map<String, Object>) resultMap1.get("CPC_VALUE");

                                List<Map<String, Object>> taskChlNewList = (List<Map<String, Object>>) ((Map) resultMap1.get("CPC_VALUE")).get("taskChlList");

                                String integrationId = (String) taskMap.get("integrationId");

                                // 判断该活动是否配置了销售品过滤
                                Integer mktCampaignId = (Integer) taskMap.get("activityId");

                                List<FilterRule> filterRuleList = null;//(List<FilterRule>) redisUtils.get("FILTER_RULE_" + mktCampaignId);
                                if (filterRuleList == null) {
                                    filterRuleList = filterRuleMapper.selectFilterRuleList(Long.valueOf(mktCampaignId));
                                    redisUtils.set("FILTER_RULE_" + mktCampaignId, filterRuleList);
                                }

                                boolean prodConfig = false;
                                boolean pordFilter = true; // true:未包含要过滤的销售品， false：包含要过滤的销售品
                                for (FilterRule filterRule : filterRuleList) {
                                    if ("3000".equals(filterRule.getFilterType()) || "3000" == filterRule.getFilterType()) {
                                        prodConfig = true;
                                    }
                                    if (prodConfig) {
                                        ResponseResult<AssetDto> assetDtoResponseResult = ctgCacheAssetService.queryCachedAssetDetailByIntegId(integrationId, landName);
                                        AssetDto assetDto = assetDtoResponseResult.getData();
                                        List<String> prodStrList = new ArrayList<>();
                                        if (assetDto != null) {
                                            List<AssetPromDto> assetPromDtoList = assetDto.getAssetPromDtoList();
                                            for (AssetPromDto assetPromDto : assetPromDtoList) {
                                                prodStrList.add(assetPromDto.getSelectablePromNum());
                                            }
                                        }
                                        // 获取过滤规则中的销售品集合
                                        List<String> codeList = ChannelUtil.StringToList(filterRule.getChooseProduct());
                                        //存在于校验
                                        if ("1000".equals(filterRule.getOperator())) {  //  1000 - 存在
                                            int sum = 0;
                                            for (String productCode : codeList) {
                                                // 包含销售品过滤
                                                if (prodStrList.contains(productCode)) {
                                                    sum++;
                                                    break;
                                                }
                                            }
                                            // 若有销售品存在跳过，若没有，直接过滤
                                            if (sum > 0) {
                                                continue;
                                            } else {
                                                pordFilter = false;
                                            }
                                        } else if ("2000".equals(filterRule.getOperator())) {      //  2000 - 不存在
                                            int cou = 0;
                                            for (String productCode : codeList) {
                                                // 不包含销售品过滤
                                                if (prodStrList.contains(productCode)) {
                                                    cou++;
                                                    break;
                                                }
                                            }
                                            // 若有销售品存在，这直接过滤
                                            if (cou > 0) {
                                                pordFilter = false;
                                                break;
                                            }
                                        }
                                    }
                                    prodConfig = false;
                                }
                                if (pordFilter) {
                                    taskMapNewList.addAll(taskChlNewList);

                                }
                                ((Map) resultMap1.get("CPC_VALUE")).put("taskChlList", taskMapNewList);
                            }

                            List<Map> taskChlCountList = (List<Map>) ((Map) resultMap1.get("CPC_VALUE")).get("taskChlList");
                            // 清单方案放入采集项
                            Map<String, Object> evtContent = (Map<String, Object>) JSON.parse(map.get("evtContent"));
                            for (Map<String, Object> taskChlCountMap : taskChlCountList) {
                                // taskChlCountMap.put("triggers", JSONArray.parse(JSONArray.toJSON(map.get("evtContent")).toString()));
                                List<Map<String, Object>> triggersList = new ArrayList<>();
                                if(evtContent!=null){
                                    for (Map.Entry entry : evtContent.entrySet()) {
                                        Map<String, Object> trigger = new HashMap<>();
                                        trigger.put("key", entry.getKey());
                                        trigger.put("value", entry.getValue());
                                        triggersList.add(trigger);
                                    }
                                    taskChlCountMap.put("triggers", triggersList);
                                }
                            }

                            if (taskChlCountList != null && taskChlCountList.size() > 0) {
                                result.putAll((Map) resultMap1.get("CPC_VALUE"));
                                result.put("orderISI", map.get("reqId"));
                                result.put("skipCheck", "0");
                                result.put("orderPriority", "0");
                                Long activityId = Long.valueOf(resultMap1.get("ACTIVITY_ID").toString());
                                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(activityId);
                                if (mktCampaignDO != null) {
                                    if ("1000".equals(mktCampaignDO.getMktCampaignType())) {
                                        result.put("activityType", "0"); //营销
                                    } else if ("5000".equals(mktCampaignDO.getMktCampaignType())) {
                                        result.put("activityType", "1"); //服务
                                    } else if ("6000".equals(mktCampaignDO.getMktCampaignType())) {
                                        result.put("activityType", "2"); //随销
                                    } else {
                                        result.put("activityType", "0"); //活动类型 默认营销
                                    }

                                    result.put("activityStartTime", simpleDateFormat.format(mktCampaignDO.getPlanBeginTime()));
                                    result.put("activityEndTime", simpleDateFormat.format(mktCampaignDO.getPlanEndTime()));
                                } else {
                                    result.put("activityType", "");
                                    result.put("activityStartTime", "");
                                    result.put("activityEndTime", "");
                                }
                                resultList.add(result);
                            }
                        }
                    }
                }
                resultMap.put("ruleList", resultList);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Exception = " + e);
            }
            return resultMap;
        }
    }


    // 计算剩余需要的流量
    private String getCpcpNeedFlow(String cpcpUsedFlow, String cpcpLeftFlow) {
        // 获取当天
        int currentDay = DateUtil.getCurrentDay();
        // 当前月的天数
        int lastDay = DateUtil.getLastDayForCurrentMonth();
        // 已用的流量数
        double cpcpUsedFlowDouble = Double.valueOf(cpcpUsedFlow.replace("GB", ""));
        // 剩余的流量数
        double cpcpLeftFlowDouble = Double.valueOf(cpcpLeftFlow.replace("GB", ""));
        // 这个月到currentDay过的日均用量
        double userAvg = cpcpUsedFlowDouble / currentDay;
        // 需要流量
        double needFlow = 0;
        if (userAvg * (lastDay - currentDay) > cpcpLeftFlowDouble) {
            needFlow = (userAvg * (lastDay - currentDay) - cpcpLeftFlowDouble) * 1024;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return String.valueOf(df.format(needFlow));
    }

    /**
     * 正则判断是否为手机号码
     *
     * @param phone
     * @return
     */
    private static boolean isMobile(String phone) {
        Pattern p = null;
        Matcher m = null;
        boolean isMatch = false;
        //制定验证条件
        String regex1 = "^[1][3,4,5,7,8][0-9]{9}$";
        String regex2 = "^((13[0-9])|(14[579])|(15([0-3,5-9]))|(16[6])|(17[0135678])|(18[0-9]|19[89]))\\d{8}$";

        p = Pattern.compile(regex2);
        m = p.matcher(phone);
        isMatch = m.matches();
        return isMatch;
    }


    private List<Map<String, Object>> getAccNbrList(String accNbr) {
        List<String> accNbrList = new ArrayList<>();
        List<Map<String, Object>> accNbrMapList = new ArrayList<>();
        // 根据accNum查询prodInstId
        //log.info("11111------prodInstIdsObject --->" + accNbr);
        CacheResultObject<Set<String>> prodInstIdsObject = iCacheProdIndexQryService.qryProdInstIndex2(accNbr);
        //log.info("22222------prodInstIdsObject --->" + JSON.toJSONString(prodInstIdsObject));
        if (prodInstIdsObject != null && prodInstIdsObject.getResultObject() != null) {
            Long mainOfferInstId = null;
            Set<String> prodInstIds = prodInstIdsObject.getResultObject();
            for (String prodInstId : prodInstIds) {
                // 查询产品实例实体缓存 取主产品（1000）的一个
                CacheResultObject<ProdInst> prodInstCacheEntity = iCacheProdEntityQryService.getProdInstCacheEntity(prodInstId);
                //        log.info("333333------prodInstCacheEntity --->" + JSON.toJSONString(prodInstCacheEntity));
                if (prodInstCacheEntity != null && prodInstCacheEntity.getResultObject() != null && "1000".equals(prodInstCacheEntity.getResultObject().getProdUseType())) {
                    mainOfferInstId = prodInstCacheEntity.getResultObject().getMainOfferInstId();
                    break;
                }
            }

            // 根据offerInstId和statusCd查询offerProdInstRelId
            if (mainOfferInstId!=null){
                CacheResultObject<Set<String>> setCacheResultObject = iCacheOfferRelIndexQryService.qryOfferProdInstRelIndex1(mainOfferInstId.toString(), "1000");
                //            log.info("444444------setCacheResultObject --->" + JSON.toJSONString(setCacheResultObject));
                if (setCacheResultObject != null && setCacheResultObject.getResultObject() != null && setCacheResultObject.getResultObject().size() > 0) {
                    Set<String> offerProdInstRelIds = setCacheResultObject.getResultObject();
                    for (String offerProdInstRelId : offerProdInstRelIds) {
                        CacheResultObject<OfferProdInstRel> offerProdInstRelCacheEntity = iCacheRelEntityQryService.getOfferProdInstRelCacheEntity(offerProdInstRelId);
                        //                    log.info("55555------offerProdInstRelCacheEntity --->" + JSON.toJSONString(offerProdInstRelCacheEntity));
                        if (offerProdInstRelCacheEntity != null && offerProdInstRelCacheEntity.getResultObject() != null) {
                            Long prodInstIdNew = offerProdInstRelCacheEntity.getResultObject().getProdInstId();
                            CacheResultObject<ProdInst> prodInstCacheEntityNew = iCacheProdEntityQryService.getProdInstCacheEntity(prodInstIdNew.toString());
                            //                        log.info("6666666------prodInstCacheEntityNew --->" + JSON.toJSONString(prodInstCacheEntityNew));
                            if (prodInstCacheEntityNew != null && prodInstCacheEntityNew.getResultObject() != null) {
                                //                            log.info("777777------AccNum --->" + prodInstCacheEntityNew.getResultObject().getAccNum());
                                final CacheResultObject<RowIdMapping> prodInstIdMappingCacheEntity = iCacheIdMappingEntityQryService.getProdInstIdMappingCacheEntity(prodInstIdNew.toString());
                                //                               log.info("888888------prodInstIdMappingCacheEntity --->" + JSON.toJSONString(prodInstIdMappingCacheEntity));
                                if (prodInstIdMappingCacheEntity != null && prodInstIdMappingCacheEntity.getResultObject() != null) {
                                    Map<String, Object> accNbrMap = new HashMap<>();
                                    accNbrMap.put("ACC_NBR", prodInstCacheEntityNew.getResultObject().getAccNum());
                                    accNbrMap.put("ASSET_INTEG_ID", prodInstIdMappingCacheEntity.getResultObject().getCrmRowId());
                                    //                                    log.info("999999------accNbrMap --->" + JSON.toJSONString(accNbrMap));
                                    accNbrMapList.add(accNbrMap);
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("10101010------accNbrMapList --->" + JSON.toJSONString(accNbrMapList));
        return accNbrMapList;
    }



    /**
     * 计费短信合并功能 CPCP_JIFEI_CONTENT
     * @param labelItems
     */
    private void cpcpJifeiContent(Map<String, String> labelItems, JSONObject evtParams){
        StringBuilder content = new StringBuilder();
        String usedFlow = "";
        String totalFlow = "";

        usedFlow = (String) redisUtils.get(USED_FLOW);
        if (usedFlow == null || "".equals(usedFlow)) {
            List<SysParams> usedFlowList = sysParamsMapper.listParamsByKeyForCampaign(USED_FLOW);
            if (usedFlowList != null && usedFlowList.size() > 0) {
                SysParams usedFlowParams = usedFlowList.get(0);
                if (usedFlowParams != null) {
                    usedFlow = usedFlowParams.getParamValue();
                    redisUtils.set(USED_FLOW, usedFlow);
                }
            }
        }

        totalFlow = (String) redisUtils.get(TOTAL_FLOW);
        if (totalFlow == null || "".equals(totalFlow)) {
            List<SysParams> totalFlowList = sysParamsMapper.listParamsByKeyForCampaign(TOTAL_FLOW);
            if (totalFlowList != null && totalFlowList.size() > 0) {
                SysParams totalFlowParams = totalFlowList.get(0);
                if (totalFlowParams != null) {
                    totalFlow = totalFlowParams.getParamValue();
                    redisUtils.set(TOTAL_FLOW, totalFlow);
                }
            }
        }

        if (evtParams.get("CPCP_JIFEI_CONTENT") != null) {
            List<Map<String, String>> contentMapList = (List<Map<String, String>>) evtParams.get("CPCP_JIFEI_CONTENT");
            for (int i = 0; i < contentMapList.size(); i++) {
                if (i > 0) {
                    content.append("，");
                }
                content.append(contentMapList.get(i).get("message_name").toString());
                content.append(usedFlow + contentMapList.get(i).get(USED_FLOW) + "，");
                content.append(totalFlow + contentMapList.get(i).get(TOTAL_FLOW));
            }
        }
        labelItems.put("CPCP_JIFEI_MESSAGE", content.toString());
    }

}