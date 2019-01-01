/**
 * @(#)TarGrpTemplateServiceImpl.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.grouping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateMapper;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.enums.LeftParamType;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.enums.RightParamType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;
import com.zjtelcom.cpct.vo.grouping.TarGrpVO;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * Description:
 * author: linchao
 * date: 2018/09/06 16:15
 * version: V1.0
 */
@Service
@Transactional
public class TarGrpTemplateServiceImpl extends BaseService implements TarGrpTemplateService {

    @Autowired
    private TarGrpTemplateMapper tarGrpTemplateMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private OfferRestrictMapper offerRestrictMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private SynTarGrpTemplateService synTarGrpTemplateService;
    @Autowired
    private TarGrpService tarGrpService;
    @Autowired
    private OfferResRelMapper offerResRelMapper;
    @Autowired
    private MktResourceMapper resourceMapper;
    @Autowired
    private GrpSystemRelMapper grpSystemRelMapper;
    @Autowired
    private RequestInstRelMapper requestInstRelMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private ProductService productService;
    @Value("${sync.value}")
    private String value;

    /**
     * 需求涵id 获取分类对象
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getTarGrpTemByOfferId(Long requestId) {
        Map<String, Object> result = new HashMap<>();
        List<CampaignInstVO> instVOS = new ArrayList<>();
        List<ProductParam> paramList = new ArrayList<>();
        //todo 通过需求涵id获取销售品idList
        List<RequestInstRel> requestInstRels = requestInstRelMapper.selectByRequestId(requestId,"offer");
        ProductParam offerParam = new ProductParam();
        List<Long> offerIds = new ArrayList<>();
        offerParam.setItemType("1000");
        offerParam.setStatusCd("2000");

        ProductParam resourceParam = new ProductParam();
        List<Long> resList = new ArrayList<>();
        resourceParam.setItemType("3000");
        resourceParam.setStatusCd("2000");
        for (RequestInstRel requestInstRel : requestInstRels){
            Long offerId = requestInstRel.getRequestObjId();
            Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
            if (offer==null){
                continue;
            }
            offerIds.add(offerId);
            offerParam.setIdList(offerIds);
            List<Long> offerList = new ArrayList<>();
            offerList.add(offerId);
            CampaignInstVO instVO = new CampaignInstVO();
            instVO.setOfferName(offer.getOfferName());
            instVO.setOfferList(offerList);
            //客户分群列表
            List<OfferRestrict> restrict = offerRestrictMapper.selectByOfferId(offerId,"7000");
            if (restrict!=null && restrict.size()>0){
                List<TarGrpCondition> tarGrpConditions = new ArrayList<>();
                for (OfferRestrict offerRestrict : restrict){
                    if (offerRestrict.getRstrObjId()==null){
                        continue;
                    }
                    Long targrpId = offerRestrict.getRstrObjId();
                    TarGrp tarGrpTem = tarGrpMapper.selectByPrimaryKey(targrpId);
                    if (tarGrpTem==null){
                        continue;
                    }
                    List<TarGrpCondition> conditionDOList = tarGrpConditionMapper.listTarGrpCondition(targrpId);
                    tarGrpConditions.addAll(conditionDOList);
                }
                TarGrpDetail addVO = new TarGrpDetail();
                addVO.setTarGrpName("增存量模板导入转换模板");
                addVO.setTarGrpType("1000");
                addVO.setCreateDate(DateUtil.getCurrentTime());
                addVO.setUpdateDate(DateUtil.getCurrentTime());
                addVO.setStatusDate(DateUtil.getCurrentTime());
                addVO.setUpdateStaff(UserUtil.loginId());
                addVO.setCreateStaff(UserUtil.loginId());
                addVO.setRemark(null);
                addVO.setTarGrpConditions(tarGrpConditions);

                Map<String,Object> createTarGrp = tarGrpService.createTarGrp(addVO,false);
                if (createTarGrp.get("resultCode").equals(CODE_SUCCESS)){
                    TarGrp tarGrp = (TarGrp) createTarGrp.get("tarGrp");
                    instVO.setTarGrpTempleteId(tarGrp.getTarGrpId());
                }
            }
            //营销资源列表
            List<OfferResRel> offerResRel = offerResRelMapper.selectByOfferIdAndObjType(offerId,"1000");
            List<Long> resourceList = new ArrayList<>();
            for (OfferResRel resRel : offerResRel){
                MktResource resource = resourceMapper.selectByPrimaryKey(resRel.getObjId());
                if (resource!=null){
                    resourceList.add(resource.getMktResId());
                    resList.add(resource.getMktResId());
                    resourceParam.setIdList(resList);
                }
            }
            instVO.setResourceList(resourceList);
            //渠道列表
            List<OfferRestrict> channelRestrictList = offerRestrictMapper.selectByOfferId(offerId,"5000");
            List<ChannelDetail> channelList = new ArrayList<>();
            List<Long> channelIdList = new ArrayList<>();
            for (OfferRestrict channelRestrict : channelRestrictList ){
                GrpSystemRel grpSystemRel = grpSystemRelMapper.selectByOfferId(channelRestrict.getRstrObjId());
                if (grpSystemRel==null){
                    continue;
                }
                Channel channel = channelMapper.selectByPrimaryKey(grpSystemRel.getOfferVrulGrpId());
                if (channel!=null && !channelIdList.contains(channel.getContactChlId())){
                    ChannelDetail detail = new ChannelDetail();
                    detail.setChannelId(channel.getContactChlId());
                    detail.setChannelName(channel.getContactChlName());
                    detail.setChannelCode(channel.getContactChlCode());
                    channelList.add(detail);
                    channelIdList.add(channel.getContactChlId());
                }
            }
            instVO.setChannelList(channelList);
            instVOS.add(instVO);
        }
        List<Long> itemList = new ArrayList<>();
        if (offerParam.getIdList()!=null && !offerParam.getIdList().isEmpty()){
            Map<String,Object> offerMap = productService.addProductRule(offerParam);
            if (offerMap.get("resultCode").equals(CODE_SUCCESS)){
                itemList.addAll((List<Long>)offerMap.get("resultMsg"));
            }
        }
        if (resourceParam.getIdList()!=null && !resourceParam.getIdList().isEmpty()){
            Map<String,Object> resMap = productService.addProductRule(resourceParam);
            if (resMap.get("resultCode").equals(CODE_SUCCESS)){
                itemList.addAll((List<Long>)resMap.get("resultMsg"));
            }
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",instVOS);
        result.put("itemList",itemList);
        return result;
    }

    /**
     * 新增目标分群模板
     *
     * @param tarGrpTemplateDetail
     * @return
     */
    @Override
    public Map<String, Object> saveTarGrpTemplate(TarGrpTemplateDetail tarGrpTemplateDetail) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        TarGrp tarGrpTemplateDO = BeanUtil.create(tarGrpTemplateDetail, new TarGrp());
        tarGrpTemplateDO.setTarGrpName(tarGrpTemplateDetail.getTarGrpTemplateName()==null ? "" : tarGrpTemplateDetail.getTarGrpTemplateName() );
        tarGrpTemplateDO.setTarGrpDesc(tarGrpTemplateDetail.getTarGrpTemplateDesc()==null ? "" : tarGrpTemplateDetail.getTarGrpTemplateDesc() );
        tarGrpTemplateDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        tarGrpTemplateDO.setStatusDate(new Date());
        tarGrpTemplateDO.setCreateStaff(UserUtil.loginId());
        tarGrpTemplateDO.setCreateDate(new Date());
        tarGrpTemplateDO.setUpdateStaff(UserUtil.loginId());
        tarGrpTemplateDO.setUpdateDate(new Date());
        //todo 待验证
        tarGrpTemplateDO.setRemark("0");

