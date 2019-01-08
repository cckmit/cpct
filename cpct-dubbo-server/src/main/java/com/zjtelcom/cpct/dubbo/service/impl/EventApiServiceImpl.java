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
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsService;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.MONTH;

@Service
public class EventApiServiceImpl implements EventApiService {

    private static final int THREAD_COUNT_ACTIVE = 50;
    private static final int THREAD_COUNT_STRATEGY = 20;
    private static final int THREAD_COUNT_RULE = 20;

    private static final Logger log = LoggerFactory.getLogger(EventApiServiceImpl.class);

    @Autowired
    private ContactEvtMapper contactEvtMapper; //事件总表

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //活动策略关联

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired
    private FilterRuleMapper filterRuleMapper; //过滤规则

    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;//过滤规则与策略关系

    @Autowired
    private MktCamItemMapper mktCamItemMapper; //销售品

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
    private EsService esService;  //es存储

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
    private TarGrpMapper tarGrpMapper;

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


    @Override
    public Map<String, Object> CalculateCPC(Map<String, Object> map) {

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
        esJson.put("msg", "事件接入，开始异步流程");
        esService.save(esJson, IndexList.EVENT_MODULE, reqId);

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
//            System.out.println("开始调用协同中心异步回调");
            //调用协同中心回调接口
            Map<String, Object> back = iContactTaskReceiptService.contactTaskReceipt(result);
            if (back != null) {
                if ("1".equals(back.get("resultCode"))) {
//                    System.out.println("协同中心接口回调调用成功");
                    return null;
                }
            }
//            System.out.println("协同中心接口回调调用失败" + back.get("resultMsg"));

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
        esJson.put("msg", "事件接入，开始异步流程");
        esService.save(esJson, IndexList.EVENT_MODULE, reqId);

        //调用计算方法
        result = new EventTask().cpc(params);

        return result;

    }

    /**
     * 事件验证模块公共方法
     */
    class EventTask {

        public Map<String, Object> cpc(Map<String, String> map) {
            //开始时间
            long begin = System.currentTimeMillis();

            //初始化返回结果
            Map<String, Object> result = new HashMap();

            //构造返回结果
            String custId = map.get("custId");
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);

            //初始化es log
            JSONObject esJson = new JSONObject();
            esJson.put("reqId", map.get("reqId"));

            JSONObject paramsJson = new JSONObject();
            paramsJson.put("reqId", map.get("reqId"));
            try {
                paramsJson.put("intoParams", map);  //保存入参
            } catch (Exception e) {
                e.printStackTrace();
            }

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

                e.printStackTrace();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                esJson.put("evtCollectTime", simpleDateFormat.format(new Date()));
            }

            try {
                //事件验证开始↓↓↓↓↓↓↓↓↓↓↓↓↓
                //解析事件采集项
                JSONObject evtParams = JSONObject.parseObject(map.get("evtContent"));
                //根据事件code查询事件信息
                ContactEvt event = (ContactEvt) redisUtils.get("EVENT_" + map.get("eventCode"));
                if (event == null) {
                    event = contactEvtMapper.getEventByEventNbr(map.get("eventCode"));
                    redisUtils.set("EVENT_" + map.get("eventCode"), event);
                }
                if (event == null) {
                    esJson.put("hit", false);
                    esJson.put("msg", "未找到相关事件");
                    esService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    result.put("CPCResultMsg", "未找到相关事件");
                    return result;
                }
                //获取事件id
                Long eventId = event.getContactEvtId();

                esJson.put("eventId", eventId);

                //验证事件状态
                if (!"1000".equals(event.getStatusCd())) {
                    esJson.put("hit", false);
                    esJson.put("msg", "事件已关闭");
                    esService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
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
                    esJson.put("msg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
                    esService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
                    return result;
                }

                //!!!验证事件规则命中
                Map<String, Object> stringObjectMap = matchRulCondition(eventId, labelItems, map);
                if (!stringObjectMap.get("code").equals("success")) {
                    //判断不符合条件 直接返回不命中
                    result.put("CPCResultMsg", stringObjectMap.get("result"));
                    result.put("CPCResultCode", "1000");
                    esJson.put("hit", false);
                    esJson.put("msg", stringObjectMap.get("result"));
                    esService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));
                    return result;
                }

                //获取事件推荐活动数
                int recCampaignAmount;
                String recCampaignAmountStr = event.getRecCampaignAmount();
                if (recCampaignAmountStr == null || "".equals(recCampaignAmountStr)) {
                    recCampaignAmount = 0;
                } else {
                    recCampaignAmount = Integer.parseInt(recCampaignAmountStr);
                }

                //根据事件id 查询所有关联活动（根据优先级排序 正序）
                //获取事件下绑定的所有活动
                List<MktCamEvtRelDO> mktCamEvtRelDOs = mktCamEvtRelMapper.listActByEventId(eventId);
                //初始化返回结果中的工单信息
                List<Map<String, Object>> activityList = new ArrayList<>();

                //遍历活动id  查询并匹配活动规则 需要根据事件推荐活动数 取前n个活动
                int max = mktCamEvtRelDOs.size();
                //初始化结果集
                List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                //初始化线程池
//                ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT_ACTIVE);
                ExecutorService executorService = Executors.newCachedThreadPool();

