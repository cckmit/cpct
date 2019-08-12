package com.zjtelcom.cpct.open.serviceImpl.mktCampaign;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.MktCamStrategyRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.event.OpenEvent;
import com.zjtelcom.cpct.open.entity.event.OpenEvtTrigCamRulEntity;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfAttrEntity;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfEntity;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItemEntity;
import com.zjtelcom.cpct.open.entity.mktCampaign.OpenMktCampaign;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.*;
import com.zjtelcom.cpct.open.entity.mktCpcAlgorithmsRule.OpenMktCpcAlgorithmsRulEntity;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategy;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategyEntity;
import com.zjtelcom.cpct.open.entity.script.OpenScript;
import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrpConditionEntity;
import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrpEntity;
import com.zjtelcom.cpct.open.service.mktCampaign.OpenMktCampaignService;
import com.zjtelcom.cpct.pojo.MktCamStrategyRel;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamStrategyConfRelPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-05 17:32:51
 * @version: V 1.0
 * 营销活动openapi相关服务
 */
@Service
@Transactional
public class OpenMktCampaignServiceImpl extends BaseService implements OpenMktCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktStrategyConfRuleMapper  mktStrategyConfRuleMapper;
    @Autowired
    private MktCamStrategyRelMapper mktCamStrategyRelMapper;
    @Autowired
    private MktStrategyMapper mktStrategyMapper;
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private MktCpcAlgorithmsRulMapper mktCpcAlgorithmsRulMapper;
    @Autowired
    private MktCamRecomCalcRelMapper mktCamRecomCalcRelMapper;


    /**
     * 查询营销活动信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(queryId);
        if (null == mktCampaignDO) {
            JSONObject json=new JSONObject();
            json.put("message","对应营销活动信息不存在!");
            resultMap.put("params", json);
            return resultMap;
        }
        //将活动信息转换为openapi返回格式
        OpenMktCampaign campaign=getOpenCampaign(mktCampaignDO);
        //活动推荐条目列表  活动脚本列表  维挽策略列表
        List<OpenMktCamItem> mktCamItemList=new ArrayList<>();
        List<OpenScript> mktScriptList=new ArrayList<>();
        List<OpenMktStrategy> mktStrategyList=new ArrayList<>();

        //1.查询营销推荐维挽策略
        List<MktCamStrategyRel> mktCamStrategyRels = mktCamStrategyRelMapper.selectByMktCampaignId(queryId);
        if (!mktCamStrategyRels.isEmpty()){
            for (MktCamStrategyRel rel:mktCamStrategyRels){
                MktStrategy mktStrategy = mktStrategyMapper.selectByPrimaryKey(rel.getStrategyId());
                if(null!=mktStrategy){
                    OpenMktStrategy openMktStrategy = BeanUtil.create(mktStrategy, new OpenMktStrategy());
                    if(null!=mktStrategy.getStatusDate()){
                        openMktStrategy.setStatusDate(DateUtil.getDatetime(mktStrategy.getStatusDate()));
                    }
                    mktStrategyList.add(openMktStrategy);
                }
            }
            campaign.setMktStrategy(mktStrategyList);
        }

        //2. 查询营销活动推荐脚本
        List<CamScript> camScripts = mktCamScriptMapper.selectByCampaignId(queryId);
        if(!camScripts.isEmpty()){
            for (CamScript script:camScripts){
                if(null!=script){
                    OpenScript openScript = BeanUtil.create(script, new OpenScript());
                    openScript.setId(script.getMktCampaignScptId());
                    openScript.setHref("/mktScript/"+script.getMktCampaignScptId().toString());
                    openScript.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                    mktScriptList.add(openScript);
                }
            }
            campaign.setMktCamScript(mktScriptList);
        }


        //查询相关营销活动推荐条目列表(属于规则) 活动id  471比较多   468比较少
        //2.查出活动下的策略   500
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOS = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        if (mktCamStrategyConfRelDOS.isEmpty()){
            resultMap.put("params", campaign);
            return resultMap;
        }

        //3.查出策略下的规则
        for (MktCamStrategyConfRelDO m:mktCamStrategyConfRelDOS){
            List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOS = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(m.getStrategyConfId());

            if(mktStrategyConfRuleRelDOS.isEmpty()){
                resultMap.put("params", campaign);
                return resultMap;
            }
            //3.1 通过规则id查出规则
            for (MktStrategyConfRuleRelDO ruleRelDo:mktStrategyConfRuleRelDOS){
                MktStrategyConfRuleDO rule = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleRelDo.getMktStrategyConfRuleId());
                // 规则信息中可以得到 1分群id  2推送条目id集合  3协同渠道配置id集合  4二次协同渠道配置结果id集合
                if(rule!=null){
                      //3.2 获取推荐条目信息   997/998/999/1000格式
                    if(StringUtils.isNotBlank(rule.getProductId())){
                        String[] split = rule.getProductId().split("/");
                        for (int i = 0; i <split.length ; i++) {
                            //得到活动推荐条目
                            MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(split[i]));
                            if(null!=mktCamItem){
                                OpenMktCamItem openMktCamItem = BeanUtil.create(mktCamItem, new OpenMktCamItem());
                                openMktCamItem.setId(mktCamItem.getItemId());
                                openMktCamItem.setHref("/mktCamItem/"+mktCamItem.getItemId().toString());
                                openMktCamItem.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                                mktCamItemList.add(openMktCamItem);
                            }
                        }

                    }
                }

            }
            //返回活动推荐条目列表
            campaign.setMktCamItem(mktCamItemList);
        }

        //设置id  和href  转换时间为对应格式
        campaign.setId(Long.valueOf(id));
        campaign.setHref("/mktCampaign/" + id);

        resultMap.put("params", campaign);
        return resultMap;
    }


    /**
     * 通过活动id获取策略列表
     * @param campaignId
     */
    public static List<MktCamStrategyConfRelDO> getStrategyList(Long campaignId){
        List<MktCamStrategyConfRelDO> list=new ArrayList<>();


        return list;
    }


    /**
     * 通过策略id获取规则列表
     * @param strategyId
     * @return
     */
    public static List<MktStrategyConfRuleDO> getRuleList(Long strategyId){
        List<MktStrategyConfRuleDO> list=new ArrayList<>();


        return list;
    }


    /**
     * 通过规则获取活动推荐条目列表
     * @param ruleId
     * @return
     */
    public static List<MktCamItem> getMktCamItemList(Long ruleId){
        List<MktCamItem> list=new ArrayList<>();

        return list;
    }











    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        return null;
    }

    @Override
    public Map<String, Object> deleteById(String id) {
        return null;
    }

    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        return null;
    }

    /**
     * 转换为openapi格式
     * @param mktCampaignDO
     * @return
     */
    public OpenMktCampaign getOpenCampaign(MktCampaignDO mktCampaignDO){
        OpenMktCampaign campaign=BeanUtil.create(mktCampaignDO,new OpenMktCampaign());
        //转换时间格式  beginTime endTime  planBeginTime  planEndTime statusDate
        if(null!=mktCampaignDO.getBeginTime()){
            campaign.setBeginTime(DateUtil.getDatetime(mktCampaignDO.getBeginTime()));
        }
        if(null!=mktCampaignDO.getEndTime()){
            campaign.setEndTime(DateUtil.getDatetime(mktCampaignDO.getEndTime()));
        }
        if(null!=mktCampaignDO.getPlanBeginTime()){
            campaign.setPlanBeginTime(DateUtil.getDatetime(mktCampaignDO.getPlanBeginTime()));
        }
        if(null!=mktCampaignDO.getPlanEndTime()){
            campaign.setPlanEndTime(DateUtil.getDatetime(mktCampaignDO.getPlanEndTime()));
        }

        return campaign;
    }


    public static void main(String[] args) {
        String s="997/998/999/1000";
        String[] split = s.split("/");
        for (int i = 0; i <split.length ; i++) {
            System.out.println(split[i]);
        }
        
    }

    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> singleMktCampaign = new HashMap<>();
        List<Map<String, Object>> mktCampaigns = new ArrayList<>();

        CreateMktCampaignReq createMktCampaignReq = (CreateMktCampaignReq)object;
        List<OpenMktCampaignEntity> openMktCampaignEntityList = createMktCampaignReq.getMktCampaignDetails();
        for(OpenMktCampaignEntity openMktCampaignEntity : openMktCampaignEntityList) {
            singleMktCampaign.put("mktActivityNbr",openMktCampaignEntity.getMktActivityNbr());
            singleMktCampaign.put("mktCampaignId",openMktCampaignEntity.getMktCampaignId());
            singleMktCampaign.put("mktCampaignName",openMktCampaignEntity.getMktCampaignName());
            mktCampaigns.add(singleMktCampaign);
            if(!openMktCampaignEntity.getActType().equals("ADD")) {
                resultObject.put("mktCampaigns",mktCampaigns);
                resultMap.put("resultCode","1");
                resultMap.put("resultMsg","处理失败,营服活动的数据操作类型字段的值不是ADD");
                resultMap.put("resultObject",resultObject);
                return resultMap;
            }
            MktCampaignDO singleCampaign = mktCampaignMapper.selectByPrimaryKey(openMktCampaignEntity.getMktCampaignId());
            if(singleCampaign != null) {
                resultObject.put("mktCampaigns",mktCampaigns);
                resultMap.put("resultCode","1");
                resultMap.put("resultMsg","处理失败，营服活动标识"+ openMktCampaignEntity.getMktCampaignId() + "已存在");
                resultMap.put("resultObject",resultObject);
                return resultMap;
            }
            //新增营服活动
            MktCampaignDO mktCampaignDO = BeanUtil.create(openMktCampaignEntity, new MktCampaignDO());
            mktCampaignDO.setInitId(openMktCampaignEntity.getMktCampaignId());
            mktCampaignDO.setMktCampaignCategory(openMktCampaignEntity.getManageType());
            mktCampaignDO.setLanId(AreaCodeEnum.getLandIdByRegionId(mktCampaignDO.getRegionId()));
            mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
            mktCampaignDO.setStatusDate(openMktCampaignEntity.getCreateDate());
            mktCampaignDO.setUpdateDate(openMktCampaignEntity.getCreateDate());
            mktCampaignDO.setUpdateStaff(openMktCampaignEntity.getCreateStaff());
            mktCampaignMapper.insert(mktCampaignDO);

            //新增营服活动分群规则
            List<OpenMktCamGrpRulEntity> mktCamGrpRuls = openMktCampaignEntity.getMktCamGrpRuls();
            if(mktCamGrpRuls != null && mktCamGrpRuls.size() > 0) {
                for (OpenMktCamGrpRulEntity openMktCamGrpRulEntity : mktCamGrpRuls) {
                    if (openMktCamGrpRulEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动分群规则的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    MktCamGrpRul mktCamGrpRul = BeanUtil.create(openMktCamGrpRulEntity, new MktCamGrpRul());
                    //新增目标分群
                    OpenTarGrpEntity openTarGrpEntity = openMktCamGrpRulEntity.getTarGrp();
                    mktCamGrpRul.setTarGrpId(openMktCamGrpRulEntity.getTarGrpId());
                    TarGrp tarGrp = new TarGrp();
                    if(openTarGrpEntity != null) {
                        tarGrp = BeanUtil.create(openTarGrpEntity, new TarGrp());
                        tarGrpMapper.createTarGrp(tarGrp);
                        mktCamGrpRul.setTarGrpId(tarGrp.getTarGrpId());
                        //新增目标分群条件
                        OpenTarGrpConditionEntity openTarGrpConditionEntity = openTarGrpEntity.getTarGrpConditions();
                        if (openTarGrpConditionEntity != null) {
                            TarGrpCondition tarGrpCondition = BeanUtil.create(openTarGrpConditionEntity, new TarGrpCondition());
                            tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                            tarGrpConditionMapper.insert(tarGrpCondition);
                        }
                    }
                    //关联表
                    mktCamGrpRul.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamGrpRul.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    mktCamGrpRul.setStatusDate(mktCampaignDO.getCreateDate());
                    mktCamGrpRul.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCamGrpRul.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCamGrpRul.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktCamGrpRul.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCamGrpRulMapper.insert(mktCamGrpRul);
                }
            }

            //新增营服活动推荐条目
            List<OpenMktCamItemEntity> mktCamItems = openMktCampaignEntity.getMktCamItems();
            if(mktCamItems != null && mktCamItems.size() > 0) {
                for (OpenMktCamItemEntity openMktCamItemEntity : mktCamItems) {
                    if (!openMktCamItemEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动推荐条目的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    MktCamItem mktCamItem = BeanUtil.create(openMktCamItemEntity, new MktCamItem());
                    mktCamItem.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamItem.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    mktCamItem.setStatusDate(mktCampaignDO.getCreateDate());
                    mktCamItem.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCamItem.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCamItem.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktCamItem.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCamItemMapper.insert(mktCamItem);
                }
            }

            //新增营服活动渠道推送配置
            List<OpenMktCamChlConfEntity> mktCamChlConfDetails = openMktCampaignEntity.getMktCamChlConfDetails();
            if(mktCamChlConfDetails != null && mktCamChlConfDetails.size() > 0) {
                for (OpenMktCamChlConfEntity openMktCamChlConfEntity : mktCamChlConfDetails) {
                    if (!openMktCamChlConfEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动渠道推送配置的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConfEntity, new MktCamChlConfDO());
                    mktCamChlConfDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamChlConfMapper.insert(mktCamChlConfDO);
                    //新增营服活动脚本
                    List<OpenMktCamScriptEntity> mktCamScripts = openMktCamChlConfEntity.getMktCamScripts();
                    if(mktCamScripts != null) {
                        for (OpenMktCamScriptEntity openMktCamScriptEntity : mktCamScripts) {
                            if (!openMktCamScriptEntity.getActType().equals("ADD")) {
                                resultObject.put("mktCampaigns", mktCampaigns);
                                resultMap.put("resultCode", "1");
                                resultMap.put("resultMsg", "处理失败,营服活动脚本的数据操作类型字段的值不是ADD");
                                resultMap.put("resultObject", resultObject);
                                return resultMap;
                            }
                            CamScript camScript = BeanUtil.create(openMktCamScriptEntity, new CamScript());
                            camScript.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                            camScript.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                            camScript.setCreateStaff(mktCampaignDO.getCreateStaff());
                            camScript.setCreateDate(mktCampaignDO.getCreateDate());
                            camScript.setUpdateStaff(mktCampaignDO.getCreateStaff());
                            camScript.setUpdateDate(mktCampaignDO.getCreateDate());
                            mktCamScriptMapper.insert(camScript);
                        }
                    }
                    //新增调查问卷
                    List<OpenMktCamQuestEntity> mktCamQuests = openMktCamChlConfEntity.getMktCamQuests();
                    //新增营服活动执行渠道配置属性
                    List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs = openMktCamChlConfEntity.getMktCamChlConfAttrs();
                    if(mktCamChlConfAttrs != null) {
                        for (OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity : mktCamChlConfAttrs) {
                            MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttrEntity, new MktCamChlConfAttrDO());
                            mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                            mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                            mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                        }
                    }
                }
            }

            //新增营服活动执行算法规则关联
            List<OpenMktCpcAlgorithmsRulEntity> mktCpcAlgorithmsRulDetails = openMktCampaignEntity.getMktCpcAlgorithmsRulDetails();
            if(mktCpcAlgorithmsRulDetails != null && mktCpcAlgorithmsRulDetails.size() > 0) {
                for (OpenMktCpcAlgorithmsRulEntity openMktCpcAlgorithmsRulEntity : mktCpcAlgorithmsRulDetails) {
                    if (!openMktCpcAlgorithmsRulEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动执行算法规则关联的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    //新增cpc算法规则
                    MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = BeanUtil.create(openMktCpcAlgorithmsRulEntity, new MktCpcAlgorithmsRulDO());
                    mktCpcAlgorithmsRulDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    mktCpcAlgorithmsRulDO.setStatusDate(mktCampaignDO.getCreateDate());
                    mktCpcAlgorithmsRulDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCpcAlgorithmsRulDO.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCpcAlgorithmsRulDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktCpcAlgorithmsRulDO.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
                    //关联表
                    MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
                    mktCamRecomCalcRelDO.setAlgorithmsRulId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
                    mktCamRecomCalcRelDO.setAlgoId(1L);
                    mktCamRecomCalcRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamRecomCalcRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    mktCamRecomCalcRelDO.setStatusDate(mktCampaignDO.getCreateDate());
                    mktCamRecomCalcRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCamRecomCalcRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCamRecomCalcRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktCamRecomCalcRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
                }
            }

            //新增营服活动关联事件
            List<OpenEvent> mktCampaignEvts = openMktCampaignEntity.getMktCampaignEvts();
            if(mktCampaignEvts != null && mktCampaignEvts.size() > 0) {
                for (OpenEvent openEvent : mktCampaignEvts) {
                    MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
                    mktCamEvtRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamEvtRelDO.setEventId(openEvent.getEventId());
                    mktCamEvtRelDO.setCampaignSeq(0);
                    mktCamEvtRelDO.setLevelConfig(0);
                    mktCamEvtRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    mktCamEvtRelDO.setStatusDate(mktCampaignDO.getCreateDate());
                    mktCamEvtRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCamEvtRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCamEvtRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktCamEvtRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                }
            }

            //新增营服活动渠道执行策略
            List<OpenMktStrategyEntity> mktCampaignStrategyDetails = openMktCampaignEntity.getMktCampaignStrategyDetails();
            if(mktCampaignStrategyDetails != null && mktCampaignStrategyDetails.size() > 0) {
                for (OpenMktStrategyEntity openMktStrategyEntity : mktCampaignStrategyDetails) {
                    //新增营销服务策略
                    MktStrategy mktStrategy = BeanUtil.create(openMktStrategyEntity, new MktStrategy());
                    mktStrategy.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    mktStrategy.setStatusDate(mktCampaignDO.getCreateDate());
                    mktStrategy.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktStrategy.setCreateDate(mktCampaignDO.getCreateDate());
                    mktStrategy.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktStrategy.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktStrategyMapper.insert(mktStrategy);
                    MktCamStrategyRel mktCamStrategyRel = new MktCamStrategyRel();
                    mktCamStrategyRel.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamStrategyRel.setStrategyId(mktStrategy.getStrategyId());
                    mktCamStrategyRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    mktCamStrategyRel.setStatusDate(mktCampaignDO.getCreateDate());
                    mktCamStrategyRel.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCamStrategyRel.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCamStrategyRel.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktCamStrategyRel.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCamStrategyRelMapper.insert(mktCamStrategyRel);
                }
            }

            //新增营服活动关系
            List<OpenMktCampaignRelEntity> mktCampaignRels = openMktCampaignEntity.getMktCampaignRels();

            //新增事件触发活动规则
            List<OpenEvtTrigCamRulEntity> evtTrigCamRuls = openMktCampaignEntity.getEvtTrigCamRuls();

        }
        resultObject.put("mktCampaigns",mktCampaigns);
        resultMap.put("resultCode","0");
        resultMap.put("resultMsg","处理成功");
        resultMap.put("resultObject",resultObject);
        return resultMap;
    }

    @Override
    public Map<String,Object> updateMktCampaign(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> singleMktCampaign = new HashMap<>();
        List<Map<String, Object>> mktCampaigns = new ArrayList<>();

        ModMktCampaignReq modMktCampaignReq = (ModMktCampaignReq)object;
        List<OpenMktCampaignEntity> openMktCampaignEntityList = modMktCampaignReq.getMktCampaignDetails();
        for(OpenMktCampaignEntity openMktCampaignEntity : openMktCampaignEntityList) {
            singleMktCampaign.put("mktActivityNbr",openMktCampaignEntity.getMktActivityNbr());
            singleMktCampaign.put("mktCampaignId",openMktCampaignEntity.getMktCampaignId());
            singleMktCampaign.put("mktCampaignName",openMktCampaignEntity.getMktCampaignName());
            mktCampaigns.add(singleMktCampaign);

            //修改营服活动
            MktCampaignDO mktCampaignDO = BeanUtil.create(openMktCampaignEntity, new MktCampaignDO());
            if(openMktCampaignEntity.getActType().equals("MOD")) {
                MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(openMktCampaignEntity.getMktCampaignId());
                if (mktCampaign == null) {
                    resultObject.put("mktCampaigns", mktCampaigns);
                    resultMap.put("resultCode", "1");
                    resultMap.put("resultMsg", "处理失败,对应的营服活动不存在");
                    resultMap.put("resultObject", resultObject);
                    return resultMap;
                }
                mktCampaignDO.setMktCampaignCategory(openMktCampaignEntity.getManageType());
                mktCampaignDO.setLanId(AreaCodeEnum.getLandIdByRegionId(mktCampaignDO.getRegionId()));
                mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
                mktCampaignDO.setStatusDate(mktCampaign.getStatusDate());
                mktCampaignDO.setCreateDate(mktCampaign.getCreateDate());
                mktCampaignDO.setCreateStaff(openMktCampaignEntity.getCreateStaff());
                mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
            }
            //修改营服活动分群规则
            List<OpenMktCamGrpRulEntity> mktCamGrpRuls = openMktCampaignEntity.getMktCamGrpRuls();
            if(mktCamGrpRuls.size() > 0) {
                for (OpenMktCamGrpRulEntity openMktCamGrpRulEntity : mktCamGrpRuls) {
                    MktCamGrpRul mktCamGrpRul = BeanUtil.create(openMktCamGrpRulEntity, new MktCamGrpRul());
                    mktCamGrpRul.setTarGrpId(openMktCamGrpRulEntity.getTarGrpId());
                    //修改目标分群
                    OpenTarGrpEntity openTarGrpEntity = openMktCamGrpRulEntity.getTarGrp();
                    if(openTarGrpEntity != null) {
                        OpenTarGrpConditionEntity openTarGrpConditionEntity = openTarGrpEntity.getTarGrpConditions();
                        TarGrp tarGrp = BeanUtil.create(openTarGrpEntity, new TarGrp());
                        if (openTarGrpEntity.getActType().equals("ADD")) {
                            tarGrpMapper.createTarGrp(tarGrp);
                            mktCamGrpRul.setTarGrpId(tarGrp.getTarGrpId());
                            //修改目标分群条件
                            if (openTarGrpConditionEntity != null) {
                                TarGrpCondition tarGrpCondition = BeanUtil.create(openTarGrpConditionEntity, new TarGrpCondition());
                                tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                                tarGrpConditionMapper.insert(tarGrpCondition);
                            }
                        } else if (openTarGrpEntity.getActType().equals("MOD")) {
                            TarGrp tarGroup = tarGrpMapper.selectByPrimaryKey(openMktCamGrpRulEntity.getTarGrpId());
                            if (tarGroup == null) {
                                resultObject.put("mktCampaigns", mktCampaigns);
                                resultMap.put("resultCode", "1");
                                resultMap.put("resultMsg", "处理失败,对应的目标分群不存在");
                                resultMap.put("resultObject", resultObject);
                                return resultMap;
                            }
                            tarGrpMapper.modTarGrp(tarGrp);
                            if(openTarGrpConditionEntity != null) {
                                TarGrpCondition tarGrpCondition = BeanUtil.create(openTarGrpConditionEntity, new TarGrpCondition());
                                tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                                tarGrpConditionMapper.updateByPrimaryKey(tarGrpCondition);
                            }
                        } else if (openTarGrpEntity.getActType().equals("DEL")) {
                            TarGrp tarGroup = tarGrpMapper.selectByPrimaryKey(openMktCamGrpRulEntity.getTarGrpId());
                            if (tarGroup != null) {
                                tarGrpMapper.deleteByPrimaryKey(openMktCamGrpRulEntity.getTarGrpId());
                            }
                        }
                    }
                    if(openMktCamGrpRulEntity.getActType().equals("ADD")) {
                        mktCamGrpRul.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamGrpRul.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                        mktCamGrpRul.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamGrpRul.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamGrpRul.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamGrpRul.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamGrpRul.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamGrpRulMapper.insert(mktCamGrpRul);
                    }else if(openMktCamGrpRulEntity.getActType().equals("MOD")) {
                        MktCamGrpRul mktCampaignGrpRul = mktCamGrpRulMapper.selectByTarGrpId(openMktCamGrpRulEntity.getTarGrpId());
                        if(mktCampaignGrpRul == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,活动与分群关联关系不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        mktCamGrpRul.setMktCamGrpRulId(mktCampaignGrpRul.getMktCamGrpRulId());
                        mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);
                    }else if(openMktCamGrpRulEntity.getActType().equals("DEL")) {
                        mktCamGrpRulMapper.deleteByTarGrpId(openMktCamGrpRulEntity.getMktCamGrpRulId());
                    }
                }
            }

            //修改营服活动推荐条目
            List<OpenMktCamItemEntity> mktCamItems = openMktCampaignEntity.getMktCamItems();
            if(mktCamItems.size() > 0) {
                for (OpenMktCamItemEntity openMktCamItemEntity : mktCamItems) {
                    MktCamItem mktCamItem = BeanUtil.create(openMktCamItemEntity, new MktCamItem());
                    if(openMktCamItemEntity.getActType().equals("ADD")) {
                        mktCamItem.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamItem.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                        mktCamItem.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamItem.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamItem.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamItem.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamItem.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamItemMapper.insert(mktCamItem);
                    }else if(openMktCamItemEntity.getActType().equals("MOD")) {
                        MktCamItem mktCampaignItem = mktCamItemMapper.selectByPrimaryKey(openMktCamItemEntity.getMktCamItemId());
                        if(mktCampaignItem == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应的活动推荐条目不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        mktCamItem.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamItemMapper.updateByPrimaryKey(mktCamItem);
                    }else if(openMktCamItemEntity.getActType().equals("DEL")) {
                        mktCamItemMapper.deleteByPrimaryKey(openMktCamItemEntity.getMktCamItemId());
                    }
                }
            }

            //修改营服活动渠道推送配置
            List<OpenMktCamChlConfEntity> mktCamChlConfDetails = openMktCampaignEntity.getMktCamChlConfDetails();
            if(mktCamChlConfDetails.size() > 0) {
                for (OpenMktCamChlConfEntity openMktCamChlConfEntity : mktCamChlConfDetails) {
                    List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs = openMktCamChlConfEntity.getMktCamChlConfAttrs();
                    if(openMktCamChlConfEntity.getActType().equals("ADD")) {
                        MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConfEntity, new MktCamChlConfDO());
                        mktCamChlConfDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamChlConfMapper.insert(mktCamChlConfDO);
                        //新增调查问卷
                        List<OpenMktCamQuestEntity> mktCamQuests = openMktCamChlConfEntity.getMktCamQuests();
                        //新增营服活动执行渠道配置属性
                        if(mktCamChlConfAttrs.size() > 0 ) {
                            for (OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity : mktCamChlConfAttrs) {
                                MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttrEntity, new MktCamChlConfAttrDO());
                                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                                mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                                mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                            }
                        }
                    }else if(openMktCamChlConfEntity.getActType().equals("MOD")) {
                        MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(openMktCamChlConfEntity.getEvtContactConfId());
                        if(mktCamChlConf == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应的活动渠道推送配置不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConfEntity, new MktCamChlConfDO());
                        mktCamChlConfDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamChlConfMapper.updateByPrimaryKey(mktCamChlConfDO);
                        //修改调查问卷
                        List<OpenMktCamQuestEntity> mktCamQuests = openMktCamChlConfEntity.getMktCamQuests();
                        //修改营服活动执行渠道配置属性
                        if(mktCamChlConfAttrs.size() > 0 ) {
                            mktCamChlConfAttrMapper.deleteByEvtContactConfId(openMktCamChlConfEntity.getEvtContactConfId());
                            for (OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity : mktCamChlConfAttrs) {
                                MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttrEntity, new MktCamChlConfAttrDO());
                                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                                mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                                mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                            }
                        }
                    }else if(openMktCamChlConfEntity.getActType().equals("DEL")) {
                        mktCamChlConfMapper.deleteByPrimaryKey(openMktCamChlConfEntity.getEvtContactConfId());
                        mktCamChlConfAttrMapper.deleteByEvtContactConfId(openMktCamChlConfEntity.getEvtContactConfId());
                    }
                    //修改营服活动脚本
                    List<OpenMktCamScriptEntity> mktCamScripts = openMktCamChlConfEntity.getMktCamScripts();
                    if(mktCamScripts.size() > 0) {
                        for(OpenMktCamScriptEntity openMktCamScriptEntity : mktCamScripts) {
                            CamScript camScript = BeanUtil.create(openMktCamScriptEntity, new CamScript());
                            if(openMktCamScriptEntity.getActType().equals("ADD")) {
                                camScript.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                                camScript.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                                camScript.setCreateStaff(mktCampaignDO.getCreateStaff());
                                camScript.setCreateDate(mktCampaignDO.getCreateDate());
                                camScript.setUpdateStaff(mktCampaignDO.getCreateStaff());
                                camScript.setUpdateDate(mktCampaignDO.getCreateDate());
                                mktCamScriptMapper.insert(camScript);
                            }else if(openMktCamScriptEntity.getActType().equals("MOD")) {
                                CamScript campaignScript = mktCamScriptMapper.selectByPrimaryKey(openMktCamScriptEntity.getMktCampaignScptId());
                                if(campaignScript == null) {
                                    resultObject.put("mktCampaigns", mktCampaigns);
                                    resultMap.put("resultCode", "1");
                                    resultMap.put("resultMsg", "处理失败,对应的营服活动脚本不存在");
                                    resultMap.put("resultObject", resultObject);
                                    return resultMap;
                                }
                                camScript.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                                mktCamScriptMapper.updateByPrimaryKey(camScript);
                            }else if(openMktCamScriptEntity.getActType().equals("DEL")) {
                                mktCamScriptMapper.deleteByPrimaryKey(openMktCamScriptEntity.getMktCampaignScptId());
                            }
                        }
                    }
                }
            }

            //修改营服活动执行算法规则关联
            List<OpenMktCpcAlgorithmsRulEntity> mktCpcAlgorithmsRulDetails = openMktCampaignEntity.getMktCpcAlgorithmsRulDetails();
            if(mktCpcAlgorithmsRulDetails.size() > 0) {
                for (OpenMktCpcAlgorithmsRulEntity openMktCpcAlgorithmsRulEntity : mktCpcAlgorithmsRulDetails) {
                    MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = BeanUtil.create(openMktCpcAlgorithmsRulEntity, new MktCpcAlgorithmsRulDO());
                    if (openMktCpcAlgorithmsRulEntity.getActType().equals("ADD")) {
                        mktCpcAlgorithmsRulDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                        mktCpcAlgorithmsRulDO.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCpcAlgorithmsRulDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCpcAlgorithmsRulDO.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCpcAlgorithmsRulDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCpcAlgorithmsRulDO.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
                        //关联表
                        MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
                        mktCamRecomCalcRelDO.setAlgorithmsRulId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
                        mktCamRecomCalcRelDO.setAlgoId(1L);
                        mktCamRecomCalcRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamRecomCalcRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                        mktCamRecomCalcRelDO.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamRecomCalcRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamRecomCalcRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamRecomCalcRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamRecomCalcRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
                    } else if (openMktCpcAlgorithmsRulEntity.getActType().equals("MOD")) {
                        //修改cpc算法规则
                        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRul = mktCpcAlgorithmsRulMapper.selectByPrimaryKey(openMktCpcAlgorithmsRulEntity.getAlgorithmsRulId());
                        if (mktCpcAlgorithmsRul == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应的cpc算法规则不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        mktCpcAlgorithmsRulMapper.updateByPrimaryKey(mktCpcAlgorithmsRulDO);
                    } else if (openMktCpcAlgorithmsRulEntity.getActType().equals("DEL")) {
                        mktCpcAlgorithmsRulMapper.deleteByPrimaryKey(openMktCpcAlgorithmsRulEntity.getAlgorithmsRulId());
                    }
                }
            }

            //修改营服活动关联事件
            List<OpenEvent> mktCampaignEvts = openMktCampaignEntity.getMktCampaignEvts();
            if(mktCampaignEvts.size() > 0) {
                for(OpenEvent openEvent : mktCampaignEvts) {
                    if(openEvent.getActType().equals("ADD")) {
                        MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
                        mktCamEvtRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamEvtRelDO.setEventId(openEvent.getEventId());
                        mktCamEvtRelDO.setCampaignSeq(0);
                        mktCamEvtRelDO.setLevelConfig(0);
                        mktCamEvtRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                        mktCamEvtRelDO.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamEvtRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamEvtRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamEvtRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamEvtRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                    }else if(openEvent.getActType().equals("DEL")) {
                        MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.findByCampaignIdAndEvtId(mktCampaignDO.getMktCampaignId(),openEvent.getEventId());
                        if(mktCamEvtRelDO != null) {
                            mktCamEvtRelMapper.deleteByPrimaryKey(mktCamEvtRelDO.getMktCampEvtRelId());
                        }
                    }
                }
            }

            //修改营服活动渠道执行策略
            List<OpenMktStrategyEntity> mktCampaignStrategyDetails = openMktCampaignEntity.getMktCampaignStrategyDetails();
            if(mktCampaignStrategyDetails.size() > 0) {
                for (OpenMktStrategyEntity openMktStrategyEntity : mktCampaignStrategyDetails) {
                    MktStrategy mktStrategy = BeanUtil.create(openMktStrategyEntity, new MktStrategy());
                    if(openMktStrategyEntity.getActType().equals("ADD")) {
                        //新增营销服务策略
                        mktStrategy.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                        mktStrategy.setStatusDate(mktCampaignDO.getCreateDate());
                        mktStrategy.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktStrategy.setCreateDate(mktCampaignDO.getCreateDate());
                        mktStrategy.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktStrategy.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktStrategyMapper.insert(mktStrategy);
                        MktCamStrategyRel mktCamStrategyRel = new MktCamStrategyRel();
                        mktCamStrategyRel.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamStrategyRel.setStrategyId(mktStrategy.getStrategyId());
                        mktCamStrategyRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                        mktCamStrategyRel.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamStrategyRel.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamStrategyRel.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamStrategyRel.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamStrategyRel.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamStrategyRelMapper.insert(mktCamStrategyRel);
                    }else if(openMktStrategyEntity.getActType().equals("MOD")) {
                        MktStrategy mktCamStrategy = mktStrategyMapper.selectByPrimaryKey(openMktStrategyEntity.getStrategyId());
                        if(mktCamStrategy == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应的营销服务策略不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        mktStrategyMapper.updateByPrimaryKey(mktStrategy);
                    }else if(openMktStrategyEntity.getActType().equals("DEL")) {
                        mktStrategyMapper.deleteByPrimaryKey(mktStrategy.getStrategyId());
                        mktCamStrategyRelMapper.deleteByStrategyId(mktStrategy.getStrategyId());
                    }
                }
            }

            //修改营服活动关系
            List<OpenMktCampaignRelEntity> mktCampaignRels = openMktCampaignEntity.getMktCampaignRels();

            //修改事件触发活动规则
            List<OpenEvtTrigCamRulEntity> evtTrigCamRuls = openMktCampaignEntity.getEvtTrigCamRuls();
        }
        resultObject.put("mktCampaigns",mktCampaigns);
        resultMap.put("resultCode","0");
        resultMap.put("resultMsg","处理成功");
        resultMap.put("resultObject",resultObject);
        return resultMap;
    }
}