        // 新增目标分群模板
        tarGrpMapper.createTarGrp(tarGrpTemplateDO);
        final Long tarGrpTemplateId = tarGrpTemplateDO.getTarGrpId();
        // 新增目标分群模板条件
        if (tarGrpTemplateDetail.getTarGrpTemConditionVOList() != null && tarGrpTemplateDetail.getTarGrpTemConditionVOList().size() > 0) {
            for (TarGrpTemConditionVO tarGrpTemConditionVO : tarGrpTemplateDetail.getTarGrpTemConditionVOList()) {
                if (tarGrpTemConditionVO.getOperType() == null || tarGrpTemConditionVO.getOperType().equals("")) {
                    tarGrpTemplateMap.put("resultCode", CODE_FAIL);
                    tarGrpTemplateMap.put("resultMsg", "请选择下拉框运算类型");
                    return tarGrpTemplateMap;
                }
                TarGrpCondition tarGrpTemplateConditionDO = BeanUtil.create(tarGrpTemConditionVO, new TarGrpCondition());
                tarGrpTemplateConditionDO.setRootFlag(0L);
                tarGrpTemplateConditionDO.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                tarGrpTemplateConditionDO.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                tarGrpTemplateConditionDO.setTarGrpId(tarGrpTemplateId);
                tarGrpTemplateConditionDO.setCreateDate(new Date());
                tarGrpTemplateConditionDO.setUpdateDate(new Date());
                tarGrpTemplateConditionDO.setStatusDate(new Date());
                tarGrpTemplateConditionDO.setUpdateStaff(UserUtil.loginId());
                tarGrpTemplateConditionDO.setCreateStaff(UserUtil.loginId());
                tarGrpTemplateConditionDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                tarGrpConditionMapper.insert(tarGrpTemplateConditionDO);
            }
        }
        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synTarGrpTemplateService.synchronizeSingleTarGrp(tarGrpTemplateId,"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateId", tarGrpTemplateId);
        return tarGrpTemplateMap;
    }

    /**
     * 更新目标分群模板
     *
     * @param tarGrpTemplateDetail
     * @return
     */
    @Override
    public Map<String, Object> updateTarGrpTemplate(TarGrpTemplateDetail tarGrpTemplateDetail) {

        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        if (tarGrpTemplateDetail.getTarGrpType()!=null && tarGrpTemplateDetail.getTarGrpType().equals("2000")){
            tarGrpTemplateMap.put("resultCode", CODE_FAIL);
            tarGrpTemplateMap.put("resultMsg","销售品模板不支持修改");
            return tarGrpTemplateMap;
        }
        TarGrp tarGrpTemplateDO = BeanUtil.create(tarGrpTemplateDetail, new TarGrp());
        // 更新目标分群模板
        tarGrpTemplateDO.setTarGrpId(tarGrpTemplateDetail.getTarGrpTemplateId());
        tarGrpTemplateDO.setTarGrpName(tarGrpTemplateDetail.getTarGrpTemplateName()==null ? "" : tarGrpTemplateDetail.getTarGrpTemplateName() );
        tarGrpTemplateDO.setTarGrpDesc(tarGrpTemplateDetail.getTarGrpTemplateDesc()==null ? "" : tarGrpTemplateDetail.getTarGrpTemplateDesc() );
        tarGrpTemplateDO.setUpdateDate(new Date());
        tarGrpTemplateDO.setUpdateStaff(UserUtil.loginId());
        tarGrpMapper.modTarGrp(tarGrpTemplateDO);
        final Long tarGrpTemplateId = tarGrpTemplateDetail.getTarGrpTemplateId();
        // 获取原有的标签条件
        List<TarGrpCondition> tarGrpTemplateConditionDOList = tarGrpConditionMapper.listTarGrpCondition(tarGrpTemplateId);
        List<Long> conditionIdList = new ArrayList<>();
        List<TarGrpTemConditionVO> conditionVOList = tarGrpTemplateDetail.getTarGrpTemConditionVOList();
        List<Long> newIdList = new ArrayList<>();
        for (TarGrpTemConditionVO conditionVO : conditionVOList) {
            if (conditionVO.getConditionId() == 0) {
                continue;
            }
            newIdList.add(conditionVO.getConditionId());
        }
        for (TarGrpCondition condition : tarGrpTemplateConditionDOList){
            if (!newIdList.contains(condition.getConditionId())){
                conditionIdList.add(condition.getConditionId());
            }
        }
        //批量删除条件
        if (conditionIdList != null && conditionIdList.size() > 0) {
            tarGrpConditionMapper.deleteBatch(conditionIdList);
        }
        // 新增目标分群模板条件
        if (tarGrpTemplateDetail.getTarGrpTemConditionVOList() != null && tarGrpTemplateDetail.getTarGrpTemConditionVOList().size() > 0) {
            for (TarGrpTemConditionVO tarGrpTemConditionVO : tarGrpTemplateDetail.getTarGrpTemConditionVOList()) {
                if (tarGrpTemConditionVO.getOperType() == null || tarGrpTemConditionVO.getOperType().equals("")) {
                    tarGrpTemplateMap.put("resultCode", CODE_FAIL);
                    tarGrpTemplateMap.put("resultMsg", "请选择下拉框运算类型");
                    return tarGrpTemplateMap;
                }
                TarGrpCondition tarGrpTemplateConditionDO = BeanUtil.create(tarGrpTemConditionVO, new TarGrpCondition());
                if (tarGrpTemConditionVO.getConditionId() != null && tarGrpTemConditionVO.getConditionId() != 0) {
                    tarGrpTemplateConditionDO.setUpdateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setUpdateDate(new Date());
                    tarGrpConditionMapper.updateByPrimaryKey(tarGrpTemplateConditionDO);
                } else {
                    tarGrpTemplateConditionDO.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    tarGrpTemplateConditionDO.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    tarGrpTemplateConditionDO.setTarGrpId(tarGrpTemplateId);
                    tarGrpTemplateConditionDO.setCreateDate(new Date());
                    tarGrpTemplateConditionDO.setCreateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setStatusDate(new Date());
                    tarGrpTemplateConditionDO.setUpdateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setUpdateDate(new Date());
                    tarGrpTemplateConditionDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    tarGrpConditionMapper.insert(tarGrpTemplateConditionDO);
                }
            }
        }
        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synTarGrpTemplateService.synchronizeSingleTarGrp(tarGrpTemplateId,"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateId", tarGrpTemplateId);
        return tarGrpTemplateMap;
    }

    /**
     * 获取目标分群列表(分页)
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> listTarGrpTemplatePage(String tarGrpTemplateName,String tarGrpType, Integer page, Integer pageSize) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
//        List<Long> tarRelList = strategyConfRuleMapper.listTarGrpIdList();

        // 分页获取目标分群模板
        PageHelper.startPage(page, pageSize);
        List<TarGrp> tarGrpTemplateDOList = tarGrpMapper.selectByName(tarGrpTemplateName,tarGrpType,"0");
        Page pageInfo = new Page(new PageInfo(tarGrpTemplateDOList));
        List<TarGrpTemplateDetail> tarGrpTemplateDetailList = new ArrayList<>();
        for (TarGrp tarGrpTemplateDO : tarGrpTemplateDOList) {
            TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
            tarGrpTemplateDetail.setTarGrpTemplateId(tarGrpTemplateDO.getTarGrpId());
            tarGrpTemplateDetail.setTarGrpTemplateName(tarGrpTemplateDO.getTarGrpName()==null ? "" : tarGrpTemplateDO.getTarGrpName() );
            tarGrpTemplateDetail.setTarGrpTemplateDesc(tarGrpTemplateDO.getTarGrpDesc()==null ? "" : tarGrpTemplateDO.getTarGrpDesc() );
            if (tarGrpTemplateDO.getTarGrpType()!=null){
                tarGrpTemplateDetail.setTarGrpTypeName(tarGrpTemplateDO.getTarGrpType().equals("1000") ? "客户类型" : "销售品类型");
            }
            tarGrpTemplateDetailList.add(tarGrpTemplateDetail);
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetailList", tarGrpTemplateDetailList);
        tarGrpTemplateMap.put("pageInfo", pageInfo);
        return tarGrpTemplateMap;
    }

    /**
     * 获取目标分群列表
     *
     * @return
     */
    @Override
    public Map<String, Object> listTarGrpTemplateAll() {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        // 分页获取目标分群模板
        List<TarGrpTemplateDO> tarGrpTemplateDOList = tarGrpTemplateMapper.selectAll();
        List<TarGrpTemplateDetail> tarGrpTemplateDetailList = new ArrayList<>();
        for (TarGrpTemplateDO tarGrpTemplateDO : tarGrpTemplateDOList) {
            TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
            tarGrpTemplateDetailList.add(tarGrpTemplateDetail);
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetailList", tarGrpTemplateDetailList);
        return tarGrpTemplateMap;
    }


    /**
     * 获取目标分群以及条件详情
     *
     * @param tarGrpTemplateId
     * @return
     */
    @Override
    public Map<String, Object> getTarGrpTemplate(Long tarGrpTemplateId) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        // 获取目标分群模板的基本信息
        TarGrp tarGrpTemplateDO = tarGrpMapper.selectByPrimaryKey(tarGrpTemplateId);
        TarGrpTemplateDetail tarGrpTemplateDetail = BeanUtil.create(tarGrpTemplateDO, new TarGrpTemplateDetail());
        tarGrpTemplateDetail.setTarGrpTemplateId(tarGrpTemplateDO.getTarGrpId());
        tarGrpTemplateDetail.setTarGrpTemplateName(tarGrpTemplateDO.getTarGrpName()==null ? "" : tarGrpTemplateDO.getTarGrpName() );
        tarGrpTemplateDetail.setTarGrpTemplateDesc(tarGrpTemplateDO.getTarGrpDesc()==null ? "" : tarGrpTemplateDO.getTarGrpDesc() );
        // 获取目标分群模板的对应的条件
        List<TarGrpCondition> tarGrpTemplateConditionDOS = tarGrpConditionMapper.listTarGrpCondition(tarGrpTemplateId);
        List<TarGrpTemConditionVO> tarGrpTemConditionVOList = new ArrayList<>();
        for (TarGrpCondition tarGrpTemplateConditionDO : tarGrpTemplateConditionDOS) {
            //TarGrpTemConditionVO tarGrpTemplateCondition = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemConditionVO());
            TarGrpTemConditionVO tarGrpTemConditionVO = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemConditionVO());
            List<OperatorDetail> operatorList = new ArrayList<>();
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpTemConditionVO.getLeftParam()));
            if (label == null) {
                continue;
            }
            tarGrpTemConditionVO.setLeftParamName(label.getInjectionLabelName());
            //塞入领域
