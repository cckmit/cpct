package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.org.OrgTreeMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.synchronize.SynchronizeRecordMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.dto.synchronize.SynchronizeRecord;
import com.zjtelcom.cpct.dubbo.service.MktCampaignSyncApiService;
import com.zjtelcom.cpct.dubbo.service.RecordService;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_offer.dao.inst.RequestInfoMapper;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import com.zjtelcom.cpct_prd.dao.campaign.*;
import com.zjtelcom.cpct_prd.dao.channel.MktCamScriptPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktVerbalConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktVerbalPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpPrdMapper;
import com.zjtelcom.cpct_prd.dao.strategy.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;

@Service
@Transactional
public class MktCampaignSyncApiServiceImpl implements MktCampaignSyncApiService {

    private static final Logger logger = LoggerFactory.getLogger(MktCampaignApiServiceImpl.class);
    /**
     * 营销活动
     */
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    /**
     * 系统参数
     */
    @Autowired
    private SysParamsMapper sysParamsMapper;
    /**
     * 策略配置和活动关联
     */
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    /**
     * 策略配置基本信息
     */
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Autowired
    private ContactChannelMapper contactChannelMapper;
    /**
     * 策略配置规则Mapper
     */
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
    /**
     * 首次协同
     */
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;
    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;
    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private MktCamCityRelMapper mktCamCityRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktCampaignPrdMapper mktCampaignPrdMapper;
    @Autowired
    private MktCamCityRelPrdMapper mktCamCityRelPrdMapper;
    @Autowired
    private MktCamEvtRelPrdMapper mktCamEvtRelPrdMapper;
    @Autowired
    private MktCamStrategyConfRelPrdMapper mktCamStrategyConfRelPrdMapper;
    @Autowired
    private MktStrategyConfPrdMapper mktStrategyConfPrdMapper;
    @Autowired
    private MktStrategyConfRulePrdMapper mktStrategyConfRulePrdMapper;
    @Autowired
    private MktStrategyConfRuleRelPrdMapper mktStrategyConfRuleRelPrdMapper;
    @Autowired
    private MktStrategyConfRegionRelPrdMapper mktStrategyConfRegionRelPrdMapper;
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;
    @Autowired
    private MktStrategyFilterRuleRelPrdMapper mktStrategyFilterRuleRelPrdMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private MktVerbalMapper verbalMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;
    @Autowired
    private TarGrpPrdMapper tarGrpPrdMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private TarGrpConditionPrdMapper tarGrpConditionPrdMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCamItemPrdMapper mktCamItemPrdMapper;
    @Autowired
    private MktCamChlConfPrdMapper mktCamChlConfPrdMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MktCamChlConfAttrPrdMapper mktCamChlConfAttrPrdMapper;
    @Autowired
    private MktCamChlResultPrdMapper mktCamChlResultPrdMapper;
    @Autowired
    private MktCamChlResultConfRelPrdMapper mktCamChlResultConfRelPrdMapper;
    @Autowired
    private MktVerbalPrdMapper mktVerbalPrdMapper;
    @Autowired
    private MktVerbalConditionPrdMapper mktVerbalConditionPrdMapper;
    @Autowired
    private MktCamScriptPrdMapper mktCamScriptPrdMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OrgTreeMapper orgTreeMapper;
    @Autowired
    private SynchronizeRecordMapper synchronizeRecordMapper;
    @Autowired
    private RedisUtils_prd redisUtils_prd;
    @Autowired
    private RequestInstRelMapper requestInstRelMapper;
    @Autowired
    private RecordService recordService;
    @Autowired
    private MktVerbalMapper mktVerbalMapper;
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;

    @Autowired
    private RequestInfoMapper requestInfoMapper;

    @Autowired
    private MktCamItemMapper camItemMapper;

    @Autowired
    private MktCamScriptMapper camScriptMapper;

    //同步表名
    private static final String tableName = "mkt_campaign";

    //指定下发地市人员的数据集合
    private final static String CITY_PUBLISH="CITY_PUBLISH";


