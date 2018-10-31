package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsService;
import com.zjtelcom.cpct.util.HttpUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class EventApiServiceImpl implements EventApiService {

    @Autowired
    private ContactEvtMapper contactEvtMapper; //事件总表

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

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
            async(params);

        }

        public Map async(Map<String, String> map) {

            //初始化返回结果
            Map<String, Object> result = new HashMap();
            result = new EventTask().call(params);
            System.out.println("开始调用协同中心异步回调");
            //调用协同中心回调接口
            Map<String, Object> back = iContactTaskReceiptService.contactTaskReceipt(result);
            if (back != null) {
                if ("1".equals(back.get("resultCode"))) {
                    System.out.println("协同中心接口回调调用成功");
                    return null;
                }
            }
            System.out.println("协同中心接口回调调用失败" + back.get("resultMsg"));

            return result;
        }
    }

    /**
     * 事件验证模块公共方法
     */
    class EventTask {

        public Map<String, Object> call(Map<String, String> map) {
            //开始时间
            long begin = System.currentTimeMillis();

            //初始化返回结果
            Map<String, Object> result = new HashMap();

            //初始化es log
            JSONObject esJson = new JSONObject();

            //构造返回结果
            String custId = map.get("custId");
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);

            //事件验证开始↓↓↓↓↓↓↓↓↓↓↓↓↓
            //解析事件采集项
            JSONObject evtParams = JSONObject.parseObject(map.get("evtContent"));
            //根据事件code查询事件信息
            ContactEvt event = contactEvtMapper.getEventByEventNbr(map.get("eventCode"));
            if (event == null) {
                result.put("CPCResultMsg", "未找到相关事件");
                return result;
            }
            //获取事件id
            Long eventId = event.getContactEvtId();

            //es log
            esJson.put("reqId", map.get("reqId"));
            esJson.put("eventId", eventId);
            esJson.put("eventCode", map.get("eventCode"));
            esJson.put("integrationId", map.get("integrationId"));
            esJson.put("accNbr", map.get("accNbr"));
            esJson.put("custId", custId);
            esJson.put("evtCollectTime", map.get("evtCollectTime"));

            //验证事件采集项
            List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(eventId);
            List<Map<String, Object>> evtTriggers = new ArrayList<>();
            Map<String, Object> trigger;
            //事件采集项标签集合(事件采集项标签优先规则)
            Map<String, String> labelItems = new HashMap<>();

            StringBuilder stringBuilder = new StringBuilder();
            for (ContactEvtItem contactEvtItem : contactEvtItems) {
                //排除资产号码  资产集成编码  客户编码
                if ("ACC_NBR".equals(contactEvtItem.getEvtItemCode())
                        || "INTEGRATION_ID".equals(contactEvtItem.getEvtItemCode())
                        || "custId".equals(contactEvtItem.getEvtItemCode())) {


                    continue;
                }
                if (evtParams.containsKey(contactEvtItem.getEvtItemCode())) {
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

            //事件采集项返回参数
            if (stringBuilder.length() > 0) {

                //保存es log
                long cost = System.currentTimeMillis() - begin;
                esJson.put("timeCost", cost);
                esJson.put("hit", false);
                esJson.put("msg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
                esService.save(esJson, IndexList.EVENT_MODULE);

                result.put("CPCResultCode", "1000");
                result.put("CPCResultMsg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
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
//        List<Long> activityIds = mktCamEvtRelMapper.listActivityByEventId(eventId);
            List<MktCamEvtRelDO> mktCamEvtRelDOs = mktCamEvtRelMapper.listActByEventId(eventId);
            //初始化返回结果中的工单信息
            List<Map<String, Object>> activityList = new ArrayList<>();

            //遍历活动id  查询并匹配活动规则 需要根据事件推荐活动数 取前n个活动
            int max = mktCamEvtRelDOs.size();
//            if (recCampaignAmount != 0) {
//                //事件推荐活动数
//                if (mktCamEvtRelDOs.size() > recCampaignAmount) {
//                    max = recCampaignAmount;
//                }
//            }
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
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
                        esService.save(esJson, IndexList.EVENT_MODULE);

                        //事件采集项没有客户编码
                        result.put("result", false);
                        result.put("msg", "采集项未包含客户编码");
                        return result;
                    }

                    //根据客户编码查询所有资产
                    //构造查询参数值
                    JSONObject param = new JSONObject();
                    //查询标识
                    param.put("queryNum", map.get("accNbr"));
                    param.put("c3", map.get("lanId"));
                    param.put("queryId", map.get("integrationId"));
                    String queryFields = "ASSET_INTEG_ID";
                    param.put("queryFields", queryFields);

                    System.out.println("param " + param.toString());
                    Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
                    System.out.println(dubboResult.toString());

                    JSONArray accArray = new JSONArray();
                    if ("0".equals(dubboResult.get("result_code").toString())) {
                        JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));

                        return Collections.EMPTY_MAP;
                    } else {
                        esJson.put("hit", "false");
                        esJson.put("msg", "客户级资产查询出错");
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        System.out.println("客户级资产查询出错");
                    }
                    //获取客户下所有资产
                    for (Object o : accArray) {
                        //客户级下，循环资产级
                        Map<String, String> privateParams = new HashMap<>();
                        privateParams.put("isCust", "0");
                        privateParams.put("accNbr", ((JSONObject) o).getString("ACC_NBR"));
                        privateParams.put("integrationId", ((JSONObject) o).getString("ASSET_INTEG_ID"));
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
            }
            //关闭线程池
            executorService.shutdown();

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

                if (recCampaignAmount != 0 && recCampaignAmount < activityList.size()) {
                    //事件推荐活动数
                    activityList = activityList.subList(0, recCampaignAmount - 1);
                }
            }

            //es log
            long cost = System.currentTimeMillis() - begin;
            esJson.put("timeCost", cost);
            esJson.put("msg", "客户级活动，事件采集项未包含客户编码");
            esService.save(esJson, IndexList.EVENT_MODULE);

            //构造返回参数
            result.put("CPCResultCode", "1");
            result.put("CPCResultMsg", "success");
            result.put("reqId", map.get("reqId"));
            result.put("custId", custId);

            //返回结果
            result.put("taskList", activityList); //协同回调结果

            System.out.println(result.toString());

            return result;
        }
    }


    /**
     * 获取策略列表
     */
    class ActivityTask implements Callable<Map<String, Object>> {
        private Long activityId;
        private String reqId;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private Map<String, String> labelItems;
        private List<Map<String, Object>> evtTriggers;

        public ActivityTask(Map<String, String> params, Long activityId, Map<String, String> privateParams,
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

            List<Map<String, Object>> strageyList = new ArrayList<>();
            //查询活动基本信息
            MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(activityId);

            //判断活动状态
//            if (!"2002".equals(mktCampaign.getStatusCd())) {
//                esJson.put("hit", "false");
//                esJson.put("msg", "活动状态未发布");
//                esService.save(esJson, IndexList.ACTIVITY_MODULE);
//
//                System.out.println("活动状态未发布");
//
//                return Collections.EMPTY_MAP;
//            }

            params.put("activityId", mktCampaign.getMktCampaignId().toString()); //活动编码

            privateParams.put("activityId", mktCampaign.getMktCampaignId().toString()); //活动编码
            privateParams.put("activityName", mktCampaign.getMktCampaignName()); //活动名称
            privateParams.put("activityType", mktCampaign.getMktCampaignType()); //活动类型
            privateParams.put("skipCheck", "0"); //是否校验  todo

            //es log
            esJson.put("reqId", reqId);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("activityId", mktCampaign.getMktCampaignId().toString());
            esJson.put("activityName", mktCampaign.getMktCampaignName());
            esJson.put("activityCode", mktCampaign.getMktCampaignId().toString());

            //验证活动生效时间
            Date beginTime = mktCampaign.getPlanBeginTime();
            Date endTime = mktCampaign.getPlanEndTime();
            if (now.before(beginTime) || now.after(endTime)) {
                //当前时间不在活动生效时间内
                esJson.put("hit", "false");
                esJson.put("msg", "当前时间不在活动生效时间内");
                esService.save(esJson, IndexList.ACTIVITY_MODULE);

                return Collections.EMPTY_MAP;
            }

            List<Map<String, Object>> itgTriggers = new ArrayList<>();
            Map<String, Object> itgTrigger = new HashMap<>();
//            Map<String, String> queryFields = new HashMap<>();
            StringBuilder querySb = new StringBuilder();
//            int i = 0;

            //查询展示列 （试算）
//            List<Label> calcDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getCalcDisplay());
//            //格式化返回参数结构
//            if (calcDisplay != null) {
//                for (Label label : calcDisplay) {
////                    queryFields.put(String.valueOf(i), label.getInjectionLabelCode());
////                    i++;
//                    querySb.append(label.getInjectionLabelCode()).append(",");
//                }
//            }
            //查询展示列 （iSale）
            List<Label> iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(mktCampaign.getIsaleDisplay());
            if (iSaleDisplay != null && iSaleDisplay.size() > 0) {
                for (Label label : iSaleDisplay) {
//                    queryFields.put(String.valueOf(i), label.getInjectionLabelCode());
//                    i++;
                    querySb.append(label.getInjectionLabelCode()).append(",");
                }

                if (querySb.length() > 0) {
                    querySb.deleteCharAt(querySb.length() - 1);
                }

                JSONObject httpParams = new JSONObject();
                httpParams.put("queryNum", privateParams.get("accNbr"));
                httpParams.put("c3", params.get("lanId"));
                httpParams.put("queryId", privateParams.get("integrationId"));
                //查询标签列表
                httpParams.put("queryFields", querySb.toString());
                //http查询标签
                JSONObject resJson = getLabelByPost(httpParams);

                System.out.println(resJson.toString());

                Map<String, Object> triggers = new HashMap<>();
                List<Map<String, Object>> triggerList = new ArrayList<>();

                triggers = new HashMap<>();
                triggerList = new ArrayList<>();

//            if (calcDisplay != null) {
//                for (Label label : calcDisplay) {
//                    if (resJson.containsKey(label.getInjectionLabelCode())) {
//                        triggers.put("key", label.getInjectionLabelCode());
//                        triggers.put("value", resJson.get(label.getInjectionLabelCode()));
//                        triggers.put("display", 0); //todo 确定display字段
//                        triggers.put("name", label.getInjectionLabelName());
//                    }
//
//                    triggerList.add(triggers);
//                }
//                itgTriggers.put("triggerList", triggerList);
//                itgTriggers.put("type", 1);
//            }

                if (iSaleDisplay != null) {
                    for (Label label : iSaleDisplay) {
                        if (resJson.containsKey(label.getInjectionLabelCode())) {
                            triggers.put("key", label.getInjectionLabelCode());
                            triggers.put("value", resJson.get(label.getInjectionLabelCode()));
                            triggers.put("display", 0); //todo 确定display字段
                            triggers.put("name", label.getInjectionLabelName());
                        }
                    }

                    triggerList.add(triggers);

                    itgTrigger.put("triggerList", triggerList);
                    itgTrigger.put("type", "营销信息");
                }

                itgTriggers.add(itgTrigger);
            }

            //根据活动id获取策略列表
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            //遍历策略列表
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {
                //提交线程
                Future<Map<String, Object>> f = executorService.submit(
                        new StrategyTask(params, mktCamStrategyConfRelDO.getStrategyConfId(), privateParams, labelItems, itgTriggers, evtTriggers));
                //将线程处理结果添加到结果集
                threadList.add(f);
            }
            //获取结果
            try {
                for (Future<Map<String, Object>> future : threadList) {
                    if (!future.get().isEmpty()) {
                        strageyList.addAll((List<Map<String, Object>>) future.get().get("ruleList"));
                    }
                }
                activity.put("strategyList", strageyList);

                //当前时间不在活动生效时间内
                if (strageyList.size() > 0) {
                    esJson.put("hit", "true");
                } else {
                    esJson.put("hit", "false");
                }

                esService.save(esJson, IndexList.ACTIVITY_MODULE);

            } catch (Exception e) {
                e.printStackTrace();

                //发生异常关闭线程池
                executorService.shutdown();
            }
            //关闭线程池
            executorService.shutdown();
            return activity;
        }
    }

    /**
     * 获取返回规则列表
     */
    class StrategyTask implements Callable<Map<String, Object>> {
        private Long strategyConfId; //策略配置id
        private String reqId;
        private Map<String, String> params; //公共参数
        private Map<String, String> privateParams;  //私有参数
        private Map<String, String> labelItems;  //事件采集项标签
        private List<Map<String, Object>> itgTriggers;  //试算展示列
        private List<Map<String, Object>> evtTriggers;  //试算展示列


        public StrategyTask(Map<String, String> params, Long strategyConfId, Map<String, String> privateParams, Map<String, String> labelItems,
                            List<Map<String, Object>> itgTriggers, List<Map<String, Object>> evtTriggers) {
            this.strategyConfId = strategyConfId;
            this.reqId = params.get("reqId");
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
            this.itgTriggers = itgTriggers;
            this.evtTriggers = evtTriggers;

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
            MktStrategyConfDO mktStrategyConf = mktStrategyConfMapper.selectByPrimaryKey(strategyConfId);
            // 获取策略名称
            String strategyConfName = mktStrategyConf.getMktStrategyConfName();
            strategyMap.put("mktStrategyConfName", strategyConfName);

            //es log
            esJson.put("activityId", params.get("activityId"));
            esJson.put("strategyConfId", strategyConfId);
            esJson.put("strategyConfName", strategyConfName);
            esJson.put("eventId", params.get("eventCode"));
            esJson.put("reqId", reqId);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));

            //验证策略生效时间
            if (!(now.after(mktStrategyConf.getBeginTime()) && now.before(mktStrategyConf.getEndTime()))) {
                //若当前时间在策略生效时间外
                esJson.put("hit", "false");
                esJson.put("msg", "当前时间在策略生效时间外");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }
            //下发地市校验
