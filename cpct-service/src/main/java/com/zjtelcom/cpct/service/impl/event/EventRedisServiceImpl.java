package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/10 11:09
 * @version: V1.0
 */
@Service
@Transactional
@Slf4j
public class EventRedisServiceImpl implements EventRedisService {

    @Autowired
    private RedisUtils redisUtils;  // redis方法
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper; //事件与活动关联表
    @Autowired
    private FilterRuleMapper filterRuleMapper;  //过滤规则
    @Autowired
    private MktCampaignMapper mktCampaignMapper; //活动基本信息
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper; //策略基本信息
    @Autowired
    private ContactChannelMapper contactChannelMapper;  // 渠道信息
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper; //策略规则
    @Autowired
    private SysParamsMapper sysParamsMapper;  //查询系统参数
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private SearchLabelService searchLabelService;
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper; //过滤规则与活动关系
    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper; //规则存储公共表（此处查询协同渠道子策略规则和话术规则）
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper; //分群规则条件表
    @Autowired
    private MktCamItemMapper mktCamItemMapper; //销售品
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper; //协同渠道配置的渠道
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;//协同渠道属性信息
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;  //营销脚本
    @Autowired
    private MktVerbalMapper mktVerbalMapper;  //话术


    /**
     * 获取缓存，若没有缓存则查询数据库并存入缓存
     *
     * @param key 索引的前部分key
     * @return
     */
    @Override
    public Map<String, Object> getRedis(String key) {
        return getRedis(key, 0L, new HashMap<>());
    }

    /**
     * 获取缓存，若没有缓存则查询数据库并存入缓存
     *
     * @param key 索引的前部分key
     * @return
     */
    @Override
    public Map<String, Object> getRedis(String key, Long id) {
        return getRedis(key, id, new HashMap<>());
    }

    /**
     * 获取缓存，若没有缓存则查询数据库并存入缓存
     *
     * @param key    索引的前部分key
     * @param params 其它参数
     * @return
     */
    @Override
    public Map<String, Object> getRedis(String key, Map<String, Object> params) {
        return getRedis(key, 0L, params);
    }

    /**
     * 获取缓存，若没有缓存则查询数据库并存入缓存
     *
     * @param key    索引的前部分key
     * @param id     索引的后部分id
     * @param params 其它参数
     * @return
     */
    @Override
    public Map<String, Object> getRedis(String key, Long id, Map<String, Object> params) {
        Map<String, Object> resutlt = new HashMap<>();
        if (id==null){
            log.error("查询key*****参数ID为空："+key);
            return null;
        }
        if (!id.toString().equals("0")) {
            key = key + id;
        }
        Object o = redisUtils.get(key);
        if (o != null ) {
            resutlt.put(key, o);
        } else {
            if (!id.toString().equals("0")) {
                resutlt = keyStartsWith(key, id, params);
            } else {
                resutlt = keyEquals(key, params);
            }
        }
        return resutlt;
    }