                //遍历活动
                for (int j = 0; j < max; j++) {
                    //活动id
                    MktCamEvtRelDO camEvtRelDO = mktCamEvtRelDOs.get(j);
                    //提交线程
                    if (camEvtRelDO.getLevelConfig() == 1) { //判断是客户级还是资产级
                        //客户级
                        if (custId == null || "".equals(custId)) {

                            //保存es log
                            long cost = System.currentTimeMillis() - begin;
                            esJson.put("timeCost", cost);
                            esJson.put("hit", false);
                            esJson.put("activityId", camEvtRelDO.getMktCampaignId());
                            esJson.put("msg", "客户级活动，事件采集项未包含客户编码");
                            esService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

                            //事件采集项没有客户编码
                            result.put("CPCResultCode", "1000");
                            result.put("CPCResultMsg", "采集项未包含客户编码");
                            return result;
                        }

                        //根据客户编码查询所有资产
                        //构造查询参数值
                        JSONObject param = new JSONObject();
                        //查询标识
                        param.put("c3", map.get("lanId"));
                        param.put("queryId", map.get("custId"));
                        param.put("queryNum", "");
                        param.put("queryFields", "");
                        param.put("type", "4");
//                        System.out.println("客户级查询param " + param.toString());
                        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
//                        System.out.println(dubboResult.toString());

                        JSONArray accArray = new JSONArray();
                        if ("0".equals(dubboResult.get("result_code").toString())) {
                            accArray = new JSONArray((List<Object>) dubboResult.get("msgbody"));

                        } else {
                            esJson.put("hit", "false");
                            esJson.put("msg", "客户级资产查询出错");
                            esService.save(esJson, IndexList.ACTIVITY_MODULE);
//                            System.out.println("客户级资产查询出错");
                            continue;
                        }
                        //获取客户下所有资产
                        for (Object o : accArray) {
                            //客户级下，循环资产级
                            Map<String, String> privateParams = new HashMap<>();
                            privateParams.put("isCust", "0");
                            privateParams.put("accNbr", ((Map) o).get("ACC_NBR").toString());
                            privateParams.put("integrationId", ((Map) o).get("ASSET_INTEG_ID").toString());
                            privateParams.put("custId", map.get("custId"));
                            //活动优先级为空的时候默认0
                            privateParams.put("orderPriority", camEvtRelDO.getCampaignSeq() == null ? "0" : camEvtRelDO.getCampaignSeq().toString());
                            Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, camEvtRelDO.getMktCampaignId(), privateParams, labelItems, evtTriggers));
                            //将线程处理结果添加到结果集
                            threadList.add(f);
                        }

                    } else {
                        //资产级
                        Map<String, String> privateParams = new HashMap<>();
                        privateParams.put("isCust", "1"); //是否是客户级
                        privateParams.put("accNbr", map.get("accNbr"));
                        privateParams.put("integrationId", map.get("integrationId"));
                        privateParams.put("custId", map.get("custId"));
                        if (camEvtRelDO.getCampaignSeq() == null) {
                            camEvtRelDO.setCampaignSeq(0);
                        }
                        privateParams.put("orderPriority", camEvtRelDO.getCampaignSeq().toString());
                        //资产级
                        Future<Map<String, Object>> f = executorService.submit(new ActivityTask(map, camEvtRelDO.getMktCampaignId(), privateParams, labelItems, evtTriggers));
                        //将线程处理结果添加到结果集
                        threadList.add(f);
                    }
                }
                //获取结果
                try {
                    for (Future<Map<String, Object>> future : threadList) {
                        if (!future.get().isEmpty()) {
                            activityList.addAll((List<Map<String, Object>>) future.get().get("strategyList"));
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

                //判断事件推荐活动数，按照优先级排序
                if (activityList.size() > 0) {
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

                    if (recCampaignAmount > 0 && recCampaignAmount < activityList.size()) {
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
                }

                //返回结果
                result.put("taskList", activityList); //协同回调结果
//                System.out.println("命中结果：" + result.toString());

                if (activityList.size() > 0) {
                    //构造返回参数
                    result.put("CPCResultCode", "1");
                    result.put("CPCResultMsg", "success");
                } else {
                    result.put("CPCResultCode", "1000");
                    result.put("CPCResultMsg", "success");
                }
                result.put("reqId", map.get("reqId"));
                result.put("custId", custId);

                paramsJson.put("backParams", result);
                esService.save(paramsJson, IndexList.PARAMS_MODULE);

                //es log
                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("hit", true);
                esService.save(esJson, IndexList.EVENT_MODULE, map.get("reqId"));

            } catch (Exception e) {

                e.printStackTrace();
                paramsJson.put("errorMsg", e.getMessage());
                esService.save(paramsJson, IndexList.PARAMS_MODULE);

                //构造返回参数
                result.put("CPCResultCode", "1000");
                result.put("CPCResultMsg", "策略中心计算异常");
                result.put("reqId", map.get("reqId"));
                result.put("custId", custId);
                return result;
            }

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

        ActivityTask(Map<String, String> params, Long activityId, Map<String, String> privateParams,
                     Map<String, String> labelItems, List<Map<String, Object>> evtTriggers) {
            this.activityId = activityId;
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
            this.evtTriggers = evtTriggers;
            this.reqId = params.get("reqId");
        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> activity = new HashMap<>();

            //初始化es log
            JSONObject esJson = new JSONObject();

            Date now = new Date();

            List<Map<String, Object>> strategyList = new ArrayList<>();


            //es log
            esJson.put("reqId", reqId);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("hitEntity", privateParams.get("accNbr")); //命中对象

            MktCampaignDO mktCampaign = null;
            try {
                //查询活动基本信息
//            mktCampaign = (MktCampaignDO) redisUtils.get("MKT_" + activityId);
//            if (mktCampaign == null) {
//            mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);
//                redisUtils.set("MKT_" + activityId, mktCampaign);
//            }

                mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);
            } catch (Exception e) {
                e.printStackTrace();
                esJson.put("hit", false);
                esJson.put("msg", "活动信息查询异常");
                esService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }

            if (mktCampaign == null) {
                //当前时间不在活动生效时间内
                esJson.put("hit", false);
                esJson.put("msg", "活动信息查询失败，活动为null");
                esService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }

            privateParams.put("activityId", mktCampaign.getMktCampaignId().toString()); //活动编码
            privateParams.put("activityName", mktCampaign.getMktCampaignName()); //活动名称
            if ("1000".equals(mktCampaign.getMktCampaignType())) {
                privateParams.put("activityType", "0"); //营销
            } else if ("5000".equals(mktCampaign.getMktCampaignType())) {
                privateParams.put("activityType", "1"); //服务
            } else if ("6000".equals(mktCampaign.getMktCampaignType())) {
                privateParams.put("activityType", "2"); //随销
            } else {
                privateParams.put("activityType", "0"); //活动类型 默认营销
            }
            if ("0".equals(mktCampaign.getIsCheckRule()) || "校验".equals(mktCampaign.getIsCheckRule())) {
                privateParams.put("skipCheck", "0"); //是否预校验
            } else {
                privateParams.put("skipCheck", "1"); //是否预校验
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            privateParams.put("activityStartTime", simpleDateFormat.format(mktCampaign.getPlanBeginTime())); //活动开始时间
            privateParams.put("activityEndTime", simpleDateFormat.format(mktCampaign.getPlanEndTime())); //活动结束时间

            //es log
            esJson.put("activityId", mktCampaign.getMktCampaignId().toString());
            esJson.put("activityName", mktCampaign.getMktCampaignName());
            esJson.put("activityCode", mktCampaign.getMktCampaignId().toString());

            //判断活动状态
//            if (!"2002".equals(mktCampaign.getStatusCd())) {
//                esJson.put("hit", "false");
//                esJson.put("msg", "活动状态未发布");
//                esService.save(esJson, IndexList.ACTIVITY_MODULE);
////                System.out.println("活动状态未发布");
//                return Collections.EMPTY_MAP;
//            }

            //验证活动生效时间
            Date beginTime = mktCampaign.getPlanBeginTime();
            Date endTime = mktCampaign.getPlanEndTime();
            if (now.before(beginTime) || now.after(endTime)) {
                //当前时间不在活动生效时间内
                esJson.put("hit", false);
                esJson.put("msg", "当前时间不在活动生效时间内");
                esService.save(esJson, IndexList.ACTIVITY_MODULE);
                return Collections.EMPTY_MAP;
            }

            //iSale展示列参数对象初始化
            List<Map<String, Object>> itgTriggers = new ArrayList<>();

            //验证过滤规则 活动级
            List<Long> filterRuleIds = mktStrategyFilterRuleRelMapper.selectByStrategyId(activityId);
            if (filterRuleIds != null && filterRuleIds.size() > 0) {
                //循环并判断过滤规则
                for (Long filterRuleId : filterRuleIds) {
                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(filterRuleId);
                    //判断过滤类型(红名单，黑名单)
                    if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                        //获取名单
                        String userList = filterRule.getUserList();
                        if (userList != null) {
                            int index = userList.indexOf(privateParams.get("accNbr"));
                            if (index >= 0) {
                                esJson.put("hit", "false");
                                esJson.put("msg", "红黑名单过滤规则验证被拦截");
                                esService.save(esJson, IndexList.ACTIVITY_MODULE);
                                return Collections.EMPTY_MAP;
                            }
                        }

                    } else if ("3000".equals(filterRule.getFilterType())) {  //销售品过滤
                        boolean productCheck = true;
                        //获取需要过滤的销售品
                        String checkProduct = filterRule.getChooseProduct();
                        if (checkProduct != null) {
                            String productStr = null;
                            //获取用户已办理销售品
                            JSONObject body = getLabelsByDubbo(privateParams.get("integrationId"), privateParams.get("accNbr"), params.get("lanId"), "1", "PROM_LIST");
                            if (body != null && body.containsKey("PROM_LIST")) {
                                productStr = body.getString("PROM_LIST");
                            }

//                            productCheck = checkFilerProm(checkProduct,productStr);

                            String[] checkProductArr = checkProduct.split(",");
                            String esMsg = "";
                            if (productStr != null && !"".equals(productStr)) {

                                if ("1000".equals(filterRule.getOperator())) {  //存在于
                                    for (String product : checkProductArr) {
                                        int index = productStr.indexOf(product);
                                        if (index >= 0) {
                                            productCheck = false;
                                            break;
                                        }
                                    }
                                } else if ("2000".equals(filterRule.getOperator())) { //不存在于

                                    boolean noExistCheck = true;
                                    for (String product : checkProductArr) {
                                        int index = productStr.indexOf(product);
                                        if (index >= 0) {
                                            productCheck = true;
                                            noExistCheck = false;
                                            //被过滤的销售品
                                            esMsg = product;
                                            break;
                                        }
                                    }
                                    if (noExistCheck) {
                                        productCheck = false;
                                    }
                                }
                            } else {
                                //存在于校验
                                if ("2000".equals(filterRule.getOperator())) {
                                    productCheck = false;
                                } else if ("1000".equals(filterRule.getOperator())) {
                                    productCheck = true;
                                }
                            }
                            if (productCheck) {
                                esJson.put("hit", "false");
                                esJson.put("msg", "销售品过滤验证未通过:" + esMsg);
                                esService.save(esJson, IndexList.ACTIVITY_MODULE);
                                return Collections.EMPTY_MAP;
                            }
                        }
                    } else if ("4000".equals(filterRule.getFilterType())) {  //表达式过滤
                        //暂不处理
                        //do something
                    }
//                    else if ("5000".equals(filterRule.getFilterType())) {  //时间段过滤
//                        //时间段的格式
//                        if (compareHourAndMinute(filterRule)) {
//                            esJson.put("hit", "false");
//                            esJson.put("msg", "过滤时间段验证被拦截");
//                            esService.save(esJson, IndexList.ACTIVITY_MODULE);
//                            return Collections.EMPTY_MAP;
//                        }
//                    }
                    else if ("6000".equals(filterRule.getFilterType())) {  //过扰规则
                        //将过扰规则的标签放到iSale展示列
                        StringBuilder queryLabel = new StringBuilder();
                        //获取过扰标签   todo 缓存
                        List<String> labels = mktVerbalConditionMapper.getLabelListByConditionId(filterRule.getConditionId());
                        if (labels != null && labels.size() > 0) {
                            for (String labelCode : labels) {
                                queryLabel.append(labelCode).append(",");
                            }
                        }
                        if (queryLabel.length() > 0) {
                            queryLabel.deleteCharAt(queryLabel.length() - 1);
                        }

                        //查询标签
                        JSONObject labelParam = new JSONObject();
                        labelParam.put("queryNum", privateParams.get("accNbr"));
                        labelParam.put("c3", params.get("lanId"));
                        labelParam.put("queryId", privateParams.get("integrationId"));
                        labelParam.put("type", "1");
                        labelParam.put("queryFields", queryLabel.toString());
                        Map<String, Object> queryResult = getLabelValue(labelParam);

                        JSONObject body = new JSONObject((HashMap) queryResult.get("msgbody"));
                        //获取查询结果
                        List<Map<String, Object>> triggerList = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : body.entrySet()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("key", entry.getKey());
                            map.put("value", entry.getValue().toString());
                            map.put("display", "0");
                            map.put("name", "");
                            triggerList.add(map);
                        }
                        Map<String, Object> disturb = new HashMap<>();
                        disturb.put("type", "disturb");
                        disturb.put("triggerList", triggerList);
                        itgTriggers.add(disturb);
                    }
                }
            }


            //根据活动id获取策略列表  todo 缓存
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            //遍历策略列表
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {
                //提交线程
                Future<Map<String, Object>> f = executorService.submit(
                        new StrategyTask(params, mktCamStrategyConfRelDO.getStrategyConfId(), privateParams, labelItems));
                //将线程处理结果添加到结果集
                threadList.add(f);
            }
            //获取结果
            try {
                for (Future<Map<String, Object>> future : threadList) {
                    if (!future.get().isEmpty()) {
                        strategyList.addAll((List<Map<String, Object>>) future.get().get("ruleList"));
                    }
                }
                activity.put("strategyList", strategyList);

                //判断是否有策略命中
                if (strategyList.size() > 0) {
                    esJson.put("hit", true);

                    Map<String, Object> itgTrigger;
                    StringBuilder queryFieldsCust = new StringBuilder();
                    StringBuilder queryFieldsAss = new StringBuilder();
                    StringBuilder queryFieldsSale = new StringBuilder();

                    //查询展示列 （iSale）
                    List<Map<String, String>> iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getIsaleDisplay());
                    if (iSaleDisplay != null && iSaleDisplay.size() > 0) {
                        for (Map<String, String> labelMap : iSaleDisplay) {
                            switch (labelMap.get("labelCode")) {
                                case "1000":
                                    queryFieldsCust.append(labelMap.get("labelCode")).append(",");
                                    break;
                                case "2000":
                                    queryFieldsAss.append(labelMap.get("labelCode")).append(",");
                                    break;
                                case "3000":
                                    queryFieldsSale.append(labelMap.get("labelCode")).append(",");
                                    break;
                            }
                        }

                        JSONObject resJsonAll = new JSONObject();

                        //如果有销售品级标签，就加上销售品主id的查询
                        if (queryFieldsSale.length() > 0) {
                            queryFieldsAss.append("PROM_INTEG_ID");
                        }

                        if (queryFieldsAss.length() > 0) {
                            queryFieldsAss.deleteCharAt(queryFieldsAss.length() - 1);

                            JSONObject httpParams = new JSONObject();
                            httpParams.put("queryNum", privateParams.get("accNbr"));
                            httpParams.put("c3", params.get("lanId"));
                            httpParams.put("queryId", privateParams.get("integrationId"));
                            httpParams.put("type", "1");
                            //待查询的标签列
                            httpParams.put("queryFields", queryFieldsAss.toString());
                            //dubbo接口查询标签
                            JSONObject resJson = getLabelByDubbo(httpParams);
//                    System.out.println(resJson.toString());
                            resJsonAll.putAll(resJson);
                        }
                        if (queryFieldsCust.length() > 0) {
                            queryFieldsCust.deleteCharAt(queryFieldsCust.length() - 1);

                            JSONObject httpParams = new JSONObject();
                            httpParams.put("queryNum", "");
                            httpParams.put("c3", params.get("lanId"));
                            httpParams.put("queryId", privateParams.get("custId"));
                            httpParams.put("type", "2");
                            //待查询的标签列
                            httpParams.put("queryFields", queryFieldsCust.toString());
                            //dubbo接口查询标签
                            JSONObject resJson = getLabelByDubbo(httpParams);
//                    System.out.println(resJson.toString());
                            resJsonAll.putAll(resJson);
                        }
                        if (queryFieldsSale.length() > 0) {
                            if ((resJsonAll.getString("PROM_INTEG_ID") != null) && !"".equals(resJsonAll.getString("PROM_INTEG_ID"))) {
                                queryFieldsSale.deleteCharAt(queryFieldsSale.length() - 1);

                                JSONObject httpParams = new JSONObject();
                                httpParams.put("queryNum", privateParams.get("accNbr"));
                                httpParams.put("c3", params.get("lanId"));
                                httpParams.put("queryId", resJsonAll.getString("PROM_INTEG_ID"));
                                httpParams.put("type", "3");
                                //待查询的标签列
                                httpParams.put("queryFields", queryFieldsSale.toString());
                                //dubbo接口查询标签
                                JSONObject resJson = getLabelByDubbo(httpParams);
//                        System.out.println(resJson.toString());
                                resJsonAll.putAll(resJson);
                            }
                        }

                        Map<String, Object> triggers;
                        List<Map<String, Object>> triggerList1 = new ArrayList<>();
                        List<Map<String, Object>> triggerList2 = new ArrayList<>();
                        List<Map<String, Object>> triggerList3 = new ArrayList<>();
                        List<Map<String, Object>> triggerList4 = new ArrayList<>();

                        for (Map<String, String> label : iSaleDisplay) {
                            if (resJsonAll.containsKey((String) label.get("labelCode"))) {
                                triggers = new JSONObject();
                                triggers.put("key", label.get("labelCode"));
                                triggers.put("value", resJsonAll.get((String) label.get("labelCode")));
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

                    //将iSale展示列的值放入返回结果
                    for (Map<String, Object> strategyMap : strategyList) {
                        List<Map<String, Object>> ChlMap = (List<Map<String, Object>>) strategyMap.get("taskChlList");
                        for (Map<String, Object> map : ChlMap) {
                            map.put("itgTriggers", itgTriggers);
                            map.put("triggers", JSONArray.parse(JSONArray.toJSON(evtTriggers).toString()));

//                            List<Map<String, Object>> itgMap = (List<Map<String, Object>>) map.get("itgTriggers");
//                            itgMap.addAll((List<Map<String, Object>>) JSONArray.parse(JSONArray.toJSON(itgTriggers).toString()));
                        }
                    }

                } else {
                    esJson.put("hit", false);
                    esJson.put("msg", "策略均未命中");
                    esService.save(esJson, IndexList.ACTIVITY_MODULE);
                }
                esService.save(esJson, IndexList.ACTIVITY_MODULE);

            } catch (Exception e) {
                e.printStackTrace();

                esJson.put("hit", false);
                esJson.put("msg", "获取计算结果异常");
                esService.save(esJson, IndexList.ACTIVITY_MODULE);
                //发生异常关闭线程池
                executorService.shutdown();
            } finally {
                //关闭线程池
                executorService.shutdown();
            }

            return activity;
        }
    }

    /**
     * 策略级
     */
    class StrategyTask implements Callable<Map<String, Object>> {
        private Long strategyConfId; //策略配置id
        private String reqId;
        private Map<String, String> params; //公共参数
        private Map<String, String> privateParams;  //私有参数
        private Map<String, String> labelItems;  //事件采集项标签

        public StrategyTask(Map<String, String> params, Long strategyConfId, Map<String, String> privateParams, Map<String, String> labelItems) {
            this.strategyConfId = strategyConfId;
            this.reqId = params.get("reqId");
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;

        }

        @Override
        public Map<String, Object> call() {
            //获取当前时间
            Date now = new Date();
            Map<String, Object> strategyMap = new HashMap<>();

            //初始化es log
            JSONObject esJson = new JSONObject();

            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> ruleList = new ArrayList<>();
            //查询策略基本信息
            MktStrategyConfDO mktStrategyConf = (MktStrategyConfDO) redisUtils.get("STRATEGY_" + strategyConfId);
            if (mktStrategyConf == null) {
                mktStrategyConf = mktStrategyConfMapper.selectByPrimaryKey(strategyConfId);
                redisUtils.set("STRATEGY_" + strategyConfId, mktStrategyConf);
            }

            // 获取策略名称
            String strategyConfName = mktStrategyConf.getMktStrategyConfName();
            strategyMap.put("mktStrategyConfName", strategyConfName);

            //es log
            esJson.put("activityId", privateParams.get("activityId"));
            esJson.put("strategyConfId", strategyConfId);
            esJson.put("strategyConfName", strategyConfName);
            esJson.put("eventId", params.get("eventCode"));
            esJson.put("reqId", reqId);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("hitEntity", privateParams.get("accNbr")); //命中对象

            //验证策略生效时间
            if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                //若当前时间在策略生效时间外
                esJson.put("hit", "false");
                esJson.put("msg", "当前时间不在策略生效时间内");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }
            //适用地市校验
            if (mktStrategyConf.getAreaId() != null && !"".equals(mktStrategyConf.getAreaId())) {
                String[] strArrayCity = mktStrategyConf.getAreaId().split("/");
                boolean areaCheck = true;
                for (String str : strArrayCity) {
                    String lanId = params.get("lanId");
                    if (lanId != null) {
                        if (lanId.equals(str)) {
                            areaCheck = false;
                            break;
                        }
                    } else {
                        //适用地市获取异常 lanId
                        strategyMap.put("msg", "适用地市获取异常");

                        esJson.put("hit", "false");
                        esJson.put("msg", "适用地市获取异常");
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        return Collections.EMPTY_MAP;
                    }
                }

                if (areaCheck) {
                    strategyMap.put("msg", "适用地市不符");

                    esJson.put("hit", "false");
                    esJson.put("msg", "适用地市不符");
                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }
            } else {
                //适用地市数据异常
                strategyMap.put("msg", "适用地市数据异常");

                esJson.put("hit", "false");
                esJson.put("msg", "适用地市数据异常");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }
            //判断适用渠道
            if (mktStrategyConf.getChannelsId() != null && !"".equals(mktStrategyConf.getChannelsId())) {
                String[] strArrayChannelsId = mktStrategyConf.getChannelsId().split("/");
                boolean channelCheck = true;
                for (String str : strArrayChannelsId) {

                    Channel channel = contactChannelMapper.selectByPrimaryKey(Long.parseLong(str));
                    String channelId = params.get("channelCode");
                    if (channelId != null && channel != null) {
                        if (channelId.equals(channel.getContactChlCode())) {
                            channelCheck = false;
                            break;
                        }
                    } else {
                        //适用地市获取异常 lanId
                        strategyMap.put("msg", "适用渠道获取异常");
                        esJson.put("hit", "false");
                        esJson.put("msg", "适用渠道获取异常");
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        return Collections.EMPTY_MAP;
                    }
                }

                if (channelCheck) {
                    strategyMap.put("msg", "适用渠道不符");
                    esJson.put("hit", "false");
                    esJson.put("msg", "适用渠道不符");
                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }
            } else {
                //适用地市数据异常
                strategyMap.put("msg", "适用渠道数据异常");
                esJson.put("hit", "false");
                esJson.put("msg", "适用渠道数据异常");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }


            //获取策略下所有标签
            List<Map<String, String>> allLabel = (List<Map<String, String>>) redisUtils.get("STRATEGY_ALL_LABEL_" + strategyConfId);
            if (allLabel == null) {
                allLabel = mktStrategyConfRuleMapper.selectAllLabelByStrategyConfId(strategyConfId);
                redisUtils.set("STRATEGY_ALL_LABEL_" + strategyConfId, allLabel);
            }

            boolean isSale = false;
            StringBuilder queryFieldsAss = new StringBuilder();
            StringBuilder queryFieldsCust = new StringBuilder();
            StringBuilder queryFieldsSale = new StringBuilder();

            //记录参数个数
            String saleId = "";

            for (Map<String, String> labelMap : allLabel) {
                if (labelItems.containsKey(labelMap.get("code"))) {
                    continue;
                }
                // todo 标签类型常量
                //判断是客户级标签还是资产及标签
                if ("1000".equals(labelMap.get("type"))) {  //客户级
                    queryFieldsCust.append(labelMap.get("code")).append(",");
                } else if ("2000".equals(labelMap.get("type"))) { //用户（资产级）
                    queryFieldsAss.append(labelMap.get("code")).append(",");
                }
                if ("3000".equals(labelMap.get("type"))) { //销售品级
                    isSale = true;
                    queryFieldsSale.append(labelMap.get("code")).append(",");
                } else {
                    //标签目录异常
                }
            }

            //如果运存在销售品级标签
            if (isSale) {
                queryFieldsAss.append("PROM_INTEG_ID,");  //这里的逗号是有用的
            }

            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //资产级标签
            if (queryFieldsAss.length() > 0) {

                queryFieldsAss.deleteCharAt(queryFieldsAss.length() - 1);
                JSONObject param = new JSONObject();
                param.put("queryNum", privateParams.get("accNbr"));
                param.put("c3", params.get("lanId"));
                param.put("queryId", privateParams.get("integrationId"));
                param.put("type", "1");
                param.put("queryFields", queryFieldsAss.toString());

                if (!"".equals(queryFieldsAss.toString())) {
//                    System.out.println("param " + param.toString());
                    //更换为dubbo因子查询-----------------------------------------------------
                    Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
//                    System.out.println(dubboResult.toString());

                    if ("0".equals(dubboResult.get("result_code").toString())) {
                        JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                        //ES log 标签实例
                        //拼接规则引擎上下文
                        for (Map.Entry<String, Object> entry : body.entrySet()) {
                            //添加到上下文
                            context.put(entry.getKey(), entry.getValue());

                            if ("PROM_INTEG_ID".equals(entry.getKey())) {
                                saleId = entry.getValue().toString();
                            }
                        }
//                        System.out.println("查询资产标签成功:" + context.toString());
                    } else {
//                        System.out.println("查询资产标签失败:");
                        esJson.put("hit", "false");
                        esJson.put("msg", "查询资产标签失败");
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        return Collections.EMPTY_MAP;
                    }
                }
            }

            //客户级标签
            if (queryFieldsCust.length() > 0) {
                queryFieldsCust.deleteCharAt(queryFieldsCust.length() - 1);

                JSONObject paramCust = new JSONObject();
                paramCust.put("queryNum", "");
                paramCust.put("c3", params.get("lanId"));
                paramCust.put("queryId", privateParams.get("custId"));
                paramCust.put("type", "2");
                paramCust.put("queryFields", queryFieldsCust.toString());

                if (!"".equals(queryFieldsCust.toString())) {
//                    System.out.println("param " + paramCust.toString());
                    //更换为dubbo因子查询-----------------------------------------------------
                    Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(paramCust));
//                    System.out.println(dubboResult.toString());

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
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        return Collections.EMPTY_MAP;
                    }
                }
            }

            //销售品级标签
            if (queryFieldsSale.length() > 0) {
                if ("".equals(saleId)) {
                    esJson.put("hit", false);
                    esJson.put("msg", "主销售品数据错误");
//                    System.out.println("主销售品数据错误");
                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }

                queryFieldsSale.deleteCharAt(queryFieldsSale.length() - 1);

                JSONObject paramSale = new JSONObject();
                paramSale.put("queryNum", "");
                paramSale.put("c3", params.get("lanId"));
                paramSale.put("queryId", saleId);
                paramSale.put("type", "3");
                paramSale.put("queryFields", queryFieldsSale.toString());

                if (!"".equals(queryFieldsSale.toString())) {
//                    System.out.println("param " + paramSale.toString());
                    //更换为dubbo因子查询-----------------------------------------------------
                    Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(paramSale));
//                    System.out.println(dubboResult.toString());

                    if ("0".equals(dubboResult.get("result_code").toString())) {
                        JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                        //拼接规则引擎上下文
                        for (Map.Entry<String, Object> entry : body.entrySet()) {
                            //添加到上下文
                            context.put(entry.getKey(), entry.getValue());
                        }

                    } else {
                        System.out.println("查询销售品标签失败:");
                        esJson.put("hit", "false");
                        esJson.put("msg", "查询销售品标签失败");
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        return Collections.EMPTY_MAP;
                    }
                }
            }

            context.putAll(labelItems);

            //根据策略id获取策略下发规则列表 todo
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOS = mktStrategyConfRuleMapper.selectByMktStrategyConfId(strategyConfId);
            //遍历规则↓↓↓↓↓↓↓↓↓↓
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            //遍历规则列表
            if (mktStrategyConfRuleDOS != null && mktStrategyConfRuleDOS.size() > 0) {
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOS) {
                    //获取分群id
                    Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
                    //获取销售品
                    String productStr = mktStrategyConfRuleDO.getProductId();
                    //协同渠道配置id
                    String evtContactConfIdStr = mktStrategyConfRuleDO.getEvtContactConfId();
                    //规则id
                    Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();

                    String mktStrategyConfRuleName = mktStrategyConfRuleDO.getMktStrategyConfRuleName();
                    //提交线程
                    Future<Map<String, Object>> f = executorService.submit(new RuleTask(params, privateParams, strategyConfId, tarGrpId, productStr,
                            evtContactConfIdStr, mktStrategyConfRuleId, mktStrategyConfRuleName, labelItems, context));
                    //将线程处理结果添加到结果集
                    threadList.add(f);
                }
            }
            //获取结果
            try {
                for (Future<Map<String, Object>> future : threadList) {
                    if (!future.get().isEmpty()) {
                        ruleList.add(future.get());
                    }
                }
                strategyMap.put("ruleList", ruleList);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //发生异常关闭线程池
                executorService.shutdown();
            } catch (ExecutionException e) {
                e.printStackTrace();
                //发生异常关闭线程池
                executorService.shutdown();
            } finally {
                //关闭线程池
                executorService.shutdown();
            }

            if (ruleList.size() > 0) {
                esJson.put("hit", "true");
            } else {
                esJson.put("hit", "false");
                esJson.put("msg", "规则均未命中");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }

            return strategyMap;
        }
    }

    /**
     * 获取规则列表（规则级）
     */
    class RuleTask implements Callable<Map<String, Object>> {
        private Long strategyConfId; //策略配置id
        private Long tarGrpId;
        private String productStr;
        private String evtContactConfIdStr;
        private String reqId;
        private Long ruleId;
        private String ruleName;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private Map<String, String> labelItems;
        private DefaultContext<String, Object> context;

        public RuleTask(Map<String, String> params, Map<String, String> privateParams, Long strategyConfId, Long tarGrpId, String productStr,
                        String evtContactConfIdStr, Long mktStrategyConfRuleId, String mktStrategyConfRuleName, Map<String, String> labelItems,
                        DefaultContext<String, Object> context) {
            this.strategyConfId = strategyConfId;
            this.tarGrpId = tarGrpId;
            this.reqId = params.get("reqId");
            this.productStr = productStr;
            this.evtContactConfIdStr = evtContactConfIdStr;
            this.ruleId = mktStrategyConfRuleId;
            this.ruleName = mktStrategyConfRuleName;
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
            this.context = context;
        }

        @Override
        public Map<String, Object> call() throws Exception {

            //初始化es log   标签使用
            JSONObject esJson = new JSONObject();
            //初始化es log   规则使用
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("ruleId", ruleId);
            jsonObject.put("ruleName", ruleName);
            jsonObject.put("hitEntity", privateParams.get("accNbr")); //命中对象
            jsonObject.put("reqId", reqId);
            jsonObject.put("eventId", params.get("eventCode"));
            jsonObject.put("activityId", params.get("activityId"));
            jsonObject.put("strategyConfId", strategyConfId);
            jsonObject.put("productStr", productStr);
            jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
            jsonObject.put("tarGrpId", tarGrpId);

            //ES log 标签实例
            esJson.put("reqId", reqId);
            esJson.put("eventId", params.get("eventCode"));
            esJson.put("activityId", params.get("activityId"));
            esJson.put("ruleId", ruleId);
            esJson.put("ruleName", ruleName);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("strategyConfId", strategyConfId);

            Map<String, Object> ruleMap = new HashMap<>();
            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> taskChlList = new ArrayList<>();

            //  2.判断客户分群规则---------------------------
            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
            //拼装redis key
            ExpressRunner runner = new ExpressRunner();
            runner.addFunction("toNum", new StringToNumOperator("toNum"));

            //如果分群id为空
            if (tarGrpId == null) {
                jsonObject.put("hit", "false");
                jsonObject.put("msg", "分群ID异常");
                esService.save(jsonObject, IndexList.RULE_MODULE);
                return Collections.EMPTY_MAP;
            }

            //判断redis中是否存在

            //记录实例不足的标签
            StringBuilder notEnoughLabel = new StringBuilder();

            //判断表达式在缓存中有没有
            String express = (String) redisUtils.get("EXPRESS_" + tarGrpId);
//            String express = "";
            if (express == null || "".equals(express)) {
                List<LabelResult> labelResultList = new ArrayList<>();
                try {
                    LabelResult lr;
                    //查询规则下所有标签
                    List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("RULE_ALL_LABEL_" + tarGrpId);
                    if (labelMapList == null) {
                        labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(tarGrpId);
                    }


                    //将规则拼装为表达式
                    StringBuilder expressSb = new StringBuilder();
                    expressSb.append("if(");
                    //遍历所有规则
                    for (Map<String, String> labelMap : labelMapList) {
                        //判断标签实例是否足够
                        if (!context.containsKey(labelMap.get("code"))) {
                            notEnoughLabel.append(labelMap.get("code")).append(",");
                            continue;
                        }

                        String type = labelMap.get("operType");
                        //保存标签的es log
                        lr = new LabelResult();
                        lr.setOperType(type);
                        lr.setLabelCode(labelMap.get("code"));
                        lr.setLabelName(labelMap.get("name"));
                        lr.setRightOperand(labelMap.get("rightParam"));
                        lr.setClassName(labelMap.get("className"));
                        if (context.containsKey(labelMap.get("code"))) {
                            lr.setRightParam(context.get(labelMap.get("code")).toString());
                        } else {
                            lr.setRightParam("无值");
                            lr.setResult(false);
                        }

                        expressSb.append(cpcExpression(labelMap.get("code"), type, labelMap.get("rightParam")));

                        try {
                            RuleResult ruleResultOne = runner.executeRule(cpcLabel(labelMap.get("code"), type, labelMap.get("rightParam")), context, true, true);
                            if (null != ruleResultOne.getResult()) {
                                lr.setResult((Boolean) ruleResultOne.getResult());
                            } else {
                                lr.setResult(false);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
//                        System.out.println("单个标签判断出错");
                        }
                        expressSb.append("&&");
                        labelResultList.add(lr);
                    }

                    expressSb.delete(expressSb.length() - 2, expressSb.length());
                    expressSb.append(") {return true} else {return false}");
                    express = expressSb.toString();

                    //表达式存入redis
                    redisUtils.set("EXPRESS_" + tarGrpId, express);
                } catch (Exception e) {
                    e.printStackTrace();
                    esJson.put("hit", "false");
                    esJson.put("msg", "表达式拼接异常");
                    esService.save(jsonObject, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
                }

                esJson.put("labelResultList", JSONArray.toJSON(labelResultList));

            } else {
                List<LabelResult> labelResultList = new ArrayList<>();
                try {
                    LabelResult lr;
                    //查询规则下所有标签
                    List<Map<String, String>> labelMapList = (List<Map<String, String>>) redisUtils.get("RULE_ALL_LABEL_" + tarGrpId);
                    if (labelMapList == null) {
                        labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(tarGrpId);
                    }

                    //将规则拼装为表达式
                    //遍历所有规则
                    for (Map<String, String> labelMap : labelMapList) {
                        //判断标签实例是否足够
                        if (!context.containsKey(labelMap.get("code"))) {
                            notEnoughLabel.append(labelMap.get("code")).append(",");
                            continue;
                        }

                        String type = labelMap.get("operType");
                        //保存标签的es log
                        lr = new LabelResult();
                        lr.setOperType(type);
                        lr.setLabelCode(labelMap.get("code"));
                        lr.setLabelName(labelMap.get("name"));
                        lr.setRightOperand(labelMap.get("rightParam"));
                        lr.setClassName(labelMap.get("className"));
                        if (context.containsKey(labelMap.get("code"))) {
                            lr.setRightParam(context.get(labelMap.get("code")).toString());
                        } else {
                            lr.setRightParam("无值");
                            lr.setResult(false);
                        }

                        try {
                            RuleResult ruleResultOne = runner.executeRule(cpcLabel(labelMap.get("code"), type, labelMap.get("rightParam")), context, true, true);
                            if (null != ruleResultOne.getResult()) {
                                lr.setResult((Boolean) ruleResultOne.getResult());
                            } else {
                                lr.setResult(false);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
//                        System.out.println("单个标签判断出错");
                        }
                        labelResultList.add(lr);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.EMPTY_MAP;
                }

                esJson.put("labelResultList", JSONArray.toJSON(labelResultList));
            }

            esService.save(esJson, IndexList.Label_MODULE);

            //验证是否标签实例不足
            if (notEnoughLabel.length() > 0) {
                esJson.put("hit", "false");
                esJson.put("msg", "标签实例不足：" + notEnoughLabel.toString());
                esService.save(esJson, IndexList.RULE_MODULE);
                return Collections.EMPTY_MAP;
            }
            try {
                //规则引擎计算
//                System.out.println(express);
                RuleResult ruleResult = null;
//                System.out.println(context.toString());
                ExpressRunner runnerQ = new ExpressRunner();
                runnerQ.addFunction("toNum", new StringToNumOperator("toNum"));
                try {
                    ruleResult = runnerQ.executeRule(express, context, true, true);

                } catch (Exception e) {
                    e.printStackTrace();

                    ruleMap.put("msg", "规则引擎计算失败");

                    esJson.put("hit", "false");
                    esJson.put("msg", "规则引擎计算失败");
                    esService.save(jsonObject, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
                }

//                System.out.println("result=" + ruleResult.getResult());
                System.out.println("Tree=" + ruleResult.getRule().toTree());
                System.out.println("TraceMap=" + ruleResult.getTraceMap());

                jsonObject.put("express", express);
//                jsonObject.put("reqId", reqId);
//                jsonObject.put("eventId", params.get("eventCode"));
//                jsonObject.put("activityId", params.get("activityId"));
//                jsonObject.put("strategyConfId", strategyConfId);
//                jsonObject.put("productStr", productStr);
//                jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
//                jsonObject.put("tarGrpId", tarGrpId);

                //初始化返回结果中的销售品条目
                List<Map<String, String>> productList = new ArrayList<>();
                if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {

                    jsonObject.put("hit", true);

                    //拼接返回结果
                    ruleMap.put("orderISI", params.get("reqId")); //流水号
                    ruleMap.put("activityId", privateParams.get("activityId")); //活动编码
                    ruleMap.put("activityName", privateParams.get("activityName")); //活动名称
                    ruleMap.put("activityType", privateParams.get("activityType")); //活动类型
                    ruleMap.put("activityStartTime", privateParams.get("activityStartTime")); //活动名称
                    ruleMap.put("activityEndTime", privateParams.get("activityEndTime")); //活动名称
                    ruleMap.put("skipCheck", "0"); //todo 调过预校验
                    ruleMap.put("orderPriority", privateParams.get("orderPriority")); //活动优先级
                    ruleMap.put("integrationId", privateParams.get("integrationId")); //集成编号（必填）
                    ruleMap.put("accNbr", privateParams.get("accNbr")); //业务号码（必填）
                    ruleMap.put("policyId", strategyConfId.toString()); //策略编码
                    ruleMap.put("policyName", strategyConfId.toString()); //策略名称
                    ruleMap.put("ruleId", ruleId.toString()); //规则编码
                    ruleMap.put("ruleName", ruleName); //规则名称

                    //查询销售品列表
                    if (productStr != null && !"".equals(productStr)) {
                        String[] productArray = productStr.split("/");
                        for (String str : productArray) {
                            Map<String, String> product = new HashMap<>();

                            // 从redis中获取推荐条目
                            MktCamItem mktCamItem = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_" + str);
                            if (mktCamItem == null) {
                                mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(str));
                                if (mktCamItem == null) {
                                    continue;
                                }
                                redisUtils.set("MKT_CAM_ITEM_" + mktCamItem.getMktCamItemId(), mktCamItem);
                            }

                            product.put("productId", mktCamItem.getItemId().toString());
                            product.put("productCode", mktCamItem.getOfferCode());
                            product.put("productName", mktCamItem.getOfferName());
                            product.put("productType", mktCamItem.getItemType());
                            product.put("productFlag", "1000");  //todo 销售品标签
                            //销售品优先级
                            if (mktCamItem.getPriority() != null) {
                                product.put("productPriority", mktCamItem.getPriority().toString());
                            } else {
                                product.put("productPriority", "0");
                            }
//                            System.out.println("*********************product --->>>" + JSON.toJSON(product));
                            productList.add(product);
                        }
                    }

                    jsonObject.put("productList", productList);

                    if (ruleResult.getResult() == null) {
                        ruleResult.setResult(false);
                    }

                    //获取协同渠道所有id
                    String[] evtContactConfIdArray = evtContactConfIdStr.split("/");
                    //初始化结果集
                    List<Future<Map<String, Object>>> threadList = new ArrayList<>();
                    //初始化线程池
                    ExecutorService executorService = Executors.newCachedThreadPool();

                    try {
                        //遍历协同渠道
                        for (String str : evtContactConfIdArray) {
                            //协同渠道规则表id（自建表）
                            Long evtContactConfId = Long.parseLong(str);
                            //提交线程
                            Future<Map<String, Object>> f = executorService.submit(new ChannelTask(params, evtContactConfId, productList, privateParams, labelItems));
                            //将线程处理结果添加到结果集
                            threadList.add(f);
                        }
                        //获取结果

                        for (Future<Map<String, Object>> future : threadList) {
                            if (!future.get().isEmpty()) {
                                taskChlList.add(future.get());
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
                    } finally {
                        //关闭线程池
                        executorService.shutdown();
                    }
                } else {

                    ruleMap.put("msg", "规则引擎匹配未通过");

                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "规则引擎匹配未通过");
                    esService.save(jsonObject, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
                }

                ruleMap.put("taskChlList", taskChlList);
                if (taskChlList.size() > 0) {
                    jsonObject.put("hit", true);
                } else {
                    jsonObject.put("hit", false);
                    jsonObject.put("msg", "渠道均未命中");

                }
                esService.save(jsonObject, IndexList.RULE_MODULE);
            } catch (Exception e) {
                System.out.println("+++++++++++++++++++");
                e.printStackTrace();

                jsonObject.put("hit", false);
                jsonObject.put("msg", "规则异常");
                esService.save(jsonObject, IndexList.RULE_MODULE);
            }

            return ruleMap;
        }

    }

    class ChannelTask implements Callable<Map<String, Object>> {

        //策略配置id
        private Long evtContactConfId;

        private List<Map<String, String>> productList;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private Map<String, String> labelItems;

        public ChannelTask(Map<String, String> params, Long evtContactConfId, List<Map<String, String>> productList,
                           Map<String, String> privateParams, Map<String, String> labelItems) {
            this.evtContactConfId = evtContactConfId;
            this.productList = productList;
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
        }

        @Override
        public Map<String, Object> call() {
            Date now = new Date();

            //初始化返回结果推荐信息
            Map<String, Object> channel = new HashMap<>();

            List<Map<String, Object>> taskChlAttrList = new ArrayList<>();
            Map<String, Object> taskChlAttr;

            //查询渠道属性，渠道生失效时间过滤
            MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + evtContactConfId);
            MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
            if (mktCamChlConfDetail != null) {
                BeanUtil.copy(mktCamChlConfDetail, mktCamChlConfDO);
                for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfDetail.getMktCamChlConfAttrList()) {
                    MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(mktCamChlConfAttr, new MktCamChlConfAttrDO());
                    mktCamChlConfAttrDOList.add(mktCamChlConfAttrDO);
                }
            } else {
                // 从数据库中获取并拼成ktCamChlConfDetail对象存入redis
                mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
                mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
                List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
                mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
                for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                    MktCamChlConfAttr mktCamChlConfAttrNew = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                    mktCamChlConfAttrList.add(mktCamChlConfAttrNew);
                }
                mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
                redisUtils.set("MktCamChlConfDetail_" + evtContactConfId, mktCamChlConfDetail);
            }
            boolean checkTime = true;
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {

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
                }

                if (mktCamChlConfAttrDO.getAttrId() == 500600010005L ||
                        mktCamChlConfAttrDO.getAttrId() == 500600010011L) {
                    taskChlAttr = new HashMap<>();
                    taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId().toString());
                    taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId().toString());
                    taskChlAttr.put("attrValue", mktCamChlConfAttrDO.getAttrValue());
                    taskChlAttrList.add(taskChlAttr);
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
                }

                //获取调查问卷ID
                if (mktCamChlConfAttrDO.getAttrId() == 500600010008L) {
                    //调查问卷
                    channel.put("naireId", mktCamChlConfAttrDO.getAttrValue());
                }

                //获取接触账号/推送账号(如果有)
                if (mktCamChlConfAttrDO.getAttrId() == 500600010012L) {

                    if (mktCamChlConfAttrDO.getAttrValue() != null && !"".equals(mktCamChlConfAttrDO.getAttrValue())) {

                        if (labelItems.containsKey(mktCamChlConfAttrDO.getAttrValue())) {
                            channel.put("contactAccount", labelItems.get(mktCamChlConfAttrDO.getAttrValue()));
                        } else {
                            JSONObject httpParams = new JSONObject();
                            httpParams.put("queryNum", privateParams.get("accNbr"));
                            httpParams.put("c3", params.get("lanId"));
                            httpParams.put("queryId", privateParams.get("integrationId"));
                            httpParams.put("type", "1");
                            //待查询的标签列表
                            httpParams.put("queryFields", mktCamChlConfAttrDO.getAttrValue());
                            //dubbo接口查询标签
                            JSONObject resJson = getLabelByDubbo(httpParams);
                            if (resJson.containsKey(mktCamChlConfAttrDO.getAttrValue())) {
                                channel.put("contactAccount", resJson.get(mktCamChlConfAttrDO.getAttrValue()));
                            } else {
                                //todo 不命中
                                channel.put("contactAccount", mktCamChlConfAttrDO.getAttrValue());
                            }
                        }
                    }
                }

            }
            channel.put("taskChlAttrList", taskChlAttrList);

            if (!checkTime) {
                return Collections.EMPTY_MAP;
            }

            //查询渠道信息基本信息
            // MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

            //渠道信息
            Channel channelMessage = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
            channel.put("channelId", channelMessage.getContactChlCode());
            //查询渠道id
            channel.put("channelConfId", channelMessage.getContactChlId().toString()); //渠道id
            channel.put("pushType", mktCamChlConfDetail.getPushType()); //推送类型

            channel.put("pushTime", ""); // 推送时间

            //返回结果中添加销售品信息
            channel.put("productList", JSONArray.toJSON(productList));

            //查询渠道子策略 这里老系统暂时不返回
//              List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

            //查询推荐指引
            List<String> scriptLabelList = new ArrayList<>();
            String contactScript = null;
            String mktVerbalStr = null;
            // 从redis获取的mktCamChlConfDetail中获取脚本
            CamScript camScript = mktCamChlConfDetail.getCamScript();
            if (camScript != null) {
                //获取脚本信息
                contactScript = camScript.getScriptDesc();
                if (contactScript != null) {
                    scriptLabelList.addAll(subScript(contactScript));
                }
            } else {
                // 数据库中获取脚本存入redis
                camScript = mktCamScriptMapper.selectByConfId(evtContactConfId);
                mktCamChlConfDetail.setCamScript(camScript);
                redisUtils.set("MktCamChlConfDetail_" + evtContactConfId, mktCamChlConfDetail);
            }


            //查询痛痒点
            // 从redis获取的mktCamChlConfDetail中获取话术
            List<VerbalVO> verbalVOList = mktCamChlConfDetail.getVerbalVOList();
            if (verbalVOList != null) {

            } else {
                List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(evtContactConfId);
                verbalVOList = new ArrayList<>();
                for (MktVerbal mktVerbal : mktVerbals) {
                    VerbalVO verbalVO = BeanUtil.create(mktVerbal, new VerbalVO());
                    verbalVOList.add(verbalVO);
                }
            }

            if (verbalVOList.size() > 0) {
                for (VerbalVO verbalVO : verbalVOList) {
                    //查询痛痒点规则 todo
//                        List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());

                    mktVerbalStr = verbalVOList.get(0).getScriptDesc();
                    if (mktVerbalStr != null) {
                        scriptLabelList.addAll(subScript(mktVerbalStr));
                    }
                }
            }

            if (scriptLabelList.size() > 0) {

                StringBuilder queryFieldsAss = new StringBuilder();
                StringBuilder queryFieldsCust = new StringBuilder();
                StringBuilder queryFieldsSale = new StringBuilder();

                JSONObject allBody = new JSONObject();

                for (String labelCode : scriptLabelList) {
                    if (queryFieldsAss.toString().contains(labelCode)
                            || queryFieldsCust.toString().contains(labelCode)
                            || queryFieldsSale.toString().contains(labelCode)) {
                        continue;
                    }

                    //判断销售品、促销券、服务的标签
                    if ("PROM_SCRIPT_YZ".equals(labelCode)) { //销售品
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, String> pro : productList) {
                            if ("1000".equals(pro.get("productType"))) {

                            }
                            sb.append(pro.get(""));
                        }
                        allBody.put(labelCode, sb.toString());

                        continue;
                    } else if ("SALE_SCRIPT_YZ".equals(labelCode)) {  //促销券
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, String> pro : productList) {
                            if ("3000".equals(pro.get("productType"))) {

                            }
                            sb.append(pro.get(""));
                        }
                        allBody.put(labelCode, sb.toString());

                        continue;
                    } else if ("SERVICE_SCRIPT_YZ".equals(labelCode)) {  //服务
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, String> pro : productList) {
                            if ("4000".equals(pro.get("productType"))) {

                            }
                            sb.append(pro.get(""));
                        }
                        allBody.put(labelCode, sb.toString());

                        continue;
                    }

                    Label label = (Label) redisUtils.get("LABEL_CODE_" + labelCode);
                    if (label == null) {
                        label = injectionLabelMapper.selectByLabelCode(labelCode);
                        redisUtils.set("LABEL_CODE_" + labelCode, label);
                    }

//                    Label label = injectionLabelMapper.selectByLabelCode(labelCode);
                    if (label != null) {
                        if ("1000".equals(label.getLabelType())) {
                            queryFieldsCust.append(labelCode).append(",");
                        } else if ("2000".equals(label.getLabelType())) {
                            queryFieldsAss.append(labelCode).append(",");
                        } else if ("3000".equals(label.getLabelType())) {
                            queryFieldsSale.append(labelCode).append(",");
                        } else {

                        }
                    }
                }


                if (queryFieldsAss.length() > 0) {
                    queryFieldsAss.deleteCharAt(queryFieldsAss.length() - 1);

                    JSONObject labelParam = new JSONObject();
                    labelParam.put("queryNum", privateParams.get("accNbr"));
                    labelParam.put("c3", params.get("lanId"));
                    labelParam.put("queryId", privateParams.get("integrationId"));
                    labelParam.put("type", "1");
                    labelParam.put("queryFields", queryFieldsAss.toString());
                    Map<String, Object> queryResult = getLabelValue(labelParam);
                    JSONObject assBody = new JSONObject((HashMap) queryResult.get("msgbody"));
                    allBody.putAll(assBody);
                }
                if (queryFieldsCust.length() > 0) {
                    queryFieldsCust.deleteCharAt(queryFieldsCust.length() - 1);

                    JSONObject labelParam = new JSONObject();
                    labelParam.put("queryNum", "");
                    labelParam.put("c3", params.get("lanId"));
                    labelParam.put("queryId", privateParams.get("custId"));
                    labelParam.put("type", "2");
                    labelParam.put("queryFields", queryFieldsCust.toString());
                    Map<String, Object> queryResult = getLabelValue(labelParam);
                    JSONObject custBody = new JSONObject((HashMap) queryResult.get("msgbody"));
                    allBody.putAll(custBody);
                }
                if (queryFieldsSale.length() > 0) {
                    queryFieldsSale.deleteCharAt(queryFieldsSale.length() - 1);

                    JSONObject labelParam = new JSONObject();
                    labelParam.put("queryNum", privateParams.get("accNbr"));
                    labelParam.put("c3", params.get("lanId"));
                    labelParam.put("queryId", privateParams.get("integrationId"));
                    labelParam.put("type", "3");
                    labelParam.put("queryFields", queryFieldsSale.toString());
                    Map<String, Object> queryResult = getLabelValue(labelParam);
                    JSONObject saleBody = new JSONObject((HashMap) queryResult.get("msgbody"));
                    allBody.putAll(saleBody);
                }

