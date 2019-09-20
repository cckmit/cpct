package com.zjtelcom.cpct.dubbo.service.impl;

import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
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
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.filter.FilterRuleModel;
import com.zjtelcom.cpct.dubbo.model.*;
import com.zjtelcom.cpct.dubbo.model.MktCamChlConfDetail;
import com.zjtelcom.cpct.dubbo.model.MktCamChlResult;
import com.zjtelcom.cpct.dubbo.service.MktCampaignApiService;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
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

/**
 * @Description:
 * @author: linchao
 * @date: 2018/07/17 11:11
 * @version: V1.0
 */

@Service
@Transactional
public class MktCampaignApiServiceImpl implements MktCampaignApiService {

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

    @Autowired
    private MktCampaignService mktCampaignService;
    @Autowired(required =false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    //同步表名
    private static final String tableName = "mkt_campaign";



    @Override
    public RetCamResp qryMktCampaignDetail(Long initId) throws Exception {
        RetCamResp ret = new RetCamResp();
        MktCampaignResp mktCampaignResp = new MktCampaignResp();
        // 获取活动基本信息
        //MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByInitId(initId);
        Long mktCampaignId = mktCampaignDO.getMktCampaignId();
        try {
            mktCampaignResp = BeanUtil.create(mktCampaignDO, new MktCampaignResp());
            // 获取所有的sysParam
            Map<String, String> paramMap = new HashMap<>();
            List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
            for (SysParams sysParams : sysParamList) {
                paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
            }
            mktCampaignResp.setTiggerTypeValue(paramMap.
                    get(ParamKeyEnum.TIGGER_TYPE.getParamKey() + mktCampaignDO.getTiggerType()));
            mktCampaignResp.setMktCampaignCategoryValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey() + mktCampaignDO.getMktCampaignCategory()));
            mktCampaignResp.setMktCampaignTypeValue(paramMap.
                    get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignDO.getMktCampaignType()));
            mktCampaignResp.setStatusCdValue(paramMap.
                    get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignDO.getStatusCd()));
            mktCampaignResp.setMktCampaignId(initId);
            // 获取过滤规则集合
            ArrayList<FilterRuleModel> filterRuleModels = filterRuleMapper.selectFilterRuleByStrategyIdArrayList(mktCampaignId);
            for (FilterRuleModel filterRuleModel:filterRuleModels) {
                logger.info("filterRuleModel = " + filterRuleModel.getLabelName());
            }

            mktCampaignResp.setFilterRuleModelList(filterRuleModels);

            // 获取活动关联策略集合
            List<MktStrategyConfDO> mktStrategyConfDOList = mktStrategyConfMapper.selectByCampaignId(mktCampaignId);
            ArrayList<MktStrategyConfResp> mktStrategyConfRespList = new ArrayList<>();
        //    List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
            for (MktStrategyConfDO mktStrategyConfDO : mktStrategyConfDOList) {
                MktStrategyConfResp mktStrategyConfResp = getMktStrategyConf(mktStrategyConfDO.getMktStrategyConfId());
                mktStrategyConfResp.setMktStrategyConfId(mktStrategyConfDO.getInitId());
                mktStrategyConfRespList.add(mktStrategyConfResp);
            }
            mktCampaignResp.setMktStrategyConfRespList(mktStrategyConfRespList);
            ret.setResultCode(CODE_SUCCESS);
            ret.setData(mktCampaignResp);
            ret.setResultMsg("success");
        } catch (Exception e) {
            ret.setResultCode(CODE_FAIL);
            ret.setResultMsg("failed");
            ret.setData(mktCampaignResp);
        }
        return ret;
    }

    /**
     * 查询配置配置信息
     *
     * @param mktStrategyConfId
     * @return
     */

    public MktStrategyConfResp getMktStrategyConf(Long mktStrategyConfId) throws Exception {
        MktStrategyConfResp mktStrategyConfResp = new MktStrategyConfResp();

        //更具Id查询策略配置信息
        MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktStrategyConfId);
        CopyPropertiesUtil.copyBean2Bean(mktStrategyConfResp, mktStrategyConfDO);

        //查询与策略匹配的所有规则
        ArrayList<MktStrConfRuleResp> mktStrConfRuleRespList = new ArrayList<>();
        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfId);
        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
            MktStrConfRuleResp mktStrConfRuleResp = BeanUtil.create(mktStrategyConfRuleDO, new MktStrConfRuleResp());
            mktStrConfRuleResp.setMktStrategyConfRuleId(mktStrategyConfRuleDO.getInitId());

            if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                ArrayList<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();

                List<MktCamChlConf> mktCamChlConfList = new ArrayList<>();
                for (int i = 0; i < evtContactConfIds.length; i++) {
                    if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                        MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(evtContactConfIds[i]));
                        MktCamChlConf mktCamChlConf = BeanUtil.create(mktCamChlConfDO, new MktCamChlConf());
                        mktCamChlConfList.add(mktCamChlConf);
                        MktCamChlConfDetail mktCamChlConfDetail = getMktCamChlConf(Long.valueOf(evtContactConfIds[i]));
                        // 获取触点渠道编码
                        Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
                        if(channel!=null){
                            mktCamChlConfDetail.setContactChlCode(channel.getContactChlCode());
                        }
                        mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    }
                }
                mktStrConfRuleResp.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            }

            if (mktStrategyConfRuleDO.getMktCamChlResultId() != null) {
                String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                ArrayList<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
                for (int i = 0; i < mktCamChlResultIds.length; i++) {
                    if (mktCamChlResultIds[i] != null && !"".equals(mktCamChlResultIds[i])) {
                        MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(Long.valueOf(mktCamChlResultIds[i]));
                        MktCamChlResult mktCamChlResult = BeanUtil.create(mktCamChlResultDO, new MktCamChlResult());
                        List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultDO.getMktCamChlResultId());
                        ArrayList<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                        for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                            MktCamChlConfDetail mktCamChlConfDetail = getMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
                            // 获取触点渠道编码
                            Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
                            if(channel!=null){
                                mktCamChlConfDetail.setContactChlCode(channel.getContactChlCode());
                            }
                            mktCamChlConfDetailList.add(mktCamChlConfDetail);
                        }
                        mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                        mktCamChlResultList.add(mktCamChlResult);
                    }
                }
                mktStrConfRuleResp.setMktCamChlResultList(mktCamChlResultList);
            }
            mktStrConfRuleRespList.add(mktStrConfRuleResp);
        }
        mktStrategyConfResp.setMktStrategyConfId(mktStrategyConfDO.getInitId());
        mktStrategyConfResp.setMktStrConfRuleRespList(mktStrConfRuleRespList);
        return mktStrategyConfResp;
    }


    public MktCamChlConfDetail getMktCamChlConf(Long evtContactConfId) {
        MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
        MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
        return mktCamChlConfDetail;
    }


    @Override
    public Map<String, Object> copyMktCampaign(Long parentMktCampaignId) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(parentMktCampaignId);
            resultMap.put("mktCampaignId", parentMktCampaignId);
            if(StatusCode.STATUS_CODE_PUBLISHED.getStatusCode().equals(mktCampaignDO.getStatusCd())){
                // 修改源活动状态
                mktCampaignService.changeMktCampaignStatus(parentMktCampaignId, StatusCode.STATUS_CODE_ADJUST.getStatusCode());
                resultMap = mktCampaignService.copyMktCampaign(parentMktCampaignId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> rollBackMktCampaign(Long childCampaignId){
        Map<String, Object> map = new HashMap<>();
        try {
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(childCampaignId);
            // 删除子活动，并回滚,并保证不能删除源活动
            if (mktCampaignDO != null && !childCampaignId.equals(mktCampaignDO.getInitId())) {
                map = mktCampaignService.delMktCampaign(childCampaignId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> salesOffShelf(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        Object type = map.get("type");
        Object id = map.get("id");
        if (type == null && id == null){
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg","请传入正确的类型和id");
            return resultMap;
        }
        List<MktCamItem> mktCamItems = mktCamItemMapper.selectByCampaignAndType(Long.valueOf(id.toString()), type.toString(), null);
        if (!mktCamItems.isEmpty()){
            for (MktCamItem mktCamItem : mktCamItems) {
                Long mktCampaignId = mktCamItem.getMktCampaignId();
                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
                Long createStaff = mktCampaignDO.getCreateStaff();
                //调用dubbo接口查询改创建人的手机号码 下发短信
                SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(createStaff, new ArrayList<Long>());
                if (systemUserDtoSysmgrResultObject != null && systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    String sysUserCode = systemUserDtoSysmgrResultObject.getResultObject().getSysUserCode();
                    // TODO  调用发送短信接口

                }
            }
        }
        return null;
    }


}