    /**
     * 发布并下发活动
     *
     * @param requestId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> publishMktCampaign(final Long requestId) {
        Map<String, Object> result = new HashMap<>();
        List<RequestInstRel> mktCampaignRels = requestInstRelMapper.selectByRequestId(requestId, "mkt");
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            //初始化结果集
            List<Future<Map<String, Object>>> threadList = new ArrayList<>();
            for (RequestInstRel rel : mktCampaignRels) {
                Future<Map<String, Object>> future = null;
                ExecutorService executorService = Executors.newCachedThreadPool();
                future = executorService.submit(new issureCampaignTask(rel.getRequestObjId()));
                threadList.add(future);
                Map<String, Object> retr = new HashMap<>();
                String source = (String) future.get().get("resultMsg");
                retr.put("resultCode", (String) future.get().get("resultCode"));
                retr.put("活动ID:" + rel.getRequestObjId().toString(), source);
                res.add(retr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode", "200");
        result.put("resultMsg", "下发成功");
        result.put("resultData", res);
        return result;
    }

    class issureCampaignTask implements Callable<Map<String, Object>> {
        private Long mktCampaignId;

        public issureCampaignTask(Long campaignId) {
            this.mktCampaignId = campaignId;
        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> mktCampaignMap = new HashMap<>();
            try {
                // 获取当前活动信息
                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
                // 获取当前活动标识
                Long parentMktCampaignId = mktCampaignDO.getMktCampaignId();
                // 获取活动下策略的集合
                List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(parentMktCampaignId);
                // 获取生失效时间
                Date effDate = mktCampaignDO.getPlanBeginTime();
                Date expDate = mktCampaignDO.getPlanEndTime();
                // 获取活动与事件的关系
                List<MktCamEvtRelDO> MktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(parentMktCampaignId);
                // 获取当前活动的下发城市集合
                List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(parentMktCampaignId);
                List<Long> childMktCampaignIdList = new ArrayList<>();
                if (mktCampaignDO == null) {
                    mktCampaignMap.put("resultCode", CommonConstant.CODE_FAIL);
                    mktCampaignMap.put("resultMsg", "活动不存在！");
                    return mktCampaignMap;
                }
                if (!StatusCode.STATUS_CODE_CHECKING.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                    mktCampaignMap.put("resultCode", CommonConstant.CODE_FAIL);
                    mktCampaignMap.put("resultMsg", "非已通过活动，无法发布！");
                    return mktCampaignMap;
                }
                mktCampaignMapper.changeMktCampaignStatus(mktCampaignId, "2002", new Date(), UserUtil.loginId());
                if (!mktCamCityRelDOList.isEmpty()) {

                    // 遍历活动下发城市集合
                    for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
                        // 为下发城市生成新的活动
                        mktCampaignDO.setMktCampaignId(null);
                        mktCampaignDO.setMktCampaignCategory(StatusCode.AUTONOMICK_CAMPAIGN.getStatusCode()); // 子活动默认为自主活动
                        mktCampaignDO.setLanId(mktCamCityRelDO.getCityId()); // 本地网标识
                        mktCampaignDO.setCreateDate(new Date());
                        mktCampaignDO.setCreateStaff(UserUtil.loginId());
                        mktCampaignDO.setUpdateDate(new Date());
                        mktCampaignDO.setUpdateStaff(UserUtil.loginId());
                        mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
                        mktCampaignMapper.insert(mktCampaignDO);
                        // 获取新的活动的Id
                        Long childMktCampaignId = mktCampaignDO.getMktCampaignId();
                        // 活动编码
                        mktCampaignDO.setMktActivityNbr("MKT" + String.format("%06d", childMktCampaignId));
                        mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);


                        childMktCampaignIdList.add(childMktCampaignId);
                        // 与父活动进行关联
                        MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
                        mktCampaignRelDO.setaMktCampaignId(parentMktCampaignId);
                        mktCampaignRelDO.setzMktCampaignId(childMktCampaignId);
                        mktCampaignRelDO.setApplyRegionId(mktCamCityRelDO.getCityId());
                        mktCampaignRelDO.setEffDate(effDate);
                        mktCampaignRelDO.setExpDate(expDate);
                        mktCampaignRelDO.setRelType(StatusCode.PARENT_CHILD_RELATION.getStatusCode());   //  1000-父子关系
                        mktCampaignRelDO.setCreateDate(new Date());
                        mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
                        mktCampaignRelDO.setUpdateDate(new Date());
                        mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
                        mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());  // 1000-有效
                        mktCampaignRelDO.setStatusDate(new Date());
                        mktCampaignRelMapper.insert(mktCampaignRelDO);

                        //事件与新活动建立关联
                        for (MktCamEvtRelDO mktCamEvtRelDO : MktCamEvtRelDOList) {
                            MktCamEvtRelDO childMktCamEvtRelDO = new MktCamEvtRelDO();
                            childMktCamEvtRelDO.setMktCampaignId(childMktCampaignId);
                            childMktCamEvtRelDO.setEventId(mktCamEvtRelDO.getEventId());
                            childMktCamEvtRelDO.setCampaignSeq(0); // 默认等级为 0
                            childMktCamEvtRelDO.setLevelConfig(0); // 默认为资产级 0
                            childMktCamEvtRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                            childMktCamEvtRelDO.setStatusDate(new Date());
                            childMktCamEvtRelDO.setCreateDate(new Date());
                            childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                            childMktCamEvtRelDO.setUpdateDate(new Date());
                            childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                            mktCamEvtRelMapper.insert(childMktCamEvtRelDO);
                        }

                        // 推荐条目下发
                        copyItemByCampaignPublish(parentMktCampaignId, childMktCampaignId, mktCampaignDO.getMktCampaignCategory());


                        // 遍历活动下策略的集合
                        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                            Map<String, Object> mktStrategyConfMap = copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId(), childMktCampaignId, true);
                            Long childMktStrategyConfId = (Long) mktStrategyConfMap.get("childMktStrategyConfId");
                            // 建立活动和策略的关系
                            MktCamStrategyConfRelDO chaildMktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
                            chaildMktCamStrategyConfRelDO.setMktCampaignId(childMktCampaignId);
                            chaildMktCamStrategyConfRelDO.setStrategyConfId(childMktStrategyConfId);
                            //                chaildMktCamStrategyConfRelDO.setStatusCd("1000"); // 1000-有效
                            //                chaildMktCamStrategyConfRelDO.setStatusDate(new Date());
                            chaildMktCamStrategyConfRelDO.setCreateDate(new Date());
                            chaildMktCamStrategyConfRelDO.setCreateStaff(UserUtil.loginId());
                            chaildMktCamStrategyConfRelDO.setUpdateDate(new Date());
                            chaildMktCamStrategyConfRelDO.setUpdateStaff(UserUtil.loginId());
                            mktCamStrategyConfRelMapper.insert(chaildMktCamStrategyConfRelDO);
                        }

                        // 活动下过滤规则
                        List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOS = mktStrategyFilterRuleRelMapper.selectRuleByStrategyId(mktCampaignId);
                        for (MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO : mktStrategyFilterRuleRelDOS) {
                            mktStrategyFilterRuleRelDO.setMktStrategyFilterRuleRelId(null);
                            mktStrategyFilterRuleRelDO.setStrategyId(childMktCampaignId);
                            mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                        }

                        //如果是框架活动 生成子活动后  生成对应的子需求函 下发给指定岗位的指定人员
                        generateRequest(mktCampaignDO,mktCamCityRelDO.getCityId());
                        //  发布活动时异步去同步大数据
                        if (SystemParamsUtil.isCampaignSync()) {
                            // 发布活动异步同步活动到生产环境
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        String roleName = "admin";
                                        synchronizeCampaign(mktCampaignId, roleName);
                                        // 删除生产redis缓存
                                        deleteCampaignRedisProd(mktCampaignId);
                                    } catch (Exception e) {
                                        logger.error("[op:MktCampaignServiceImpl] 活动同步失败 by mktCampaignId = {}, Expection = ",mktCampaignId, e);
                                    }
                                }
                            }.start();
                        }
                    }
                }
                mktCampaignMap.put("resultCode", CommonConstant.CODE_SUCCESS);
                mktCampaignMap.put("resultMsg", "发布活动成功！");
                mktCampaignMap.put("childMktCampaignIdList", childMktCampaignIdList);
            } catch (Exception e) {
                mktCampaignMap.put("resultCode", CommonConstant.CODE_FAIL);
                mktCampaignMap.put("resultMsg", "发布活动失败！");
            }
            return mktCampaignMap;
        }
    }

    /**
     * 根据地市生成子需求函，子活动和子需求函的关联，和指定的承接人员
     * @param mktCampaignDO 新生成的子活动
     * @param lanId 地市id
     */
    public void generateRequest(MktCampaignDO mktCampaignDO,Long lanId){
        RequestInfo requestInfo=new RequestInfo();
        requestInfo.setRequestType("mkt");
        //需求函批次号按规律递增1
        requestInfo.setBatchNo(getBatchNo(requestInfoMapper.selectMaxBatchNo()));
        requestInfo.setName(mktCampaignDO.getMktCampaignName());
        requestInfo.setDesc(mktCampaignDO.getMktCampaignName());
        requestInfo.setReason(mktCampaignDO.getMktCampaignName());
        requestInfo.setStartDate(mktCampaignDO.getPlanBeginTime());
        requestInfo.setExpectFinishDate(mktCampaignDO.getPlanEndTime());
        requestInfo.setStatusCd("1000");
        requestInfo.setStatusDate(new Date());
        requestInfo.setCreateDate(new Date());
        requestInfo.setUpdateDate(new Date());
        requestInfo.setActionType("add");
        requestInfo.setActivitiKey("mkt_force_province");  //需求函活动类型
        requestInfo.setRequestUrgentType("一般");
        requestInfo.setProcessType("0");
        requestInfo.setIsstartup("1");
        requestInfo.setReportTag("0");
        //得到指定下发的人员信息集合
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign(CITY_PUBLISH);
        if(!sysParams.isEmpty()){
            SysParams s=sysParams.get(0);
            String paramValue = s.getParamValue();
            if(StringUtils.isNotBlank(paramValue)){
                JSONArray jsonArray = JSONArray.parseArray(paramValue);
                for (int i = 0; i <jsonArray.size() ; i++) {
                    JSONObject o = JSONObject.parseObject(JSON.toJSONString(jsonArray.get(i)));
                    String lan =o.getString("lanId");
                    if(lanId-Long.valueOf(lan)==0){
                        requestInfo.setContName(o.getString("name"));
                        requestInfo.setDeptCode(o.getString("department"));
                        requestInfo.setCreateStaff(o.getLong("employeeId"));   //创建人,目前指定到承接人的工号
                        break;
                    }
                }
            }
        }
        requestInfoMapper.insert(requestInfo);
        //开始增加子活动和需求函的关系
        RequestInstRel rel=new RequestInstRel();
        rel.setRequestObjId(mktCampaignDO.getMktCampaignId());
        rel.setRequestInfoId(requestInfo.getRequestTemplateInstId());
        rel.setRequestObjType("mkt");
        rel.setStatusDate(new Date());
        rel.setUpdateDate(new Date());
        rel.setCreateDate(new Date());
        rel.setStatusCd(STATUSCD_EFFECTIVE);
        requestInstRelMapper.insertInfo(rel);

    }

