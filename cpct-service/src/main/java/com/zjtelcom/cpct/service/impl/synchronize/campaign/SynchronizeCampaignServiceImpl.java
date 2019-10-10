package com.zjtelcom.cpct.service.impl.synchronize.campaign;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/07/17 11:11
 * @version: V1.0
 */

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.dao.filter.MktStrategyCloseRuleRelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.domain.strategy.*;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.dto.channel.VerbalConditionVO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamDisplayColumnRelService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynchronizeCampaignService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.RedisUtils_prd;
import com.zjtelcom.cpct_prd.dao.campaign.*;
import com.zjtelcom.cpct_prd.dao.channel.ContactChannelPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktCamScriptPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktVerbalConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktVerbalPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelPrdMapper;
import com.zjtelcom.cpct_prd.dao.label.InjectionLabelValuePrdMapper;
import com.zjtelcom.cpct_prd.dao.strategy.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/17 10:38
 * @version: V1.0
 */
@Service
@Transactional
public class SynchronizeCampaignServiceImpl extends BaseService implements SynchronizeCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private MktCampaignPrdMapper mktCampaignPrdMapper;

    @Autowired
    private MktCamCityRelMapper mktCamCityRelMapper;

    @Autowired
    private MktCamCityRelPrdMapper mktCamCityRelPrdMapper;

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;

    @Autowired
    private MktCamEvtRelPrdMapper mktCamEvtRelPrdMapper;

    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;

    @Autowired
    private MktCamStrategyConfRelPrdMapper mktCamStrategyConfRelPrdMapper;

    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Autowired
    private MktStrategyConfPrdMapper mktStrategyConfPrdMapper;

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

    @Autowired
    private MktStrategyConfRulePrdMapper mktStrategyConfRulePrdMapper;

    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;

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
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private MktCamChlConfPrdMapper mktCamChlConfPrdMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    @Autowired
    private MktCamChlConfAttrPrdMapper mktCamChlConfAttrPrdMapper;

    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;

    @Autowired
    private MktCamChlResultPrdMapper mktCamChlResultPrdMapper;

    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired
    private MktCamChlResultConfRelPrdMapper mktCamChlResultConfRelPrdMapper;

    @Autowired
    private MktVerbalMapper mktVerbalMapper;

    @Autowired
    private MktVerbalPrdMapper mktVerbalPrdMapper;

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;

    @Autowired
    private MktVerbalConditionPrdMapper mktVerbalConditionPrdMapper;

    @Autowired
    private InjectionLabelPrdMapper injectionLabelPrdMapper;
    @Autowired
    private ContactChannelPrdMapper contactChannelPrdMapper;
    @Autowired
    private InjectionLabelValuePrdMapper injectionLabelValuePrdMapper;

    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;

    @Autowired
    private MktCamScriptPrdMapper mktCamScriptPrdMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired
    private MktStrategyCloseRuleRelMapper mktStrategyCloseRuleRelMapper;

    @Autowired
    private MktStrategyCloseRuleRelPrdMapper mktStrategyCloseRuleRelPrdMapper;

    @Autowired
    private MktCamDisplayColumnRelPrdMapper mktCamDisplayColumnRelPrdMapper;

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    @Autowired
    private MktCamDisplayColumnRelService mktCamDisplayColumnRelService;
    //同步表名
    private static final String tableName = "mkt_campaign";

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisUtils_prd redisUtils_prd;

    /**
     * 准生产同步活动到生产环境--多数据源
     *
     * @param mktCampaignId
     * @param roleName
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> synchronizeCampaign(Long mktCampaignId, String roleName) throws Exception {
        // 判断该活动是否存在
        Map<String, Object> synchronizeCampaignMap = new HashMap<>();
        MktCampaignDO mktCampaignPrdDO = mktCampaignPrdMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        if (mktCampaignPrdDO != null) {
            // 更新活动
            mktCampaignPrdMapper.updateByPrimaryKey(mktCampaignDO);
            synchronizeRecordService.addRecord(roleName, tableName, mktCampaignDO.getMktCampaignId(), SynchronizeType.update.getType());
        } else {
            // 新增活动
            mktCampaignPrdMapper.insert(mktCampaignDO);
            synchronizeRecordService.addRecord(roleName, tableName, mktCampaignDO.getMktCampaignId(), SynchronizeType.add.getType());
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

        // 删除与活动关联的过滤规则
        mktStrategyFilterRuleRelPrdMapper.deleteByStrategyId(mktCampaignDO.getMktCampaignId());

        // 删除与活动关联的关单规则
        mktStrategyCloseRuleRelPrdMapper.deleteByStrategyId(mktCampaignDO.getMktCampaignId());

        //获取活动对应的过滤规则
        List<Long> ruleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktCampaignDO.getMktCampaignId());
        List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOList = mktStrategyFilterRuleRelMapper.selectRuleByStrategyId(mktCampaignDO.getMktCampaignId());
        // 与新的活动建立关联
        for (MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO : mktStrategyFilterRuleRelDOList) {
            mktStrategyFilterRuleRelPrdMapper.insert(mktStrategyFilterRuleRelDO);
        }

        List<MktStrategyCloseRuleRelDO> mktStrategyCloseRuleRelDOList = mktStrategyCloseRuleRelMapper.selectRuleByStrategyId(mktCampaignDO.getMktCampaignId());
        for (MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO : mktStrategyCloseRuleRelDOList) {
            mktStrategyCloseRuleRelPrdMapper.insert(mktStrategyCloseRuleRelDO);
        }


        mktCamDisplayColumnRelPrdMapper.deleteByMktCampaignId(mktCampaignId);

        // 试算展示列实例化同步
        mktCamDisplayColumnRelService.syncMktCamDisplayColumnRel(mktCampaignId);

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
     * 更新活动下的所有redis
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> updateCampaignRedis(Long mktCampaignId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        // 查询活动下的所有规则
        try {
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRulePrdMapper.selectByCampaignId(mktCampaignId);
            for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                // 更新客户分群redis
                Long tarGrpId = mktStrategyConfRuleDO.getTarGrpId();
                if (tarGrpId != null) {
                    TarGrp tarGrp = tarGrpPrdMapper.selectByPrimaryKey(tarGrpId);
                    TarGrpDetail tarGrpDetail = BeanUtil.create(tarGrp, new TarGrpDetail());
                    List<TarGrpCondition> conditionDOList = tarGrpConditionPrdMapper.listTarGrpCondition(tarGrpId);
                    tarGrpDetail.setTarGrpConditions(conditionDOList);
                    redisUtils_prd.set("TAR_GRP_" + tarGrpId, tarGrpDetail);
                }

                // 更新推荐条目redis
                String productIds = mktStrategyConfRuleDO.getProductId();
                if (productIds != null) {
                    String[] itemIds = productIds.split("/");
                    if (itemIds != null && !"".equals(itemIds[0])) {
                        for (int i = 0; i < itemIds.length; i++) {
                            MktCamItem mktCamItem = mktCamItemPrdMapper.selectByPrimaryKey(Long.valueOf(itemIds[i]));
                            redisUtils_prd.set("MKT_CAM_ITEM_" + mktCamItem.getMktCamItemId(), mktCamItem);
                        }
                    }
                }

                // 更新首次协同
                String evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId();
                if (evtContactConfIds != null) {
                    // 分割推动渠道id
                    String[] evtContactConfIdArray = evtContactConfIds.split("/");
                    if (evtContactConfIdArray != null && !"".equals(evtContactConfIdArray[0])) {
                        List<Long> evtContactConfIdList = new ArrayList<>();
                        for (String evtContactConfId : evtContactConfIdArray) {
                            evtContactConfIdList.add(Long.valueOf(evtContactConfId));
                        }
                        List<MktCamChlConfDetail> mktCamChlConfDetailList = getMktCamChlConfDetailList(evtContactConfIdList);
                        for (MktCamChlConfDetail mktCamChlConfDetail : mktCamChlConfDetailList) {
                            redisUtils_prd.set("MktCamChlConfDetail_" + mktCamChlConfDetail.getEvtContactConfId(), mktCamChlConfDetail);
                        }
                    }
                }

                //更新二次协同
                String mktCamChlResultId = mktStrategyConfRuleDO.getMktCamChlResultId();
                if (mktCamChlResultId!=null && !"".equals(mktCamChlResultId)){
                    String[] mktCamChlResultIds = mktCamChlResultId.split("/");
                    for (int i = 0; i <mktCamChlResultIds.length ; i++) {
                        MktCamChlResultDO mktCamChlResultDO = mktCamChlResultPrdMapper.selectByPrimaryKey(Long.valueOf(mktCamChlResultIds[i]));
                        MktCamChlResult mktCamChlResult = BeanUtil.create(mktCamChlResultDO, new MktCamChlResult());
                        List<Long> evtContactConfIdList = mktCamChlResultConfRelPrdMapper.selectConfIdByResultId(mktCamChlResult.getMktCamChlResultId());
                        List<MktCamChlConfDetail> mktCamChlConfDetailList = getMktCamChlConfDetailList(evtContactConfIdList);
                        mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                        redisUtils_prd.set("MktCamChlResult_" + mktCamChlResult.getMktCamChlResultId(), mktCamChlResult);
                    }
                }
            }
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "更新生产环境redis成功！");
        } catch (Exception e) {
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "更新生产环境redis失败！");
           logger.error("[op:SynchronizeCampaignServiceImpl] failed to updateCampaignRedis by mktCampaignId = {} , Exception = ", mktCampaignId, e);
        }
        return resultMap;
    }



    /**
     * 删除活动下的所有redis -- 生成
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> deleteCampaignRedisProd(Long mktCampaignId) {
        Map<String, Object> resultMap = new HashMap<>();
        try{
            MktCampaignDO mktCampaignDO = mktCampaignPrdMapper.selectByPrimaryKey(mktCampaignId);
            // 删除活动缓存
            redisUtils_prd.del("MKT_CAMPAIGN_" + mktCampaignId);
            // 删除过滤规则缓存
            List<Long> longList = mktStrategyFilterRuleRelPrdMapper.selectByStrategyId(mktCampaignId);
            redisUtils_prd.del("MKT_FILTER_RULE_IDS_" + mktCampaignId);
            for (Long filterRuleId : longList) {
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

                    redisUtils_prd.del("MKT_CAMCHL_CONF_LIST_" + mktStrategyConfRuleDO.getMktStrategyConfRuleId());

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
     * 删除活动下的所有redis -- 准生成
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> deleteCampaignRedisPre(Long mktCampaignId) {
        Map<String, Object> resultMap = new HashMap<>();
        try{
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
            // 删除活动缓存
            redisUtils.del("MKT_CAMPAIGN_" + mktCampaignId);
//            // 删除活动对应数据的所有标签缓存
//            List<MktCamEvtRelDO> mktCamEvtRelDOS = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignId);
//            for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOS) {
//                redisUtils.del("EVT_ALL_LABEL_" + mktCamEvtRelDO.getEventId());
//            }

            // 删除过滤规则缓存
            List<Long> longList = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktCampaignId);
            redisUtils.del("MKT_FILTER_RULE_IDS_" + mktCampaignId);
            for (Long filterRuleId : longList) {
                redisUtils.del("FILTER_RULE_DISTURB_" + filterRuleId);
            }

            // 删除展示列的标签
            redisUtils.del("MKT_ISALE_LABEL_" + mktCampaignDO.getIsaleDisplay());
            ExecutorService executorService = Executors.newCachedThreadPool();
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOS = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
            for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOS) {

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 删除策略关系缓存

                        redisUtils.del("MKT_STRATEGY_REL_" + mktCamStrategyConfRelDO.getStrategyConfId());
                        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktCamStrategyConfRelDO.getStrategyConfId());
                        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
                            // 删除客户分群标签
                            redisUtils.del("RULE_ALL_LABEL_" + mktStrategyConfRuleDO.getTarGrpId());
                            //表达式存入redis
                            redisUtils.del("EXPRESS_" + mktStrategyConfRuleDO.getTarGrpId());

                            // 删除推荐条目
                            if (mktStrategyConfRuleDO.getProductId() != null) {
                                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                                if (productIds != null && !"".equals(productIds[0])) {
                                    for (String productId : productIds) {
                                        redisUtils.del("MKT_CAM_ITEM_" + productId);
                                    }
                                }
                            }

                            redisUtils.del("MKT_CAMCHL_CONF_LIST_" + mktStrategyConfRuleDO.getMktStrategyConfRuleId());

                            // 删除推送渠道
                            if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                                if (evtContactConfIds != null && !"".equals(evtContactConfIds[0])) {
                                    for (String evtContactConfId : evtContactConfIds) {
                                        redisUtils.del("CHL_CONF_DETAIL_" + evtContactConfIds);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            resultMap.put("resultMsg", "删除准生产环境redis成功！");
        } catch (Exception e) {
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
            resultMap.put("resultMsg", "删除准生产环境redis失败！");
            logger.error("[op:SynchronizeCampaignServiceImpl] failed to delete campaignRedisPre by mktCampaignId = {} , Exception = ", mktCampaignId, e);
        }
        return resultMap;
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
                        copyMktCamChlConf(Long.valueOf(evtContactConfIds[i]));
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
                    copyMktCamChlResult(Long.valueOf(mktCamChlResultIds[i]));
                }
            }

        }
        mktStrategyConfRuleMap.put("mktStrategyConfRuleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
        return mktStrategyConfRuleMap;
    }


    /**
     * 复制客户分群 返回
     *
     * @param tarGrpId
     * @param isCopy   是否为
     * @return
     */
    public Map<String, Object> copyTarGrp(Long tarGrpId, boolean isCopy) throws Exception {
        Map<String, Object> result = new HashMap<>();
        //从redis获取
//        TarGrpDetail detail = (TarGrpDetail) redisUtils.get("TAR_GRP_" + tarGrpId);
//        if (detail == null) {
        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
        if (tarGrp == null) {
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "请选择下拉框运算类型");
            return result;
        }
        List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
        TarGrpDetail detail = BeanUtil.create(tarGrp, new TarGrpDetail());
        detail.setTarGrpConditions(conditionList);
