package com.zjtelcom.cpct.service.impl.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.ctzj.smt.bss.sysmgr.privilege.service.dubbo.api.ISystemUserDtoDubboService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.channel.OfferDetail;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.CloseRuleAddVO;
import com.zjtelcom.cpct.dto.filter.CloseRuleVO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.PostEnum;
import com.zjtelcom.cpct.request.filter.CloseRuleReq;
import com.zjtelcom.cpct.service.filter.CloseRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prod.dao.offer.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@EnableCaching(proxyTargetClass = true)
@Service
@Transactional
@Slf4j
public class CloseRuleServiceImpl implements CloseRuleService {

    @Autowired
    CloseRuleMapper closeRuleMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisUtils_es redisUtils_es;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private TarGrpMapper TarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired(required = false)
    private ISystemUserDtoDubboService iSystemUserDtoDubboService;

    /**
     * 根据关单规则id集合查询过滤规则集合
     */
    @Override
    public Map<String, Object> getFilterRule(List<Integer> closeRuleIdList) {
        Map<String, Object> map = new HashMap<>();
        List<CloseRule> closeRuleList = new ArrayList<>();
        for (Integer closeRuleId : closeRuleIdList) {
            CloseRule closeRule = closeRuleMapper.selectByPrimaryKey(closeRuleId.longValue());
            closeRuleList.add(closeRule);
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRuleList", closeRuleList);
        return map;
    }

    /**
     * 关单规则列表（含分页）
     */
    @Override
    public Map<String, Object> qryFilterRule(CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        Page pageInfo = closeRuleReq.getPageInfo();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<CloseRule> closeRules = closeRuleMapper.qryFilterRule(closeRuleReq.getCloseRule());
        Page page = new Page(new PageInfo(closeRules));
        List<CloseRuleVO> voList = new ArrayList<>();
        for (CloseRule rule : closeRules){
            CloseRuleVO vo = BeanUtil.create(rule,new CloseRuleVO());
            SysParams sysParams = sysParamsMapper.findParamsByValue("CAM-C-999",rule.getCloseType());
            if (sysParams!=null){
                vo.setFilterTypeName(sysParams.getParamName());
            }
            voList.add(vo);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", voList);
        maps.put("pageInfo",page);
        return maps;
    }

    /**
     * 关单规则列表（不含分页）
     */
    @Override
    public Map<String, Object> qryFilterRules(CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        List<CloseRule> closeRules = closeRuleMapper.qryFilterRule(closeRuleReq.getCloseRule());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", closeRules);
        return maps;
    }

    /**
     * 删除关单规则
     */
    @Override
    public Map<String, Object> delFilterRule(CloseRule closeRule) {
        Map<String, Object> maps = new HashMap<>();
        CloseRule rule = closeRuleMapper.selectByPrimaryKey(closeRule.getRuleId());
        if(rule != null) {
            closeRuleMapper.delFilterRule(closeRule);
            verbalConditionMapper.deleteByPrimaryKey(rule.getConditionId());
            if (StringUtils.isNotBlank(rule.getLabelCode())){
                TarGrpMapper.deleteByPrimaryKey(Long.valueOf(rule.getLabelCode()).longValue());
                tarGrpConditionMapper.deleteByTarGrpTemplateId(Long.valueOf(rule.getLabelCode()).longValue());
            }

        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    /**
     * 新建关单规则
     *  "closeCode": 0 成功 1 失败
        " productType": 产品类型,
     expression 关单编码自动生成 关单类型 CR001: 判断类型 000001 自然数自增
     */
    @Override
    public Map<String, Object> createFilterRule(CloseRuleAddVO addVO) {
        //受理关单规则 欠费关单规则  拆机关单规则
        Map<String, Object> maps = new HashMap<>();
        final CloseRule closeRule = BeanUtil.create(addVO,new CloseRule());
        //关单规则名称 去除重复
        String closeName = closeRule.getCloseName();
        Integer count = closeRuleMapper.getCloseNameCount(closeName);
        if (count>0){
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "关单规则名称重复！");
            return maps;
        }

        //添加所属地市
        String createChannel = PostEnum.ADMIN.getPostCode();
        if (UserUtil.getUser() != null) {
            // 获取当前用户的岗位编码包含“cpcpch”
            SystemUserDto userDetail = UserUtil.getRoleCode();
            for (SystemPostDto role : userDetail.getSystemPostDtoList()) {
                // 判断是否为超级管理员
                if (role.getSysPostCode().contains(PostEnum.ADMIN.getPostCode())) {
                    createChannel = role.getSysPostCode();
                    break;
                } else if (role.getSysPostCode().contains("cpcpch")) {
                    createChannel = role.getSysPostCode();
                    continue;
                }
            }
        }
        String sysPostCode =  AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
        if (createChannel.equals(AreaCodeEnum.sysAreaCode.SHENGJI.getSysPostCode())) {
            sysPostCode = AreaCodeEnum.sysAreaCode.SHENGJI.getSysArea();
        } else if (createChannel.equals(AreaCodeEnum.sysAreaCode.FENGONGSI.getSysPostCode())) {
            sysPostCode = AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea();
        } else if (createChannel.equals(AreaCodeEnum.sysAreaCode.FENGJU.getSysPostCode())) {
            sysPostCode = AreaCodeEnum.sysAreaCode.FENGJU.getSysArea();
        } else if (createChannel.equals(AreaCodeEnum.sysAreaCode.ZHIJU.getSysPostCode())) {
            sysPostCode = AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea();
        }
        closeRule.setRegionFlg(sysPostCode);
        closeRule.setCreateDate(DateUtil.getCurrentTime());
        closeRule.setUpdateDate(DateUtil.getCurrentTime());
        closeRule.setStatusDate(DateUtil.getCurrentTime());
        closeRule.setUpdateStaff(UserUtil.loginId());
        closeRule.setCreateStaff(UserUtil.loginId());
        closeRule.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        System.out.println("closeRule --->>>" + JSON.toJSONString(closeRule));
        List<String> codeList = new ArrayList<>();

        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("2000")){
            if (addVO.getChooseProduct()!= null && !addVO.getChooseProduct().isEmpty()){
                if (addVO.getProductType().equals("1000")){
                    for (Long offerId : addVO.getChooseProduct()){
                    Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
                    if (offer==null){
                        continue;
                    }
                    codeList.add(offer.getOfferNbr());
                    }
                }else {
                    for (Long offerId : addVO.getChooseProduct()){
                        Product product = productMapper.selectByPrimaryKey(offerId);
                        if (product==null){
                            continue;
                        }
                        codeList.add(product.getProdNbr());

                    }
                }
                closeRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
            }
        }
        if (StringUtils.isNotBlank(addVO.getOfferInfo()) && !addVO.getOfferInfo().equals("2000")){
            addVO.setOfferInfo("");
        }
        if (StringUtils.isNotBlank(addVO.getProductType()) && !addVO.getProductType().equals("2000")){
            addVO.setProductType("");
        }
        closeRuleMapper.createFilterRule(closeRule);
        // 类型为标签关单时
        if(addVO.getCloseType().equals("5000")){
            Long ruleId = closeRule.getRuleId();
            String express = saveExpressions2Redis(Long.valueOf(ruleId), Long.valueOf(closeRule.getLabelCode()));
            if(ruleId > 0){
                closeRuleMapper.updateLabelCodeByPrimaryKey(ruleId, express);
            }
        }
        if (StringUtils.isNotBlank(closeRule.getLabelCode())){
            String labelCode = closeRule.getLabelCode();
            TarGrp tarGrp = TarGrpMapper.selectByPrimaryKey(Long.valueOf(labelCode).longValue());
            if (tarGrp!=null){
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(tarGrp));
                //赋值以后转为对象 再去更新
                TarGrpDetail change = JSONObject.parseObject(json.toJSONString(), TarGrpDetail.class);
                change.setTarGrpName(closeRule.getCloseName());
                change.setTarGrpType("4000");
                int i = TarGrpMapper.updateByPrimaryKey(change);
            }
        }
        //关单编码确认类型 自然数自增
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("1000")){
            addVO.setExpression("CR001");
        }
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("3000")){
            addVO.setExpression("CR003");
        }
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("4000")){
            addVO.setExpression("CR004");
        }
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("2000")){
            addVO.setExpression("CR002");
        }
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("5000")){
            addVO.setExpression("CR005");
        }
        //自动步枪6位数 前面补零
        String expression = CpcUtil.addZeroForNum(String.valueOf(closeRule.getRuleId()), 6);
        closeRuleMapper.updateExpression(closeRule.getRuleId().toString(),addVO.getExpression()+expression);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", closeRule);
        return maps;
    }

    /**
     * 修改过滤规则
     */
    @Override
    public Map<String, Object> modFilterRule(CloseRuleAddVO editVO) {
        Map<String, Object> maps = new HashMap<>();
        final CloseRule closeRule = closeRuleMapper.selectByPrimaryKey(editVO.getRuleId());
        if (closeRule==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", StringUtils.EMPTY);
            return maps;
        }
        //过滤名称重复
        String closeName = editVO.getCloseName();
        Integer count = closeRuleMapper.getCloseNameCount(closeName);
        if (count>1){
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "修改过滤规则 关单规则名称重复！");
            return maps;
        }
        BeanUtil.copy(editVO,closeRule);
        closeRule.setUpdateDate(DateUtil.getCurrentTime());
        closeRule.setUpdateStaff(UserUtil.loginId());

        List<String> codeList = new ArrayList<>();
        if (StringUtils.isNotBlank(editVO.getCloseType()) && editVO.getCloseType().equals("2000")){
            if (editVO.getChooseProduct()!= null && !editVO.getChooseProduct().isEmpty()){
                if (editVO.getProductType().equals("1000")){
                    for (Long offerId : editVO.getChooseProduct()){
                        Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
                        if (offer==null){
                            continue;
                        }
                        codeList.add(offer.getOfferNbr());
                    }
                }else {
                    for (Long offerId : editVO.getChooseProduct()){
                        Product product = productMapper.selectByPrimaryKey(offerId);
                        if (product==null){
                            continue;
                        }
                        codeList.add(product.getProdNbr());

                    }
                }
                closeRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
            }
        }
        if(editVO.getCloseType().equals("5000")){
            String express = saveExpressions2Redis(closeRule.getRuleId(), Long.valueOf(closeRule.getLabelCode()));
            closeRule.setNoteFive(express);
        }
        closeRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
        closeRuleMapper.updateByPrimaryKey(closeRule);
        if (StringUtils.isNotBlank(closeRule.getLabelCode())){
            String labelCode = closeRule.getLabelCode();
            TarGrp tarGrp = TarGrpMapper.selectByPrimaryKey(Long.valueOf(labelCode).longValue());
            if (tarGrp!=null && !tarGrp.getTarGrpType().equals("4000")){
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(tarGrp));
                //赋值以后转为对象 再去更新
                TarGrpDetail change = JSONObject.parseObject(json.toJSONString(), TarGrpDetail.class);
                change.setTarGrpName(closeRule.getCloseName());
                change.setTarGrpType("4000");
                TarGrpMapper.updateByPrimaryKey(change);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", closeRule);

        return maps;
    }

    /**
     * 根据过滤规则id集合查询过滤规则集合
     */
    @Override
    public Map<String, Object> getFilterRule(Long ruleId) {
        Map<String, Object> map = new HashMap<>();
        CloseRule closeRuleT = closeRuleMapper.selectByPrimaryKey(ruleId);
        if(null == closeRuleT) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", "过滤规则不存在");
        }
        CloseRuleVO vo = BeanUtil.create(closeRuleT,new CloseRuleVO());
        if (closeRuleT.getChooseProduct()!=null && !closeRuleT.getChooseProduct().equals("")){
            List<String> codeList = ChannelUtil.StringToList(closeRuleT.getChooseProduct());
            List<OfferDetail> productList = new ArrayList<>();
            if (closeRuleT.getProductType().equals("1000")){
                for (String code : codeList){
                    List<Offer> offer = offerMapper.selectByCode(code);
                    if (offer!=null && !offer.isEmpty()){
                        OfferDetail offerDetail = BeanUtil.create(offer.get(0),new OfferDetail());
                        productList.add(offerDetail);
                    }
                }
            }else {
                for (String code : codeList){
                    List<Product> product = productMapper.selectByCode(code);
                    if (product!=null && !product.isEmpty()){
                        OfferDetail offerDetail = new OfferDetail();
                        offerDetail.setOfferId(Integer.valueOf(product.get(0).getProdId().toString()));
                        offerDetail.setOfferName(product.get(0).getProdName());
                        productList.add(offerDetail);
                    }
                }
            }

            vo.setProductList(productList);
        }
        if (closeRuleT.getConditionId()!=null){
            MktVerbalCondition condition = verbalConditionMapper.selectByPrimaryKey(closeRuleT.getConditionId());
            if (condition!=null){
                Label label = labelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                if (label!=null){
                    vo.setLabelId(label.getInjectionLabelId());
                    vo.setConditionName(label.getInjectionLabelName());
                    vo.setOperType(condition.getOperType());
                    vo.setRightParam(condition.getRightParam());
                }
            }
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRule", vo);
        return map;
    }

    // 创建、修改关单规则时，拼接表达式存入缓存
    public String saveExpressions2Redis(Long ruleId, Long tarGrpId){
        List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);

        List<LabelResult> labelResultList = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        StringBuilder express = new StringBuilder();
        if (tarGrpId != null && tarGrpId != 0) {
            //将规则拼装为表达式
            if (conditionList != null && conditionList.size() > 0) {
                express.append("if(");
                //遍历所有规则
                for (int i = 0; i < conditionList.size(); i++) {
                    LabelResult labelResult = new LabelResult();
                    String type = conditionList.get(i).getOperType();
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(conditionList.get(i).getLeftParam()));
                    if (label==null){
                        continue;
                    }
                    labelResult.setLabelCode(label.getInjectionLabelCode());
                    labelResult.setLabelName(label.getInjectionLabelName());
                    labelResult.setRightOperand(label.getLabelType());
                    labelResult.setRightParam(conditionList.get(i).getRightParam());
                    labelResult.setClassName(label.getClassName());
                    labelResult.setOperType(type);
                    labelResult.setLabelDataType(label.getLabelDataType()==null ? "1100" : label.getLabelDataType());
                    labelResultList.add(labelResult);
                    codeList.add(label.getInjectionLabelCode());
                    if ("7100".equals(type)) {
                        express.append("!");
                    }
                    express.append("(");
                    express.append(label.getInjectionLabelCode());
                    if ("1000".equals(type)) {
                        express.append(">");
                    } else if ("2000".equals(type)) {
                        express.append("<");
                    } else if ("3000".equals(type)) {
                        express.append("==");
                    } else if ("4000".equals(type)) {
                        express.append("!=");
                    } else if ("5000".equals(type)) {
                        express.append(">=");
                    } else if ("6000".equals(type)) {
                        express.append("<=");
                    } else if ("7000".equals(type)) {
                        express.append("in");
                    }else if ("7200".equals(type)) {
                        express.append("@@@@");//区间于
                    } else if ("7100".equals(type)) {
                        express.append("notIn");
                    }
                    express.append(conditionList.get(i).getRightParam());
                    express.append(")");
                    if (i + 1 != conditionList.size()) {
                        express.append("&&");
                    }
                }
                express.append(") {return true} else {return false}");
            }else {
                express.append("");
            }
        }
        redisUtils_es.hset("CLOSE_RULE_EXPRESS_" + ruleId,"express", express.toString());
        redisUtils_es.hset("CLOSE_RULE_EXPRESS_" + ruleId,"labelResultList", JSON.toJSONString(labelResultList));
        return express.toString();
    }

    /**
     * 导入销售品
     */
    @Override
    public Map<String,Object> importProductList(MultipartFile multipartFile, Long ruleId, String closeName, String closeType, String offerInfo, String productType, String closeCode, Long[] rightListId)throws IOException {
        Map<String,Object> maps = new HashMap<>();
        if(closeName.equals("") || closeType.equals("") || offerInfo.equals("") || closeCode.equals("")) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "关单规则信息不完善");
        }
        List<String> resultList = new ArrayList<>();
        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        Sheet sheet = wb.getSheetAt(0);
        int total = sheet.getLastRowNum() + rightListId.length;
        if(total > 300) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "销售品数量超过上限300个");
            return maps;
        }
        for(int i=0;i<rightListId.length;i++) {
            Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(rightListId[i].toString()));
            if (offer==null){
                continue;
            }
            resultList.add(offer.getOfferNbr());
        }
        Integer rowNums = sheet.getLastRowNum() + 1;
        List<String> errorOffer = new ArrayList<>();
        for (int i = 1; i < rowNums; i++) {
            Row row = sheet.getRow(i);
            if (row.getLastCellNum() >= 2) {
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg", "请返回检查模板格式");
                return maps;
            }
            Cell cell = row.getCell(0);
            String cellValue = ChannelUtil.getCellValue(cell).toString();
            if (!cellValue.equals("null")){
                List<Offer> offer = offerMapper.selectByCode(cellValue);
                if (offer!=null && !offer.isEmpty()) {
                    if(!resultList.contains(cellValue)) {
                        resultList.add(cellValue);
                    }
                }else {
                    errorOffer.add(cellValue);
                }
            }
        }
        if(errorOffer.size() > 0) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "失败原因：以下" + errorOffer.size() + "个销售品编码错误！" + "\n" + errorOffer);
            return maps;
        }
        CloseRule closeRule = new CloseRule();
        if(ruleId == null) {
            //新增 出现同名的情况
            Integer count = closeRuleMapper.getCloseNameCount(closeName);
            if (count>=1){
                maps.put("resultCode", CommonConstant.CODE_FAIL);
                maps.put("resultMsg", "导入销售品！关单规则名称重复！");
                return maps;
            }
            closeRule.setCloseName(closeName);
            closeRule.setCloseType(closeType);
            closeRule.setOfferInfo(offerInfo);
            closeRule.setProductType(productType);
            closeRule.setCloseCode(closeCode);
            closeRule.setChooseProduct(ChannelUtil.StringList2String(resultList));
            closeRule.setCreateDate(DateUtil.getCurrentTime());
            closeRule.setCreateStaff(UserUtil.loginId());
            closeRule.setUpdateDate(DateUtil.getCurrentTime());
            closeRule.setUpdateStaff(UserUtil.loginId());
            closeRule.setStatusDate(DateUtil.getCurrentTime());
            closeRule.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            closeRuleMapper.createFilterRule(closeRule);
        }else {
            Integer count = closeRuleMapper.getCloseNameCount(closeName);
            if (count>1){
                maps.put("resultCode", CommonConstant.CODE_FAIL);
                maps.put("resultMsg", "导入销售品！关单规则名称重复！");
                return maps;
            }
            closeRule = closeRuleMapper.selectByPrimaryKey(ruleId);
            if (closeRule==null){
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg","关单规则不存在");
                return maps;
            }
            closeRule.setCloseName(closeName);
            closeRule.setCloseType(closeType);
            closeRule.setOfferInfo(offerInfo);
            closeRule.setProductType(productType);
            closeRule.setCloseCode(closeCode);
            closeRule.setChooseProduct(ChannelUtil.StringList2String(resultList));
            closeRule.setUpdateDate(DateUtil.getCurrentTime());
            closeRule.setUpdateStaff(UserUtil.loginId());
            closeRuleMapper.updateByPrimaryKey(closeRule);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", "导入成功，文件导入" + sheet.getLastRowNum() + "个，共计" + total + "个");
        return maps;
    }

    @Override
    public Map<String, Object> qryCloseRuleForUser(CloseRuleReq closeRuleReq) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> maps = new HashMap<>();
        //获取用户信息
        SystemUserDto user = BssSessionHelp.getSystemUserDto();
        Long sysUserId = user.getSysUserId();