    /**
     * 得到最新的批次编号
     * 浙电产品套餐需求浙【2019】1002116号
     * @param batchNo
     * @return
     */
    public String getBatchNo(String batchNo){
        String substring = "浙电营销活动需求【"+DateUtil.getCurrentYear().toString()+"】";
        Long num = requestInfoMapper.selectBatchNoNum();
        String path=substring+num.toString()+"号";
        return  path;
    }
    /**
     * 删除活动下的所有redis -- 生成
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    private Map<String, Object> deleteCampaignRedisProd(Long mktCampaignId) {
        Map<String, Object> resultMap = new HashMap<>();
        try{
            MktCampaignDO mktCampaignDO = mktCampaignPrdMapper.selectByPrimaryKey(mktCampaignId);
            // 删除活动缓存
            redisUtils_prd.del("MKT_CAMPAIGN_" + mktCampaignId);
            // 删除活动所有标签缓存
            List<MktCamEvtRelDO> mktCamEvtRelDOS = mktCamEvtRelPrdMapper.selectByMktCampaignId(mktCampaignId);
            for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOS) {
                redisUtils_prd.del("EVT_ALL_LABEL_" + mktCamEvtRelDO.getEventId());
            }

            // 删除过滤规则缓存
            List<Long> longList = mktStrategyFilterRuleRelPrdMapper.selectByStrategyId(mktCampaignId);
            for (Long filterRuleId : longList) {
                redisUtils_prd.del("MKT_FILTER_RULE_IDS_" + filterRuleId);
                redisUtils_prd.del("FILTER_RULE_DISTURB_" + filterRuleId);
            }

            // 删除展示列的标签
            redisUtils_prd.del("MKT_ISALE_LABEL_" + mktCampaignDO.getIsaleDisplay());

            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOS = mktCamStrategyConfRelPrdMapper.selectByMktCampaignId(mktCampaignId);
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOS) {
                // 删除策略关系缓存
                redisUtils_prd.del("MKT_STRATEGY_REL_" + mktCamStrategyConfRelDO.getStrategyConfId());
                List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRulePrdMapper.selectByMktStrategyConfId(mktCamStrategyConfRelDO.getStrategyConfId());
                for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                    // 删除客户分群标签
                    redisUtils_prd.del("RULE_ALL_LABEL_" + mktStrategyConfRuleDO.getTarGrpId());
                    //表达式存入redis
                    redisUtils_prd.del("EXPRESS_" + mktStrategyConfRuleDO.getTarGrpId());

                    // 删除推荐条目
                    if (mktStrategyConfRuleDO.getProductId() != null) {
                        String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                        if (productIds != null && !"".equals(productIds[0])) {
                            for (String productId : productIds) {
                                redisUtils_prd.del("MKT_CAM_ITEM_" + productId);
                            }
                        }
                    }

                    // 删除推送渠道
                    if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                        String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                        if (evtContactConfIds != null && !"".equals(evtContactConfIds[0])) {
                            for (String evtContactConfId : evtContactConfIds) {
                                redisUtils_prd.del("CHL_CONF_DETAIL_" + evtContactConfIds);
                            }
                        }
                    }
                }
            }
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "删除生产环境redis成功！");
        } catch (Exception e) {
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "删除生产环境redis失败！");
            logger.error("[op:SynchronizeCampaignServiceImpl] failed to delete campaignRedisProd by mktCampaignId = {} , Exception = ", mktCampaignId, e);
        }
        return resultMap;
    }

    /**
     * 准生产同步活动到生产环境--多数据源
     *
     * @param mktCampaignId
     * @param roleName
     * @return
     * @throws Exception
     */
    public Map<String, Object> synchronizeCampaign(Long mktCampaignId, String roleName) throws Exception {
        // 判断该活动是否存在
        Map<String, Object> synchronizeCampaignMap = new HashMap<>();
        MktCampaignDO mktCampaignPrdDO = mktCampaignPrdMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        if (mktCampaignPrdDO != null) {
            // 更新活动
            mktCampaignPrdMapper.updateByPrimaryKey(mktCampaignDO);
            addRecord(roleName, tableName, mktCampaignDO.getMktCampaignId(), SynchronizeType.update.getType());
        } else {
            // 新增活动
            mktCampaignPrdMapper.insert(mktCampaignDO);
            addRecord(roleName, tableName, mktCampaignDO.getMktCampaignId(), SynchronizeType.add.getType());
        }

        // 删除下发城市
        mktCamCityRelPrdMapper.deleteByMktCampaignId(mktCampaignDO.getMktCampaignId());
        // 关联下发地市
        List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
            mktCamCityRelPrdMapper.insert(mktCamCityRelDO);
        }


        mktCamItemPrdMapper.deleteByCampaignId(mktCampaignDO.getMktCampaignId());
        List<MktCamItem> itemList = mktCamItemMapper.selectByCampaignId(mktCampaignDO.getMktCampaignId());
        for (MktCamItem item : itemList){
            mktCamItemPrdMapper.insert(item);
        }
        // 删除关联事件
        mktCamEvtRelPrdMapper.deleteByMktCampaignId(mktCampaignDO.getMktCampaignId());
        // 关联事件
        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
            mktCamEvtRelPrdMapper.insert(mktCamEvtRelDO);
        }

        // 删除活动策略关系
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelPrdDOList = mktCamStrategyConfRelPrdMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelPrdDOList) {
            //删除策略下所有信息
            deleteMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
        }
        // 删除该活动下的所有策略
        mktCamStrategyConfRelPrdMapper.deleteByMktCampaignId(mktCampaignDO.getMktCampaignId());

        // 删除与策略关联的过滤规则
        mktStrategyFilterRuleRelPrdMapper.deleteByStrategyId(mktCampaignDO.getMktCampaignId());

        //获取策略对应的过滤规则
        List<Long> ruleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktCampaignDO.getMktCampaignId());
        List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOList = mktStrategyFilterRuleRelMapper.selectRuleByStrategyId(mktCampaignDO.getMktCampaignId());
        // 与新的策略建立关联
        for (MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO : mktStrategyFilterRuleRelDOList) {
            mktStrategyFilterRuleRelPrdMapper.insert(mktStrategyFilterRuleRelDO);
        }

        /*复制活动下的策略到生产环境*/
        // 查询活动策略关系
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            mktCamStrategyConfRelPrdMapper.insert(mktCamStrategyConfRelDO);
            //复制策略下所有信息
            copyMktStrategyConfToPrd(mktCamStrategyConfRelDO.getStrategyConfId());

        }
        synchronizeCampaignMap.put("mktCampaignId", mktCampaignId);
        synchronizeCampaignMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        synchronizeCampaignMap.put("resultMsg", "同步成功！");
        return synchronizeCampaignMap;
    }


    /**
     * 通过原策略id复制策略
     *
     * @param parentMktStrategyConfId
     * @return
     * @throws Exception
     */
    public Map<String, Object> copyMktStrategyConfToPrd(Long parentMktStrategyConfId) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        // 通过原策略id 获取原策略基本信息
        MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(parentMktStrategyConfId);
        // 获取策略下规则信息
        List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(parentMktStrategyConfId);
        mktStrategyConfPrdMapper.insert(mktStrategyConfDO);
        Long childMktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();
        // 遍历规则
        for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
            // 建立策略和规则的关系
            mktStrategyConfRuleRelPrdMapper.insert(mktStrategyConfRuleRelDO);
            // 复制获取规则
            copyMktStrategyConfRuleToPrd(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
        }
        mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        mktStrategyConfMap.put("childMktStrategyConfId", childMktStrategyConfId);
        return mktStrategyConfMap;
    }


    /**
     * 通过父规则Id复制策略规则
     *
     * @param parentMktStrategyConfRuleId
     * @return
     */
    public Map<String, Object> copyMktStrategyConfRuleToPrd(Long parentMktStrategyConfRuleId) throws Exception {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(parentMktStrategyConfRuleId);
        mktStrategyConfRulePrdMapper.insert(mktStrategyConfRuleDO);
        /**
         * 客户分群配置
         */
        Map<String, Object> tarGrpMap = new HashMap<>();
        // 复制客户分群
        copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), false);
        /**
         * 销售品配置
         */