//        redisUtils.set("TAR_GRP_" + tarGrpId, detail);
        result = createTarGrp(detail, isCopy);
        // 将客户分群数据同步到生产环境redis
//        redisUtils_prd.set_prd("TAR_GRP_" + tarGrpId, detail);
        return result;
    }

    /**
     * 生产环境同步新增目标分群
     */
    public Map<String, Object> createTarGrp(TarGrpDetail tarGrpDetail, boolean isCopy) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        TarGrp tarGrp = BeanUtil.create(tarGrpDetail, new TarGrp());
        tarGrpPrdMapper.createTarGrp(tarGrp);
        List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
        for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
            tarGrpConditionPrdMapper.insert(tarGrpCondition);
        }

        //插入客户分群条件
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrp);
        return maps;
    }

    /**
     * 复制首次协同
     *
     * @param parentEvtContactConfId
     * @return
     * @throws Exception
     */
    public Map<String, Object> copyMktCamChlConf(Long parentEvtContactConfId) throws Exception {
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
     * 查询协同子策略规则并拼接格式
     *
     * @param evtContactConfId
     * @return
     */
    public String ruleSelect(Long evtContactConfId) {
        //唯一ID
        //查询出所有规则
        List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);

        List<MktVerbalCondition> labels = new ArrayList<>(); //标签因子
        List<MktVerbalCondition> expressions = new ArrayList<>(); //表达式

        //分类
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
                                ruleDetail.setName(label.getInjectionLabelName());
                            } else {
                                ruleDetail.setName("");
                            }
                            ruleDetail.setContent(condition.getRightParam());
                            ruleDetail.setOperType(condition.getOperType());
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

    /**
     * 子策略规则插入数据库（标签因子类型）
     *
     * @param evtContactConfId
     * @param left
     * @param operType
     * @param right
     * @return
     */
    public Long insertToPrd(Long evtContactConfId, Integer left, String operType, String right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right);
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("1000"); //标签因子
        mktVerbalCondition.setRightParamType("3000"); //固定值
        mktVerbalCondition.setConditionType("1");
        mktVerbalConditionPrdMapper.insert(mktVerbalCondition);
        return mktVerbalCondition.getConditionId();
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
     * 复制营销活动推荐条目
     *
     * @param userId
     * @param ItemIdList
     * @return
     */
    public Map<String, Object> copyProductRuleToPrd(Long userId, List<Long> ItemIdList) {
        Map<String, Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        for (Long itemId : ItemIdList) {
//            MktCamItem item = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_" + itemId);
            MktCamItem item = null;
            if (item == null || item.getMktCampaignId() == null) {
                item = mktCamItemMapper.selectByPrimaryKey(itemId);
                if (item == null) {
                    continue;
                }
                MktCamItem prdItem = mktCamItemPrdMapper.selectByPrimaryKey(itemId);
                if (prdItem!=null){
                    continue;
                }
//                redisUtils.set("MKT_CAM_ITEM_" + item.getMktCamItemId(), item);
            }
            mktCamItemPrdMapper.insert(item);
            // 同步推进条目数据到生产环境redis
//            redisUtils_prd.set_prd("MKT_CAM_ITEM_" + item.getMktCamItemId(), item);
            ruleIdList.add(item.getMktCamItemId());
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("ruleIdList", ruleIdList);
        return result;
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
                copyMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
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

    /**
     * 获取推动渠道
     * @param evtContactConfIdList 所有推送渠道拼接结果
     * @return
     */
    private List<MktCamChlConfDetail> getMktCamChlConfDetailList(List evtContactConfIdList){
        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
        // 更新首次协同推送渠道redis
        for (int i = 0; i < evtContactConfIdList.size(); i++) {
            // 查询推送渠道属性
            MktCamChlConfDO mktCamChlConfDO = mktCamChlConfPrdMapper.selectByPrimaryKey((Long) evtContactConfIdList.get(i));
            MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            // 查询推送渠道属性集合
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrPrdMapper.selectByEvtContactConfId((Long) evtContactConfIdList.get(i));
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttrList.add(mktCamChlConfAttr);
            }
            mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);

            // 查询话术
            List<MktVerbal> verbalList = mktVerbalPrdMapper.findVerbalListByConfId((Long) evtContactConfIdList.get(i));
            List<VerbalVO> verbalVOS = new ArrayList<>();
            for (MktVerbal mktVerbal:verbalList) {
                VerbalVO verbalVO = BeanUtil.create(mktVerbal, new VerbalVO());
                List<MktVerbalCondition> conditionList = mktVerbalConditionPrdMapper.findConditionListByVerbalId(verbalVO.getVerbalId());
                List<VerbalConditionVO> conditionVOList = new ArrayList<>();
                for (MktVerbalCondition mktVerbalCondition:conditionList) {
                    VerbalConditionVO verbalConditionVO = BeanUtil.create(mktVerbalCondition, new VerbalConditionVO());
                    conditionVOList.add(verbalConditionVO);
                }
                verbalVO.setConditionList(conditionVOList);
                verbalVOS.add(verbalVO);
            }
            mktCamChlConfDetail.setVerbalVOList(verbalVOS);
            mktCamChlConfDetailList.add(mktCamChlConfDetail);
        }
        return mktCamChlConfDetailList;
    }
}