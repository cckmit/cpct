/*
 * 文件名：MktCampaignServiceImpl.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月30日
 * 修改内容：
 */

package com.zjtelcom.cpct.service.impl.cpct;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.dao.strategy.*;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCpcAlgorithmsRul;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EvtSceneCamRel;
import com.zjtelcom.cpct.dto.pojo.MktCampaignPO;
import com.zjtelcom.cpct.dto.pojo.Result;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.pojo.*;
import com.zjtelcom.cpct.service.cpct.MktCampaignJTService;
import com.zjtelcom.cpct.util.*;
import com.ztesoft.uccp.dubbo.interfaces.UCCPSendService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 集团活动服务接口实现</br>
 * 主要用于集团活动的新增删除修改
 *
 * @author linchao
 * @version 1.0
 * @see MktCampaignJTService
 * @since JDK1.7
 */

@Service
@Transactional
public class MktCampaignJTServiceImpl implements MktCampaignJTService {

    private static final Logger logger = Logger.getLogger(MktCampaignJTServiceImpl.class);

    private static Map<String, LinkedList<Integer>> SEQ_MAP = new HashMap<>(5);

    private static Map<String, Long> SEQ_MAP_COPIES = new HashMap<>(5);


    /**
     * 活动基本信息
     */
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    /**
     * 活动策略配置信息
     */
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;
    /**
     * 活动与策略配置信息关系
     */
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    /**
     * 客服分群规则
     */
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;
    /**
     * 渠道协同推送配置
     */
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    /**
     * 渠道协同推送属性配置
     */
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    /**
     * 活动脚本
     */
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;
    /**
     * 策略配置规则
     */
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
    /**
     * 策略与规则关系
     */
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    /**
     * 销售品
     */
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    /**
     * 事件
     */
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    /**
     * 活动与事件关系
     */
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    /**
     * CPC计算规则
     */
    @Autowired
    private MktCpcAlgorithmsRulMapper mktCpcAlgorithmsRulMapper;
    /**
     * CPC计算规则与活动建立关联
     */
    @Autowired
    private MktCamRecomCalcRelMapper mktCamRecomCalcRelMapper;

    @Autowired
    private MktStrategyMapper mktStrategyMapper;

    @Autowired
    private EventSceneMapper eventSceneMapper;

    @Autowired
    private EvtSceneCamRelMapper evtSceneCamRelMapper;

    @Autowired
    private MktCamStrategyRelMapper mktCamStrategyRelMapper;

