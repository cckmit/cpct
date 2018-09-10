package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.rule.RuleResult;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import com.zjtelcom.cpct.dubbo.task.RuleTask;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsService;
import com.zjtelcom.cpct.util.CollectionUtils;
import com.zjtelcom.cpct.util.HttpUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
//@Transactional
public class EventApiServiceImpl implements EventApiService {

    @Autowired(required = false)
    private ContactEvtMapper contactEvtMapper; //事件总表

    @Autowired(required = false)
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表

    @Autowired(required = false)
    private MktCampaignMapper mktCampaignMapper; //活动基本信息

    @Autowired(required = false)
    private UserListMapper userListMapper; //过滤规则（红名单、黑名单数据）

    @Autowired(required = false)
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表

    @Autowired(required = false)
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper; //活动策略关联

    @Autowired(required = false)
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息

    @Autowired(required = false)
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;//策略规则

    @Autowired(required = false)
    private FilterRuleMapper filterRuleMapper; //过滤规则

    @Autowired(required = false)
    private PpmProductMapper ppmProductMapper; //销售品

    @Autowired(required = false)
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper; //协同渠道配置基本信息

    @Autowired(required = false)
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道

    @Autowired(required = false)
    private MktVerbalConditionMapper mktVerbalConditionMapper; //规则存储公共表（此处查询协同渠道子策略规则和话术规则）

    @Autowired(required = false)
    private MktCamScriptMapper mktCamScriptMapper; //营销脚本

    @Autowired(required = false)
    private MktVerbalMapper mktVerbalMapper; //话术

    @Autowired(required = false)
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired(required = false)
    private EsService esService;  //es存储

    @Autowired(required = false)
    private RedisUtils redisUtils;  // redis方法

    @Autowired(required = false)
    private ContactEvtItemMapper contactEvtItemMapper;  // 事件采集项


    @Override
    @SuppressWarnings("unchecked")
    public Map deal(Map<String, Object> map) {

        //初始化返回结果
        Map<String, Object> result = new HashMap();

        final Map<String, String> params = new HashMap<>();

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


        result.put("reqId", reqId);
        result.put("resultCode", "1");
        result.put("resultMsg", "success");

        return result;
    }