//            FitDomain fitDomain = null;
//            if (label.getFitDomain() != null) {
//                fitDomain = FitDomain.getFitDomain(Integer.parseInt(label.getFitDomain()));
//                tarGrpTemConditionVO.setFitDomainId(Long.valueOf(fitDomain.getValue()));
//                tarGrpTemConditionVO.setFitDomainName(fitDomain.getDescription());
//            }
            //将操作符转为中文
            if (tarGrpTemConditionVO.getOperType() != null && !tarGrpTemConditionVO.getOperType().equals("")) {
                Operator op = Operator.getOperator(Integer.parseInt(tarGrpTemConditionVO.getOperType()));
                tarGrpTemConditionVO.setOperTypeName(op.getDescription());
            }
            //todo 通过左参id
            String operators = label.getOperator();
            String[] operator = operators.split(",");
            if (operator.length > 1) {
                for (int i = 0; i < operator.length; i++) {
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[i]));
                    OperatorDetail operatorDetail = new OperatorDetail();
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            } else {
                if (operator.length == 1) {
                    OperatorDetail operatorDetail = new OperatorDetail();
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[0]));
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            }
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());
            List<LabelValueVO> valueList = ChannelUtil.valueList2VOList(labelValues);
            tarGrpTemConditionVO.setValueList(valueList);
            tarGrpTemConditionVO.setConditionType(label.getConditionType());
            tarGrpTemConditionVO.setOperatorList(operatorList);
            tarGrpTemConditionVOList.add(tarGrpTemConditionVO);
        }
        tarGrpTemplateDetail.setTarGrpTemConditionVOList(tarGrpTemConditionVOList);
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetail", tarGrpTemplateDetail);
        return tarGrpTemplateMap;
    }

    /**
     * 删除目标分群模板以及条件详情
     *
     * @param tarGrpTemplateId
     * @return
     */
    @Override
    public Map<String, Object> deleteTarGrpTemplate(final Long tarGrpTemplateId) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpTemplateId);
        if (tarGrp==null){
            tarGrpTemplateMap.put("resultCode", CODE_FAIL);
            tarGrpTemplateMap.put("resultMsg","模板不存在");
            return tarGrpTemplateMap;
        }
        if (tarGrp.getTarGrpType().equals("2000")){
            tarGrpTemplateMap.put("resultCode", CODE_FAIL);
            tarGrpTemplateMap.put("resultMsg","销售品模板不支持删除");
            return tarGrpTemplateMap;
        }
        tarGrpMapper.deleteByPrimaryKey(tarGrpTemplateId);
        tarGrpConditionMapper.deleteByTarGrpTemplateId(tarGrpTemplateId);

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synTarGrpTemplateService.deleteSingleTarGrp(tarGrpTemplateId,"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateId", tarGrpTemplateId);
        return tarGrpTemplateMap;
    }

    /**
     * 删除目标分群模板条件
     *
     * @param conditionId
     * @return
     */
    @Override
    public Map<String, Object> deleteTarGrpTemplateCondition(Long conditionId) {
        Map<String, Object> tarGrpTemplateMap = new HashMap<>();
        try {
            tarGrpConditionMapper.deleteByPrimaryKey(conditionId);
            tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            tarGrpTemplateMap.put("conditionId", conditionId);
        } catch (Exception e) {
            tarGrpTemplateMap.put("resultCode", CODE_FAIL);
            tarGrpTemplateMap.put("conditionId", conditionId);
            logger.error("[op:TarGrpTemplateServiceImpl] failed to delete TarGrpTemplateCondition by conditionId = {}, Expection=", conditionId, e);
        }
        return tarGrpTemplateMap;
    }
}