    /**
     * 批量保存活动
     *
     * @param req 活动列表
     * @return 处理结果
     */
    @Override
    public Result saveBatch(MktCampaignDetailReq req) {
        logger.debug(String.format("req.getTransactionId(): %s, data: %s", req.getTransactionId(), JSON.toJSONString(req.getMktCampaignDetails())));
        Map<String, Object> campaignResult = new HashMap<>();
        try {
            List<MktCampaignPO> mktCampaignList = req.getMktCampaignDetails();
            for (MktCampaignPO mktCampaign : mktCampaignList) {
                // 保存活动基本信息
                MktCampaignDO mktCampaignDO = new MktCampaignDO();
                CopyPropertiesUtil.copyBean2Bean(mktCampaignDO, mktCampaign);
                mktCampaignMapper.insert(mktCampaignDO);
                // 保存完活动并获取活动Id
                Long mktCampaignId = mktCampaignDO.getMktCampaignId();
                campaignResult.put("mktCampaignId", mktCampaignId);
                campaignResult.put("mktCampaignName", mktCampaign.getMktCampaignName());

                // 保存活动和事件的关联
                List<MktContactEvt> mktContactEvtList = mktCampaign.getMktCampaignEvts();
                for (MktContactEvt mktContactEvt : mktContactEvtList) {
                    ContactEvt contactEvt = BeanUtil.create(mktContactEvt, new ContactEvt());
                    contactEvt.setCreateDate(new Date());
                    contactEvt.setUpdateDate(new Date());
                    contactEvtMapper.createContactEvt(contactEvt);
                    // 获取新增事件的Id
                    Long contactEvtId = contactEvt.getContactEvtId();
                    // 与活动建立关系
                    MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
                    mktCamEvtRelDO.setStatusCd("1000");
                    mktCamEvtRelDO.setEventId(contactEvtId);
                    mktCamEvtRelDO.setMktCampaignId(mktCampaignId);
                    mktCamEvtRelDO.setCreateDate(new Date());
                    mktCamEvtRelDO.setUpdateDate(new Date());
                    mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                }

                // 新增策略配置
                MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
                mktStrategyConfDO.setMktStrategyConfName("默认策略");
                mktStrategyConfDO.setCreateDate(new Date());
                mktStrategyConfDO.setUpdateDate(new Date());
                mktStrategyConfMapper.insert(mktStrategyConfDO);
                // 获取策略配置Id
                Long mktStrategyConfId = mktStrategyConfDO.getMktStrategyConfId();
                // 与活动建立关系
                MktCamStrategyConfRelDO mktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
                mktCamStrategyConfRelDO.setMktCampaignId(mktCampaignId);
                mktCamStrategyConfRelDO.setStrategyConfId(mktStrategyConfId);
                mktCamStrategyConfRelDO.setCreateDate(new Date());
                mktCamStrategyConfRelDO.setUpdateDate(new Date());
                mktCamStrategyConfRelMapper.insert(mktCamStrategyConfRelDO);

                // 保存客户分群规则
                MktCamGrpRul mktCamGrpRul = mktCampaign.getMktCamGrpRuls().get(0);
                mktCamGrpRul.setMktCampaignId(mktCampaignId);
                mktCamGrpRul.setCreateDate(new Date());
                mktCamGrpRul.setUpdateDate(new Date());
                mktCamGrpRulMapper.insert(mktCamGrpRul);
                Long tarGrpId = mktCamGrpRul.getTarGrpId();

                // 保存销售品
                String mktCamItemIds = "";
                for (int i = 0; i < mktCampaign.getMktCamItems().size(); i++) {
                    MktCamItem mktCamItem = mktCampaign.getMktCamItems().get(i);
                    mktCamItem.setMktCampaignId(mktCampaignId);
                    mktCamItem.setCreateDate(new Date());
                    mktCamItem.setUpdateDate(new Date());
                    mktCamItemMapper.insert(mktCamItem);
                    if (i == 0) {
                        mktCamItemIds += mktCamItem.getMktCamItemId();
                    } else {
                        mktCamItemIds += mktCamItem.getMktCamItemId();
                    }
                }

                // 推送渠道
                String evtContactConfIds = "";
                for (int i = 0; i < mktCampaign.getMktCamChlConfDetails().size(); i++) {
                    MktCamChlConf mktCamChlConf = mktCampaign.getMktCamChlConfDetails().get(i);
                    //添加协同渠道基本信息
                    MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
                    CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConf);
                    mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());
                    mktCamChlConfDO.setCreateDate(new Date());
                    mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
                    mktCamChlConfDO.setUpdateDate(new Date());
                    mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
                    mktCamChlConfMapper.insert(mktCamChlConfDO);
                    // 获取推送渠道的 Id
                    Long evtContactConfId = mktCamChlConfDO.getEvtContactConfId();
                    if (i == 0) {
                        evtContactConfIds += evtContactConfId;
                    } else {
                        evtContactConfIds += "/" + evtContactConfId;
                    }
                    // 保存推送渠道属性
                    for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConf.getMktCamChlConfAttrs()) {
                        MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(mktCamChlConfAttr, new MktCamChlConfAttrDO());
                        mktCamChlConfAttrDO.setEvtContactConfId(evtContactConfId);
                        mktCamChlConfAttrDO.setCreateDate(new Date());
                        mktCamChlConfAttrDO.setUpdateDate(new Date());
                        mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                    }
                    // 保存脚本
                    for (MktCamScript mktCamScript : mktCamChlConf.getMktCamScripts()) {
                        CamScript camScript = BeanUtil.create(mktCamScript, new CamScript());
                        camScript.setEvtContactConfId(evtContactConfId);
                        camScript.setMktCampaignId(mktCampaignId);
                        camScript.setCreateDate(new Date());
                        camScript.setUpdateDate(new Date());
                        mktCamScriptMapper.insert(camScript);
                    }
                }

