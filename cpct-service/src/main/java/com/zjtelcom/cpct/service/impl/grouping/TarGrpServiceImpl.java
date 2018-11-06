package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.model.policy.CalcReqModel;
import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;
import com.zjhcsoft.eagle.main.dubbo.service.PolicyCalculateService;
import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.constants.ResponseCode;
import com.zjtelcom.cpct.dao.campaign.MktCamGrpRulMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpTemplateMapper;
import com.zjtelcom.cpct.dao.org.OrgTreeMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateConditionDO;
import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import com.zjtelcom.cpct.domain.org.OrgTree;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.channel.LabelValueVO;
import com.zjtelcom.cpct.dto.channel.OperatorDetail;
import com.zjtelcom.cpct.dto.grouping.*;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.dto.system.SystemParam;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.model.EagleDatabaseConfig;
import com.zjtelcom.cpct.pojo.Company;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.EagleDatabaseConfCache;
import com.zjtelcom.cpct.service.TryCalcService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct.validator.ValidateResult;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;
import com.zjtelcom.cpct.vo.grouping.TarGrpVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description 目标分群serviceImpl
 * @Author pengy
 * @Date 2018/6/25 10:34
 */
@Service
@Transactional
public class TarGrpServiceImpl extends BaseService implements TarGrpService {

    @Autowired
    private TarGrpTemplateMapper tarGrpTemplateMapper;
    @Autowired
    private TarGrpTemplateConditionMapper tarGrpTemplateConditionMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TryCalcService tryCalcService;
    @Autowired(required = false)
    private PolicyCalculateService policyCalculateService;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;
    @Autowired
    private SysAreaMapper areaMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OrgTreeMapper orgTreeMapper;
    @Autowired
    private MktStrategyConfRuleMapper ruleMapper;


    /**
     * 复制客户分群 返回
     * @param tarGrpId
     * @return
     */
    @Override
    public Map<String, Object> copyTarGrp(Long tarGrpId,boolean isCopy) {
        Map<String,Object> result = new HashMap<>();
//        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
//        if (tarGrp==null){
//            result.put("resultCode", CODE_FAIL);
//            result.put("resultMsg", "请选择下拉框运算类型");
//            return result;
//        }
//        List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
//        TarGrpDetail detail = BeanUtil.create(tarGrp,new TarGrpDetail());
//        detail.setTarGrpConditions(conditionList);
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
     * 模板创建客户分群
     * @param templateId
     * @return‘
     */
    @Override
    public Map<String, Object> createTarGrpByTemplateId(Long templateId) {
        Map<String, Object> result = new HashMap<>();
        TarGrp template = tarGrpMapper.selectByPrimaryKey(templateId);
        if (template==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "模板不存在");
            return result;
        }
        List<TarGrpCondition> conditionDOList = tarGrpConditionMapper.listTarGrpCondition(templateId);

        TarGrpDetail addVO = BeanUtil.create(template,new TarGrpDetail());
        List<TarGrpCondition> conditionAdd = new ArrayList<>();
        for (TarGrpCondition conditionDO : conditionDOList){
            TarGrpCondition con = BeanUtil.create(conditionDO,new TarGrpCondition());
            conditionAdd.add(con);
        }
        addVO.setTarGrpConditions(conditionAdd);
        return createTarGrp(addVO,false);
    }