//                JSONObject labelParam = new JSONObject();
//                labelParam.put("queryNum", privateParams.get("accNbr"));
//                labelParam.put("c3", params.get("lanId"));
//                labelParam.put("queryId", privateParams.get("integrationId"));
//                labelParam.put("type", "1");
//                labelParam.put("queryFields", queryFieldsSb.toString());
//                Map<String, Object> queryResult = getLabelValue(labelParam);
//
//                JSONObject body = new JSONObject((HashMap) queryResult.get("msgbody"));
                allBody.putAll(labelItems);
                //获取查询结果
                for (Map.Entry<String, Object> entry : allBody.entrySet()) {
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
//                    System.out.println("推荐话术标签替换含有无值的标签");
                    return Collections.EMPTY_MAP;
                }
            }
            channel.put("contactScript", contactScript == null ? "" : contactScript);
            //痛痒点
            if (mktVerbalStr != null) {
                if (subScript(mktVerbalStr).size() > 0) {
//                    System.out.println("推荐指引标签替换含有无值的标签");
                    return Collections.EMPTY_MAP;
                }
            }
            channel.put("reason", mktVerbalStr == null ? "" : mktVerbalStr);
            //展示列标签
//            channel.put("itgTriggers", JSONArray.parse(JSONArray.toJSON(itgTriggers).toString()));
//            channel.put("triggers", JSONArray.parse(JSONArray.toJSON(evtTriggers).toString()));

            return channel;
        }
    }


    private JSONObject getLabelByDubbo(JSONObject param) {
        //查询标签实例数据
//        System.out.println("param " + param.toString());

        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));