                // 创建规则
                MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
                mktStrategyConfRuleDO.setProductId(mktCamItemIds);
                mktStrategyConfRuleDO.setEvtContactConfId(evtContactConfIds);
                mktStrategyConfRuleDO.setTarGrpId(tarGrpId);
                mktStrategyConfRuleMapper.insert(mktStrategyConfRuleDO);
                // 获取规则Id
                Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();
                // 建立规则与策略配置的关系
                MktStrategyConfRuleRel mktStrategyConfRuleRel = new MktStrategyConfRuleRel();
                mktStrategyConfRuleRel.setMktStrategyConfRuleId(mktStrategyConfRuleId);
                mktStrategyConfRuleRel.setMktStrategyConfId(mktStrategyConfId);
                mktStrategyConfRuleRelMapper.insert(mktStrategyConfRuleRel);

                // CPC计算规则
                for (MktCpcAlgorithmsRul mktCpcAlgorithmsRul : mktCampaign.getMktCpcAlgorithmsRulDetails()) {
                    MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = BeanUtil.create(mktCpcAlgorithmsRul, new MktCpcAlgorithmsRulDO());
                    mktCpcAlgorithmsRulDO.setCreateDate(new Date());
                    mktCpcAlgorithmsRulDO.setUpdateDate(new Date());
                    mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
                    // 算法规则标识
                    Long algorithmsRulId = mktCpcAlgorithmsRulDO.getAlgorithmsRulId();
                    // CPC计算规则与活动建立关联
                    MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
                    mktCamRecomCalcRelDO.setAlgorithmsRulId(algorithmsRulId);
                    mktCamRecomCalcRelDO.setCreateDate(new Date());
                    mktCamRecomCalcRelDO.setUpdateDate(new Date());
                    mktCamRecomCalcRelDO.setMktCampaignId(mktCampaignId);
                    mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
                }

                // 策略详情
                for (MktStrategy mktStrategy : mktCampaign.getMktCampaignStrategyDetails()) {
                    mktStrategy.setCreateDate(new Date());
                    mktStrategy.setUpdateDate(new Date());
                    mktStrategyMapper.insert(mktStrategy);
                    // 添加策略与活动关系
                    MktCamStrategyRel mktCamStrategyRel = new MktCamStrategyRel();
                    mktCamStrategyRel.setMktCampaignId(mktCampaignId);
                    mktCamStrategyRel.setStrategyId(mktStrategy.getStrategyId());
                    mktCamStrategyRelMapper.insert(mktCamStrategyRel);
                }

