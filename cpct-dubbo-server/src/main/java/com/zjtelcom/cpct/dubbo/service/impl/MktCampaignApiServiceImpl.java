package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
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
    private MktCampaignService mktCampaignService;
    @Autowired
    private MktRequestMapper  mktRequestMapper;
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
    public Map<String, Object> getStaffByMktRequest(Map<String, Object> paramMap) {

        Map<String,Object> resultMap  = new HashMap<>();
        String requestType =(String) paramMap.get("requestType");
        String nodeId = (String)paramMap.get("nodeId");
        Integer mktCamId = (Integer)paramMap.get("mktCamId");
        Map<String,Object> dataMap  = new HashMap<>();
        logger.info("需求函类型获取审批员工：" + requestType);
        logger.info("需求函类型获取审批员工：" + nodeId);
        logger.info("需求函类型获取审批员工：" + mktCamId);
        try {

            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCamId.longValue());
            //12是外场营销目录
            if(mktCampaignDO.getDirectoryId() == 614401299L && mktCampaignDO.getLanId() == 571){
                MktRequestDO  mktRequestDO = mktRequestMapper.getRequestInfoByMktId(requestType,nodeId,mktCamId.longValue());
                logger.info("需求函类型获取审批员工：" + mktRequestDO);
                dataMap.put("requestId",mktRequestDO.getRequestId());
                dataMap.put("requestType",mktRequestDO.getRequestType());
                dataMap.put("nodeId",mktRequestDO.getNodeId());
                dataMap.put("catelogId",mktRequestDO.getCatelogId());
                dataMap.put("lanId",mktRequestDO.getLanId());
                String staffjson  = mktRequestDO.getStaff();
                JSONArray objects  = JSONObject.parseArray(staffjson);
                List<StaffDO> staffList = new ArrayList();
                for(int i=0; i<objects.size(); i++){
                    //通过数组下标取到object，使用强转转为JSONObject，之后进行操作
                    JSONObject object = (JSONObject) objects.get(i);
                    String name = object.getString("name");
                    String staffId = object.getString("staffid");
                    logger.info(name + "需求函类型获取审批员工" + staffId);
                    StaffDO staffDO = new StaffDO();
                    staffDO.setName(name);
                    staffDO.setStaffid(staffId);
                    staffList.add(staffDO);
                }
                dataMap.put("staff",staffList);
            }

        }catch ( Exception e){
            e.printStackTrace();
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMessage","消息返回异常");
            return resultMap;
        }
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMessage","消息返回成功");
        resultMap.put("data",dataMap);
        return resultMap;
    }

}
