package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
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
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalConditionAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalConditionVO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.dto.synchronize.SynchronizeRecord;
import com.zjtelcom.cpct.dubbo.service.MktcampaignSyncApiService;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prd.dao.campaign.*;
import com.zjtelcom.cpct_prd.dao.channel.MktCamScriptPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktVerbalConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.channel.MktVerbalPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpConditionPrdMapper;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpPrdMapper;
import com.zjtelcom.cpct_prd.dao.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class MktCampaignSyncApiServiceImpl implements MktcampaignSyncApiService {

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
    private MktCamItemMapper camItemMapper;
    @Autowired
    private MktVerbalMapper verbalMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;
    @Autowired
    private MktCamScriptMapper camScriptMapper;
    @Autowired
    private SynchronizeRecordMapper synchronizeRecordMapper;
    @Autowired
    private RedisUtils_prd redisUtils_prd;
    //同步表名
    private static final String tableName = "mkt_campaign";


    /**
     * 发布并下发活动
     *
     * @param mktCampaignId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> publishMktCampaign(final Long mktCampaignId){
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
            // 遍历活动下发城市集合
            for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
                // 为下发城市生成新的活动
                mktCampaignDO.setMktCampaignId(null);
                mktCampaignDO.setLanId(mktCamCityRelDO.getCityId()); // 本地网标识
                mktCampaignDO.setCreateDate(new Date());
                mktCampaignDO.setCreateStaff(UserUtil.loginId());
                mktCampaignDO.setUpdateDate(new Date());
                mktCampaignDO.setUpdateStaff(UserUtil.loginId());
                mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
                mktCampaignMapper.insert(mktCampaignDO);
                // 获取新的活动的Id
                Long childMktCampaignId = mktCampaignDO.getMktCampaignId();
                childMktCampaignIdList.add(childMktCampaignId);
                // 与父活动进行关联
                MktCampaignRelDO mktCampaignRelDO = new MktCampaignRelDO();
                mktCampaignRelDO.setaMktCampaignId(parentMktCampaignId);
                mktCampaignRelDO.setzMktCampaignId(childMktCampaignId);
                mktCampaignRelDO.setApplyRegionId(mktCamCityRelDO.getCityId());
                mktCampaignRelDO.setEffDate(effDate);
                mktCampaignRelDO.setExpDate(expDate);
                mktCampaignRelDO.setRelType("1000");   //  1000-父子关系
                mktCampaignRelDO.setCreateDate(new Date());
                mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
                mktCampaignRelDO.setUpdateDate(new Date());
                mktCampaignRelDO.setCreateStaff(UserUtil.loginId());
                mktCampaignRelDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());  // 1000-有效
                mktCampaignRelDO.setStatusDate(new Date());
                mktCampaignRelMapper.insert(mktCampaignRelDO);

                //事件与新活动建立关联
                for (MktCamEvtRelDO mktCamEvtRelDO : MktCamEvtRelDOList) {
                    MktCamEvtRelDO childMktCamEvtRelDO = new MktCamEvtRelDO();
                    childMktCamEvtRelDO.setMktCampaignId(childMktCampaignId);
                    childMktCamEvtRelDO.setEventId(childMktCamEvtRelDO.getEventId());
                    childMktCamEvtRelDO.setStatusCd("1000");
                    childMktCamEvtRelDO.setStatusDate(new Date());
                    childMktCamEvtRelDO.setCreateDate(new Date());
                    childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                    childMktCamEvtRelDO.setUpdateDate(new Date());
                    childMktCamEvtRelDO.setCreateStaff(UserUtil.loginId());
                    mktCamEvtRelMapper.insert(childMktCamEvtRelDO);
                }

                // 遍历活动下策略的集合
                for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                    Map<String, Object> mktStrategyConfMap = copyMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId(), true);
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
                //  发布活动时异步去同步大数据
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronizeCampaign(mktCampaignId, "admin");
                        } catch (Exception e) {
                            logger.error("[op:publishMktCampaign] 发布活动 id = {} 时，同步到生产失败！Exception= ", mktCampaignId, e);
                        }
                    }
                }.start();

                // 协同中心活动信息同步
    /*            new Thread(){
                    @Override
                    public void run(){
                        Map<String, Object> map = iMktCampaignService.campaignPublishDetail();
                    }
                }*/


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

        //获取策略对应的过滤规则
        List<Long> ruleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(parentMktStrategyConfId);
        List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOList = mktStrategyFilterRuleRelMapper.selectRuleByStrategyId(parentMktStrategyConfId);
        // 与新的策略建立关联
        for (MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO : mktStrategyFilterRuleRelDOList) {
            mktStrategyFilterRuleRelPrdMapper.insert(mktStrategyFilterRuleRelDO);
        }
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
        List<Long> productIdList = new ArrayList<>();
        if (mktStrategyConfRuleDO.getProductId() != null) {
            String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] != "" && !"".equals(productIds[i])) {
                    productIdList.add(Long.valueOf(productIds[i]));
                }
            }
        }
        copyProductRuleToPrd(UserUtil.loginId(), productIdList);

        /**
         * 协同渠道配置
         */
        String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
        if (evtContactConfIds != null && !"".equals(evtContactConfIds[0])) {
            for (int i = 0; i < evtContactConfIds.length; i++) {
                if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                    copyMktCamChlConf(Long.valueOf(evtContactConfIds[i]));
                }
            }
        }

        /**
         * 二次协同结果
         */
        String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
        if (mktCamChlResultIds != null && !"".equals(mktCamChlResultIds[0])) {
            for (int i = 0; i < mktCamChlResultIds.length; i++) {
                copyMktCamChlResult(Long.valueOf(mktCamChlResultIds[i]));
            }
        }
        mktStrategyConfRuleMap.put("mktStrategyConfRuleId", mktStrategyConfRuleDO.getMktStrategyConfRuleId());
        return mktStrategyConfRuleMap;
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
            MktCamItem item = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_" + itemId);
            if (item == null) {
                item = mktCamItemMapper.selectByPrimaryKey(itemId);
                if (item == null) {
                    continue;
                }
                redisUtils.set("MKT_CAM_ITEM_" + item.getMktCamItemId(), item);
            }
            mktCamItemPrdMapper.insert(item);
            // 同步推进条目数据到生产环境redis
            redisUtils_prd.set_prd("MKT_CAM_ITEM_" + item.getMktCamItemId(), item);
            ruleIdList.add(item.getMktCamItemId());
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("ruleIdList", ruleIdList);
        return result;
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
        // 删除与策略关联的过滤规则
        mktStrategyFilterRuleRelPrdMapper.deleteByStrategyId(mktStrategyConfId);
        //删除策略下的规则，以及关联的表
        List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOList = mktStrategyConfRuleRelPrdMapper.selectByMktStrategyConfId(mktStrategyConfId);
        for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRulePrdMapper.selectByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
            // 删除客户分群
            tarGrpConditionPrdMapper.deleteByTarGrpId(mktStrategyConfRuleDO.getTarGrpId());
            tarGrpPrdMapper.deleteByPrimaryKey(mktStrategyConfRuleDO.getTarGrpId());

            // 删除销售品
            String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
            for (String productId : productIds) {
                mktCamItemPrdMapper.deleteByPrimaryKey(Long.valueOf(productId));
            }

            // 删除首次协同
            String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
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

            // 删除二次协同结果
            String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
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
            mktStrategyConfRulePrdMapper.deleteByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId());
            mktStrategyConfRuleRelPrdMapper.deleteByPrimaryKey(mktStrategyConfRuleRelDO.getMktStrategyConfRuleRelId());
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
     * @param roleName  角色
     * @param name      同步表名称
     * @param eventId   同步主键
     * @param type      操作类型
     * @return
     */
    public int addRecord(String roleName, String name,Long eventId, Integer type) {
        SynchronizeRecord synchronizeRecord=new SynchronizeRecord();
        synchronizeRecord.setRoleName(roleName);
        synchronizeRecord.setSynchronizeName(name);
        synchronizeRecord.setSynchronizeType(type);
        synchronizeRecord.setSynchronizeId(eventId.toString());
        return  synchronizeRecordMapper.insert(synchronizeRecord);
    }


    /**
     * 通过原策略id复制策略
     *
     * @param parentMktStrategyConfId
     * @param isPublish 是否为发布操作
     * @return
     * @throws Exception
     */
    public Map<String, Object> copyMktStrategyConf(Long parentMktStrategyConfId, Boolean isPublish) throws Exception {
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
            List<Long> ruleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(parentMktStrategyConfId);
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
            }
            // 遍历规则
            for (MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO : mktStrategyConfRuleRelDOList) {
                // 复制获取规则
                Map<String, Object> mktStrategyConfRuleMap = copyMktStrategyConfRule(mktStrategyConfRuleRelDO.getMktStrategyConfRuleId(),true);
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
    public Map<String, Object> copyMktStrategyConfRule(Long parentMktStrategyConfRuleId, Boolean isPublish) throws Exception {
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
                tarGrpMap = copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), true);
            } else {
                tarGrpMap = copyTarGrp(mktStrategyConfRuleDO.getTarGrpId(), false);
            }

            TarGrp tarGrp = (TarGrp) tarGrpMap.get("tarGrp");
            /**
             * 销售品配置
             */
            List<Long> productIdList = new ArrayList<>();
            if (mktStrategyConfRuleDO.getProductId() != null) {
                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                for (int i = 0; i < productIds.length; i++) {
                    if (productIds[i] != "" && !"".equals(productIds[i])) {
                        productIdList.add(Long.valueOf(productIds[i]));
                    }
                }
            }
            Map<String, Object> productRuleMap = copyProductRule(UserUtil.loginId(), productIdList);
            List<Long> ruleIdList = (List<Long>) productRuleMap.get("ruleIdList");
            String childProductIds = "";
            for (int i = 0; i < ruleIdList.size(); i++) {
                if (i == 0) {
                    childProductIds += ruleIdList.get(i);
                } else {
                    childProductIds += "/" + ruleIdList.get(i);
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
                            MktCamChlConfDO mktCamChlConfDO = (MktCamChlConfDO) mktCamChlConfDOMap.get("mktCamChlConfDO");
                            if (i == 0) {
                                childEvtContactConfIds += mktCamChlConfDO.getEvtContactConfId();
                            } else {
                                childEvtContactConfIds += "/" + mktCamChlConfDO.getEvtContactConfId();
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
                    MktCamChlResultDO mktCamChlResultDO = (MktCamChlResultDO) mktCamChlResultDOMap.get("mktCamChlResultDO");
                    if (i == 0) {
                        childMktCamChlResultIds += mktCamChlResultDO.getMktCamChlResultId();
                    } else {
                        childMktCamChlResultIds += "/" + mktCamChlResultDO.getMktCamChlResultId();
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
            // 获取原二次协同渠道下结果的推送渠道
            List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(parentMktCamChlResultId);
            List<com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
            // 遍历获取原二次协同渠道下结果的推送渠道
            for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                // 复制推送渠道
                Map<String, Object> mktCamChlConfMap = copyMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
                MktCamChlConfDO mktCamChlConfDO = (MktCamChlConfDO) mktCamChlConfMap.get("mktCamChlConfDO");
                // 新的推送渠道与新的结果简历关联
                if (mktCamChlConfDO != null) {
                    // 结果与推送渠道的关联
                    MktCamChlResultConfRelDO childCamChlResultConfRelDO = new MktCamChlResultConfRelDO();
                    childCamChlResultConfRelDO.setMktCamChlResultId(mktCamChlResultId);
                    childCamChlResultConfRelDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                    childCamChlResultConfRelDO.setCreateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setCreateDate(new Date());
                    childCamChlResultConfRelDO.setUpdateStaff(UserUtil.loginId());
                    childCamChlResultConfRelDO.setUpdateDate(new Date());
                    mktCamChlResultConfRelMapper.insert(childCamChlResultConfRelDO);
                }
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
            com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail mktCamChlConfDetailNew = BeanUtil.create(mktCamChlConfDO, new com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail());
            mktCamChlConfDetailNew.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            redisUtils.set("MktCamChlConfDetail_" + mktCamChlConfDetailNew.getEvtContactConfId(), mktCamChlConfDetailNew);

            // 查询痛痒点话术列表
            copyVerbal(parentEvtContactConfId, childEvtContactConfId);

            // 查询脚本
            copyCamScript(parentEvtContactConfId, null, childEvtContactConfId);

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
     * 复制活动脚本
     * @param contactConfId
     * @param newConfId
     * @return
     */
    public Map<String, Object> copyCamScript(Long contactConfId, String scriptDesc, Long newConfId) {
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
        com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail detail = (com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + contactConfId);
        if (detail == null) {
            camScript = camScriptMapper.selectByConfId(contactConfId);
        } else {
            camScript = detail.getCamScript();
        }
        if (camScript != null) {
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
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",newScript);
        return result;
    }

    /**
     * 复制痛痒点
     * @param contactConfId
     * @return
     */
    public Map<String, Object> copyVerbal(Long contactConfId,Long newConfId) {
        Map<String,Object> map = new HashMap<>();
        com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail detail = (com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_"+contactConfId);
        if (detail==null){
            map.put("resultCode", CODE_FAIL);
            map.put("resultMsg", "推送渠道配置不存在");
            return map;
        }
        List<com.zjtelcom.cpct.dto.channel.VerbalVO> verbalVOList = detail.getVerbalVOList();
        if(verbalVOList!=null &&verbalVOList.size()>0){
            for (com.zjtelcom.cpct.dto.channel.VerbalVO verbalVO : verbalVOList){
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

    /**
     * 添加痛痒点话术
     */
    @Transactional
    public Map<String, Object> addVerbal(Long userId, VerbalAddVO addVO) {
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
        com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail detail = (com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail)redisUtils.get("MktCamChlConfDetail_"+addVO.getContactConfId());
        if (detail!=null){
            com.zjtelcom.cpct.dto.channel.VerbalVO verbalVO = BeanUtil.create(verbal,new com.zjtelcom.cpct.dto.channel.VerbalVO());
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

    /**
     * 保存协同渠道子策略规则
     *
     * @param evtContactConfId
     * @param param
     */
    public void ruleInsert(Long evtContactConfId, String param) {
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
     * 复制营销活动推荐条目
     * @param userId
     * @param ItemIdList
     * @return
     */
    public Map<String, Object> copyProductRule(Long userId, List<Long> ItemIdList) {
        Map<String,Object> result = new HashMap<>();
        List<Long> ruleIdList = new ArrayList<>();
        if(ItemIdList!=null && ItemIdList.size()>0){
            List<MktCamItem> mktCamItems = new ArrayList<>();
            for (Long itemId : ItemIdList) {
                MktCamItem item = (MktCamItem) redisUtils.get("MKT_CAM_ITEM_"+itemId);
                if (item == null) {
                    item = camItemMapper.selectByPrimaryKey(itemId);
                    redisUtils.set("MKT_CAM_ITEM_"+itemId, item);
                }
                MktCamItem newItem = BeanUtil.create(item, new MktCamItem());
                newItem.setMktCamItemId(null);
                newItem.setMktCampaignId(1000L);
                mktCamItems.add(newItem);
            }
            camItemMapper.insertByBatch(mktCamItems);
            for(MktCamItem item : mktCamItems){
                redisUtils.set("MKT_CAM_ITEM_" + item.getMktCamItemId(), item);
                ruleIdList.add(item.getMktCamItemId());
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("ruleIdList",ruleIdList);
        return result;
    }

    /**
     * 复制客户分群 返回
     * @param tarGrpId
     * @return
     */

    public Map<String, Object> copyTarGrp(Long tarGrpId,boolean isCopy) {
        Map<String,Object> result = new HashMap<>();
        TarGrpDetail detail = (TarGrpDetail)redisUtils.get("TAR_GRP_"+tarGrpId);
        if (detail==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "客户分群不存在");
            return result;
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
            tarGrp.setCreateDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateDate(DateUtil.getCurrentTime());
            tarGrp.setStatusDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateStaff(UserUtil.loginId());
            tarGrp.setCreateStaff(UserUtil.loginId());
            if (isCopy){
                tarGrp.setStatusCd("2000");
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
                    if (tarGrpCondition.getAreaIdList()!=null){
                        area2RedisThread(tarGrp, tarGrpCondition);
                    }
                    tarGrpCondition.setConditionId(null);
                    tarGrpCondition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    tarGrpCondition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                    tarGrpCondition.setCreateDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setUpdateDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setStatusDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setUpdateStaff(UserUtil.loginId());
                    tarGrpCondition.setCreateStaff(UserUtil.loginId());
                    tarGrpCondition.setStatusCd("1000");
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

    private void area2RedisThread(TarGrp tarGrp, final TarGrpCondition tarGrpCondition) {
        final Long targrpId = tarGrp.getTarGrpId();
        List<OrgTreeDO> sysAreaList = new ArrayList<>();
        for (Integer id : tarGrpCondition.getAreaIdList()){
            OrgTreeDO orgTreeDO = orgTreeMapper.selectByAreaId(id);
            if (orgTreeDO!=null){
                sysAreaList.add(orgTreeDO);
            }
        }
        redisUtils.set("AREA_RULE_ENTITY_"+targrpId,sysAreaList);
        new Thread() {
            public void run() {
                areaList2Redis(targrpId,tarGrpCondition.getAreaIdList());
            }
        }.start();
    }

    public void areaList2Redis(Long targrpId,List<Integer> areaIdList){
        List<String> resultList = new ArrayList<>();
        List<OrgTreeDO> sysAreaList = new ArrayList<>();
        for (Integer id : areaIdList){
            areaList(id,resultList,sysAreaList);
        }
        redisUtils.set("AREA_RULE_"+targrpId,resultList.toArray(new String[resultList.size()]));
    }

    public List<String> areaList(Integer parentId,List<String> resultList,List<OrgTreeDO> areas){
        List<OrgTreeDO> sysAreaList = orgTreeMapper.selectBySumAreaId(parentId);
        if (sysAreaList.isEmpty()){
            return resultList;
        }
        for (OrgTreeDO area : sysAreaList){
            resultList.add(area.getAreaName());
            areas.add(area);
            areaList(area.getAreaId(),resultList,areas);
        }
        return resultList;
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


}