//            if (mktStrategyConf.getAreaId() != null && !"".equals(mktStrategyConf.getAreaId())) {
//                String[] strArrayCity = mktStrategyConf.getAreaId().split("/");
//                boolean areaCheck = true;
//                for (String str : strArrayCity) {
//                    String lanId = params.get("lanId");
//                    if (lanId != null) {
//                        if (lanId.equals(str)) {
//                            areaCheck = false;
//                            break;
//                        }
//                    } else {
//                        //下发地址获取异常 lanId
//                        strategyMap.put("msg", "下发地址获取异常");
//
//                        esJson.put("hit", "false");
//                        esJson.put("msg", "下发地址获取异常");
//                        esService.save(esJson, IndexList.STRATEGY_MODULE);
//                        return Collections.EMPTY_MAP;
//                    }
//                }
//
//                if (areaCheck) {
//                    strategyMap.put("msg", "下发地址不符");
//
//                    esJson.put("hit", "false");
//                    esJson.put("msg", "下发地址不符");
//                    esService.save(esJson, IndexList.STRATEGY_MODULE);
//                    return Collections.EMPTY_MAP;
//                }
//            } else {
//                //下发地市数据异常
//                strategyMap.put("msg", "下发地市数据异常");
//
//                esJson.put("hit", "false");
//                esJson.put("msg", "下发地市数据异常");
//                esService.save(esJson, IndexList.STRATEGY_MODULE);
//                return Collections.EMPTY_MAP;
//            }
            //判断下发渠道
            if (mktStrategyConf.getChannelsId() != null && !"".equals(mktStrategyConf.getChannelsId())) {
                String[] strArrayChannelsId = mktStrategyConf.getChannelsId().split("/");
                boolean channelCheck = true;
                for (String str : strArrayChannelsId) {

                    Channel channel = contactChannelMapper.selectByPrimaryKey(Long.parseLong(str));
                    String channelId = params.get("channelCode");
                    if (channelId != null) {
                        if (channel != null) {
                            if (channelId.equals(channel.getContactChlCode())) {
                                channelCheck = false;
                                break;
                            }
                        } else {
                            //下发地址获取异常 lanId
                            strategyMap.put("msg", "下发渠道获取异常");
                            esJson.put("hit", "false");
                            esJson.put("msg", "下发渠道获取异常");
                            esService.save(esJson, IndexList.STRATEGY_MODULE);
                            return Collections.EMPTY_MAP;
                        }
                    } else {
                        //下发地址获取异常 lanId
                        strategyMap.put("msg", "下发渠道获取异常");
                        esJson.put("hit", "false");
                        esJson.put("msg", "下发渠道获取异常");
                        esService.save(esJson, IndexList.STRATEGY_MODULE);
                        return Collections.EMPTY_MAP;
                    }
                }

                if (channelCheck) {
                    strategyMap.put("msg", "下发渠道不符");
                    esJson.put("hit", "false");
                    esJson.put("msg", "下发渠道不符");
                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }
            } else {
                //下发地市数据异常
                strategyMap.put("msg", "下发渠道数据异常");
                esJson.put("hit", "false");
                esJson.put("msg", "下发渠道数据异常");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }

            //验证过滤规则
            List<Long> filterRuleIds = mktStrategyFilterRuleRelMapper.selectByStrategyId(strategyConfId);
            if (filterRuleIds != null && filterRuleIds.size() > 0) {
                //循环并判断过滤规则
                for (Long filterRuleId : filterRuleIds) {
                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(filterRuleId);
                    //判断过滤类型(红名单，黑名单)
                    if ("1000".equals(filterRule.getFilterType()) || "2000".equals(filterRule.getFilterType())) {
                        //查询红名单黑名单列表
                        int count = userListMapper.checkRule(privateParams.get("accNbr"), filterRule.getRuleId(), filterRule.getFilterType());
                        if (count > 0) {
                            System.out.println("红黑名单过滤规则验证被拦截");
                            esJson.put("hit", "false");
                            esJson.put("msg", "红黑名单过滤规则验证被拦截");
                            esService.save(esJson, IndexList.STRATEGY_MODULE);
                            return Collections.EMPTY_MAP;
                        }
                    } else if ("3000".equals(filterRule.getFilterType())) {  //销售品过滤
                        //获取用户已办理销售品，验证互斥


                    } else if ("4000".equals(filterRule.getFilterType())) {  //表达式过滤
                        //暂不处理
                        //do something
                    } else if ("5000".equals(filterRule.getFilterType())) {  //时间段过滤
                        //时间段的格式

                        if (compareHourAndMinute(filterRule)) {
                            System.out.println("过滤时间段验证被拦截");
                            esJson.put("hit", "false");
                            esJson.put("msg", "过滤时间段验证被拦截");
                            esService.save(esJson, IndexList.STRATEGY_MODULE);
                            return Collections.EMPTY_MAP;
                        }

                    }
                }
            }

            //根据策略id获取策略下发规则列表
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
                    //过滤规则id
