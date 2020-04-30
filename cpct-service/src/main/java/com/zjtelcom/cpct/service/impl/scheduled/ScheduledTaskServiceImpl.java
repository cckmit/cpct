package com.zjtelcom.cpct.service.impl.scheduled;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.ICpcAPIService;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.enums.TrialStatus;
import com.zjtelcom.cpct.service.cpct.ProjectManageService;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.service.scheduled.ScheduledTaskService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.enums.DateUnit.DAY;

@Service
@Transactional
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    public static final Logger logger = LoggerFactory.getLogger(ScheduledTaskServiceImpl.class);

    @Autowired
    private TrialOperationMapper trialOperationMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private UCCPService uccpService;
    @Autowired
    private ActivityStatisticsService activityStatisticsService;
    @Autowired
    private ProjectManageService projectManageService;
    @Autowired
    private SysParamsMapper SysParamsMapper;
    @Autowired(required = false)
    private IReportService iReportService;
    @Autowired(required = false)
    private ICpcAPIService iCpcAPIService;
    @Autowired
    private RedisUtils redisUtils;

    // 批次下发时间最大允许时间
    public static final String maxDays = "BATCH_ISSUED_TIME";
    // 批次下发处理率最小允许值
    public static final String minRate = "BATCH_DEAL_RATE";
    // 有效活动列表(只执行存在列表中的)
    public static final String executeCamList = "EXECUTE_CAM_LIST";

    @Override
    public void issuedSuccessMktCheck() {
        if (batchExpirySwitch()) {
            // 每个派单成功活动取最后一个批次若该批次
            List<TrialOperation> trialOperations = trialOperationMapper.queryIssuedSuccess();
            Integer days = getSysParamsIntegerValue(maxDays);
            Integer rate = getSysParamsIntegerValue(minRate);
            String redisOrSysParams = redisUtils.getRedisOrSysParams(executeCamList);
            List<String> camList = Arrays.asList(redisOrSysParams.split(","));
            x: for (TrialOperation trialOperation : trialOperations) {
                try {
                    Date createDate = trialOperation.getCreateDate();
                    String batchNum = String.valueOf(trialOperation.getBatchNum());
                    Integer daysBetween = DateUtil.daysBetween(createDate, new Date());
                    Long campaignId = trialOperation.getCampaignId();
                    MktCampaignDO mktCampaignDO1 = mktCampaignMapper.selectByPrimaryKey(campaignId);
                    if (mktCampaignDO1 == null || mktCampaignDO1.getInitId() == null || !camList.contains(campaignId.toString())) {
                        continue;
                    }
                    Long initId = mktCampaignDO1.getInitId();
                    if (daysBetween > days) {
                        // TODO 调用营服查询处理率
                        List<Map<String, String>> rptBatchOrder = getRptBatchOrder(initId.toString(), DateUtil.date2String(createDate));
                        if (rptBatchOrder != null) {
                            //logger.info("调用营服查询处理率" + JSON.toJSONString(rptBatchOrder));
                            for (Map<String, String> stringStringMap : rptBatchOrder) {
                                String batchNbr = stringStringMap.get("batchNbr");
                                logger.info("String batchNbr:" + batchNbr + ",batchNum:" + batchNum + ",batchNum.equals(batchNbr):" + batchNum.equals(batchNbr));
                                if (batchNum.equals(batchNbr) && stringStringMap.get("handleRate")!=null) {
                                    String handleRateString = String.valueOf(stringStringMap.get("handleRate"));
                                    if (null == handleRateString || handleRateString.isEmpty()){
                                        continue;
                                    }
                                    handleRateString = String.valueOf(handleRateString);
                                    Double handleRate = Double.valueOf(handleRateString);
                                    boolean handleRateFlag = handleRate * 100 < rate;
                                    logger.info("handleRate:->" + handleRate + ",handleRateFlag:->" + handleRateFlag);
                                    if (handleRate * 100 < rate) {
                                        // TODO 调用营服调整批次生失效时间，使其失效，并短信通知
                                        if (modifyCampaignBatchFailureTime(batchNum)) {
                                            logger.info("批次失效短信通知");
                                            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(initId);
                                            String content = "您创建的活动" + mktCampaignDO.getMktCampaignName() + "的" + batchNum + "该批次的派单任务因处理率过低，现已自动失效！";
                                            uccpService.sendShortMessage4CampaignStaff(mktCampaignDO, content);
                                            trialOperation.setStatusCd(TrialStatus.UPLOAD_EXPIRED.getValue());
                                            trialOperationMapper.updateByPrimaryKey(trialOperation);
                                            continue x;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }catch (Exception e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // 查询批次失效开关
    public boolean batchExpirySwitch() {
        return redisUtils.getSwitch("BATCH_EXPIRY_SWITCH");
    }

    // 修改派单到营服的批次的失效时间
    public boolean modifyCampaignBatchFailureTime(String batchNum) {
        boolean result = false;
        try {
            String date = DateUtil.date2StringDate(DateUtil.addDate(new Date(), -1, DAY));
            Map map = new HashMap();
            map.put("workFlowId", batchNum);
            map.put("invalidDate", date);
            map.put("updateLoginname", "18957181789");
            map.put("updateLoginWorkNo", "Y33000063714");
            map.put("updateUsername", "解晓强");
            Map resultMap = iCpcAPIService.updateProjectStateTime(map);
            logger.info("修改派单到营服的批次的失效时间->:" + JSON.toJSONString(resultMap) + ",batchNum:" + batchNum);
            if (resultMap != null && resultMap.get("resultCode").equals("1")) {
                result = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Integer getSysParamsIntegerValue(String key) {
        String sysParamsValue = getSysParamsValue(key);
        return Integer.valueOf(sysParamsValue);
    }

    public String getSysParamsValue(String key) {
        List<Map<String, String>> maps = SysParamsMapper.listParamsByKey(key);
        String value = maps.get(0).get("value");
        return value;
    }

    public List<Map<String,String>> getRptBatchOrder(String mktCampaignId, String startDate) {
        Map map = new HashMap();
        map.put("startDate", startDate);
        map.put("endDate", DateUtil.date2String(new Date()));
        map.put("channelCode", "all");
        map.put("mktCampaignId", mktCampaignId);
        map.put("orglevel1", "800000000004");
        map.put("flag", "1");
        map.put("currenPage", "1");
        map.put("pageSize", "999");
        List<Map<String,String>> rptBatchOrderList = null;
        try {
            Map stringObjectMap = iReportService.queryRptBatchOrder(map);
            if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
                rptBatchOrderList = (List<Map<String,String>>)stringObjectMap.get("rptBatchOrderList");
            } else {
                Object reqId = stringObjectMap.get("reqId");
                stringObjectMap.put("resultCode", CODE_FAIL);
                stringObjectMap.put("resultMsg", "查询无结果 queryRptBatchOrder error :" + reqId.toString());
            }
            //logger.info("getRptBatchOrder->:" + JSON.toJSONString(stringObjectMap));11
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rptBatchOrderList;
    }
}