                // 事件
                for (EventScene eventScene : mktCampaign.getEventScenes()) {
                    eventScene.setContactEvtCode("1000");
                    eventScene.setCreateDate(new Date());
                    eventScene.setUpdateDate(new Date());
                    eventSceneMapper.insert(eventScene);
                    // 添加事件场景和活动的关系
                    EvtSceneCamRel evtSceneCamRel = new EvtSceneCamRel();
                    evtSceneCamRel.setMktCampaignId(mktCampaignId);
                    evtSceneCamRel.setEventSceneId(eventScene.getEventSceneId());
                    evtSceneCamRelMapper.insert(evtSceneCamRel);
                }
            }
        } catch (Exception e) {
            logger.error("[op:saveBatch] 保存活动失败, Exception = ", e);
        }

        Map<String, Object> resultObject = new HashMap<>();
        resultObject.put("mktCampaigns", campaignResult);
        // clearSeqMap(true);
        return ResultUtil.buildSuccessResult(resultObject);
    }


    /**
     * 批量修改活动
     *
     * @param req 活动列表
     * @return 处理结果
     */
    @Override
    public Result updateBatch(MktCampaignDetailReq req)  {
        logger.debug(String.format("req.getTransactionId(): %s, data: %s", req.getTransactionId(), JSON.toJSONString(req.getMktCampaignDetails())));
        Map<String, Object> campaignResult = new HashMap<>();
        try {
            List<MktCampaignPO> mktCampaignList = req.getMktCampaignDetails();
            for (MktCampaignPO mktCampaign : mktCampaignList) {
                // 保存活动基本信息
                MktCampaignDO mktCampaignDO = new MktCampaignDO();
                CopyPropertiesUtil.copyBean2Bean(mktCampaignDO, mktCampaign);
                // 更新活动
                mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
                Long mktCampaignId = mktCampaign.getMktCampaignId();
                // 更新活动并获取活动Id
                campaignResult.put("mktCampaignId", mktCampaignId);
                campaignResult.put("mktCampaignName", mktCampaign.getMktCampaignName());

                // 保存活动和事件的关联
                List<MktContactEvt> mktContactEvtList = mktCampaign.getMktCampaignEvts();
                for (MktContactEvt mktContactEvt : mktContactEvtList) {
                    // 判断是否为新增还是修改，有id为修改
                    if (mktContactEvt.getContactChlId() != null && !"".equals(mktContactEvt.getContactChlId())) {
                        ContactEvt contactEvt = BeanUtil.create(mktContactEvt, new ContactEvt());
                        contactEvt.setUpdateDate(new Date());
                        contactEvtMapper.modContactEvt(contactEvt);
                    } else {
                        // 没有Id，则新增
                        ContactEvt contactEvt = BeanUtil.create(mktContactEvt, new ContactEvt());
                        contactEvt.setCreateDate(new Date());
                        contactEvt.setUpdateDate(new Date());
                        contactEvtMapper.createContactEvt(contactEvt);
                        // 获取新增事件的Id
                        Long contactEvtId = contactEvt.getContactEvtId();
                        // 与活动建立关系
                        MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
                        mktCamEvtRelDO.setEventId(contactEvtId);
                        mktCamEvtRelDO.setMktCampaignId(mktCampaignId);
                        mktCamEvtRelDO.setStatusCd("1000");
                        mktCamEvtRelDO.setCreateDate(new Date());
                        mktCamEvtRelDO.setUpdateDate(new Date());
                        mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                    }
                }

                // 修改客户分群规则
                MktCamGrpRul mktCamGrpRul = mktCampaign.getMktCamGrpRuls().get(0);
                mktCamGrpRul.setCreateDate(new Date());
                mktCamGrpRul.setUpdateDate(new Date());
                mktCamGrpRul.setMktCampaignId(mktCampaignId);
                mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);
                Long tarGrpId = mktCamGrpRul.getTarGrpId();

                // 修改销售品
                String mktCamItemIds = "";
                for (int i = 0; i < mktCampaign.getMktCamItems().size(); i++) {
                    MktCamItem mktCamItem = mktCampaign.getMktCamItems().get(i);
                    mktCamItem.setMktCampaignId(mktCampaignId);
                    if (mktCamItem.getMktCamItemId() != null && !"".equals(mktCamItem.getMktCamItemId())) {
                        mktCamItem.setUpdateDate(new Date());
                        mktCamItemMapper.updateByPrimaryKey(mktCamItem);
                    } else {
                        mktCamItem.setCreateDate(new Date());
                        mktCamItem.setUpdateDate(new Date());
                        mktCamItemMapper.insert(mktCamItem);
                    }
                    if (i == 0) {
                        mktCamItemIds += mktCamItem.getMktCamItemId();
                    } else {
                        mktCamItemIds += mktCamItem.getMktCamItemId();
                    }
                }

                // 推送渠道
                String evtContactConfIds = "";
                for (int i = 0; i < mktCampaign.getMktCamChlConfDetails().size(); i++) {
                    MktCamChlConf mktCamChlConf = mktCampaign.getMktCamChlConfDetails().get(i);
                    //添加协同渠道基本信息
                    MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
                    CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConf);
                    if (mktCamChlConfDO.getEvtContactConfId() != null && !"".equals(mktCamChlConfDO.getEvtContactConfId())) {
                        mktCamChlConfDO.setUpdateDate(new Date());
                        mktCamChlConfMapper.updateByPrimaryKey(mktCamChlConfDO);
                    } else {
                        mktCamChlConfDO.setCreateDate(new Date());
                        mktCamChlConfDO.setUpdateDate(new Date());
                        mktCamChlConfMapper.insert(mktCamChlConfDO);
                    }
                    // 获取推送渠道的 Id
                    Long evtContactConfId = mktCamChlConfDO.getEvtContactConfId();
                    if (i == 0) {
                        evtContactConfIds += evtContactConfId;
                    } else {
                        evtContactConfIds += "/" + evtContactConfId;
                    }

                    // 保存推送渠道属性
                    for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConf.getMktCamChlConfAttrs()) {
                        MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(mktCamChlConfAttr, new MktCamChlConfAttrDO());

                        if(mktCamChlConfAttrDO.getContactChlAttrRstrId()!=null && !"".equals(mktCamChlConfAttrDO.getContactChlAttrRstrId())){
                            mktCamChlConfAttrDO.setEvtContactConfId(evtContactConfId);
                            mktCamChlConfAttrDO.setUpdateDate(new Date());
                            mktCamChlConfAttrMapper.updateByPrimaryKey(mktCamChlConfAttrDO);
                        } else {
                            mktCamChlConfAttrDO.setEvtContactConfId(evtContactConfId);
                            mktCamChlConfAttrDO.setCreateDate(new Date());
                            mktCamChlConfAttrDO.setUpdateDate(new Date());
                            mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                        }

                    }
                    // 保存脚本
                    for (MktCamScript mktCamScript : mktCamChlConf.getMktCamScripts()) {
                        CamScript camScript = BeanUtil.create(mktCamScript, new CamScript());
                        camScript.setEvtContactConfId(evtContactConfId);
                        camScript.setMktCampaignId(mktCampaignId);
                        camScript.setUpdateDate(new Date());
                        if(camScript.getMktCampaignScptId()!=null && !"".equals(camScript.getMktCampaignScptId())){
                            mktCamScriptMapper.updateByPrimaryKey(camScript);
                        } else {
                            camScript.setCreateDate(new Date());
                            mktCamScriptMapper.insert(camScript);
                        }
                    }
                }

                // 修改创建规则
                MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
                mktStrategyConfRuleDO.setProductId(mktCamItemIds);
                mktStrategyConfRuleDO.setEvtContactConfId(evtContactConfIds);
           //     mktStrategyConfRuleDO.setTarGrpId(tarGrpId);
                mktStrategyConfRuleMapper.updateByPrimaryKey(mktStrategyConfRuleDO);
                // 获取规则Id
                Long mktStrategyConfRuleId = mktStrategyConfRuleDO.getMktStrategyConfRuleId();

                // CPC计算规则
                for (MktCpcAlgorithmsRul mktCpcAlgorithmsRul : mktCampaign.getMktCpcAlgorithmsRulDetails()) {
                    MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = BeanUtil.create(mktCpcAlgorithmsRul, new MktCpcAlgorithmsRulDO());
                    mktCpcAlgorithmsRulDO.setUpdateDate(new Date());
                    if(mktCpcAlgorithmsRulDO.getAlgorithmsRulId()!=null && !"".equals(mktCpcAlgorithmsRulDO.getAlgorithmsRulId())){
                        mktCpcAlgorithmsRulMapper.updateByPrimaryKey(mktCpcAlgorithmsRulDO);
                    } else {
                        mktCpcAlgorithmsRulDO.setCreateDate(new Date());
                        mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
                        // 算法规则标识
                        Long algorithmsRulId = mktCpcAlgorithmsRulDO.getAlgorithmsRulId();
                        // CPC计算规则与活动建立关联
                        MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
                        mktCamRecomCalcRelDO.setAlgorithmsRulId(algorithmsRulId);
                        mktCamRecomCalcRelDO.setCreateDate(new Date());
                        mktCamRecomCalcRelDO.setUpdateDate(new Date());
                        mktCamRecomCalcRelDO.setMktCampaignId(mktCampaignId);
                        mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
                    }
                }

                // 策略详情
                for (MktStrategy mktStrategy : mktCampaign.getMktCampaignStrategyDetails()) {
                    mktStrategy.setUpdateDate(new Date());
                    if(mktStrategy.getStrategyId()!=null && !"".equals(mktStrategy.getStrategyId())){
                        mktStrategyMapper.updateByPrimaryKey(mktStrategy);
                    } else {
                        mktStrategy.setCreateDate(new Date());
                        mktStrategyMapper.insert(mktStrategy);
                        // 添加策略与活动关系
                        MktCamStrategyRel mktCamStrategyRel = new MktCamStrategyRel();
                        mktCamStrategyRel.setMktCampaignId(mktCampaignId);
                        mktCamStrategyRel.setStrategyId(mktStrategy.getStrategyId());
                        mktCamStrategyRelMapper.insert(mktCamStrategyRel);
                    }
                }

                // 事件
                for (EventScene eventScene : mktCampaign.getEventScenes()) {
                    eventScene.setUpdateDate(new Date());
                    if(eventScene.getEventId()!=null && !"".equals(eventScene.getEventId())){
                        eventSceneMapper.updateByPrimaryKey(eventScene);
                    } else {
                        eventScene.setCreateDate(new Date());
                        eventSceneMapper.insert(eventScene);
                        // 添加事件场景和活动的关系
                        EvtSceneCamRel evtSceneCamRel = new EvtSceneCamRel();
                        evtSceneCamRel.setMktCampaignId(mktCampaignId);
                        evtSceneCamRel.setEventSceneId(eventScene.getEventSceneId());
                        evtSceneCamRelMapper.insert(evtSceneCamRel);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[op:saveBatch] 修改活动失败, Exception = ", e);
        }
        Map<String, Object> resultObject = new HashMap<>();
        resultObject.put("mktCampaigns", campaignResult);
        return ResultUtil.buildSuccessResult(resultObject);
    }

    @Autowired(required = false)
    private UCCPSendService uCCPSendService;

    @Override
    public Map<String, Object> sendMsgResult(String targPhone, String sendContent, String lanId) {
        HashMap params = new HashMap();
        //请求消息流水，格式：系统编码（6位）+yyyymmddhhmiss+10位序列号
        params.put("TransactionId","CPCPYX"+ DateUtil.date2St4Trial(new Date()) + getRandom(10));
        //UCCP分配的系统编码
        params.put("SystemCode","CPCPYX");
        //UCCP分配的帐号
        params.put("UserAcct","CPCPYX");
        //UCCP分配的认证密码
        params.put("Password","908234");
        //场景标识
        params.put("SceneId","7149");
        //请求的时间,请求发起的时间,必须为下边的格式
        params.put("RequestTime",DateUtil.date2StringDate(new Date()));
        //接收消息推送的手机号码
        //params.put("AccNbr",targPhone);
        params.put("AccNbr","18957181789");
        //消息内容
        params.put("OrderContent",sendContent);
        //本地网/辖区
        //params.put("LanId",lanId);
        params.put("LanId","571");
        //定时发送的时间设置
        //params.put("SendDate","");
        //如果使用场景模板来发送短信,必须填值
        //params.put("ContentParam","");
        //外系统流水ID,查询发送结构用,可填
        //params.put("ExtOrderId", "");
        try {
            Map map = uCCPSendService.sendShortMessage(params);
            System.out.println("接口返回结果:"+map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRandom(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

}
