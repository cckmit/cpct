package com.zjtelcom.cpct.open.serviceImpl.completeMktCampaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.core.util.UUIDUtil;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfAttrEntity;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfEntity;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.CompleteMktCampaign;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.CompleteMktCampaignJtReq;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.ObjRegionRel;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.OpenMktCampaignEntity;
import com.zjtelcom.cpct.open.service.completeMktCampaign.OpenCompleteMktCampaignService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class OpenCompleteMktCampaignServiceImpl extends BaseService implements OpenCompleteMktCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private MktCampaignCompleteMapper mktCampaignCompleteMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Override
    public Map<String, Object> completeMktCampaign(Long mktCampaignId, String tacheCd) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> inputMap = new HashMap<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<OpenMktCampaignEntity> openMktCampaignEntityList = new ArrayList<>();
        CompleteMktCampaignJtReq completeMktCampaignJtReq = new CompleteMktCampaignJtReq();
        CompleteMktCampaign completeMktCampaign = new CompleteMktCampaign();
        //查询出对应活动（排除非集团下发活动）
        List<MktCampaignComplete> mktCampaignCompleteList = mktCampaignCompleteMapper.selectByCampaignId(mktCampaignId);
        if(mktCampaignCompleteList.isEmpty()) {
            resultMap.put("message", "此活动不是集团活动");
            return resultMap;
        }
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        //转换成集团模型
        OpenMktCampaignEntity openMktCampaignEntity = BeanUtil.create(mktCampaignDO, new OpenMktCampaignEntity());
        openMktCampaignEntity.setMktCampaignId(mktCampaignDO.getInitId());
        if (null != mktCampaignDO.getMktCampaignCategory()) {
            openMktCampaignEntity.setManageType(mktCampaignDO.getMktCampaignCategory());
        }
        //对象区域关系
        List<ObjRegionRel> objRegionRels = new ArrayList<>();
        if(mktCampaignDO.getLanId() != null) {
            ObjRegionRel objRegionRel = new ObjRegionRel();
            objRegionRel.setObjId(mktCampaignDO.getInitId());
            objRegionRel.setObjNbr(mktCampaignDO.getMktActivityNbr());
            Long regionId = AreaCodeEnum.getRegionIdByLandId(mktCampaignDO.getLanId());
            if(regionId != null) {
                objRegionRel.setApplyRegionNbr(regionId.toString());
                objRegionRel.setApplyRegionId(regionId);
            }
            objRegionRel.setStatusCd("1000");
            objRegionRels.add(objRegionRel);
        }
        openMktCampaignEntity.setObjRegionRels(objRegionRels);
        //营服活动渠道推送配置
        List<OpenMktCamChlConfEntity> mktCamChlConfs = new ArrayList<>();
        //查活动策略
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOS = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        if (!mktCamStrategyConfRelDOS.isEmpty()) {
            for (MktCamStrategyConfRelDO m : mktCamStrategyConfRelDOS) {
                //查出策略下的规则
                List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOS = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(m.getStrategyConfId());
                if (!mktStrategyConfRuleRelDOS.isEmpty()) {
                    for (MktStrategyConfRuleRelDO ruleRelDo : mktStrategyConfRuleRelDOS) {
                        MktStrategyConfRuleDO rule = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleRelDo.getMktStrategyConfRuleId());
                        if (rule != null) {
                            //遍历规则下的渠道
                            if (StringUtils.isNotBlank(rule.getEvtContactConfId())) {
                                String[] splits = rule.getEvtContactConfId().split("/");
                                for (int i = 0; i < splits.length; i++) {
                                    MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(splits[i]));
                                    if(mktCamChlConfDO != null) {
                                        OpenMktCamChlConfEntity openMktCamChlConfEntity = BeanUtil.create(mktCamChlConfDO, new OpenMktCamChlConfEntity());
                                        openMktCamChlConfEntity.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                                        Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDO.getContactChlId());
                                        openMktCamChlConfEntity.setContactChlCode(channel.getContactChlCode());
                                        //营服活动执行渠道配置属性
                                        List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs = new ArrayList<>();
                                        List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                                        if(!mktCamChlConfAttrDOList.isEmpty()) {
                                            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                                                OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity = BeanUtil.create(mktCamChlConfAttrDO, new OpenMktCamChlConfAttrEntity());
                                                mktCamChlConfAttrs.add(openMktCamChlConfAttrEntity);
                                            }
                                        }
                                        openMktCamChlConfEntity.setMktCamChlConfAttrs(mktCamChlConfAttrs);
                                        mktCamChlConfs.add(openMktCamChlConfEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        openMktCampaignEntity.setMktCamChlConfs(mktCamChlConfs);
        openMktCampaignEntityList.add(openMktCampaignEntity);

        //配置营销互动反馈单
        /*StringBuilder detaileTacheList = new StringBuilder();
        for(MktCampaignComplete mktCampaignComplete : mktCampaignCompleteList) {
            logger.info(JSON.toJSONString(mktCampaignComplete));
            SysParams sysParams = sysParamsMapper.findParamsByValue("JTCAMPAIGN_NODE", mktCampaignComplete.getTacheCd());
            detaileTacheList.append(sysParams.getParamName()).append(" 开始：").append(sdf.format(mktCampaignComplete.getBeginTime()));
            detaileTacheList.append(" 结束：").append(sdf.format(mktCampaignComplete.getEndTime()));
            detaileTacheList.append(" 处理人：").append("Y33010117205").append("#").append("\r\n");
            if(mktCampaignComplete.getTacheCd().equals(tacheCd)) {
                BeanUtil.copy(mktCampaignComplete, completeMktCampaign);
                String beginTime = df.format(mktCampaignComplete.getBeginTime());
                String endTime = df.format(mktCampaignComplete.getEndTime());
                completeMktCampaign.setTacheCd(tacheCd);
                if(tacheCd.equals("1400")) {
                    completeMktCampaign.setTacheValueCd("10");
                }else if (tacheCd.equals("1300") || tacheCd.equals("1200")){
                    completeMktCampaign.setTacheValueCd("11");
                }
                completeMktCampaign.setStatusCd("1200");
                completeMktCampaign.setBeginTime(beginTime);
                completeMktCampaign.setEndTime(endTime);
                completeMktCampaign.setDetaileTacheList(detaileTacheList.toString());
                completeMktCampaign.setRegionCode("8330000");
                completeMktCampaign.setMktCampaigns(openMktCampaignEntityList);
                break;
            }
        }*/


        //配置营销互动反馈单
        StringBuilder detaileTacheList = new StringBuilder();
        for(MktCampaignComplete mktCampaignComplete : mktCampaignCompleteList) {
            logger.info(JSON.toJSONString(mktCampaignComplete));
            SysParams sysParams = sysParamsMapper.findParamsByValue("JTCAMPAIGN_NODE", mktCampaignComplete.getTacheCd());
            if ("1100".equals(mktCampaignComplete.getTacheCd()) || "1400".equals(mktCampaignComplete.getTacheCd())) {
                detaileTacheList.append(sysParams.getParamName()).append(" 开始：").append(sdf.format(mktCampaignComplete.getBeginTime()))
                        .append(" 处理人：").append("Y33010117205").append("#").append("\r\n");
            } else {
                if ("11".equals(mktCampaignComplete.getTacheValueCd())) {
                    MktCampaignComplete mktCampaignComplete1 = mktCampaignCompleteMapper.selectByCampaignIdAndTacheCdAndTacheValueCd(mktCampaignId, tacheCd, "10");
                    detaileTacheList.append(sysParams.getParamName()).append(" 开始：").append(sdf.format(mktCampaignComplete1.getBeginTime()))
                            .append(" 结束：").append(sdf.format(mktCampaignComplete.getEndTime()))
                            .append(" 处理人：").append("Y33010117205").append("#").append("\r\n");
                }
            }
            if(mktCampaignComplete.getTacheCd().equals(tacheCd)) {
                BeanUtil.copy(mktCampaignComplete, completeMktCampaign);
                String beginTime = df.format(mktCampaignComplete.getBeginTime());
                String endTime = df.format(mktCampaignComplete.getEndTime());
                completeMktCampaign.setTacheCd(tacheCd);
                if(tacheCd.equals("1400")) {
                    completeMktCampaign.setTacheValueCd("10");
                }else if (tacheCd.equals("1300") || tacheCd.equals("1200")){
                    completeMktCampaign.setTacheValueCd("11");
                }
                completeMktCampaign.setStatusCd("1200");
                completeMktCampaign.setBeginTime(beginTime);
                completeMktCampaign.setEndTime(endTime);
                completeMktCampaign.setDetaileTacheList(detaileTacheList.toString());
                completeMktCampaign.setRegionCode("8330000");
                completeMktCampaign.setMktCampaigns(openMktCampaignEntityList);
                break;
            }
        }

        //设置集团营服活动反馈接口入参
        completeMktCampaignJtReq.setCompleteMktCampaign(completeMktCampaign);
        inputMap.put("requestObject",completeMktCampaignJtReq);
        String jsonString = JSON.toJSONString(inputMap);
        logger.info("集团活动反馈接口入参：" + jsonString);

        //调用集团营服活动反馈接口地址
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("OPENAPI_URL");
        String url = sysParamsList.get(0).getParamValue();
        HttpPost ht = new HttpPost(url);
        ht.setHeader("Content-Type", "application/json");
        String uuid = UUIDUtil.getUUID();
        ht.setHeader("X-CTG-Request-ID", uuid);
        logger.info("集团活动反馈接口流水号：" + uuid);
        List<SysParams> sysParamList = sysParamsMapper.selectAll(null, "OPENAPI_HEADER");
        JSONObject json = JSON.parseObject(sysParamList.get(0).getParamValue());
        String appId = json.get("X-APP-ID").toString();
        String appKey = json.get("X-APP-KEY").toString();
        String reginId = json.get("X-CTG-Region-ID").toString();
        ht.setHeader("X-CTG-Region-ID", reginId);
        ht.setHeader("X-APP-ID", appId);
        ht.setHeader("X-APP-KEY", appKey);
        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        logger.info(entity.toString());
        ht.setEntity(entity);
        String content = "";
        try {
            HttpResponse response = httpClient.execute(ht);
            content = EntityUtils.toString(response.getEntity());
            logger.info("集团返回结果：" + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultMap.put("message", "营服活动信息反馈完成");
        resultMap.put("inputMap", jsonString);
        return resultMap;
    }

    /**
     *
     * @param mktCampaignId
     * @param tacheCd
     * @param tacheValueCd
     * @return
     */
    @Override
    public Map<String, Object> completeMktCampaign(Long mktCampaignId, String tacheCd, String tacheValueCd) {
        MktCampaignComplete mktCampaignComplete = new MktCampaignComplete();
        mktCampaignComplete.setMktCampaignId(mktCampaignId);
        mktCampaignComplete.setTacheCd(tacheCd);
        mktCampaignComplete.setTacheValueCd(tacheValueCd);
        if ("1100".equals(tacheCd)) {
            mktCampaignCompleteMapper.update(mktCampaignComplete);
        } else {
            mktCampaignCompleteMapper.insert(mktCampaignComplete);
        }
        Map<String, Object> map = completeMktCampaign(mktCampaignId, tacheCd);
        return map;
    }

}