    /**
     * 新增目标分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createTarGrp(final TarGrpDetail tarGrpDetail, boolean isCopy) {
         TarGrp tarGrp = new TarGrp();
        Map<String, Object> maps = new HashMap<>();
        //插入客户分群记录
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
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrp);
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
     * 新增目标分群(暂时废弃)
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveTagNumFetch(Long mktCamGrpRulId, List<TarGrpCondition> tarGrpConditionDTOList) {
        Map<String, Object> maps = new HashMap<>();
        TarGrpDetail tarGrpDetail = new TarGrpDetail();
        try {
            //生成客户分群
            tarGrpDetail = new TarGrpDetail();
            tarGrpDetail.setStatusCd("1000");
            tarGrpMapper.insert(tarGrpDetail);
            //添加客户分群条件
            for (int i = 0; i < tarGrpConditionDTOList.size(); i++) {
//                TarGrpConditionDO tarGrpConditionDO = tarGrpConditionDTOList.get(i);
                TarGrpConditionDO tarGrpConditionDO = null;
                tarGrpConditionDO.setTarGrpId(tarGrpDetail.getTarGrpId());
//                tarGrpConditionMapper.insert(tarGrpCondition);
            }
            //更新营销活动分群规则表
            MktCamGrpRul mktCamGrpRul = new MktCamGrpRul();
            mktCamGrpRul.setMktCamGrpRulId(mktCamGrpRulId);
            mktCamGrpRul.setTarGrpId(tarGrpDetail.getTarGrpId());
            mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);

        } catch (Exception e) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", ErrorCode.SAVE_TAR_GRP_FAILURE.getErrorMsg());
            maps.put("tarGrp", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to saveTagNumFetch ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrpDetail);
        return maps;
    }

    /**
     * 删除目标分群条件
     */
    @Override
    public Map<String, Object> delTarGrpCondition(Long conditionId) {
        Map<String, Object> mapsT = new HashMap<>();


        try {
            TarGrpCondition condition = tarGrpConditionMapper.selectByPrimaryKey(conditionId);
            if (condition==null){
                mapsT.put("resultCode", CODE_FAIL);
                mapsT.put("resultMsg", ErrorCode.DEL_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
                return mapsT;
            }
            Long tarGrpId = condition.getTarGrpId();
            tarGrpConditionMapper.deleteByPrimaryKey(conditionId);
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
            if (tarGrp==null){
                mapsT.put("resultCode", CODE_FAIL);
                mapsT.put("resultMsg", "分群不存在");
                return mapsT;
            }

            List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            if (conditionList.isEmpty()){
                tarGrpMapper.deleteByPrimaryKey(tarGrpId);
            }
        } catch (Exception e) {
            mapsT.put("resultCode", CODE_FAIL);
            mapsT.put("resultMsg", ErrorCode.DEL_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            mapsT.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to delTarGrpCondition ", e);
            return mapsT;
        }
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        mapsT.put("resultObject", StringUtils.EMPTY);
        return mapsT;
    }

    /**
     * 编辑目标分群条件
     */
    @Override
    public Map<String, Object> editTarGrpConditionDO(Long conditionId) {
        Map<String, Object> maps = new HashMap<>();
        TarGrpCondition tarGrpCondition = new TarGrpCondition();
        tarGrpCondition = tarGrpConditionMapper.getTarGrpCondition(conditionId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrpCondition", tarGrpCondition);
        return maps;
    }


    /**
     * 修改目标分群
     */
    @Override
    public Map<String, Object> modTarGrp(TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        TarGrp tarGrp = new TarGrp();
        tarGrp = tarGrpDetail;
        tarGrp.setUpdateDate(DateUtil.getCurrentTime());
        tarGrp.setUpdateStaff(UserUtil.loginId());
        tarGrpMapper.modTarGrp(tarGrp);
        List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
        List<TarGrpCondition> insertConditions = new ArrayList<>();
        List<TarGrpCondition> allCondition = new ArrayList<>();
        for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
            TarGrpCondition tarGrpCondition1 = tarGrpConditionMapper.selectByPrimaryKey(tarGrpCondition.getConditionId());
            if (tarGrpCondition1 == null) {
                if (tarGrpCondition.getOperType()==null || tarGrpCondition.getOperType().equals("")){
                    maps.put("resultCode", CODE_FAIL);
                    maps.put("resultMsg", "请选择下拉框运算类型");
                    return maps;
                }
                TarGrpCondition condition = BeanUtil.create(tarGrpCondition,new TarGrpCondition());
                if (tarGrpCondition.getAreaIdList()!=null){
                    area2RedisThread(tarGrp, tarGrpCondition);
                }
                condition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                condition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                condition.setTarGrpId(tarGrp.getTarGrpId());
                condition.setUpdateDate(DateUtil.getCurrentTime());
                condition.setCreateDate(DateUtil.getCurrentTime());
                condition.setStatusDate(DateUtil.getCurrentTime());
                condition.setUpdateStaff(UserUtil.loginId());
                condition.setCreateStaff(UserUtil.loginId());
                insertConditions.add(condition);
            } else {
                tarGrpCondition.setUpdateDate(DateUtil.getCurrentTime());
                tarGrpCondition.setUpdateStaff(UserUtil.loginId());
                tarGrpConditionMapper.modTarGrpCondition(tarGrpCondition);
                allCondition.add(tarGrpCondition);
            }
        }
        if (!insertConditions.isEmpty()){
            tarGrpConditionMapper.insertByBatch(insertConditions);
        }
        allCondition.addAll(insertConditions);
        //更新redis分群数据
        TarGrpDetail detail = BeanUtil.create(tarGrp,new TarGrpDetail());
        detail.setTarGrpConditions(allCondition);
        redisUtils.set("TAR_GRP_"+tarGrp.getTarGrpId(),detail);

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除目标分群
     */
    @Override
    public Map<String, Object> delTarGrp(TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        TarGrp tarGrp = tarGrpDetail;
        tarGrpMapper.delTarGrp(tarGrp);
        List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
        for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
            tarGrpConditionMapper.delTarGrpCondition(tarGrpCondition);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 修改目标分群条件
     */
    @Override
    public Map<String, Object> updateTarGrpCondition(TarGrpCondition tarGrpCondition) {
        Map<String, Object> mapsT = new HashMap<>();
        tarGrpCondition.setUpdateDate(DateUtil.getCurrentTime());
        tarGrpCondition.setUpdateStaff(UserUtil.loginId());
        tarGrpConditionMapper.modTarGrpCondition(tarGrpCondition);
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        return mapsT;
    }

    /**
     * 新增大数据模型
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveBigDataModel(Long mktCamGrpRulId) {
        Map<String, Object> maps = new HashMap<>();
        //从大数据获取信息返回前台
        return maps;
    }

    /**
     * 获取目标分群条件信息
     */
    @Override
    public Map<String, Object> listTarGrpCondition(Long tarGrpId){
        Map<String, Object> maps = new HashMap<>();
        if (tarGrpId==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg","");
            return maps;
        }
        //通过mktCamGrpRulId获取所有活动关联关系
//        MktCamGrpRul mktCamGrpRul = mktCamGrpRulMapper.selectByPrimaryKey(mktCamGrpRulId);
//        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(mktCamGrpRul.getTarGrpId());
        List<TarGrpCondition> listTarGrpCondition = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
        List<TarGrpConditionVO> grpConditionList = new ArrayList<>();
        List<TarGrpVO> tarGrpVOS = new ArrayList<>();//传回前端展示信息
        for (TarGrpCondition tarGrpCondition : listTarGrpCondition) {
            List<OperatorDetail> operatorList = new ArrayList<>();
            TarGrpConditionVO tarGrpConditionVO = new TarGrpConditionVO();
            BeanUtil.copy(tarGrpCondition,tarGrpConditionVO);
//            CopyPropertiesUtil.copyBean2Bean(tarGrpConditionVO, tarGrpCondition);
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpConditionVO.getLeftParam()));
            if (label==null){
                continue;
            }
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());
            List<LabelValueVO> valueList = ChannelUtil.valueList2VOList(labelValues);
            tarGrpConditionVO.setLeftParamName(label.getInjectionLabelName());
            //塞入领域
//            FitDomain fitDomain = null;
//            if (label.getFitDomain() != null) {
//                fitDomain = FitDomain.getFitDomain(Integer.parseInt(label.getFitDomain()));
//                tarGrpConditionVO.setFitDomainId(Long.valueOf(fitDomain.getValue()));
//                tarGrpConditionVO.setFitDomainName(fitDomain.getDescription());
//            }
            //将操作符转为中文
            if (tarGrpConditionVO.getOperType()!=null && !tarGrpConditionVO.getOperType().equals("")){
                Operator op = Operator.getOperator(Integer.parseInt(tarGrpConditionVO.getOperType()));
                tarGrpConditionVO.setOperTypeName(op.getDescription());
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
            tarGrpConditionVO.setConditionType(label.getConditionType());
            tarGrpConditionVO.setValueList(valueList);
            tarGrpConditionVO.setOperatorList(operatorList);
            grpConditionList.add(tarGrpConditionVO);
        }
        List<OrgTreeDO> sysAreaList = (List<OrgTreeDO>)redisUtils.get("AREA_RULE_ENTITY_"+tarGrpId);
        if (sysAreaList!=null){
            List<SysAreaVO> voList = new ArrayList<>();
            for (OrgTreeDO area : sysAreaList){
                SysAreaVO vo = BeanUtil.create(area,new SysAreaVO());
                voList.add(vo);
            }
            TarGrpConditionVO tarGrpConditionVO = new TarGrpConditionVO();

            tarGrpConditionVO.setSysAreaList(voList);
            grpConditionList.add(tarGrpConditionVO);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("listTarGrpCondition", grpConditionList);
        maps.put("conditionList",listTarGrpCondition);
        return maps;
    }

    /**
     * 获取大数据模型
     */
    @Override
    public Map<String, Object> listBigDataModel(Long mktCamGrpRulId) {
        //通过传入参数获取大数据模型返回前台 todo

        return null;
    }

    /**
     * 策略试运算
     */
    @Override
    public Map<String, Object> strategyTrial(CalcReqModel req, String serialNum) {
        //通过调用大数据返回试算结果返回前台 todo
        Map result = new HashMap(2);
        try {
            Map<String, Object> validateResult = validate(req);
            if (!validateResult.isEmpty()) {
                return validateResult;
            }
            SystemParam param = (SystemParam) CacheManager.getInstance().getCache(
                    CacheConstants.SYSTEMPARAM_CACHE_NAME).queryOne("cpc.dubbo.client.url");
            String url = param.getParamValue() + "/policy/trycalc";
            if (StringUtils.isNotEmpty(serialNum)) {
                url = url + "?serialNum=" + serialNum;
            }
            result = restTemplate.postForObject(url, req, Map.class);
        } catch (Exception e) {
            logger.error("calc error", e);
            result.put("resultCode", ErrorCode.INTERNAL_ERROR.getErrorCode());
            result.put("resultMsg", ErrorCode.INTERNAL_ERROR.getErrorMsg());
        }
        logger.debug("trycalc result: " + JSON.toJSONString(result));
        return result;
    }

    /**
     * 策略试运算（老系统）
     */
    @Override
    public Map<String, String> trycalc(CalcReqModel calcReqModel, String serialNum) {
        ResponseHeaderModel resp = null;
        Map<String, String> result = new HashMap<String, String>(2);

        try {
            //获取策略列表
            List<Map<String, Object>> policyList = calcReqModel.getPolicyList();
            //拼装sql前验证
            ValidateResult validateResult = tryCalcService.validate(serialNum, calcReqModel);
//            ValidateResult validateResult = new ValidateResult();
            validateResult.setResult(true);
            validateResult.setMessage("111");
            validateResult.setCode("111");

            //校验通过后进行试运算，否则返回消息给web
            if (validateResult.getResult()) {

                for (Map<String, Object> policy : policyList) {

                    //页面选择的资产域
                    String recommendType = policy.get("recommendType").toString();
                    List<Map<String, String>> tagInfos = new ArrayList<>();
                    //防止tagInfos添加重复的数据
                    Map<String, String> tagInfoKeys = new HashMap<>();

                    //规则
                    List<Map<String, Object>> ruleList = (List<Map<String, Object>>) policy.get("rules");
                    for (Map<String, Object> rule : ruleList) {

                        List<Map<String, String>> triggers = (List<Map<String, String>>) rule.get("triggers");
                        List<Company> company = (List<Company>) rule.get("company");
                        String sql = null;
                        //tagInfos 获取标签信息
                        sql = SqlUtil.integrationSql(recommendType, triggers, tagInfos, company,
                                tagInfoKeys);

                        // 清除不必要的参数
                        rule.remove("triggers");
                        rule.remove("company");
                        rule.remove("xietong");

                        rule.put("sql", sql);
                    }
                    //清除不必要的参数
                    policy.remove("recommendName");
                    policy.remove("recommendType");
                    policy.remove("place");

                    policy.put("tagInfos", tagInfos);
                }

                //目前只支持一个数据源DB2
//                EagleDatabaseConfig config = (EagleDatabaseConfig)CacheManager.getInstance().getCache(
//                        CacheConstants.DATABASE_COPNFIG_CACHE_NAME).queryOne(
//                        EagleDatabaseConfCache.CACHE_DB2_KEY);
                EagleDatabaseConfig config = new EagleDatabaseConfig();
                config.setDbConfRowId(21L);
                calcReqModel.setDbConfRowId(config.getDbConfRowId().toString());

                logger.debug("calcReqModel: " + JSON.toJSONString(calcReqModel));
                resp = policyCalculateService.tryCalculate(calcReqModel);
                result.put("resultCode", resp.getResultCode());
                result.put("resultMessage", resp.getResultMessage());
                return result;
            }

            result.put("resultCode", validateResult.getCode());
            result.put("resultMessage", validateResult.getMessage());
        } catch (Exception e) {
            result.put("resultCode", ResponseCode.INTERNAL_ERROR);
            result.put("resultMessage", ResponseCode.INTERNAL_ERROR_MSG);
            logger.error("policyCalculateService.tryCalculate", e);
        }

        return result;

    }

    /**
     * 参数校验
     */
    private Map<String, Object> validate(CalcReqModel req) {
        Map<String, Object> result = new HashMap<>(2);
        if (StringUtils.isEmpty(req.getActivityId())) {
            result.put("resultCode", ErrorCode.VALIDATE_ERROR.getErrorCode());
            result.put("resultMsg", "activityId不能为空");
            return result;
        }

        if (CollectionUtils.isEmpty(req.getPolicyList())) {
            result.put("resultCode", ErrorCode.VALIDATE_ERROR.getErrorCode());
            result.put("resultMsg", "policyList不能为空");
            return result;
        }

        for (Map<String, Object> policy : req.getPolicyList()) {
            Object policyId = policy.get("policyId");
            if (null == policyId || StringUtils.isEmpty(policyId.toString())) {
                result.put("resultCode", ErrorCode.VALIDATE_ERROR.getErrorCode());
                result.put("resultMsg", "policyId不能为空");
                return result;
            }
        }
        return result;
    }

}
