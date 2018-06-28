package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.model.policy.CalcReqModel;
import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;
import com.zjhcsoft.eagle.main.dubbo.service.PolicyCalculateService;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamGrpRulMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpConditionDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.dto.system.SystemParam;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.model.EagleDatabaseConfig;
import com.zjtelcom.cpct.pojo.Company;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.EagleDatabaseConfCache;
import com.zjtelcom.cpct.service.TryCalcService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.SqlUtil;
import com.zjtelcom.cpct.validator.ValidateResult;
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

/**
 * @Description 目标分群serviceImpl
 * @Author pengy
 * @Date 2018/6/25 10:34
 */
@Service
@Transactional
public class TarGrpServiceImpl extends BaseService implements TarGrpService {

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


    /**
     * 新增目标分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createTarGrp(TarGrpDetail tarGrpDetail) {
        TarGrp tarGrp = new TarGrp();
        Map<String, Object> maps = new HashMap<>();
        try {
            tarGrpMapper.insert(tarGrpDetail);
            CopyPropertiesUtil.copyBean2Bean(tarGrp, tarGrpDetail);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", StringUtils.EMPTY);
            maps.put("tarGrp", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to createTarGrp ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrp);
        return maps;
    }

    /**
     * 新增客户分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveTagNumFetch(Long mktCamGrpRulId, List<TarGrpConditionDTO> tarGrpConditionDTOList) {
        Map<String, Object> maps = new HashMap<>();
        TarGrpDetail tarGrpDetail = new TarGrpDetail();
        try {
            //生成客户分群
            tarGrpDetail = new TarGrpDetail();
            tarGrpMapper.insert(tarGrpDetail);
            //添加客户分群条件
            for (int i = 0; i < tarGrpConditionDTOList.size(); i++) {
                tarGrpConditionMapper.insert(tarGrpConditionDTOList.get(i));
            }
            //更新营销活动分群规则表
            MktCamGrpRul mktCamGrpRul = new MktCamGrpRul();
            mktCamGrpRul.setMktCamGrpRulId(mktCamGrpRulId);
            mktCamGrpRul.setTarGrpId(tarGrpDetail.getTarGrpId());
            mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);

        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
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
            tarGrpConditionMapper.deleteByPrimaryKey(conditionId);
        } catch (Exception e) {
            mapsT.put("resultCode", CommonConstant.CODE_FAIL);
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
        TarGrpConditionDO tarGrpConditionDO = new TarGrpConditionDO();
        try {
            tarGrpConditionDO = tarGrpConditionMapper.getTarGrpCondition(conditionId);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", ErrorCode.EDIT_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            maps.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to editTarGrpConditionDO ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("resultObject", tarGrpConditionDO);
        return maps;
    }

    /**
     * 更新目标分群条件
     */
    @Override
    public Map<String, Object> updateTarGrpConditionDO(TarGrpConditionDO tarGrpConditionDO) {
        Map<String, Object> mapsT = new HashMap<>();
        try {
            tarGrpConditionMapper.updateByPrimaryKey(tarGrpConditionDO);
        } catch (Exception e) {
            mapsT.put("resultCode", CommonConstant.CODE_FAIL);
            mapsT.put("resultMsg", ErrorCode.UPDATE_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            mapsT.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to updateTarGrpConditionDO ", e);
            return mapsT;
        }
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        mapsT.put("resultObject", StringUtils.EMPTY);
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
    public Map<String, Object> listTarGrpCondition(Long mktCamGrpRulId) {
        Map<String, Object> maps = new HashMap<>();
        //通过mktCamGrpRulId获取所有活动关联关系
        MktCamGrpRul mktCamGrpRul = mktCamGrpRulMapper.selectByPrimaryKey(mktCamGrpRulId);
        TarGrpDetail tarGrpDetail = tarGrpMapper.selectByPrimaryKey(mktCamGrpRul.getTarGrpId());
        List<TarGrpConditionDO> list = tarGrpConditionMapper.listTarGrpCondition(tarGrpDetail.getTarGrpId());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("resultObject", list);
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
                    CommonConstant.SYSTEMPARAM_CACHE_NAME).queryOne("cpc.dubbo.client.url");
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
            List<Map<String, Object>> policyList = calcReqModel.getPolicyList();
            ValidateResult validateResult = tryCalcService.validate(serialNum, calcReqModel);

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
                EagleDatabaseConfig config = (EagleDatabaseConfig) CacheManager.getInstance().getCache(
                        CommonConstant.DATABASE_COPNFIG_CACHE_NAME).queryOne(
                        EagleDatabaseConfCache.CACHE_DB2_KEY);
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
            result.put("resultCode", ErrorCode.INTERNAL_ERROR.getErrorCode());
            result.put("resultMessage", ErrorCode.INTERNAL_ERROR.getErrorMsg());
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