//                    Long ruleConfId = mktStrategyConfRuleDO.getRuleConfId();
                    Long ruleConfId = 0L;
                    //协同渠道配置id
                    String evtContactConfIdStr = mktStrategyConfRuleDO.getEvtContactConfId();
                    //规则id
                    Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();

                    String mktStrategyConfRuleName = mktStrategyConfRuleDO.getMktStrategyConfRuleName();
                    //提交线程
                    Future<Map<String, Object>> f = executorService.submit(new RuleTask(params, privateParams, strategyConfId, tarGrpId, productStr,
                            ruleConfId, evtContactConfIdStr, mktStrategyConfRuleId, mktStrategyConfRuleName, labelItems, itgTriggers, evtTriggers));
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
                // return null;
            }
            //关闭线程池
            executorService.shutdown();
            if (ruleList.size() > 0) {
                esJson.put("hit", "true");
            } else {
                esJson.put("hit", "false");
            }
            try {
                esService.save(esJson, IndexList.STRATEGY_MODULE);
            } catch (Exception e) {
                e.printStackTrace();
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
        private Long ruleConfId;
        private String evtContactConfIdStr;
        private String reqId;
        private Long ruleId;
        private String ruleName;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private Map<String, String> labelItems;
        private List<Map<String, Object>> itgTriggers;
        private List<Map<String, Object>> evtTriggers;

        public RuleTask(Map<String, String> params, Map<String, String> privateParams, Long strategyConfId, Long tarGrpId, String productStr, Long ruleConfId,
                        String evtContactConfIdStr, Long mktStrategyConfRuleId, String mktStrategyConfRuleName,
                        Map<String, String> labelItems, List<Map<String, Object>> itgTriggers, List<Map<String, Object>> evtTriggers) {
            this.strategyConfId = strategyConfId;
            this.tarGrpId = tarGrpId;
            this.reqId = params.get("reqId");
            this.productStr = productStr;
            this.ruleConfId = ruleConfId;
            this.evtContactConfIdStr = evtContactConfIdStr;
            this.ruleId = mktStrategyConfRuleId;
            this.ruleName = mktStrategyConfRuleName;
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
            this.itgTriggers = itgTriggers;
            this.evtTriggers = evtTriggers;
        }

        @Override
        public Map<String, Object> call() throws Exception {

            //初始化es log
            JSONObject esJson = new JSONObject();

            Map<String, Object> ruleMap = new HashMap<>();
            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> taskChlList = new ArrayList<>();

            //  2.判断客户分群规则---------------------------
            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
            //拼装redis key
            String key = "EVENT_RULE_" + params.get("actId") + "_" + strategyConfId + "_" + ruleId;

            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //查询标签实例数据
            //构造查询参数值
            JSONObject param = new JSONObject();
            //查询标识
            param.put("queryNum", privateParams.get("accNbr"));
            param.put("c3", params.get("lanId"));
            param.put("queryId", privateParams.get("integrationId"));
            //查询标签列表
            StringBuilder queryFieldsSb = new StringBuilder();
            //从redis获取规则使用的所有标签
            List<LabelResult> labelResultList = (List<LabelResult>) redisUtils.get(key + "_LABEL");
//            LabelResult lr;
            if (labelResultList == null || labelResultList.size() <= 0) {
                labelResultList = new ArrayList<>();
                //redis中没有，从数据库查询标签
                List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                //遍历所有分群规则
                for (int i = 1; i <= tarGrpConditionDOs.size(); i++) {
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i - 1).getLeftParam()));
//                    lr = new LabelResult();
//                    lr.setLabelCode(label.getInjectionLabelCode());
//                    lr.setLabelName(label.getInjectionLabelName());
//                    lr.setRightOperand(label.getRightOperand());
//                    lr.setOperType(label.getOperator());
//                    labelResultList.add(lr);
                    queryFieldsSb.append(label.getInjectionLabelCode()).append(",");
                }
            } else {
                //redis中获取标签
                for (int i = 1; i <= labelResultList.size(); i++) {
                    if (labelItems.containsKey(String.valueOf(i))) {
                        continue;
                    }
                    queryFieldsSb.append(labelResultList.get(i - 1).getLabelCode()).append(",");
                }
            }

            if (queryFieldsSb.length() > 0) {
                queryFieldsSb.deleteCharAt(queryFieldsSb.length() - 1);
            }

            param.put("queryFields", queryFieldsSb.toString());
            //记录参数个数