//        List<Long> productIdList = new ArrayList<>();
//        if (mktStrategyConfRuleDO.getProductId() != null) {
//            String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
//            for (int i = 0; i < productIds.length; i++) {
//                if (productIds[i] != "" && !"".equals(productIds[i])) {
//                    productIdList.add(Long.valueOf(productIds[i]));
//                }
//            }
//        }
//        copyProductRuleToPrd(UserUtil.loginId(), productIdList);

        /**
         * 协同渠道配置
         */
        if (mktStrategyConfRuleDO.getEvtContactConfId()!=null){
            String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
            if (evtContactConfIds != null && !"".equals(evtContactConfIds[0])) {
                for (int i = 0; i < evtContactConfIds.length; i++) {
                    if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                        copyMktCamChlConfToPrd(Long.valueOf(evtContactConfIds[i]));
                    }
                }
            }
        }

        /**
         * 二次协同结果
         */
        if (mktStrategyConfRuleDO.getMktCamChlResultId()!=null){
            String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
            if (mktCamChlResultIds != null && !"".equals(mktCamChlResultIds[0])) {
                for (int i = 0; i < mktCamChlResultIds.length; i++) {
                    copyMktCamChlResultToPrd(Long.valueOf(mktCamChlResultIds[i]));
                }
            }

        }
        mktStrategyConfRuleMap.put("mktStrategyConfRuleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
        return mktStrategyConfRuleMap;
    }


    /**
     * 删除活动下策略的所有信息
     *
     * @param mktStrategyConfId
     * @return
     * @throws Exception
     */
    public Map<String, Object> deleteMktStrategyConf(Long mktStrategyConfId) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        // 删除与策略关联的下发城市
        mktStrategyConfRegionRelPrdMapper.deleteByMktStrategyConfId(mktStrategyConfId);
        //删除策略下的规则，以及关联的表
        List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelPrdMapper.selectByMktStrategyConfId(mktStrategyConfId);
        for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRulePrdMapper.selectByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
            // 删除客户分群
            if(mktStrategyConfRuleDO!=null){
                tarGrpConditionPrdMapper.deleteByTarGrpId(mktStrategyConfRuleDO.getTarGrpId());
                tarGrpPrdMapper.deleteByPrimaryKey(mktStrategyConfRuleDO.getTarGrpId());

//                if(mktStrategyConfRuleDO.getProductId()!=null && !"".equals(mktStrategyConfRuleDO.getProductId())){
//                // 删除销售品
//                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
//                    for (String productId : productIds) {
//                        mktCamItemPrdMapper.deleteByPrimaryKey(Long.valueOf(productId));
//                    }
//                }


                // 删除首次协同
                if (mktStrategyConfRuleDO.getEvtContactConfId()!=null){
                    String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                    if(!"".equals(evtContactConfIds[0])){
                        for (String evtContactConfId : evtContactConfIds) {
                            Long confId = Long.valueOf(evtContactConfId);
                            mktCamChlConfPrdMapper.deleteByPrimaryKey(confId);
                            mktCamChlConfAttrPrdMapper.deleteByEvtContactConfId(confId);
                            // 删除话术
                            List<MktVerbal> verbalList = mktVerbalPrdMapper.findVerbalListByConfId(confId);
                            for (MktVerbal mktVerbal : verbalList) {
                                mktVerbalConditionPrdMapper.deleteByVerbalId("0", mktVerbal.getVerbalId());
                            }
                            mktVerbalPrdMapper.deleteByConfId(confId);
                            // 删除脚本
                            mktCamScriptPrdMapper.deleteByConfId(confId);
                        }
                    }
                }



                if (mktStrategyConfRuleDO.getMktCamChlResultId()!=null){
                    // 删除二次协同结果
                    String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                    if(mktCamChlResultIds !=null && !"".equals(mktCamChlResultIds[0])){
                        for (String mktCamChlResultId : mktCamChlResultIds) {
                            // 删除结果下的推动渠道以及属性
                            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelPrdMapper.selectByMktCamChlResultId(Long.valueOf(mktCamChlResultId));
                            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                                Long confId = Long.valueOf(mktCamChlResultConfRelDO.getEvtContactConfId());
                                mktCamChlConfPrdMapper.deleteByPrimaryKey(confId);
                                mktCamChlConfAttrPrdMapper.deleteByEvtContactConfId(confId);
                                // 删除话术
                                List<MktVerbal> verbalList = mktVerbalPrdMapper.findVerbalListByConfId(confId);
                                for (MktVerbal mktVerbal : verbalList) {
                                    mktVerbalConditionPrdMapper.deleteByVerbalId("0", mktVerbal.getVerbalId());
                                }
                                mktVerbalPrdMapper.deleteByConfId(confId);
                                // 删除脚本
                                mktCamScriptPrdMapper.deleteByConfId(confId);
                            }
                            mktCamChlResultConfRelPrdMapper.deleteByMktCamChlResultId(Long.valueOf(mktCamChlResultId));
                            mktCamChlResultPrdMapper.deleteByPrimaryKey(Long.valueOf(mktCamChlResultId));
                        }
                    }
                }
                mktStrategyConfRulePrdMapper.deleteByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
                mktStrategyConfRuleRelPrdMapper.deleteByMktStrategyConfId(mktStrategyConfRuleRelDO.getMktStrategyConfId());
            }
        }
        //删除策略与活动的关联
        mktCamStrategyConfRelPrdMapper.deleteByStrategyConfId(mktStrategyConfId);
        //删除策略
        mktStrategyConfPrdMapper.deleteByPrimaryKey(mktStrategyConfId);
        mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        mktStrategyConfMap.put("resultMsg", ErrorCode.SAVE_MKT_CAMPAIGN_SUCCESS.getErrorMsg());
        return mktStrategyConfMap;
    }

    /**
     * 新增同并记录
     *
     * @param roleName 角色
     * @param name     同步表名称
     * @param eventId  同步主键
     * @param type     操作类型
     * @return
     */
    public int addRecord(String roleName, String name, Long eventId, Integer type) {
        SynchronizeRecord synchronizeRecord = new SynchronizeRecord();
        synchronizeRecord.setRoleName(roleName);
        synchronizeRecord.setSynchronizeName(name);
        synchronizeRecord.setSynchronizeType(type);
        synchronizeRecord.setSynchronizeId(eventId.toString());
        return synchronizeRecordMapper.insert(synchronizeRecord);
    }


    /**
     * 通过原策略id复制策略
     *
     * @param parentMktStrategyConfId
     * @param isPublish               是否为发布操作
     * @return
     * @throws Exception
     */
    public Map<String, Object> copyMktStrategyConf(Long parentMktStrategyConfId, Long childMktCampaignId, Boolean isPublish) throws Exception {
        Map<String, Object> mktStrategyConfMap = new HashMap<>();
        // 通过原策略id 获取原策略基本信息
        try {
            MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(parentMktStrategyConfId);
            // 获取策略下规则信息
            List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(parentMktStrategyConfId);

            mktStrategyConfDO.setMktStrategyConfId(null);
            mktStrategyConfDO.setCreateDate(new Date());
            mktStrategyConfDO.setCreateStaff(UserUtil.loginId());
            mktStrategyConfDO.setUpdateDate(new Date());
            mktStrategyConfDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfMapper.insert(mktStrategyConfDO);
            Long childMktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();

            //获取策略对应的过滤规则
/*            List<Long> ruleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(parentMktStrategyConfId);
            // 与新的策略建立关联
            for (Long ruleId : ruleIdList) {
                MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                mktStrategyFilterRuleRelDO.setStrategyId(childMktStrategyConfId);
                mktStrategyFilterRuleRelDO.setRuleId(ruleId);
                mktStrategyFilterRuleRelDO.setCreateDate(new Date());
                mktStrategyFilterRuleRelDO.setCreateStaff(UserUtil.loginId());
                mktStrategyFilterRuleRelDO.setUpdateDate(new Date());
                mktStrategyFilterRuleRelDO.setUpdateStaff(UserUtil.loginId());
                mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
            }*/
            // 遍历规则
            for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
                // 复制获取规则
                Map<String, Object> mktStrategyConfRuleMap = copyMktStrategyConfRule(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId(), childMktCampaignId,true);
                Long mktStrategyConfRuleId = (Long) mktStrategyConfRuleMap.get("mktStrategyConfRuleId");
                // 简历策略和规则的关系
                MktStrategyConfRuleRelDO childMktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                childMktStrategyConfRuleRelDO.setMktStrategyConfId(childMktStrategyConfId);
                childMktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleId);
                childMktStrategyConfRuleRelDO.setCreateDate(new Date());
                childMktStrategyConfRuleRelDO.setCreateStaff(UserUtil.loginId());
                childMktStrategyConfRuleRelDO.setUpdateDate(new Date());
                childMktStrategyConfRuleRelDO.setUpdateStaff(UserUtil.loginId());
                mktStrategyConfRuleRelMapper.insert(childMktStrategyConfRuleRelDO);
            }
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfMap.put("resultMsg", "复制策略成功！");
            mktStrategyConfMap.put("childMktStrategyConfId", childMktStrategyConfId);
        } catch (Exception e) {
            logger.error("[op:copyMktStrategyConf] copyMktStrategyConf parentMktStrategyConfId ={} 失败Exception = ", parentMktStrategyConfId, e);
            mktStrategyConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfMap.put("resultMsg", "复制策略失败！");
        }
        return mktStrategyConfMap;
    }

    /**
     * 通过父规则Id复制策略规则
     *
     * @param parentMktStrategyConfRuleId
     * @param isPublish                   是否为发布操作
     * @return
     */
    public Map<String, Object> copyMktStrategyConfRule(Long parentMktStrategyConfRuleId, Long childMktCampaignId, Boolean isPublish) throws Exception {
        Map<String, Object> mktStrategyConfRuleMap = new HashMap<>();
        try {
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(parentMktStrategyConfRuleId);
            MktStrategyConfRuleDO chiledMktStrategyConfRuleDO = new MktStrategyConfRuleDO();
            /**
             * 客户分群配置
             */
            //判断是否为发布操作
            Map<String, Object> tarGrpMap = new HashMap<>();
            if (isPublish) {
                tarGrpMap =copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), true);
            } else {
                tarGrpMap = copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), false);
            }

            TarGrp tarGrp = (TarGrp) tarGrpMap.get("tarGrp");
            /**
             * 销售品配置
             */
            String childProductIds = "";
            if (mktStrategyConfRuleDO.getProductId() != null) {
                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                if (productIds != null && !"".equals(productIds[0])) {
                    for (int i = 0; i <productIds.length ; i++) {
                        MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(productIds[i]));
                        if (mktCamItem != null) {
                            MktCamItem mktCamItemNew = mktCamItemMapper.selectByCampaignIdAndItemIdAndType(mktCamItem.getItemId(), childMktCampaignId, mktCamItem.getItemType());
                            if (mktCamItemNew != null) {
                                if (i == 0){
                                    childProductIds += mktCamItemNew.getMktCamItemId();
                                } else {
                                    childProductIds += "/" + mktCamItemNew.getMktCamItemId();
                                }
                            }
                        }
                    }
                }
            }


            /**
             * 协同渠道配置
             */
            String childEvtContactConfIds = "";
            if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                if (evtContactConfIds != null && !"".equals(evtContactConfIds[0])) {
                    for (int i = 0; i < evtContactConfIds.length; i++) {
                        if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                            Map<String, Object> mktCamChlConfDOMap = copyMktCamChlConf(Long.valueOf(evtContactConfIds[i]));
                            MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfDOMap.get("mktCamChlConfDetail");
                            if (i == 0) {
                                childEvtContactConfIds += mktCamChlConfDetail.getEvtContactConfId();
                            } else {
                                childEvtContactConfIds += "/" + mktCamChlConfDetail.getEvtContactConfId();
                            }
                        }
                    }
                }
            }

            /**
             * 二次协同结果
             */
            String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
            String childMktCamChlResultIds = "";
            if (mktCamChlResultIds != null && !"".equals(mktCamChlResultIds[0])) {
                for (int i = 0; i < mktCamChlResultIds.length; i++) {
                    Map<String, Object> mktCamChlResultDOMap = copyMktCamChlResult(Long.valueOf(mktCamChlResultIds[i]));
                    MktCamChlResult mktCamChlResult = (MktCamChlResult) mktCamChlResultDOMap.get("mktCamChlResult");
                    if (i == 0) {
                        childMktCamChlResultIds += mktCamChlResult.getMktCamChlResultId();
                    } else {
                        childMktCamChlResultIds += "/" + mktCamChlResult.getMktCamChlResultId();
                    }
                }
            }
            chiledMktStrategyConfRuleDO.setMktStrategyConfRuleName(mktStrategyConfRuleDO.getMktStrategyConfRuleName());
            if (tarGrp != null) {
                chiledMktStrategyConfRuleDO.setTarGrpId(tarGrp.getTarGrpId());
            }
            chiledMktStrategyConfRuleDO.setProductId(childProductIds);
            chiledMktStrategyConfRuleDO.setEvtContactConfId(childEvtContactConfIds);
            chiledMktStrategyConfRuleDO.setMktCamChlResultId(childMktCamChlResultIds);
            chiledMktStrategyConfRuleDO.setCreateDate(new Date());
            chiledMktStrategyConfRuleDO.setCreateStaff(UserUtil.loginId());
            chiledMktStrategyConfRuleDO.setUpdateDate(new Date());
            chiledMktStrategyConfRuleDO.setUpdateStaff(UserUtil.loginId());
            mktStrategyConfRuleMapper.insert(chiledMktStrategyConfRuleDO);
            mktStrategyConfRuleMap.put("mktStrategyConfRuleId", chiledMktStrategyConfRuleDO.getMktStrategyConfRuleId());
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktStrategyConfRuleMap.put("resultMsg", "复制成功！");
        } catch (Exception e) {
            logger.error("[op:MktStrategyConfRuleServiceImpl] failed to copyMktStrategyConfRule by parentMktStrategyConfRuleId = {},  Exception=", parentMktStrategyConfRuleId, e);
            mktStrategyConfRuleMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktStrategyConfRuleMap.put("resultMsg", "复制失败！");
        }
        return mktStrategyConfRuleMap;
    }


    /**
     * 复制二次协同渠道
     *
     * @param parentMktCamChlResultId
     * @return
     */
    public Map<String, Object> copyMktCamChlResult(Long parentMktCamChlResultId) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        try {
            MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(parentMktCamChlResultId);
            mktCamChlResultDO.setMktCamChlResultId(null);
            mktCamChlResultDO.setCreateDate(new Date());
            mktCamChlResultDO.setCreateStaff(UserUtil.loginId());
            mktCamChlResultDO.setUpdateDate(new Date());
            mktCamChlResultDO.setUpdateStaff(UserUtil.loginId());
            // 新增结果 并获取Id
            mktCamChlResultMapper.insert(mktCamChlResultDO);
            Long mktCamChlResultId = mktCamChlResultDO.getMktCamChlResultId();
            MktCamChlResult mktCamChlResult = BeanUtil.create(mktCamChlResultDO, new MktCamChlResult());
            // 获取原二次协同渠道下结果的推送渠道
            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(parentMktCamChlResultId);
            List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            // 遍历获取原二次协同渠道下结果的推送渠道
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                // 复制推送渠道
                Map<String, Object> mktCamChlConfMap = copyMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
                MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) mktCamChlConfMap.get("mktCamChlConfDetail");
                // 新的推送渠道与新的结果简历关联
                if (mktCamChlConfDetail != null) {
                    mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    // 结果与推送渠道的关联
                    MktCamChlResultConfRelDO childCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                    childCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResultId);
                    childCamChlResultConfRelDO.setEvtContactConfId(mktCamChlConfDetail.getEvtContactConfId());
                    childCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setCreateDate(new Date());
                    childCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setUpdateDate(new Date());
                    mktCamChlResultConfRelMapper.insert(childCamChlResultConfRelDO);
                }
            }
            mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResult", mktCamChlResult);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to get mktCamChlResultDO by mktCamChlResultId = {}", parentMktCamChlResultId);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return mktCamChlResultMap;
    }

    /**
     * 复制痛痒点
     *
     * @param contactConfId
     * @return
     */
    public Map<String, Object> copyVerbalToPrd(Long contactConfId) {
        Map<String, Object> map = new HashMap<>();
        try {
            //MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + contactConfId);
            MktCamChlConfDetail detail = null;
            MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
            if (detail == null) {
                // 新增协同渠道
                mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(contactConfId);
                // 获取原渠道的属性
                mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(contactConfId);
                detail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
                List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrList, mktCamChlConfAttrDOList);
                detail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            }
            if (detail.getVerbalVOList() == null) {
                List<MktVerbal> verbalList = mktVerbalMapper.findVerbalListByConfId(contactConfId);
                List<VerbalVO> voList = new ArrayList<>();
                for (MktVerbal verbal : verbalList) {
                    mktVerbalPrdMapper.insert(verbal);
                    List<MktVerbalCondition> conditions = mktVerbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
                    for (MktVerbalCondition verbalCondition : conditions) {
                        mktVerbalConditionPrdMapper.insert(verbalCondition);
                    }
                    VerbalVO verbalVO = BeanUtil.create(verbal, new VerbalVO());
                    List<VerbalConditionVO> conditionVOList = new ArrayList<>();
                    for (MktVerbalCondition condition : conditions) {
                        VerbalConditionVO vo = BeanUtil.create(condition, new VerbalConditionVO());
                        conditionVOList.add(vo);
                    }
                    verbalVO.setConditionList(conditionVOList);
                    voList.add(verbalVO);
                }
                detail.setVerbalVOList(voList);
            } else if (detail.getVerbalVOList() != null) {
                List<VerbalVO> verbalVOList = detail.getVerbalVOList();
                for (VerbalVO verbalVO : verbalVOList) {
                    MktVerbal mktVerbal = BeanUtil.create(verbalVO, new MktVerbal());
                    mktVerbalPrdMapper.insert(mktVerbal);
                    List<VerbalConditionVO> verbalConditionVOList = verbalVO.getConditionList();
                    for (VerbalConditionVO verbalConditionVO : verbalConditionVOList) {
                        MktVerbalCondition condition = BeanUtil.create(verbalConditionVO, new MktVerbalCondition());
                        mktVerbalConditionPrdMapper.insert(condition);
                    }
                }
            }
//            redisUtils.set("MktCamChlConfDetail_" + contactConfId, detail);
//            redisUtils_prd.set_prd("MktCamChlConfDetail_" + contactConfId, detail);
            map.put("resultCode", CODE_SUCCESS);
            map.put("resultMsg", "添加成功");
        } catch (Exception e) {
            logger.error("[op:copyVerbalToPrd] 复制痛痒点失败，Exception = ", e);
        }
        return map;
    }


    /**
     * 通过父推送渠道Id复制协同渠道
     *
     * @param parentEvtContactConfId
     * @return
     */
    public Map<String, Object> copyMktCamChlConf(Long parentEvtContactConfId) throws Exception {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            // 获取原协同渠道
            MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(parentEvtContactConfId);
            mktCamChlConfDO.setEvtContactConfId(null);
            mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
            mktCamChlConfDO.setCreateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfDO.setUpdateDate(new Date());
            // 新增协同渠道
            mktCamChlConfMapper.insert(mktCamChlConfDO);
            Long childEvtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            // 获取原渠道的属性
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(parentEvtContactConfId);
            // 获取原渠道的规则，通过parentEvtContactConfId获取规则放入属性中
            String rule = ruleSelect(parentEvtContactConfId);
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDONewList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                mktCamChlConfAttrDO.setContactChlAttrRstrId(null);
                mktCamChlConfAttrDO.setEvtContactConfId(childEvtContactConfId);
                mktCamChlConfAttrDONewList.add(mktCamChlConfAttrDO);
                if (mktCamChlConfAttrDO.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                    mktCamChlConfAttrDO.setAttrValue(childEvtContactConfId.toString());
                    //协同渠道自策略规则保存
                    mktCamChlConfAttrDO.setAttrValue(childEvtContactConfId.toString());
                    //  String params = mktCamChlConfAttrDO.getAttrValue();
                    ruleInsert(childEvtContactConfId, rule);
                }
                MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttr.setEvtContactConfId(childEvtContactConfId);
                mktCamChlConfAttrList.add(mktCamChlConfAttr);
            }
            // 批量插入
            mktCamChlConfAttrMapper.insertBatch(mktCamChlConfAttrDONewList);
            MktCamChlConfDetail mktCamChlConfDetailNew = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            mktCamChlConfDetailNew.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            redisUtils.set("MktCamChlConfDetail_" + mktCamChlConfDetailNew.getEvtContactConfId(), mktCamChlConfDetailNew);

            // 查询痛痒点话术列表
            copyVerbal(parentEvtContactConfId, childEvtContactConfId);

            // 查询脚本
            copyCamScript(parentEvtContactConfId, null, childEvtContactConfId);

            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetailNew);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to getMktCamChlConfDO by parentEvtContactConfId = {}", parentEvtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlConfMap;
    }

    /**
     * 复制活动脚本
     *
     * @param contactConfId
     * @param newConfId
     * @return
     */
    public Map<String, Object> copyCamScriptToPrd(Long contactConfId, Long newConfId) {
        Map<String, Object> result = new HashMap<>();
        try {
            //           MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + contactConfId);
            MktCamChlConfDetail detail = null;
            MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
            CamScript script = new CamScript();
            if (detail == null) {
                // 新增协同渠道
                mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(contactConfId);
                // 获取原渠道的属性
                mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(contactConfId);
                detail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
                List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrList, mktCamChlConfAttrDOList);
                detail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            }
            if (detail.getCamScript() == null) {
                script = mktCamScriptMapper.selectByConfId(contactConfId);
                if (script == null) {
                    result.put("resultCode", CODE_FAIL);
                    result.put("resultMsg", "活动脚本不存在");
                    return result;
                }
                mktCamScriptPrdMapper.insert(script);
                detail.setCamScript(script);
            } else if (detail.getCamScript() != null) {
                script = detail.getCamScript();
                if(script.getMktCampaignScptId()==null){
                    script = mktCamScriptMapper.selectByConfId(script.getEvtContactConfId());
                    detail.setCamScript(script);
                }
                mktCamScriptPrdMapper.insert(script);
            }
//            redisUtils.set("MktCamChlConfDetail_" + detail.getEvtContactConfId(), detail);
//            redisUtils_prd.set_prd("MktCamChlConfDetail_" + detail.getEvtContactConfId(), detail);
            result.put("resultCode", CODE_SUCCESS);
            result.put("resultMsg", script);
        } catch (Exception e) {
            logger.error("[op:copyCamScriptToPrd] 复制脚本contactConfId = {}到生产环境失败，Exception = ", contactConfId, e);
        }
        return result;
    }

    /**
     * 递归规则并保存
     *
     * @param evtContactConfId
     * @param rule
     * @return
     */
    public Long saveDetail(Long evtContactConfId, Rule rule) {
        //保存规则
        List<RuleDetail> list = rule.getListData();
        Long idLeft;
        Long idRight;
        String type = rule.getType();
        if (list.size() > 0) {  //第一层
            //先保存第一条标签因子
            idLeft = insert(evtContactConfId, list.get(0).getId(), list.get(0).getOperType(), list.get(0).getContent());
            for (int i = 1; i < list.size(); i++) {
                idRight = insert(evtContactConfId, list.get(i).getId(), list.get(i).getOperType(), list.get(i).getContent());
                idLeft = insert(evtContactConfId, idLeft, type, idRight);
            }
            if (rule.getRuleChildren() != null) {
                idLeft = insert(evtContactConfId, idLeft, type, saveDetail(evtContactConfId, rule.getRuleChildren()));
            }
        } else {
            idLeft = 0L;
        }
        return idLeft;
    }

    /**
     * 子策略规则插入数据库（表达式类型）
     *
     * @param evtContactConfId
     * @param left
     * @param operType
     * @param right
     * @return
     */
    public Long insert(Long evtContactConfId, Long left, String operType, Long right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right.toString());
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("2000"); //表达式
        mktVerbalCondition.setRightParamType("2000"); //表达式
        mktVerbalCondition.setConditionType("1");
        mktVerbalConditionMapper.insert(mktVerbalCondition);
        return mktVerbalCondition.getConditionId();
    }

    /**
     * 子策略规则插入数据库（标签因子类型）
     *
     * @param evtContactConfId
     * @param left
     * @param operType
     * @param right
     * @return
     */
    public Long insert(Long evtContactConfId, Integer left, String operType, String right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right);
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("1000"); //标签因子
        mktVerbalCondition.setRightParamType("3000"); //固定值
        mktVerbalCondition.setConditionType("1");
        mktVerbalConditionMapper.insert(mktVerbalCondition);
        return mktVerbalCondition.getConditionId();
    }


    /**
     * 复制客户分群 返回
     *
     * @param tarGrpId
     * @return
     */

    public Map<String, Object> copyTarGrp(Long tarGrpId, boolean isCopy) {
        Map<String,Object> result = new HashMap<>();
        TarGrpDetail detail = (TarGrpDetail)redisUtils.get("TAR_GRP_"+tarGrpId);
        if (detail==null){
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
            if (tarGrp!=null){
                detail = BeanUtil.create(tarGrp,new TarGrpDetail());
                List<TarGrpCondition> conditions = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                detail.setTarGrpConditions(conditions);
            }else {
                return null;
            }
        }
        result = createTarGrp(detail,isCopy);
        return result;
    }

    /**
     * 新增目标分群
     */
    @Transactional(readOnly = false)
    public Map<String, Object> createTarGrp(TarGrpDetail tarGrpDetail, boolean isCopy) {
        Map<String, Object> maps = new HashMap<>();
        try {
            //插入客户分群记录
            TarGrp tarGrp = new TarGrp();
            tarGrp = tarGrpDetail;
            tarGrp.setTarGrpType(tarGrpDetail.getTarGrpType()==null ? "1000" : tarGrpDetail.getTarGrpType());
            tarGrp.setCreateDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateDate(DateUtil.getCurrentTime());
            tarGrp.setStatusDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateStaff(UserUtil.loginId());
            tarGrp.setCreateStaff(UserUtil.loginId());
            if (isCopy){
                tarGrp.setStatusCd(StatusCode.STATUS_CODE_FAILURE.getStatusCode());
            }else {
                tarGrp.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            }
            tarGrpMapper.createTarGrp(tarGrp);
            List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
            List<TarGrpCondition> conditionList = new ArrayList<>();
            if(tarGrpConditions!=null && tarGrpConditions.size()>0){
                for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
                    if (tarGrpCondition.getOperType()==null || tarGrpCondition.getOperType().equals("")){
                        maps.put("resultCode", CODE_FAIL);
                        maps.put("resultMsg", "请选择下拉框运算类型");
                        return maps;
                    }
//                    if (tarGrpCondition.getAreaIdList()!=null){
//                        area2RedisThread(tarGrp, tarGrpCondition);
//                    }
                    tarGrpCondition.setConditionId(null);
                    tarGrpCondition.setRootFlag(0L);
                    tarGrpCondition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    tarGrpCondition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                    tarGrpCondition.setCreateDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setUpdateDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setStatusDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setUpdateStaff(UserUtil.loginId());
                    tarGrpCondition.setCreateStaff(UserUtil.loginId());
                    if (isCopy){
                        tarGrpCondition.setStatusCd(StatusCode.STATUS_CODE_FAILURE.getStatusCode());
                    }else if (tarGrpCondition.getStatusCd()==null){
                        tarGrpCondition.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    }
                    conditionList.add(tarGrpCondition);
                }
                tarGrpConditionMapper.insertByBatch(conditionList);
            }
            //数据加入redis
            TarGrpDetail detail = BeanUtil.create(tarGrp,new TarGrpDetail());
            detail.setTarGrpConditions(conditionList);
            redisUtils.set("TAR_GRP_"+tarGrp.getTarGrpId(),detail);
            //插入客户分群条件
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("tarGrp", tarGrp);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return maps;
    }

    /**
     * 递归查询规则
     *
     * @param labels
     * @param expressions
     * @param index
     * @return
     */

    public Rule parseRules(List<MktVerbalCondition> labels, List<MktVerbalCondition> expressions, int index) {
        Rule rule = new Rule();
        List<RuleDetail> ruleDetails = new ArrayList<>();
        RuleDetail ruleDetail;

        //遍历所有表达式
        if (expressions.size() > 0) {
            rule.setType(expressions.get(index).getOperType());
            for (int i = index; i < expressions.size(); i++) {
                //判断类型  如果不相同就进入下一级
                if (rule.getType().equals(expressions.get(i).getOperType())) {
                    for (MktVerbalCondition condition : labels) {
                        if (expressions.get(i).getLeftParam().equals(condition.getConditionId().toString()) || expressions.get(i).getRightParam().equals(condition.getConditionId().toString())) {
                            ruleDetail = new RuleDetail();
                            ruleDetail.setId(Integer.parseInt(condition.getLeftParam()));
                            //查询获取标签因子名称
                            Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(condition.getLeftParam()));
                            if (label != null) {
                                String[] operators = label.getOperator().split(",");
                                List<OperatorDetail> operatorDetailList = new ArrayList<>();
                                for (String operator : operators) {
                                    OperatorDetail operatorDetail = new OperatorDetail();
                                    operatorDetail.setOperValue(Integer.valueOf(operator));
                                    operatorDetail.setOperName(Operator.getOperator(Integer.valueOf(operator)).getDescription());
                                    operatorDetailList.add(operatorDetail);
                                }

                                List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());
                                List<LabelValueVO> valueList = ChannelUtil.valueList2VOList(labelValues);
                                ruleDetail.setValueList(valueList);
                                ruleDetail.setOperatorList(operatorDetailList);
                                ruleDetail.setName(label.getInjectionLabelName());
                                ruleDetail.setConditionType(label.getConditionType());
                            } else {
                                ruleDetail.setName("");
                            }
                            ruleDetail.setContent(condition.getRightParam());
                            ruleDetail.setOperType(condition.getOperType());
                            ruleDetail.setOperTypeName(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
                            ruleDetails.add(ruleDetail);
                        }
                    }
                } else {
                    rule.setRuleChildren(parseRules(labels, expressions, i));
                    break;
                }
            }
        }

        //判断是否是一个标签的情况
        if (labels.size() == 1) {
            rule.setType("1000");
            ruleDetail = new RuleDetail();
            ruleDetail.setId(Integer.parseInt(labels.get(0).getLeftParam()));
            //查询获取标签因子名称
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(labels.get(0).getLeftParam()));
            if (label != null) {
                ruleDetail.setName(label.getInjectionLabelName());
            } else {
                ruleDetail.setName("");
            }
            ruleDetail.setContent(labels.get(0).getRightParam());
            ruleDetail.setOperType(labels.get(0).getOperType());
            ruleDetails.add(ruleDetail);
        }

        if (ruleDetails.size() == 0) {
            return null;
        }

        rule.setListData(ruleDetails);
        return rule;
    }


    private Map<String, Object> copyItemByCampaignPublish(Long oldCampaignId, Long newCampaignId, String mktCampaignCategory) {
        Map<String,Object> result = new HashMap<>();
        List<Long> idList = new ArrayList<>();
        List<MktCamItem> oldItemList = camItemMapper.selectByCampaignId(oldCampaignId);
        for (MktCamItem item : oldItemList){
            MktCamItem newItem = BeanUtil.create(item,new MktCamItem());
            if(StatusCode.ENFORCEMENT_CAMPAIGN.getStatusCode().equals(mktCampaignCategory)){
                newItem.setStatusCd(StatusCode.STATUS_CODE_FAILURE.getStatusCode());
            } else {
                newItem.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            }

            newItem.setMktCamItemId(null);
            newItem.setMktCampaignId(newCampaignId);
            camItemMapper.insert(newItem);
            idList.add(newItem.getMktCamItemId());
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",idList);
        return result;
    }

    /**
     * 保存协同渠道子策略规则
     *
     * @param evtContactConfId
     * @param param
     */
    private void ruleInsert(Long evtContactConfId, String param) {
        //转换为json对象
        if (param != null && !"null".equals(param)) {
            JSONObject jsonObject = JSONObject.parseObject(param);
            System.out.println(jsonObject.toString());
            //解析参数
            Rule rule = jsonObject.toJavaObject(Rule.class);
            //保存
            saveDetail(evtContactConfId, rule);
        }
    }


    /**
     * 查询协同子策略规则并拼接格式
     *
     * @param evtContactConfId
     * @return
     */
    private String ruleSelect(Long evtContactConfId) {
        //唯一ID
        //查询出所有规则
        List<MktVerbalCondition> mktVerbalConditions = (List<MktVerbalCondition>) redisUtils.get("RULE_BERBALC_CONDITION_" + evtContactConfId);
        if (mktVerbalConditions == null) {
            mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);
        }


        List<MktVerbalCondition> labels = new ArrayList<>(); //标签因子
        List<MktVerbalCondition> expressions = new ArrayList<>(); //表达式

        //将标签和表达式分类
        for (MktVerbalCondition mktVerbalCondition : mktVerbalConditions) {
            if ("1000".equals(mktVerbalCondition.getLeftParamType())) {
                labels.add(mktVerbalCondition);
            } else if ("2000".equals(mktVerbalCondition.getLeftParamType())) {
                expressions.add(mktVerbalCondition);
            }
        }
        Rule rule = parseRules(labels, expressions, 0);
        return JSON.toJSONString(rule);
    }


    /**
     * 复制痛痒点
     * @param contactConfId
     * @return
     */
    private Map<String, Object> copyVerbal(Long contactConfId,Long newConfId) {
        Map<String,Object> map = new HashMap<>();
        MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_"+contactConfId);
        List<VerbalVO> verbalVOList = new ArrayList<>();
        if (detail==null){
            verbalVOList = ( List<VerbalVO>)getVerbalListByConfId(1L,contactConfId).get("resultMsg");
        }else {
            verbalVOList = detail.getVerbalVOList();
        }
        if(verbalVOList!=null &&verbalVOList.size()>0){
            for (VerbalVO verbalVO : verbalVOList){
                VerbalAddVO addVO = BeanUtil.create(verbalVO,new VerbalAddVO());
                addVO.setContactConfId(newConfId);
                List<VerbalConditionAddVO> conditionAddVOList = new ArrayList<>();
                for (VerbalConditionVO conditionVO : verbalVO.getConditionList()){
                    VerbalConditionAddVO conditionAddVO = BeanUtil.create(conditionVO,new VerbalConditionAddVO());
                    conditionAddVOList.add(conditionAddVO);
                }
                addVO.setAddVOList(conditionAddVOList);
                Map<String, Object> addMap = addVerbal(1L,addVO);
                if (!addMap.get("resultCode").equals(CODE_SUCCESS)){
                    return addMap;
                }
            }
        }
        map.put("resultCode", CODE_SUCCESS);
        map.put("resultMsg", "添加成功");
        return map;
    }

    private Map<String, Object> getVerbalListByConfId(Long userId, Long confId) {
        Map<String, Object> result = new HashMap<>();
        //todo 推送渠道对象
        List<MktVerbal> verbalList = verbalMapper.findVerbalListByConfId(confId);
        List<VerbalVO> verbalVOS = new ArrayList<>();
        for (MktVerbal verbal : verbalList) {
            if (verbal == null) {
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "痛痒点话术不存在");
                return result;
            }
            VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal), verbal);
            verbalVOS.add(verbalVO);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", verbalVOS);
        return result;
    }

    /**
     * 痛痒点话术返回结果包装
     */
    private VerbalVO supplementVo(VerbalVO verbalVO, MktVerbal verbal) {
        List<VerbalConditionVO> conditionVOList = new ArrayList<>();
        List<MktVerbalCondition> conditions = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions) {
            VerbalConditionVO vo = BeanUtil.create(condition, new VerbalConditionVO());
            vo.setOperName(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
            if (condition.getLeftParam()!=null && !condition.getLeftParamType().equals("2000")) {
                Label label = labelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                if (label!=null){
                    if (label.getConditionType()!=null && !label.getConditionType().equals("")){
                        vo.setConditionType(label.getConditionType());
                    }
                    vo.setLeftParamName(label.getInjectionLabelName());
                    List<LabelValue> valueList = labelValueMapper.selectByLabelId(label.getInjectionLabelId());
                    if (!valueList.isEmpty()) {
                        vo.setValueList(ChannelUtil.valueList2VOList(valueList));
                    }
                    setOperator(vo, label);
                }
            }
            conditionVOList.add(vo);
        }
        verbalVO.setConditionList(conditionVOList);
        Channel channel = channelMapper.selectByPrimaryKey(verbalVO.getChannelId());
        if (channel!=null){
            verbalVO.setChannelName(channel.getContactChlName());
            verbalVO.setChannelParentId(channel.getParentId());
            Channel parent = channelMapper.selectByPrimaryKey(channel.getParentId());
            if (parent!=null){
                verbalVO.setChannelParentName(parent.getContactChlName());
            }
        }
        return verbalVO;
    }

    private void setOperator(VerbalConditionVO vo, Label label) {
        if (label.getOperator() != null && !label.getOperator().equals("")) {
            List<String> opratorList = ChannelUtil.StringToList(label.getOperator());
            List<OperatorDetail> opStList = new ArrayList<>();
            for (String operator : opratorList) {
                Operator op = Operator.getOperator(Integer.valueOf(operator));
                OperatorDetail detail = new OperatorDetail();
                if (op != null) {
                    detail.setOperValue(op.getValue());
                    detail.setOperName(op.getDescription());
                }
                opStList.add(detail);
            }
            vo.setOperatorList(opStList);
        }
    }


    /**
     * 添加痛痒点话术
     */
    private Map<String, Object> addVerbal(Long userId, VerbalAddVO addVO) {
        Map<String, Object> result = new HashMap<>();

        MktVerbal verbal = BeanUtil.create(addVO, new MktVerbal());
        //todo 活动id 通过配置获取 或直接删除
        verbal.setCampaignId(1000L);
        verbal.setCreateDate(new Date());
        verbal.setCreateStaff(userId);
        verbal.setStatusCd("1000");
        verbalMapper.insert(verbal);
        //删除旧的条件
        List<MktVerbalCondition> conditions = new ArrayList<>();
        for (VerbalConditionAddVO vcAddVO : addVO.getAddVOList()) {
            if (vcAddVO.getOperType()==null){

            }
            //类型为标签时
            MktVerbalCondition mktVerbalCondition = BeanUtil.create(vcAddVO, new MktVerbalCondition());
            mktVerbalCondition.setVerbalId(verbal.getVerbalId());
            //标签类型
            if (vcAddVO.getLeftParamType().equals("1000")) {
                mktVerbalCondition.setRightParamType("3000"); //固定值
            } else {
                mktVerbalCondition.setRightParamType("2000");
            }
            mktVerbalCondition.setConditionType(ConditionType.CHANNEL.getValue().toString());
            conditions.add(mktVerbalCondition);
        }
        if (conditions.size()>0){
            verbalConditionMapper.insertByBatch(conditions);
        }

        //更新redis分群数据,先查出来再更新
        MktCamChlConfDetail detail = (MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+addVO.getContactConfId());
        if (detail!=null){
            VerbalVO verbalVO = BeanUtil.create(verbal,new VerbalVO());
            List<VerbalConditionVO> conditionVOList = new ArrayList<>();
            for (MktVerbalCondition condition : conditions){
                VerbalConditionVO vo = BeanUtil.create(condition,new VerbalConditionVO());
                conditionVOList.add(vo);
            }
            verbalVO.setConditionList(conditionVOList);
            List<VerbalVO> voList = new ArrayList<>();
            voList.add(verbalVO);
            detail.setVerbalVOList(voList);
            redisUtils.set("MktCamChlConfDetail_"+addVO.getContactConfId(),detail);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", "添加成功");
        return result;
    }


    private Map<String, Object> copyCamScript(Long contactConfId, String scriptDesc, Long newConfId) {
        Map<String,Object> result = new HashMap<>();
/*
        CamScript script = camScriptMapper.selectByConfId(contactConfId);
        if (script==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","活动脚本不存在");
            return result;
        }*/
        CamScript newScript = new CamScript();
        CamScript camScript = new CamScript();
        MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + contactConfId);
        if (detail == null) {
            camScript = camScriptMapper.selectByConfId(contactConfId);
        } else {
            camScript = detail.getCamScript();
            // 从缓存中拿不到
            if(camScript ==null){
                camScript = camScriptMapper.selectByConfId(contactConfId);
            }
        }
        // 从数据库中查询不到
        if(camScript == null){
            if(scriptDesc != null){
                newScript.setScriptDesc(scriptDesc);
            }
        } else{
            newScript.setScriptDesc(camScript.getScriptDesc());
        }
        newScript.setMktCampaignId(0L);
        newScript.setEvtContactConfId(newConfId);
        newScript.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        newScript.setCreateDate(new Date());
        newScript.setCreateStaff(UserUtil.loginId());
        newScript.setUpdateDate(new Date());
        newScript.setUpdateStaff(UserUtil.loginId());
        camScriptMapper.insert(newScript);
        //更新redis推送渠道配置
        MktCamChlConfDetail de = (MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+newConfId);
        if (detail!=null){
            detail.setCamScript(newScript);
            redisUtils.set("MktCamChlConfDetail_"+newConfId,detail);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",newScript);
        return result;
    }

    /**
     * 复制首次协同 to prd
     *
     * @param parentEvtContactConfId
     * @return
     * @throws Exception
     */
    public Map<String, Object> copyMktCamChlConfToPrd(Long parentEvtContactConfId) throws Exception {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            // 获取原协同渠道
//            MktCamChlConfDetail detail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + parentEvtContactConfId);
            MktCamChlConfDetail detail = null;
            MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
            if (detail == null) {
                // 新增协同渠道
                mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(parentEvtContactConfId);
                // 获取原渠道的属性
                mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(parentEvtContactConfId);
                detail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
                List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrList, mktCamChlConfAttrDOList);
                detail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
//                redisUtils.set("MktCamChlConfDetail_" + detail.getEvtContactConfId(), detail);
            } else {
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, detail);
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrDOList, detail.getMktCamChlConfAttrList());
            }
            mktCamChlConfPrdMapper.insert(mktCamChlConfDO);
            // 获取原渠道的规则，通过parentEvtContactConfId获取规则放入属性中
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                mktCamChlConfAttrPrdMapper.insert(mktCamChlConfAttrDO);
                //协同渠道自策略规则保存
                List<MktVerbalCondition> mktVerbalConditionList = mktVerbalConditionMapper.findConditionListByVerbalId(mktCamChlConfAttrDO.getEvtContactConfId());
                for (MktVerbalCondition mktVerbalCondition : mktVerbalConditionList) {
                    mktVerbalConditionPrdMapper.insert(mktVerbalCondition);
                }
            }

            // 查询痛痒点话术列表
            copyVerbalToPrd(parentEvtContactConfId);

            // 查询脚本
            copyCamScriptToPrd(parentEvtContactConfId, parentEvtContactConfId);

