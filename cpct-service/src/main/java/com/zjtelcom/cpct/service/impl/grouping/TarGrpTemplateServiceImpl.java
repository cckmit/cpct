/**
 * @(#)TarGrpTemplateServiceImpl.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cpc.configure.service.api.offer.IOfferRestrictConfigureService;
import com.ctzj.smt.bss.cpc.evn.type.EvnType;
import com.ctzj.smt.bss.cpc.model.offer.atomic.OfferRestrict;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.IFuncCompDubboService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;
import com.zjtelcom.cpct.vo.grouping.TarGrpVO;
import com.zjtelcom.cpct_offer.dao.inst.RequestInstRelMapper;
import com.zjtelcom.cpct_prod.dao.offer.MktResourceProdMapper;
import com.zjtelcom.cpct_prod.dao.offer.OfferProdMapper;
import com.zjtelcom.es.es.entity.TrialOperationVOES;
import com.zjtelcom.es.es.entity.model.LabelResultES;
import com.zjtelcom.es.es.entity.model.TrialOperationParamES;
import com.zjtelcom.es.es.entity.model.TrialResponseES;
import com.zjtelcom.es.es.service.EsTarGrpTemplate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    private MktResourceProdMapper resourceMapper;
    @Autowired
    private GrpSystemRelMapper grpSystemRelMapper;
    @Autowired
    private RequestInstRelMapper requestInstRelMapper;
    @Autowired
    private OfferProdMapper offerMapper;
    @Autowired
    private ContactChannelMapper channelMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private LabelService labelService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MktCamCustMapper camCustMapper;
    @Autowired(required = false)
    private IOfferRestrictConfigureService iOfferRestrictConfigureService;
    @Autowired(required = false)
    private EsTarGrpTemplate esTarGrpTemplateService;
    @Autowired
    private SysParamsMapper systemParamMapper;


    /**
     * 分群模板导入清单
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> importUserList4TarTemp(MultipartFile multipartFile,String tempName)throws IOException{
        Map<String, Object> result = new HashMap<>();
        String batchNumSt = DateUtil.date2St4Trial(new Date()) + ChannelUtil.getRandomStr(4);

        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        Integer rowNums = sheet.getLastRowNum() + 1;
        List<Map<String,Object>> customerList = new ArrayList<>();
        List<LabelDTO>  labelDTOList = new ArrayList<>();
        List<Map<String,Object>> labelList = new ArrayList<>();
        Row labelRowFirst = sheet.getRow(0);
        Row labelRow = sheet.getRow(1);
        for (int j = 0; j < labelRow.getLastCellNum(); j++) {
            Cell cellTitle = labelRowFirst.getCell(j);
            Cell cell = labelRow.getCell(j);
            LabelDTO  labelDTO = new LabelDTO();
            labelDTO.setLabelCode((String) ChannelUtil.getCellValue(cell));
            labelDTO.setInjectionLabelName(cellTitle.getStringCellValue());
            labelDTOList.add(labelDTO);
        }
        for (int i = 3; i < rowNums ; i++) {
            Map<String, Object> customers = new HashMap<>();
            Row rowCode = sheet.getRow(1);
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cellTitle = rowCode.getCell(j);
                Cell cell = row.getCell(j);
                customers.put(cellTitle.getStringCellValue(), ChannelUtil.getCellValue(cell));
            }
            customerList.add(customers);
        }
        for (int i = 0 ; i< labelDTOList.size();i++){
            Map<String,Object> label = new HashMap<>();
            label.put("code",labelDTOList.get(i).getLabelCode());
            label.put("name",labelDTOList.get(i).getInjectionLabelName());
            labelList.add(label);
        }
        //
        TarGrp tarGrpTemplateDO = new TarGrp();
        //销售品类型模板
        tarGrpTemplateDO.setTarGrpType(TarTempType.PROM_IMPORT_TEMPLETE.getValue());
        tarGrpTemplateDO.setTarGrpName(tempName );
        tarGrpTemplateDO.setTarGrpDesc(tempName );
        tarGrpTemplateDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        tarGrpTemplateDO.setStatusDate(new Date());
        tarGrpTemplateDO.setCreateStaff(TarTempCreateType.NAORMAL_TEMP.getValue());
        tarGrpTemplateDO.setCreateDate(new Date());
        tarGrpTemplateDO.setUpdateStaff(UserUtil.loginId());
        tarGrpTemplateDO.setUpdateDate(new Date());
        //todo 待验证
        tarGrpTemplateDO.setRemark("0");
        // 新增目标分群模板
        tarGrpMapper.createTarGrp(tarGrpTemplateDO);

        Label label = injectionLabelMapper.selectByLabelCode("PPM_IMPROT_USER_LIST");
        if (label==null){
            LabelAddVO addVO = new LabelAddVO();
            addVO.setInjectionLabelName("PPM清单导入特殊标签");
            addVO.setConditionType(LabelCondition.INPUT.getValue().toString());
            addVO.setInjectionLabelCode("PPM_IMPROT_USER_LIST");
            addVO.setRightOperand("1/2");
            labelService.addLabel(1L,addVO);
            label = injectionLabelMapper.selectByLabelCode("PPM_IMPROT_USER_LIST");
        }
        TarGrpCondition condition = new TarGrpCondition();
        condition.setLeftParam(label.getInjectionLabelId().toString());
        condition.setOperType("3000");
        condition.setRightParam(tarGrpTemplateDO.getTarGrpId().toString());
        condition.setRootFlag(0L);
        condition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
        condition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
        condition.setTarGrpId(tarGrpTemplateDO.getTarGrpId());
        condition.setCreateDate(new Date());
        condition.setUpdateDate(new Date());
        condition.setStatusDate(new Date());
        condition.setUpdateStaff(0L);
        condition.setCreateStaff(UserUtil.loginId());
        condition.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        tarGrpConditionMapper.insert(condition);
        new Thread(){
            public void run(){
                addCamCust(customerList,labelList,tarGrpTemplateDO.getTarGrpId());
            }
        }.start();
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "导入成功,请稍后查看结果");
        return result;
    }

    @Override
    public Map<String, Object> tarGrpTemplateScheduledBatchIssue() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, String>> mapList = systemParamMapper.listParamsByKey("TARGRPTEMPLATE_SCHEDULED_BATCH");
        List<String> failTarGrpIdList = new ArrayList<>();
        Map<String, String> mapping = new HashMap<>();
        if (mapList != null ) {
            String[] tarGrpIds = mapList.get(0).get("value").split(",");
            for (String tarGrpId : tarGrpIds) {
                try {
                    Map<String, Object> map = tarGrpTemplateCountAndIssue(tarGrpId, "2");
                    mapping.put(tarGrpId, map.get("resultData") == null ? "" : map.get("resultData").toString());
                }catch (Exception e) {
                    failTarGrpIdList.add(tarGrpId);
                    logger.error("tarGrpTemplateScheduledBatchIssue=>分群定时批量下发报错" + tarGrpId);
                    e.printStackTrace();
                }
            }
        }
        if (failTarGrpIdList.isEmpty()) {
            resultMap.put("result", "succees");
            resultMap.put("mapping", mapping);
            return resultMap;
        }else {
            resultMap.put("result", "error");
            resultMap.put("mapping", mapping);
            resultMap.put("failTarGrpIdList", failTarGrpIdList);
            return resultMap;
        }
    }

    @Override
    public Map<String, Object> tarGrpTemplateCountAndIssue(String tarGrpTemplateId, String operationType) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> list = tarGrpConditionMapper.selectAllLabelByTarId(Long.valueOf(tarGrpTemplateId));
        List<String> expressions = new ArrayList<>();
        List<LabelResultES> labelList = new ArrayList<>();
        for (Map<String, String> tarGrpCondition : list) {
            String code = tarGrpCondition.get("code");
            String operType = tarGrpCondition.get("operType");
            operType = equationSymbolConversion(operType);
            String rightParam = tarGrpCondition.get("rightParam");
            String expression = code + operType + rightParam;
            expressions.add(expression);
            LabelResultES label = new LabelResultES();
            label.setLabelCode(code);
            label.setLabelDataType(tarGrpCondition.get("labelType") == null? "":tarGrpCondition.get("labelType"));
            labelList.add(label);
        }
        map.put("expressions", expressions);
        map.put("operationType", operationType);
        map.put("labelList", labelList);
        try {
            String result = esTarGrpTemplateService.tarGrpTemplateCountAndIssue(map);
            map.put("resultCode",CODE_SUCCESS);
            map.put("resultMsg","查询成功");
            map.put("resultData", result);
        }catch (Exception e){
            logger.error("esTarGrpTemplateService错误！");
            e.printStackTrace();
            map.put("resultCode",CODE_FAIL);
            map.put("resultMsg","查询失败");
        }
        return map;
    }

    public String equationSymbolConversion(String type){
        switch (type) {
            case "1000":
                return ">";
            case "2000":
                return "<";
            case "3000":
                return "==";
            case "4000":
                return "!=";
            case "5000":
                return ">=";
            case "6000":
                return "<=";
            case "7000":
                return "in";
            case "7100":
                return "notIn";
            case "7200":
                return "@@@@";
            default:
                return "";
        }
    }


    private void addCamCust( List<Map<String,Object>> customerList, List<Map<String,Object>>  labelList,Long tarTempId){
        List<MktCamCust> camCustList = new ArrayList<>();
        for (Map<String,Object> customer : customerList){
            if (customer.get("CCUST_ID")==null || customer.get("CCUST_ID").toString().equals("null")){
                continue;
            }
            MktCamCust camCust = new MktCamCust();
            camCust.setMktCampaignId(tarTempId);
            camCust.setTargetObjNbr(customer.get("CCUST_ID").toString());
            camCust.setTargetObjType("1000");
            camCust.setAttrValue(JSON.toJSONString(customer));
            camCust.setStatusCd("1000");
            camCust.setCreateDate(new Date());
            camCust.setRemark(JSON.toJSONString(labelList));
            camCustList.add(camCust);
        }
        if (!camCustList.isEmpty()){
            camCustMapper.insertByBatch(camCustList);
        }
    }

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
        offerParam.setStatusCd("1100");

        ProductParam resourceParam = new ProductParam();
        List<Long> resList = new ArrayList<>();
        resourceParam.setItemType("3000");
        resourceParam.setStatusCd("1100");
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
            List<OfferRestrictEntity> restrict = offerRestrictMapper.selectByOfferId(offerId,"7000");
            if (restrict!=null && restrict.size()>0){
                List<TarGrpCondition> tarGrpConditions = new ArrayList<>();
                boolean save = false;
                for (OfferRestrictEntity offerRestrict : restrict){
                    if (offerRestrict.getRstrObjId()==null){
                        continue;
                    }
                    Long targrpId = offerRestrict.getRstrObjId();
                    TarGrp tarGrpTem = tarGrpMapper.selectByPrimaryKey(targrpId);
                    if (tarGrpTem==null){
                        continue;
                    }
                    if (tarGrpTem.getTarGrpType().equals(TarTempType.PROM_IMPORT_TEMPLETE.getValue())){
                        save = true;
                    }
                    List<TarGrpCondition> conditionDOList = tarGrpConditionMapper.listTarGrpCondition(targrpId);
                    tarGrpConditions.addAll(conditionDOList);
                }
                TarGrpDetail addVO = new TarGrpDetail();
                addVO.setTarGrpName("增存量模板导入转换模板");
                addVO.setTarGrpType(save? TarTempType.PROM_IMPORT_TEMPLETE.getValue() : "1000" );
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
            List<OfferRestrictEntity> channelRestrictList = offerRestrictMapper.selectByOfferId(offerId,"5000");
            List<ChannelDetail> channelList = new ArrayList<>();
            List<Long> channelIdList = new ArrayList<>();
            for (OfferRestrictEntity channelRestrict : channelRestrictList ){
                GrpSystemRel grpSystemRel = grpSystemRelMapper.selectByOfferId(channelRestrict.getRstrObjId());
                if (grpSystemRel==null){
                    continue;
                }
                Long channelId = grpSystemRel.getOfferVrulGrpId();
                if (channelId == 3221281L){
                    channelId = 11L;
                }
                if (channelId == 3221280L){
                    channelId = 36L;
                }
                if (channelId == 3221279L){
                    channelId = 16L;
                }
                Channel channel = channelMapper.selectByPrimaryKey(channelId);
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
        tarGrpTemplateDO.setCreateStaff(TarTempCreateType.NAORMAL_TEMP.getValue());
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
                tarGrpTemplateConditionDO.setCreateStaff(UserUtil.loginId());
                tarGrpTemplateConditionDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                tarGrpConditionMapper.insert(tarGrpTemplateConditionDO);
            }
        }
        //销售品
        if(tarGrpTemplateDetail.getOfferId() != null) {
            OfferRestrict offerRestrict = new OfferRestrict();
            Long num = offerRestrictMapper.selectBatchNoNum();
            offerRestrict.setOfferRestrictId(num);
            offerRestrict.setOfferId(tarGrpTemplateDetail.getOfferId());
            offerRestrict.setRstrObjType("7000");
            offerRestrict.setRstrObjId(tarGrpTemplateId);
            offerRestrict.setApplyRegionId(8330000L);
            offerRestrict.setCreateDate(new Date());
            offerRestrict.setUpdateDate(new Date());
            offerRestrict.setStatusDate(new Date());
            offerRestrict.setUpdateStaff(UserUtil.loginId());
            offerRestrict.setCreateStaff(UserUtil.loginId());
            offerRestrict.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            iOfferRestrictConfigureService.saveOfferRestrict(offerRestrict, EvnType.EVO);
        }
        if (SystemParamsUtil.isSync()){
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
                    tarGrpTemplateConditionDO.setUpdateDate(new Date());
                    tarGrpConditionMapper.updateByPrimaryKey(tarGrpTemplateConditionDO);
                } else {
                    tarGrpTemplateConditionDO.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    tarGrpTemplateConditionDO.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    tarGrpTemplateConditionDO.setTarGrpId(tarGrpTemplateId);
                    tarGrpTemplateConditionDO.setCreateDate(new Date());
                    tarGrpTemplateConditionDO.setCreateStaff(UserUtil.loginId());
                    tarGrpTemplateConditionDO.setStatusDate(new Date());
                    tarGrpTemplateConditionDO.setUpdateDate(new Date());
                    tarGrpTemplateConditionDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    tarGrpConditionMapper.insert(tarGrpTemplateConditionDO);
                }
            }
        }
        if (SystemParamsUtil.isSync()){
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
        boolean  check = false;
        for (TarGrpCondition tarGrpTemplateConditionDO : tarGrpTemplateConditionDOS) {
            //TarGrpTemConditionVO tarGrpTemplateCondition = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemConditionVO());
            TarGrpTemConditionVO tarGrpTemConditionVO = BeanUtil.create(tarGrpTemplateConditionDO, new TarGrpTemConditionVO());
            List<OperatorDetail> operatorList = new ArrayList<>();
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpTemConditionVO.getLeftParam()));
            if (label == null) {
                continue;
            }
            if (label.getRightOperand()!=null && label.getRightOperand().equals("1")){
                check = true;
            }
            tarGrpTemConditionVO.setLeftParamName(label.getInjectionLabelName());
            tarGrpTemConditionVO.setLabelDataType(label.getLabelDataType());
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
            //枚举标签替换中文名
            if (valueList!=null && !valueList.isEmpty() ){
                List<String> rightParam = new ArrayList<>();
                String[] paramList = tarGrpTemConditionVO.getRightParam().split(",");
                for (String value : paramList){
                    for (LabelValueVO valueVO : valueList){
                        if (valueVO.getLabelValue().equals(value)){
                            rightParam.add(valueVO.getValueName());
                        }
                    }
                }
                tarGrpTemConditionVO.setRightParamName(ChannelUtil.list2String(rightParam,","));
            }else {
                tarGrpTemConditionVO.setRightParamName(tarGrpTemConditionVO.getRightParam());
            }
            tarGrpTemConditionVO.setValueList(valueList);
            tarGrpTemConditionVO.setConditionType(label.getConditionType());
            tarGrpTemConditionVO.setOperatorList(operatorList);
            tarGrpTemConditionVO.setLabelCode(label.getInjectionLabelCode());
            tarGrpTemConditionVOList.add(tarGrpTemConditionVO);
            if ("PROM_LIST".equals(label.getInjectionLabelCode()) && tarGrpTemConditionVO.getRightParam()!=null){
                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.valueOf(tarGrpTemConditionVO.getRightParam()));
                if (filterRule!=null){
                    tarGrpTemConditionVO.setPromListName(filterRule.getRuleName());
                }
            }
        }
        tarGrpTemplateDetail.setTarGrpTemConditionVOList(tarGrpTemConditionVOList);
        tarGrpTemplateMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        tarGrpTemplateMap.put("tarGrpTemplateDetail", tarGrpTemplateDetail);
        tarGrpTemplateMap.put("labelCheck",check);
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

        if (SystemParamsUtil.isSync()){
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