//            int paramsSize = queryFields.size();
            System.out.println("param " + param.toString());
            //更换为dubbo因子查询-----------------------------------------------------
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));
            System.out.println(dubboResult.toString());

            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                //ES log 标签实例
                esJson.put("reqId", reqId);
                esJson.put("eventId", params.get("eventCode"));
                esJson.put("activityId", params.get("activityId"));
                esJson.put("ruleId", ruleId);
                esJson.put("integrationId", params.get("integrationId"));
                esJson.put("accNbr", params.get("accNbr"));
                esJson.put("strategyConfId", strategyConfId);

                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    context.put(entry.getKey(), entry.getValue());

                    //存入es
//                    for (LabelResult labelResult : labelResultList) {
//                        if (entry.getKey().equals(labelResult.getLabelCode())) ;
//                        labelResult.setRightParam(entry.getValue().toString());
//                    }
                }

                //添加事件采集项的值到上下文
                context.putAll(labelItems);
                System.out.println("查询标签成功:" + context.toString());
            } else {
//                System.out.println("查询标签失败:" + httpResult.getString("result_msg"));
                System.out.println("查询标签失败:");

                esJson.put("hit", "false");
//                esJson.put("msg", "查询标签失败:" + httpResult.getString("result_msg"));
                esService.save(esJson, IndexList.RULE_MODULE);
                return Collections.EMPTY_MAP;
            }


            //判断参数是否有无值的 todo