//            redisUtils.set("MktCamChlConfDetail_" + detail.getEvtContactConfId(), detail);

            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDO", mktCamChlConfDO);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to getMktCamChlConfDO by parentEvtContactConfId = {}", parentEvtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlConfMap;
    }


    /**
     * 复制二次协同渠道 to prd
     *
     * @param parentMktCamChlResultId
     * @return
     */
    public Map<String, Object> copyMktCamChlResultToPrd(Long parentMktCamChlResultId) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        try {
//            MktCamChlResult mktCamChlResult = (MktCamChlResult) redisUtils.get("MktCamChlResult_" + parentMktCamChlResultId);
            MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(parentMktCamChlResultId);
            // 新增结果 并获取Id
            mktCamChlResultPrdMapper.insert(mktCamChlResultDO);
            // 获取原二次协同渠道下结果的推送渠道
            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(parentMktCamChlResultId);
            // 遍历获取原二次协同渠道下结果的推送渠道
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                mktCamChlResultConfRelPrdMapper.insert(mktCamChlResultConfRelDO);
                // 复制推送渠道
                copyMktCamChlConfToPrd(mktCamChlResultConfRelDO.getEvtContactConfId());
            }
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResultDO", mktCamChlResultDO);
        } catch (Exception e) {
            logger.error("[op:MktCamChlResultServiceImpl] failed to get mktCamChlResultDO by mktCamChlResultId = {}", parentMktCamChlResultId);
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlResultMap.put("resultMsg", ErrorCode.GET_MKT_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlResultMap;
    }


}