    /**
     * 事件触发服务
     *
     * @param map
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map CalculateCPC(Map<String, String> map) {
//    public Map CalculateCPC(Map<String, Object> map) {
        long begin = System.currentTimeMillis();

        Map<String, String> params = map;
//        Map<String, String> params = new HashMap<>();

        //初始化返回结果
        Map<String, Object> result = new HashMap();

        //初始化es log
        JSONObject esJson = new JSONObject();

//        //事件传入参数 开始--------------------
//        //获取事件code（必填）
//        String eventCode = (String) map.get("eventCode");
//        //获取渠道编码（必填）
//        String channelCode = (String) map.get("channelCode");
//        //本地网
//        String lanId = (String) map.get("lanId");
//        //业务号码（必填）
//        String accNbr = (String) map.get("accNbr");
//        //集成编号（必填）
//        String integrationId = (String) map.get("integrationId");
//        //客户编码（必填）
//        String custId = (String) map.get("custId");
//        //销售员编码（必填）
//        String reqId = (String) map.get("reqId");
//        //采集时间(yyyy-mm-dd hh24:mm:ss)
//        String evtCollectTime = (String) map.get("evtCollectTime");
//        //自定义参数集合json字符串
//        String evtContent = (String) map.get("evtContent");
//        //事件传入参数 结束--------------------
//
//        //构造下级线程使用参数
//        params.put("eventCode", eventCode); //事件编码
//        params.put("channelCode", channelCode); //渠道编码
//        params.put("lanId", lanId); //本地网
//        params.put("custId", custId); //客户编码
//        params.put("reqId", reqId); //流水号
//        params.put("evtCollectTime", evtCollectTime); //事件触发时间
//        params.put("evtContent", evtContent); //事件采集项


        String custId = params.get("custId");
        //构造返回结果
        result.put("reqId", params.get("reqId"));
        result.put("custId", custId);


        //事件验证开始↓↓↓↓↓↓↓↓↓↓↓↓↓
        //解析事件采集项
        JSONObject evtParams = JSONObject.parseObject(params.get("evtContent"));
        //根据事件code查询事件信息
        ContactEvt event = contactEvtMapper.getEventByEventNbr(params.get("eventCode"));
        //获取事件id
        Long eventId = event.getContactEvtId();

        //es log
        esJson.put("reqId", params.get("reqId"));
        esJson.put("eventId", eventId);
        esJson.put("eventCode", params.get("eventCode"));
        esJson.put("integrationId", params.get("integrationId"));
        esJson.put("accNbr", params.get("accNbr"));
        esJson.put("custId", custId);

        //验证事件采集项
        List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(eventId);

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
            } else {
                //记录缺少的事件采集项
                stringBuilder.append(contactEvtItem.getEvtItemCode()).append("、");
            }
        }

        if (stringBuilder.length() > 0) {

            //保存es log
            long cost = System.currentTimeMillis() - begin;
            esJson.put("timeCost", cost);
            esJson.put("msg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
            esService.save(esJson, IndexList.EVENT_MODULE);

            result.put("resultCode", "1000");
            result.put("resultMsg", "事件采集项验证失败，缺少：" + stringBuilder.toString());
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
        if (recCampaignAmount != 0) {
            //事件推荐活动数
            if (mktCamEvtRelDOs.size() > recCampaignAmount) {
                max = recCampaignAmount;
            }
        }
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
                    esJson.put("activityId", camEvtRelDO.getMktCampaignId());
                    esJson.put("msg", "客户级活动，事件采集项未包含客户编码");
                    esService.save(esJson, IndexList.EVENT_MODULE);

                    //事件采集项没有客户编码
                    result.put("result", false);
                    result.put("msg", "采集项未包含客户编码");
                    return result;
                }

                //根据客户编码查询所有资产
                String httpResultStr;
                String url = "http://134.96.216.155:8111/in"; //资产查询
                //构造查询参数值
                JSONObject param = new JSONObject();
                //查询标识
                param.put("queryNum", params.get("accNbr"));
                param.put("c3", params.get("lanId"));
                param.put("queryId", params.get("integrationId"));
                Map<String, String> queryFields = new HashMap<>();
                queryFields.put("1", "ASSET_INTEG_ID");
                param.put("queryFields", queryFields);

                String paramStr = param.toString();
                System.out.println("param " + param.toString());

                //验证post回调结果
                httpResultStr = HttpUtil.post(url, paramStr);
                if (httpResultStr == null || "".equals(httpResultStr)) {

                    System.out.println("客户级资产查询出错");

//                    esJson.put("hit", "false");
//                    esJson.put("msg", "客户级资产查询出错");
//                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }
                //解析返回结果
                JSONObject httpResult = JSONObject.parseObject(httpResultStr);
                //获取客户下所有资产
                JSONArray accArray = new JSONArray();
                if (httpResult.getString("result_code") != null
                        && "0".equals(httpResult.getString("result_code"))) {
                    accArray = httpResult.getJSONArray("msgbody");
                } else {
                    System.out.println("客户级资产查询出错: " + (httpResult.getString("result_msg")));
                    return Collections.EMPTY_MAP;
                }

                for (Object o : accArray) {
                    //资产级
                    Map<String, String> privateParams = new HashMap<>();
                    privateParams.put("isCust", "0");
                    privateParams.put("accNbr", ((JSONObject) o).getString("ACC_NBR"));
                    privateParams.put("integrationId", ((JSONObject) o).getString("ASSET_INTEG_ID"));
                    //活动优先级为空的时候默认0
                    privateParams.put("orderPriority", camEvtRelDO.getCampaignSeq() == null ? "0" : camEvtRelDO.getCampaignSeq().toString());
                    Future<Map<String, Object>> f = executorService.submit(new ActivityTask(params, camEvtRelDO.getMktCampaignId(), privateParams, labelItems));
                    //将线程处理结果添加到结果集
                    threadList.add(f);
                }

            } else {
                Map<String, String> privateParams = new HashMap<>();
                privateParams.put("isCust", "1"); //是否是客户级
                privateParams.put("accNbr", params.get("accNbr"));
                privateParams.put("integrationId", params.get("integrationId"));
                privateParams.put("orderPriority", camEvtRelDO.getCampaignSeq().toString());
                //资产级
                Future<Map<String, Object>> f = executorService.submit(new ActivityTask(params, camEvtRelDO.getMktCampaignId(), privateParams, labelItems));
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

        //es log
        long cost = System.currentTimeMillis() - begin;
        esJson.put("timeCost", cost);
        esJson.put("orderList", activityList);
        esJson.put("msg", "客户级活动，事件采集项未包含客户编码");
        esService.save(esJson, IndexList.EVENT_MODULE);

        //构造返回参数
        result.put("CPCResultCode", "1");
        result.put("CPCResultMsg", "success");
        result.put("reqId", params.get("reqId"));
        result.put("custId", custId);

        //返回结果
        result.put("taskList", activityList); //协同回调结果

        System.out.println(result.toString());

        //调用协同中心回调接口


        return result;
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

        public ActivityTask(Map<String, String> params, Long activityId, Map<String, String> privateParams, Map<String, String> labelItems) {
            this.activityId = activityId;
            this.params = params;
            this.privateParams = privateParams;
            this.labelItems = labelItems;
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
            if (!"2002".equals(mktCampaign.getStatusCd())) {
                esJson.put("hit", "false");
                esJson.put("msg", "活动状态未发布");
                esService.save(esJson, IndexList.ACTIVITY_MODULE);

                return Collections.EMPTY_MAP;
            }

            privateParams.put("activityId", mktCampaign.getMktActivityNbr()); //活动编码
            privateParams.put("activityName", mktCampaign.getMktCampaignName()); //活动名称
            privateParams.put("activityType", mktCampaign.getMktCampaignType()); //活动类型

            //es log
            esJson.put("orderId", reqId);
            esJson.put("integrationId", params.get("integrationId"));
            esJson.put("accNbr", params.get("accNbr"));
            esJson.put("activityId", mktCampaign.getMktCampaignId().toString());
            esJson.put("activityName", mktCampaign.getMktCampaignName());
            esJson.put("activityCode", mktCampaign.getMktActivityNbr());

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

            //根据活动id获取策略列表
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOs = mktCamStrategyConfRelMapper.selectByMktCampaignId(activityId);

            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            //初始化线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            //遍历策略列表
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOs) {
                //提交线程
                Future<Map<String, Object>> f = executorService.submit(new StrategyTask(params, mktCamStrategyConfRelDO.getStrategyConfId(), privateParams, labelItems));
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
                esJson.put("strategyList", activity.get("strategyList"));
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
                    String channelId = params.get("channelCode");
                    if (channelId != null) {
                        if (channelId.equals(str)) {
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
                    Future<Map<String, Object>> f = executorService.submit(new RuleTask(params, privateParams, strategyConfId, tarGrpId, productStr, ruleConfId, evtContactConfIdStr, mktStrategyConfRuleId, mktStrategyConfRuleName, labelItems));
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
            esJson.put("ruleList", ruleList);
            if (ruleList.size() > 0) {
                esJson.put("hit", "true");
            } else {
                esJson.put("hit", "false");
            }
            esService.save(esJson, IndexList.STRATEGY_MODULE);
            return strategyMap;
        }
    }

    /**
     * 获取规则列表
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

        public RuleTask(Map<String, String> params, Map<String, String> privateParams, Long strategyConfId, Long tarGrpId, String productStr,
                        Long ruleConfId, String evtContactConfIdStr, Long mktStrategyConfRuleId, String mktStrategyConfRuleName, Map<String, String> labelItems) {
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
        }

        @Override
        public Map<String, Object> call() throws Exception {

            //初始化es log
            JSONObject esJson = new JSONObject();

            Map<String, Object> ruleMap = new HashMap<>();
            //初始化返回结果中的推荐信息列表
            List<Map<String, Object>> taskChlList = new ArrayList<>();
            //  1.判断过滤规则---------------------------
            //获取过滤规则
//            FilterRuleConfDO filterRuleConfDO = filterRuleConfMapper.selectByPrimaryKey(ruleConfId);
//            String ruleConfIdStr = filterRuleConfDO.getFilterRuleIds();
//            if (ruleConfIdStr != null) {
//                String[] array = ruleConfIdStr.split(",");
//                boolean ruleFilter = true;
//                for (String str : array) {
//                    //获取具体规则
//                    FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.parseLong(str));
//
//                    //匹配事件过滤规则
//                    int flag = 0;
//                    if (filterRule != null) {
//                        flag = userListMapper.checkRule("", filterRule.getRuleId(), null);
//                        if (flag > 0) {
//                            ruleFilter = false;
//                        }
//                    }
//                }
//                //若存在不符合的规则 结束当前规则循环
//                if (!ruleFilter) {
//                    return Collections.EMPTY_MAP;
//                }
//            }
            //  2.判断客户分群规则---------------------------
            //判断匹配结果，如匹配则向下进行，如不匹配则continue结束本次循环
            //拼装redis key
            String key = "EVENT_RULE_" + params.get("actId") + "_" + strategyConfId + "_" + ruleId;

            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

            //查询标签实例数据
            String httpResultStr;
            String url = "http://134.96.216.156:8110/in"; //标签查询地址
            //构造查询参数值
            JSONObject param = new JSONObject();
            //查询标识
            param.put("queryNum", privateParams.get("accNbr"));
            param.put("c3", params.get("lanId"));
            param.put("queryId", privateParams.get("integrationId"));
            //查询标签列表
            Map<String, String> queryFields = new HashMap<>();
            //从redis获取规则使用的所有标签
            List<LabelResult> labelResultList = (List<LabelResult>) redisUtils.get(key + "_LABEL");
            LabelResult lr;
            if (labelResultList == null || labelResultList.size() <= 0) {
                labelResultList = new ArrayList<>();
                //redis中没有，从数据库查询标签
                List<TarGrpCondition> tarGrpConditionDOs = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                //遍历所有分群规则
                for (int i = 1; i <= tarGrpConditionDOs.size(); i++) {
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(tarGrpConditionDOs.get(i - 1).getLeftParam()));
                    lr = new LabelResult();
                    lr.setLabelCode(label.getInjectionLabelCode());
                    lr.setLabelName(label.getInjectionLabelName());
                    lr.setRightOperand(label.getOperator());
                    lr.setRightParam(label.getRightOperand());
                    labelResultList.add(lr);
                    queryFields.put(String.valueOf(i), label.getInjectionLabelCode());
                }
            } else {
                //redis中获取标签
                for (int i = 1; i <= labelResultList.size(); i++) {
                    if (labelItems.containsKey(String.valueOf(i))) {
                        continue;
                    }
                    queryFields.put(String.valueOf(i), labelResultList.get(i - 1).getLabelCode());
                }
            }
            param.put("queryFields", queryFields);
            //记录参数个数
            int paramsSize = queryFields.size();

            String paramStr = param.toString();
            System.out.println("param " + param.toString());
            //验证post回调结果
            httpResultStr = HttpUtil.post(url, paramStr);
            if (httpResultStr == null || "".equals(httpResultStr)) {

                System.out.println("查询标签出错");

                esJson.put("hit", "false");
                esJson.put("msg", "标签实例查询出错");
                esService.save(esJson, IndexList.STRATEGY_MODULE);
                return Collections.EMPTY_MAP;
            }
            JSONObject jsonobj = new JSONObject();
            //解析返回结果
            JSONObject httpResult = JSONObject.parseObject(httpResultStr);
            if (httpResult.getInteger("result_code") == 0) {
                JSONObject body = httpResult.getJSONObject("msgbody");
                //ES log 标签实例

                jsonobj.put("reqId", reqId);
                jsonobj.put("eventId", params.get("eventCode"));
                jsonobj.put("activityId", params.get("activityId"));
                if (ruleConfId != null) {
                    jsonobj.put("ruleConfId", ruleConfId);
                }
                jsonobj.put("ruleId", ruleId);
                jsonobj.put("integrationId", params.get("integrationId"));
                jsonobj.put("accNbr", params.get("accNbr"));
                jsonobj.put("strategyConfId", strategyConfId);
                //todo 存标签
                //jsonObject.put("target", body);
                //jsonobj.put("labelResultList", JSONArray.toJSON(labelResultList));

                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    context.put(entry.getKey(), entry.getValue());
                }

                //添加事件采集项的值到上下文
                context.putAll(labelItems);

                System.out.println("查询标签成功:" + context.toString());
            } else {
                System.out.println("查询标签失败:" + httpResult.getString("result_msg"));

                esJson.put("hit", "false");
                esJson.put("msg", "查询标签失败:" + httpResult.getString("result_msg"));
                esService.save(esJson, IndexList.STRATEGY_MODULE);
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

                        expressSb.append(" >= ").append("\"").append(strArray[0]).append("\"");
                        expressSb.append(" && ");
                        expressSb.append(label.getInjectionLabelCode());
                        expressSb.append(" <= ").append("\"").append(strArray[1]).append("\"");

                        express1.append(" >= ").append("\"").append(strArray[0]).append("\"");
                        express1.append(" && ");
                        express1.append(label.getInjectionLabelCode());
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
                        expressSb.append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"");//  真实值
                        express1.append("\"").append(tarGrpConditionDOs.get(i).getRightParam()).append("\"");//  真实值
                    }

                    expressSb.append(")");
                    express1.append(") {return true} else {return false}");
                    System.out.println(express1.toString());

                    try {
                        RuleResult ruleResult1 = runner.executeRule(express1.toString(), context, true, true);

                        for (LabelResult labelResult : labelResultList) {
                            if (label.getInjectionLabelCode().equals(labelResult.getLabelCode())) {

                                if (null != ruleResult1.getResult()) {
                                    labelResult.setResult((Boolean) ruleResult1.getResult());
                                } else {
                                    labelResult.setResult(false);
                                }

                            }
                        }
                        jsonobj.put("labelResultList", JSONArray.toJSON(labelResultList));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("单个标签判断出错");
                    }


                    if (i + 1 != tarGrpConditionDOs.size()) {
                        expressSb.append("&&");
                    }
                }
                expressSb.append(") {return true} else {return false}");
                express = expressSb.toString();
            }

            esService.save(jsonobj, IndexList.Label_MODULE);
            try {
                //规则引擎计算
                System.out.println(express);
                RuleResult ruleResult = null;

                try {

                    ruleResult = runner.executeRule(express, context, true, true);

                } catch (Exception e) {
                    e.printStackTrace();

                    ruleMap.put("msg", "规则引擎计算失败");

                    esJson.put("hit", "false");
                    esJson.put("msg", "规则引擎计算失败");
                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }

                System.out.println("result=" + ruleResult.getResult());
                System.out.println("Tree=" + ruleResult.getRule().toTree());
                System.out.println("TraceMap=" + ruleResult.getTraceMap());
                //初始化返回结果中的销售品条目
                List<Map<String, String>> productList = new ArrayList<>();
                if (ruleResult.getResult() != null && ((Boolean) ruleResult.getResult())) {

                    //拼接返回结果
                    ruleMap.put("orderISI", params.get("reqId")); //流水号
                    ruleMap.put("activityId", privateParams.get("activityId")); //活动编码
                    ruleMap.put("activityName", privateParams.get("activityName")); //活动名称
                    ruleMap.put("skipCheck", privateParams.get("activityName")); //调过预校验 todo
                    ruleMap.put("orderPriority", privateParams.get("orderPriority")); //活动优先级
                    ruleMap.put("integrationId", privateParams.get("integrationId")); //集成编号（必填）
                    ruleMap.put("accNbr", privateParams.get("accNbr")); //业务号码（必填）
                    ruleMap.put("policyId", strategyConfId); //策略编码
                    ruleMap.put("ruleId", ruleConfId); //规则编码

                    //查询销售品列表
                    if (productStr != null && !"".equals(productStr)) {
                        String[] productArray = productStr.split("/");
                        for (String str : productArray) {
                            Map<String, String> product = new HashMap<>();
                            PpmProduct ppmProduct = ppmProductMapper.selectByPrimaryKey(Long.parseLong(str));
                            product.put("productCode", ppmProduct.getProductCode());
                            product.put("productName", ppmProduct.getProductName());
                            product.put("productType", ppmProduct.getProductType());
                            product.put("productFlag", "销售品标签");  //todo 销售品标签
                            System.out.println("*********************product --->>>" + JSON.toJSON(product));
                            productList.add(product);
                        }
                    }

                    if (ruleResult.getResult() == null) {
                        ruleResult.setResult(false);
                    }
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
                    jsonObject.put("result", ruleResult.getResult()); //看是否命中
                    jsonObject.put("productList", productList);
                    esService.save(jsonObject, IndexList.RULE_MODULE);
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
                        Future<Map<String, Object>> f = executorService.submit(new ChannelTask(evtContactConfId, productList, privateParams));
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

                    esJson.put("hit", "false");
                    esJson.put("msg", "规则引擎匹配未通过");
                    esService.save(esJson, IndexList.STRATEGY_MODULE);
                    return Collections.EMPTY_MAP;
                }
            } catch (Exception e) {
                e.printStackTrace();

                //todo 异常处理

            }
            return ruleMap;
        }

        private void executeSingleExpress(List<TarGrpCondition> tarGrpConditionDOs, DefaultContext context, ExpressRunner runner, int i, String type, Label label, String express) {
            StringBuilder singleExpress = new StringBuilder();
            singleExpress.append("if(");
            singleExpress.append("(");
            singleExpress.append(label.getInjectionLabelCode());
            if ("1000".equals(type)) {
                singleExpress.append(">");
            } else if ("2000".equals(type)) {
                singleExpress.append("<");
            } else if ("3000".equals(type)) {
                singleExpress.append("==");
            } else if ("4000".equals(type)) {
                singleExpress.append("!=");
            } else if ("5000".equals(type)) {
                singleExpress.append(">=");
            } else if ("6000".equals(type)) {
                singleExpress.append("<=");
            }
            singleExpress.append(tarGrpConditionDOs.get(i).getRightParam());
            singleExpress.append(")");
            singleExpress.append(") {return true} else {return false}");
            try {
                RuleResult ruleResult = runner.executeRule(singleExpress.toString(), context, true, true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("express", express);
                jsonObject.put("reqId", reqId);
                jsonObject.put("eventId", params.get("eventCode"));
                jsonObject.put("activityId", params.get("activityId"));
                jsonObject.put("ruleConfId", ruleConfId);
                jsonObject.put("strategyConfId", strategyConfId);
                jsonObject.put("productStr", productStr);
                jsonObject.put("evtContactConfIdStr", evtContactConfIdStr);
                jsonObject.put("tarGrpId", tarGrpId);
                jsonObject.put("result", ruleResult.getResult()); //看是否命中
                jsonObject.put("labelCode", label.getInjectionLabelCode());
                jsonObject.put("labelName", label.getInjectionLabelName());
                esService.save(jsonObject, IndexList.Label_MODULE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class ChannelTask implements Callable<Map<String, Object>> {

        //策略配置id
        private Long evtContactConfId;

        private List<Map<String, String>> productList;
        private Map<String, String> privateParams;

        public ChannelTask(Long evtContactConfId, List<Map<String, String>> productList, Map<String, String> privateParams) {
            this.evtContactConfId = evtContactConfId;
            this.productList = productList;
            this.privateParams = privateParams;
        }

        @Override
        public Map<String, Object> call() {
            Date now = new Date();

            //查询渠道属性，渠道生失效时间过滤
            List<MktCamChlConfAttrDO> mktCamChlConfAttrs = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
            boolean checkTime = true;
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrs) {
                //判断渠道生失效时间
                if (mktCamChlConfAttrDO.getAttrId() == 1000L) {
                    if (!now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                        checkTime = false;
                    }
                }
                if (mktCamChlConfAttrDO.getAttrId() == 1001L) {
                    if (now.after(new Date(Long.parseLong(mktCamChlConfAttrDO.getAttrValue())))) {
                        checkTime = false;
                    }
                }
            }

            if (!checkTime) {
                return null;
            }

            //初始化返回结果推荐信息
            Map<String, Object> channel = new HashMap<>();

            //查询渠道信息基本信息
            MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);

            //渠道级别信息
//            channel.put("keyNote", ""); //痛痒点话术（必填）
//            channel.put("remark", ""); //备注字段
            channel.put("channelId", mktCamChlConf.getContactChlId());
            channel.put("channelConfId", mktCamChlConf.getContactChlId()); //执行渠道推送配置标识(MKT_CAM_CHL_CONF表主键) （必填） todo 林超

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
                channel.put("reason", camScript.getScriptDesc());
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
                channel.put("keyNote", mktVerbals.get(0).getScriptDesc());
            }
            return channel;
        }
    }

//        private String splicingExpression(String type) {
//
//
//            return null;
//        }


//    public Map<String, Object> getUserDetail_331040(String sn,String ab,String queryId, String idType, Map<String, Object> queryFields, String channelType) {
//        long sTme=System.currentTimeMillis();
//        String md = Thread.currentThread() .getStackTrace()[1].getMethodName();
//        String ex=md+":入参:" + queryId + ",idType=" + idType + ",queryFields=" + queryFields + ",channelType=" + channelType+"。";
//        UserDetailService userDetailServ = (UserDetailService) SpringContextHolder.getBean("userDetailServ");
//        List<Map> resultList = userDetailServ.getUserDetail(queryId, idType, queryFields, channelType ,"");
//        //System.out.println(ex + "回参:" + resultList);
//        ex+="回参:" + resultList;
//        return RtUtil.sjRt("0", resultList);
//    }


}