//            if (context.size() != paramsSize) {
//                //有参数没有查询出实例数据
//                ruleMap.put("hit", true);
//                ruleMap.put("msg", "标签取值参数实例不足");
//                return ruleMap;
//            }

            //判断redis中是否存在
            String express = "";
            if (redisUtils.exists(key)) {
                express = (String) redisUtils.get(key);
            } else {

                LabelResult lr;

                //若redis中不存在key，则从数据库中查询并拼装表达式
                //查询分群规则list
                List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                //将规则拼装为表达式
                StringBuilder expressSb = new StringBuilder();
                expressSb.append("if(");
                //遍历所有规则
                for (int i = 0; i < tarGrpConditionDOs.size(); i++) {

                    String type = tarGrpConditionDOs.get(i).getOperType();

                    StringBuilder express1 = new StringBuilder();
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i).getLeftParam()));

                    //保存标签的es log
                    lr = new LabelResult();
                    lr.setOperType(type);
                    lr.setLabelCode(label.getInjectionLabelCode());
                    lr.setLabelName(label.getInjectionLabelName());
                    lr.setRightOperand(tarGrpConditionDOs.get(i).getRightParam());
                    lr.setClassName(label.getClassName());
                    if (context.containsKey(label.getInjectionLabelCode())) {
                        lr.setRightParam(context.get(label.getInjectionLabelCode()).toString());
                    } else {
                        lr.setRightParam("无值");
                        lr.setResult(false);
                    }

                    //拼接表达式
                    if ("7100".equals(type)) {
                        expressSb.append("!");
                    }
                    expressSb.append("((");

                    express1.append("if(");
                    express1.append("(");

                    expressSb.append(label.getInjectionLabelCode()).append(")");
                    express1.append(label.getInjectionLabelCode()).append(")");
                    if ("1000".equals(type)) {
                        expressSb.append(" > ");
                        express1.append(" > ");
                    } else if ("2000".equals(type)) {
                        expressSb.append(" < ");
                        express1.append(" < ");
                    } else if ("3000".equals(type)) {
                        expressSb.append(" == ");
                        express1.append(" == ");
                    } else if ("4000".equals(type)) {
                        expressSb.append(" != ");
                        express1.append(" != ");
                    } else if ("5000".equals(type)) {
                        expressSb.append(" >= ");
                        express1.append(" >= ");
                    } else if ("6000".equals(type)) {
                        expressSb.append(" <= ");
                        express1.append(" <= ");
                    } else if ("7000".equals(type) || "7100".equals(type)) {
                        expressSb.append(" in ");
                        express1.append(" in ");
                    } else if ("7200".equals(type)) {

                        String[] strArray = tarGrpConditionDOs.get(i).getRightParam().split(",");

//                        expressSb.append(" >= ").append("\"").append(strArray[0]).append("\"").append(")");
//                        expressSb.append(" && ").append("((");
//                        expressSb.append(label.getInjectionLabelCode()).append(")");
//                        expressSb.append(" <= ").append("\"").append(strArray[1]).append("\"");

                        expressSb.append(" >= ").append(strArray[0]);
                        expressSb.append(" && ").append("(");
                        expressSb.append(label.getInjectionLabelCode()).append(")");
                        expressSb.append(" <= ").append(strArray[1]);

                        express1.append(" >= ").append("\"").append(strArray[0]).append("\"");
                        express1.append(" && ").append("(");
                        express1.append(label.getInjectionLabelCode()).append(")");
                        express1.append(" <= ").append("\"").append(strArray[1]).append("\"");

                    }

                    if ("7000".equals(type) || "7100".equals(type)) {
                        String[] strArray = tarGrpConditionDOs.get(i).getRightParam().split(",");
                        expressSb.append("(");
                        express1.append("(");
                        for (int j = 0; j < strArray.length; j++) {
                            expressSb.append("\"").append(strArray[j]).append("\"");
                            express1.append("\"").append(strArray[j]).append("\"");
                            if (j != strArray.length - 1) {
                                expressSb.append(",");
                                express1.append(",");
                            }
                        }
                        expressSb.append(")");
                        express1.append(")");
                    } else if ("7200".equals(type)) {
                        //do nothing...
                    } else {
                        expressSb.append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"");
                        express1.append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"");
                    }

                    expressSb.append(")");
                    express1.append(") {return true} else {return false}");
                    System.out.println(express1.toString());

                    try {
                        RuleResult ruleResult1 = runner.executeRule(express1.toString(), context, true, true);

                        if (null != ruleResult1.getResult()) {
                            lr.setResult((Boolean) ruleResult1.getResult());
                        } else {
                            lr.setResult(false);
                        }

//                        for (LabelResult labelResult : labelResultList) {
//                            if (label.getInjectionLabelCode().equals(labelResult.getLabelCode())) {
//
//                                if (null != ruleResult1.getResult()) {
//                                    labelResult.setResult((Boolean) ruleResult1.getResult());
//                                } else {
//                                    labelResult.setResult(false);
//                                }
//
//                            }
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("单个标签判断出错");
                    }


                    if (i + 1 != tarGrpConditionDOs.size()) {
                        expressSb.append("&&");
                    }

                    labelResultList.add(lr);
                }
                expressSb.append(") {return true} else {return false}");
                express = expressSb.toString();
            }

            esJson.put("labelResultList", JSONArray.toJSON(labelResultList));

            try {
                //规则引擎计算
                System.out.println(express);
                RuleResult ruleResult = null;
                System.out.println(context.toString());
                ExpressRunner runnerQ = new ExpressRunner();
                try {
                    ruleResult = runnerQ.executeRule(express, context, true, true);
//                    ruleResult = runnerQ.executeRule("if((\"130\") >= \"99\" && (\"130\") <= \"169\") {return true} else {return false}", context, false, true);

                } catch (Exception e) {
                    e.printStackTrace();

                    ruleMap.put("msg", "规则引擎计算失败");

                    esJson.put("hit", "false");
                    esJson.put("msg", "规则引擎计算失败");
                    esService.save(esJson, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
                }

                System.out.println("result=" + ruleResult.getResult());
                System.out.println("Tree=" + ruleResult.getRule().toTree());
                System.out.println("TraceMap=" + ruleResult.getTraceMap());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("express", express);
                jsonObject.put("reqId", reqId);
                jsonObject.put("eventId", params.get("eventCode"));
                jsonObject.put("activityId", params.get("activityId"));
                jsonObject.put("ruleConfId", ruleConfId);
                jsonObject.put("strategyConfId", strategyConfId);
                jsonObject.put("ruleId", ruleId);
                jsonObject.put("ruleName", ruleName);
                jsonObject.put("productStr", productStr);
                jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
                jsonObject.put("tarGrpId", tarGrpId);


                //初始化返回结果中的销售品条目
                List<Map<String, String>> productList = new ArrayList<>();
                if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {

                    jsonObject.put("hit", true);

                    //拼接返回结果
                    ruleMap.put("orderISI", params.get("reqId")); //流水号
                    ruleMap.put("activityId", privateParams.get("activityId")); //活动编码
                    ruleMap.put("activityName", privateParams.get("activityName")); //活动名称
                    ruleMap.put("skipCheck", "0"); //调过预校验 todo
                    ruleMap.put("orderPriority", privateParams.get("orderPriority")); //活动优先级
                    ruleMap.put("integrationId", privateParams.get("integrationId")); //集成编号（必填）
                    ruleMap.put("accNbr", privateParams.get("accNbr")); //业务号码（必填）
                    ruleMap.put("policyId", strategyConfId.toString()); //策略编码
                    ruleMap.put("ruleId", ruleId.toString()); //规则编码
                    ruleMap.put("triggers", evtTriggers); //规则编码

                    //查询销售品列表
                    if (productStr != null && !"".equals(productStr)) {
                        String[] productArray = productStr.split("/");
                        for (String str : productArray) {
                            Map<String, String> product = new HashMap<>();
                            MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.parseLong(str));
                            product.put("productCode", mktCamItem.getOfferCode());
                            product.put("productName", mktCamItem.getOfferName());
                            product.put("productType", mktCamItem.getItemType());
                            product.put("productFlag", "销售品标签");  //todo 销售品标签
                            //销售品优先级
                            if (mktCamItem.getPriority() != null) {
                                product.put("productPriority", mktCamItem.getPriority().toString());
                            } else {
                                product.put("productPriority", "0");
                            }
                            System.out.println("*********************product --->>>" + JSON.toJSON(product));
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
                    //遍历协同渠道
                    for (String str : evtContactConfIdArray) {
                        //协同渠道规则表id（自建表）
                        Long evtContactConfId = Long.parseLong(str);
                        //提交线程
                        Future<Map<String, Object>> f = executorService.submit(new ChannelTask(params, evtContactConfId, productList, privateParams, itgTriggers));
                        //将线程处理结果添加到结果集
                        threadList.add(f);
                    }
                    //获取结果
                    try {
                        for (Future<Map<String, Object>> future : threadList) {
                            if (!future.get().isEmpty()) {
                                taskChlList.add(future.get());
                            }
                        }
                        ruleMap.put("taskChlList", taskChlList);
                        jsonObject.put("hit", true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //发生异常关闭线程池
                        executorService.shutdown();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //发生异常关闭线程池
                        executorService.shutdown();
                    }
                    //关闭线程池
                    executorService.shutdown();
                } else {

                    ruleMap.put("msg", "规则引擎匹配未通过");

                    jsonObject.put("hit", "false");
                    jsonObject.put("msg", "规则引擎匹配未通过");
                    esService.save(esJson, IndexList.RULE_MODULE);
                    return Collections.EMPTY_MAP;
                }

                esService.save(jsonObject, IndexList.RULE_MODULE);
            } catch (Exception e) {
                e.printStackTrace();

                //todo 异常处理

            }

            esService.save(esJson, IndexList.Label_MODULE);

            return ruleMap;
        }

    }

    class ChannelTask implements Callable<Map<String, Object>> {

        //策略配置id
        private Long evtContactConfId;

        private List<Map<String, String>> productList;
        private Map<String, String> params;
        private Map<String, String> privateParams;
        private List<Map<String, Object>> itgTriggers;

        public ChannelTask(Map<String, String> params, Long evtContactConfId, List<Map<String, String>> productList,
                           Map<String, String> privateParams, List<Map<String, Object>> itgTriggers) {
            this.evtContactConfId = evtContactConfId;
            this.productList = productList;
            this.params = params;
            this.privateParams = privateParams;
            this.itgTriggers = itgTriggers;
        }

        @Override
        public Map<String, Object> call() {
            Date now = new Date();

            //初始化返回结果推荐信息
            Map<String, Object> channel = new HashMap<>();

            List<Map<String, Object>> taskChlAttrList = new ArrayList<>();
            Map<String, Object> taskChlAttr;

            //查询渠道属性，渠道生失效时间过滤
            List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
            boolean checkTime = true;
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrs) {

                //渠道属性数据返回给协同中心
                if (mktCamChlConfAttrDO.getAttrId() == 500600010001L
                        || mktCamChlConfAttrDO.getAttrId() == 500600010002L
                        || mktCamChlConfAttrDO.getAttrId() == 500600010003L
                        || mktCamChlConfAttrDO.getAttrId() == 500600010004L
                        || mktCamChlConfAttrDO.getAttrId() == 500600010011L) {
                    taskChlAttr = new HashMap<>();
                    taskChlAttr.put("attrId", mktCamChlConfAttrDO.getAttrId());
                    taskChlAttr.put("attrKey", mktCamChlConfAttrDO.getAttrId());  //todo 编码
                    taskChlAttr.put("attrValue", mktCamChlConfAttrDO.getAttrValue());
                    taskChlAttrList.add(taskChlAttr);
                }

                //判断渠道生失效时间
                if (mktCamChlConfAttrDO.getAttrId() == 500600010006L) {
                    if (!now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                        checkTime = false;
                    }
                }
                if (mktCamChlConfAttrDO.getAttrId() == 500600010007L) {
                    if (now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                        checkTime = false;
                    }
                }

                //获取调查问卷ID
                if (mktCamChlConfAttrDO.getAttrId() == 500600010010L) {
                    //调查问卷
                    channel.put("questionId", mktCamChlConfAttrDO.getAttrValue());
                }

                //获取推荐账号(如果有)
                if (mktCamChlConfAttrDO.getAttrId() == 500600010005L) {
                    channel.put("contactAccount", mktCamChlConfAttrDO.getAttrValue());
                }

            }

            channel.put("taskChlAttrList", taskChlAttrList);

            if (!checkTime) {
                return Collections.EMPTY_MAP;
            }

            //查询渠道信息基本信息
            MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

            //渠道级别信息
            Channel channelMessage = contactChannelMapper.selectByPrimaryKey(mktCamChlConf.getContactChlId());
            channel.put("channelId", channelMessage.getContactChlCode());
//            channel.put("channelId", mktCamChlConf.getContactChlId());
            //查询渠道id
            channel.put("channelConfId", mktCamChlConf.getContactChlId()); //执行渠道推送配置标识(MKT_CAM_CHL_CONF表主键) （必填）
            channel.put("pushType", mktCamChlConf.getPushType()); //推送类型
            channel.put("pushTime", ""); // 推送时间

            //返回结果中添加销售品信息
            channel.put("productList", productList);

            //查询渠道子策略 这里老系统暂时不返回
//              List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

            //查询脚本
            CamScript camScript = mktCamScriptMapper.selectByConfId(evtContactConfId);
            if (camScript != null) {
                //返回结果中添加脚本信息
                channel.put("contactScript", camScript.getScriptDesc());
            }
            //查询话术
            List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(evtContactConfId);
            if (mktVerbals != null && mktVerbals.size() > 0) {
                for (MktVerbal mktVerbal : mktVerbals) {
                    //查询话术规则
                    List<MktVerbalCondition> channelConditionList = mktVerbalConditionMapper.findChannelConditionListByVerbalId(mktVerbal.getVerbalId());
                    //todo 格式化话术规则 如何返回  可能需要判断规则
                }
            }
            if (mktVerbals != null && mktVerbals.size() > 0) {
                //返回结果中添加话术信息
                channel.put("reason", mktVerbals.get(0).getScriptDesc());
            }

            channel.put("itgTriggers", itgTriggers);

            return channel;
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

        //调用计算方法
        result = new EventTask().call(params);

        return result;

    }


    /**
     * 二次协同
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> secondChannelSynergy(Map<String, Object> map) {

        //初始化返回结果
        Map<String, Object> result = new HashMap();

        // 解析掺入参数'
        String activityId = (String) map.get("activityId");
        String ruleId = (String) map.get("ruleId");
        String resultNbr = (String) map.get("resultNbr");
        String accNbr = (String) map.get("accNbr");
        String integrationId = (String) map.get("integrationId");
        String custId = (String) map.get("custId");


        return result;
    }

    private JSONObject getLabelByPost(JSONObject param) {
        //查询标签实例数据
        System.out.println("param " + param.toString());

        Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(param));

        System.out.println(dubboResult.toString());

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
        Boolean result = true;

        Calendar start = Calendar.getInstance();
        start.setTime(filterRule.getDayStart());
        Calendar end = Calendar.getInstance();
        end.setTime(filterRule.getDayEnd());
        Calendar cal = Calendar.getInstance();
        int nowHour = cal.get(Calendar.HOUR_OF_DAY);
        if (nowHour > start.get(Calendar.HOUR_OF_DAY) && nowHour < end.get(Calendar.HOUR_OF_DAY)) {
            if (nowHour > start.get(Calendar.MINUTE) && nowHour < end.get(Calendar.MINUTE)) {
                if (nowHour > start.get(Calendar.SECOND) && nowHour < end.get(Calendar.SECOND)) {
                    result = false;
                }
            }
        }

        return result;
    }


}