    /**
     * 缓存key有后缀
     *
     * @param key
     * @param id
     * @param params
     * @return
     */
    private Map<String, Object> keyStartsWith(String key, Long id, Map<String, Object> params) {
        Map<String, Object> resutlt = new HashMap<>();
        // 活动和事件的关联关系
        if (key.startsWith("CAM_IDS_EVT_REL_")) {
            List<Map<String, Object>> mktCampaginIdList = mktCamEvtRelMapper.listActivityByEventId(id);
            redisUtils.set(key, mktCampaginIdList);
            resutlt.put(key, mktCampaginIdList);
        } else if (key.startsWith("FILTER_RULE_STR_")) { // 过滤规则
            List<String> strategyTypeList = (List<String>) params.get("strategyTypeList");
            List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleListByStrategyId(id, strategyTypeList);
            redisUtils.set("FILTER_RULE_STR_" + id, filterRuleList);
            resutlt.put(key, filterRuleList);
        } else if (key.startsWith("MKT_CAMPAIGN_")) {  // 活动基本信息
            MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(id);
            redisUtils.set(key, mktCampaign);
            resutlt.put(key, mktCampaign);
        } else if (key.startsWith("MKT_CAM_STRATEGY_")) {    //  通过活动查询相关联的策略
            List<MktStrategyConfDO> mktStrategyConfDOS = mktStrategyConfMapper.selectByCampaignId(id);
            redisUtils.set(key, mktStrategyConfDOS);
            resutlt.put(key, mktStrategyConfDOS);
        } else if (key.startsWith("RULE_LIST_")) {   // 规则
            List<MktStrategyConfRuleDO> mktStrategyConfRuleList = (List<MktStrategyConfRuleDO>) mktStrategyConfRuleMapper.selectByMktStrategyConfId(id);
            redisUtils.setRedis(key, mktStrategyConfRuleList);
            resutlt.put(key, mktStrategyConfRuleList);
        } else if (key.startsWith("EVENT_ITEM_")) {  // 事件采集项
            List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(id);
            redisUtils.setRedis(key, contactEvtItems);
            resutlt.put(key, contactEvtItems);
        } else if (key.startsWith("EVT_ALL_LABEL_")) {  // 事件下所有标签
            Map<String, String> mktAllLabels = searchLabelService.labelListByEventId(id);  //查询事件下使用的所有标签
            if (null != mktAllLabels) {
                redisUtils.set(key, mktAllLabels);
                resutlt.put(key, mktAllLabels);
            }
        } else if (key.startsWith("CAM_EVT_REL_")) {   // 活动事件关系
            List<MktCamEvtRel> resultByEvent = mktCamEvtRelMapper.qryBycontactEvtId(id);
            redisUtils.set(key, resultByEvent);
            resutlt.put(key, resultByEvent);
        } else if (key.startsWith("FILTER_RULE_LIST_")) { // 过滤规则集合
            List<FilterRule> filterRuleList = filterRuleMapper.selectFilterRuleList(id);
            redisUtils.set(key, filterRuleList);
            resutlt.put(key, filterRuleList);
        } else if (key.startsWith("MKT_FILTER_RULE_IDS_")) { // 过滤规则Id
            List<Long> filterRuleIds = mktStrategyFilterRuleRelMapper.selectByStrategyId(id);
            redisUtils.set(key, filterRuleIds);
            resutlt.put(key, filterRuleIds);
        } else if (key.startsWith("FILTER_RULE_")) { // 过滤规则
            FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(id);
            redisUtils.set(key, filterRule);
            resutlt.put(key, filterRule);
        } else if (key.startsWith("FILTER_RULE_DISTURB_")) {  // 过滤规则信息查询失败
            List<String> labels = mktVerbalConditionMapper.getLabelListByConditionId(id);
            redisUtils.set(key, labels);
            resutlt.put(key, labels);
        } else if (key.startsWith("MKT_ISALE_LABEL_")) {
            List<Map<String, Object>> iSaleDisplay = injectionLabelMapper.listLabelByDisplayId(id);
            redisUtils.set(key, iSaleDisplay);
            resutlt.put(key, iSaleDisplay);
        } else if (key.startsWith("RULE_ALL_LABEL_")) {
            List<Map<String, String>> labelMapList = tarGrpConditionMapper.selectAllLabelByTarId(id);
            redisUtils.set(key, labelMapList);
            resutlt.put(key, labelMapList);
        } else if (key.startsWith("MKT_CAM_ITEM_LIST_")) {
            List<MktCamItem> mktCamItemList = new ArrayList<>();
            String productStr = (String) params.get("productStr");
            String[] productArray = productStr.split("/");
            for (String str : productArray) {
                // 从redis中获取推荐条目
                MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(str));
                mktCamItemList.add(mktCamItem);
            }
            redisUtils.set(key, mktCamItemList);
            resutlt.put(key, mktCamItemList);
        } else if (key.startsWith("MKT_CAMCHL_CONF_LIST_")) {
            List<MktCamChlConfDO> mktCamChlConfDOS = new ArrayList<>();
            String[] evtContactConfIdArray = (String[]) params.get("evtContactConfIdArray");
            if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                for (String str : evtContactConfIdArray) {
                    MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(str));
                    mktCamChlConfDOS.add(mktCamChlConfDO);
                }
                redisUtils.set(key, mktCamChlConfDOS);
                resutlt.put(key, mktCamChlConfDOS);
            }
        } else if (key.startsWith("MKT_STRATEGY_")) {
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(id);
            redisUtils.set(key, mktStrategyConfDO);
            resutlt.put(key, mktStrategyConfDO);
        } else if (key.startsWith("MKT_RULE_")) {
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(id);
            redisUtils.set(key, mktStrategyConfRuleDO);
            resutlt.put(key, mktStrategyConfRuleDO);
        } else if (key.startsWith("CHL_CONF_DETAIL_")) {   // 下发渠道基本信息 + 属性
            // 从数据库中获取并拼成mktCamChlConfDetail对象存入redis
            MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(id);
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(id);
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttrNew = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttrList.add(mktCamChlConfAttrNew);
            }
            mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            //渠道信息
            Channel channelMessage = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
            mktCamChlConfDetail.setContactChlCode(channelMessage.getContactChlCode());
            redisUtils.set(key, mktCamChlConfDetail);
            resutlt.put(key, mktCamChlConfDetail);
        } else if (key.startsWith("MKT_CAM_SCRIPT_")) {  // 下发渠道脚本
            // 数据库中获取脚本存入redis
            CamScript camScript = mktCamScriptMapper.selectByConfId(id);
            redisUtils.set(key, camScript);
            resutlt.put(key, camScript);
        } else if (key.startsWith("MKT_VERBAL_")) {  // 下发渠道话术
            List<MktVerbal> mktVerbals = mktVerbalMapper.findVerbalListByConfId(id);
            List<VerbalVO> verbalVOList = new ArrayList<>();
            for (MktVerbal mktVerbal : mktVerbals) {
                VerbalVO verbalVO = BeanUtil.create(mktVerbal, new VerbalVO());
                verbalVOList.add(verbalVO);
            }
            redisUtils.set(key, verbalVOList);
            resutlt.put(key, verbalVOList);
        }
        return resutlt;
    }

    /**
     * 缓存key无后缀
     *
     * @param key
     * @param params
     * @return
     */
    private Map<String, Object> keyEquals(String key, Map<String, Object> params) {
        Map<String, Object> resutlt = new HashMap<>();
        if ("MKT_CAM_API_CODE_KEY".equals(key)) {    // 走清单活动列表
            List<String> mktCamCodeList = new ArrayList<>();
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("MKT_CAM_API_CODE");
            for (SysParams sysParams : sysParamsList) {
                mktCamCodeList.add(sysParams.getParamValue());
            }
            redisUtils.set(key, mktCamCodeList);
            resutlt.put(key, mktCamCodeList);
        } else if ("CUST_PROD_FILTER".equals(key)) { // 清单销售品过滤
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("CUST_PROD_FILTER");
            if (sysParamsList != null && sysParamsList.size() > 0) {
                String custProdFilter = sysParamsList.get(0).getParamValue();
                redisUtils.set(key, custProdFilter);
                resutlt.put(key, custProdFilter);
            }
        } else if ("REAL_PROD_FILTER".equals(key)) { // 清单销售品过滤
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("REAL_PROD_FILTER");
            if (sysParamsList != null && sysParamsList.size() > 0) {
                String realProdFilter = sysParamsList.get(0).getParamValue();
                redisUtils.set(key, realProdFilter);
                resutlt.put(key, realProdFilter);
            }
        } else if ("DATETYPE_TARGOUID_LIST".equals(key)) { // 自定义时间类型标签值
            List<TarGrpCondition> conditionList = tarGrpConditionMapper.selectAllByUpdateStaff("200");
            if (conditionList != null) {
                StringBuilder sb = new StringBuilder();
                for (TarGrpCondition tarGrpCondition : conditionList) {
                    Long tarGrpId = tarGrpCondition.getTarGrpId();
                    sb.append(tarGrpId + ",");
                }
                redisUtils.set(key, sb.toString());
                resutlt.put(key, sb.toString());
            }
        } else if ("LABEL_CODE_LIST".equals(key)) {
            List<String> labelCodeList = injectionLabelMapper.selectLabelCodeByType("1100"); // 1100 代表为时间类型的标签
            redisUtils.set(key, labelCodeList);
            resutlt.put(key, labelCodeList);
        } else if ("COOL_LOGIN_ID".equals(key)) {
            List<Map<String, String>> sysParam = sysParamsMapper.listParamsByKey("COOL_LOGIN_ID");
            redisUtils.set(key, sysParam);
            resutlt.put(key, sysParam);
        } else if ("CHANNEL_FILTER_CODE".equals(key)) {  // 渠道话术拦截开关
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("CHANNEL_FILTER_CODE");
            if (sysParamsList != null && sysParamsList.size() > 0) {
                String channelFilterCode = sysParamsList.get(0).getParamValue();
                redisUtils.set(key, channelFilterCode);
                resutlt.put(key, channelFilterCode);
            }
        } else if ("CHECK_LABEL".equals(key)) {   // 事件实时接入标签验证开关
            List<SysParams> systemParamList = sysParamsMapper.findParamKeyIn("CHECK_LABEL");
            if (systemParamList.size() > 0) {
                redisUtils.set(key, systemParamList.get(0));
                resutlt.put(key, systemParamList.get(0));
            }
        }

        if ("EVENT_".equals(key)) {  // 事件
            String eventCode = (String) params.get("eventCode");
            ContactEvt event = contactEvtMapper.getEventByEventNbr(eventCode);
            redisUtils.set(key + eventCode, event);
            resutlt.put(key + eventCode, event);
        } else if ("CHANNEL_CODE_LIST_".equals(key)) {  // 渠道编码集合
            String eventCode = (String) params.get("eventCode");
            List<String> list = contactEvtMapper.selectChannelListByEvtCode(eventCode);
            redisUtils.set(key + eventCode, list);
            resutlt.put(key + eventCode, list);
        } else if ("CHANNEL_CODE_".equals(key)) {   // 渠道编码
            String eventCode = params.get("channelCode").toString();
            Channel channelMessage = contactChannelMapper.selectByCode(eventCode);
            redisUtils.set(key + eventCode, channelMessage);
            resutlt.put(key + eventCode, channelMessage);
        }
        return resutlt;
    }

    /**
     * 删除事件下所有缓存
     *
     * @param eventCode
     * @return
     */
    @Override
    public Map<String, Object> delRedisByEventCode(String eventCode){
        Map<String, Object> result = new HashMap<>();
        try {
            redisUtils.del("EVENT_" + eventCode);
            redisUtils.del("CHANNEL_CODE_LIST_" + eventCode);
            ContactEvt event = contactEvtMapper.getEventByEventNbr(eventCode);
            if (event != null && event.getContactEvtId() != null) {
                Long eventId = event.getContactEvtId();
                redisUtils.del("CAM_IDS_EVT_REL_" + eventId);
                // 事件采集项
                redisUtils.del("EVENT_ITEM_" + eventId);
                // 事件下所有标签
                redisUtils.del("EVT_ALL_LABEL_" + eventId);
                // 事件和活动关系
                redisUtils.del("CAM_EVT_REL_" + eventId);

                // 查询事件和活动关系
                List<Map<String, Object>> mktCampaginIdList = mktCamEvtRelMapper.listActivityByEventId(eventId);
                for (Map<String, Object> act : mktCampaginIdList) {
                    Long mktCampaginId = (Long) act.get("mktCampaginId");
                    deleteByCampaign(mktCampaginId);
                }
            }
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("resultMsg", "删除缓存成功！");
        } catch (Exception e) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "删除缓存失败！" + e);
        }
        return result;
    }

    @Override
    public Map<String, Object> deleteByCampaign( Long mktCampaginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MktCamEvtRelDO> relDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaginId);
            for (MktCamEvtRelDO relDO : relDOList) {
                redisUtils.del("CAM_IDS_EVT_REL_" + relDO.getEventId());
                redisUtils.del("CAM_EVT_REL_" + relDO.getEventId());
            }
            // 删除过滤规则
            redisUtils.del("FILTER_RULE_STR_" + mktCampaginId);
            // 删除活动基本信息
            redisUtils.del("MKT_CAMPAIGN_" + mktCampaginId);
            // 删除活动策略关系信息
            redisUtils.del("MKT_CAM_STRATEGY_" + mktCampaginId);
            // 删除过滤规则
            redisUtils.del("FILTER_RULE_LIST_" + mktCampaginId);
            // 删除过滤规则Ids
            redisUtils.del("MKT_FILTER_RULE_IDS_" + mktCampaginId);

            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaginId);
            // 删除展示列标签
            redisUtils.del("MKT_ISALE_LABEL_" + mktCampaignDO.getIsaleDisplay());

            List<Long> filterRuleIds = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktCampaginId);
            for (Long filterRuleId : filterRuleIds) {
                // 删除单个过滤规则
                redisUtils.del("FILTER_RULE_" + filterRuleId);
                redisUtils.del("FILTER_RULE_DISTURB_" + filterRuleId);
            }
            List<MktStrategyConfDO> mktStrategyConfDOS = mktStrategyConfMapper.selectByCampaignId(mktCampaginId);
            for (MktStrategyConfDO mktStrategyConfDO : mktStrategyConfDOS) {
                // 策略
                redisUtils.del("MKT_STRATEGY_" + mktStrategyConfDO.getMktStrategyConfId());
                // 规则列表
                redisUtils.del("RULE_LIST_" + mktStrategyConfDO.getMktStrategyConfId());
                List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                    // 规则
                    redisUtils.del("MKT_RULE_" + mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                    // 推荐条目列表
                    redisUtils.del("MKT_CAM_ITEM_LIST_" + mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                    // 规则下所有标签
                    redisUtils.del("RULE_ALL_LABEL_" + mktStrategyConfRuleDO.getTarGrpId());
                    redisUtils.del("EXPRESS_" + mktStrategyConfRuleDO.getTarGrpId());
                    // 规则下所有渠道集合
                    redisUtils.del("MKT_CAMCHL_CONF_LIST_" + mktStrategyConfRuleDO.getTarGrpId());
                    if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                        String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                        for (String evtContactConfId : evtContactConfIds) {
                            // 渠道信息
                            redisUtils.del("CHL_CONF_DETAIL_" +evtContactConfId);
                            // 脚本
                            redisUtils.del("MKT_CAM_SCRIPT_" +evtContactConfId);
                            // 话术
                            redisUtils.del("MKT_VERBAL_" +evtContactConfId);
                        }
                    }

                }
            }
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("resultMsg", "删除缓存成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "删除缓存失败！" + e);
        }
        return result;
    }


    /**
     * 获取缓存数据
     *
     * @param key
     * @return
     */
    @Override
    public Map<String, Object> getRedisByKey(String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object o = redisUtils.get(key);
            result.put("object", o);
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("resultMsg", "删除缓存成功！");
        } catch (Exception e) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "删除缓存失败！" + e);
        }
        return result;
    }


    /**
     * 删除缓存信息
     *
     * @param key
     * @return
     */
    @Override
    public Map<String, Object> delRedisByKey(String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            redisUtils.del(key);
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("resultMsg", "删除缓存成功！");
        } catch (Exception e) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "删除缓存失败！" + e);
        }
        return result;
    }

}