//        Long sysUserId = 1000033L;
        if (sysUserId == null){
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "sysUserId为空 无创建人信息");
            maps.put("closeRules", new ArrayList<Object>());
            return maps;
        }
        //过滤参数设置
        map.put("staffId",sysUserId);
        if (StringUtils.isNotBlank(closeRuleReq.getCloseRule().getCloseName())){
            map.put("closeName",closeRuleReq.getCloseRule().getCloseName());
        }
        if (StringUtils.isNotBlank(closeRuleReq.getCloseRule().getCloseType())){
            map.put("closeType",closeRuleReq.getCloseRule().getCloseType());
        }
        if (StringUtils.isNotBlank(closeRuleReq.getCloseRule().getRegionFlg())){
            map.put("regionFlg",closeRuleReq.getCloseRule().getRegionFlg());
        }
        //分页参数设置
        Page pageInfo = closeRuleReq.getPageInfo();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<CloseRule> closeRules = closeRuleMapper.qryCloseRuleForUser(map);
        Page page = new Page(new PageInfo(closeRules));

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("closeRules", closeRules);
        maps.put("pageInfo",page);
        return maps;
    }

    @Override
    public Map<String, Object> getCloseRuleOut(CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        Page pageInfo = closeRuleReq.getPageInfo();
        if (StringUtils.isNotBlank(closeRuleReq.getCloseRule().getCloseType())
                && (closeRuleReq.getCloseRule().getCloseType().equals("1000")
                || closeRuleReq.getCloseRule().getCloseType().equals("3000"))){
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", " 不接受~~欠费关单规则和拆机关单规则");
            maps.put("closeRules", new ArrayList<Object>());
            return maps;
        }
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        CloseRule closeRule = closeRuleReq.getCloseRule();
        if("C1".equals(closeRule.getRegionFlg()) || "C2".equals(closeRule.getRegionFlg())){
            closeRule.setRegionFlg("('C1', 'C2')");
        } else if("C3".equals(closeRule.getRegionFlg())){
            closeRule.setRegionFlg("('C3')");
        } else if("C4".equals(closeRule.getRegionFlg())){
            closeRule.setRegionFlg("('C4')");
        } else if("C5".equals(closeRule.getRegionFlg())){
            closeRule.setRegionFlg("('C5')");
        }
        List<CloseRule> closeRules = closeRuleMapper.getCloseRuleOut(closeRule);
        Page page = new Page(new PageInfo(closeRules));
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("closeRules", closeRules);
        maps.put("pageInfo",page);
        return maps;
    }

    @Override
    public Map<String,Object> addRegionFlg() {
        Map<String, Object> maps = new HashMap<>();
        List<CloseRule> closeRuleList = closeRuleMapper.qryFilterRule(new CloseRule());
        for (CloseRule closeRule : closeRuleList) {
            List<String> arrayList = new ArrayList<>();
            SysmgrResultObject<SystemUserDto> systemUserDtoSysmgrResultObject = iSystemUserDtoDubboService.qrySystemUserDto(closeRule.getCreateStaff(), new ArrayList<Long>());
            log.info("systemUserDtoSysmgrResultObject --->>>" + JSON.toJSONString(systemUserDtoSysmgrResultObject) );
            if (systemUserDtoSysmgrResultObject != null) {
                if (systemUserDtoSysmgrResultObject.getResultObject() != null) {
                    List<SystemPostDto> systemPostDtoList = systemUserDtoSysmgrResultObject.getResultObject().getSystemPostDtoList();
                    if (systemPostDtoList.size() > 0 && systemPostDtoList != null) {
                        for (SystemPostDto systemPostDto : systemPostDtoList) {
                            arrayList.add(systemPostDto.getSysPostCode());
                        }
                    }
                    String sysPostCode = "";
                    if (arrayList.contains(AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysPostCode())) {
                        sysPostCode = AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
                    } else if (arrayList.contains(AreaCodeEnum.sysAreaCode.SHENGJI.getSysPostCode())) {
                        sysPostCode = AreaCodeEnum.sysAreaCode.SHENGJI.getSysArea();
                    } else if (arrayList.contains(AreaCodeEnum.sysAreaCode.FENGONGSI.getSysPostCode())) {
                        sysPostCode = AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea();
                    } else if (arrayList.contains(AreaCodeEnum.sysAreaCode.FENGJU.getSysPostCode())) {
                        sysPostCode = AreaCodeEnum.sysAreaCode.FENGJU.getSysArea();
                    } else if (arrayList.contains(AreaCodeEnum.sysAreaCode.ZHIJU.getSysPostCode())) {
                        sysPostCode = AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea();
                    } else {
                        sysPostCode = AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
                    }
                    log.info("RuleId = " + closeRule.getRuleId() + ",   sysPostCode = " + sysPostCode );
                    closeRuleMapper.updateRegionFlg(closeRule.getRuleId(), sysPostCode);
                }
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", "成功");
        return maps;
    }
}