//        System.out.println(dubboResult.toString());

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
//        System.out.println("paramScript " + param.toString());
        //更换为dubbo因子查询-----------------------------------------------------
        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
//        System.out.println(dubboResult.toString());
        return dubboResult;
    }


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


    public static String cpcExpression(Label label, String type, String rightParam) {
        StringBuilder express = new StringBuilder();

        if ("7100".equals(type)) {
            express.append("!");
        }
        express.append("((");
        express.append(assLabel(label, type, rightParam));
        express.append(")");

        return express.toString();
    }

    public static String cpcExpression(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();

        if ("7100".equals(type)) {
            express.append("!");
        }
        express.append("((");
        express.append(assLabel(code, type, rightParam));
        express.append(")");

        return express.toString();
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
                express.append(code).append(")");
                express.append(" == ");
                express.append("\"").append(rightParam).append("\"");
                break;
            case "4000":
                express.append(code).append(")");
                express.append(" != ");
                express.append("\"").append(rightParam).append("\"");
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
            case "7100":
            case "7000":
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
            case "7200":
                express.append("toNum(").append(code).append("))");
                String[] strArray2 = rightParam.split(",");
                express.append(" >= ").append(strArray2[0]);
                express.append(" && ").append("(toNum(");
                express.append(code).append("))");
                express.append(" <= ").append(strArray2[1]);

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

    public JSONObject getLabelsByDubbo(String queryId, String queryNum, String lanId, String type, String queryFields) {

        JSONObject labelParam = new JSONObject();
        labelParam.put("queryId", queryId);
        labelParam.put("queryNum", queryNum);
        labelParam.put("c3", lanId);
        labelParam.put("type", type);
        //查询标签为销售品标签
        labelParam.put("queryFields", queryFields);
        Map<String, Object> queryResult = getLabelValue(labelParam);
        JSONObject body = new JSONObject((HashMap) queryResult.get("msgbody"));

        return body;
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
            List<Map<String, String>> iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getIsaleDisplay());
            List<Map<String, Object>> itgTriggers = new ArrayList<>();
            Map<String, Object> itgTrigger;

            StringBuilder querySb = new StringBuilder();

            if (iSaleDisplay != null && iSaleDisplay.size() > 0) {
                for (Map<String, String> label : iSaleDisplay) {
                    querySb.append((String) label.get("labelCode")).append(",");
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

                for (Map<String, String> label : iSaleDisplay) {
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
        log.info("开始验证事件规则条件");
        Map<String, Object> result = new HashMap<>();
        result.put("code", "success");
        //查询事件规则
        ContactEvtMatchRul evtMatchRul = new ContactEvtMatchRul();
        evtMatchRul.setContactEvtId(eventId);
        List<ContactEvtMatchRul> contactEvtMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(evtMatchRul);
        //事件规则为空不用判断,直接返回
        if (contactEvtMatchRuls.isEmpty()) {
            log.info("事件规则为空直接返回");
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

}
