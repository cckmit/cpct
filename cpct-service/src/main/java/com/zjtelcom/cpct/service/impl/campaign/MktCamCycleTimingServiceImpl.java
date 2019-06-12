package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamCycleTimingService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MktCamCycleTimingServiceImpl extends BaseService implements MktCamCycleTimingService {

    @Autowired
    MktCampaignMapper mktCampaignMapper;
    @Autowired(required = false)
    TrialOperationService trialOperationService;
    @Autowired
    MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;

    /**
     * 单位为小时的周期性活动
     */
    @Override
    public Map<String, Object> findCampaignCycleHour() {
        Map<String, Object> result = new HashMap<>();
        List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
        for(MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            String[] execInvl = mktCampaignDO.getExecInvl().split("-");
            if(execInvl[1].equals("4000")) {
                mktCampaignDOs.add(mktCampaignDO);
            }
        }
        for(MktCampaignDO campaignDO : mktCampaignDOs) {
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(campaignDO.getMktCampaignId());

            for(MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                TrialOperation trialOperation = new TrialOperation();
                trialOperation.setCampaignId(campaignDO.getMktCampaignId());
                trialOperation.setStrategyId(mktCamStrategyConfRelDO.getStrategyConfId());
                trialOperationService.issueTrialResult(trialOperation);
            }
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", StringUtils.EMPTY);
        return result;
    }

    /**
     * 单位为天的周期性活动
     */
    @Override
    public Map<String, Object> findCampaignCycleDay() {
        Map<String, Object> result = new HashMap<>();
        List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
        for(MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            String[] execInvl = mktCampaignDO.getExecInvl().split("-");
            if(execInvl[1].equals("1000")) {
                mktCampaignDOs.add(mktCampaignDO);
            }
        }
        for(MktCampaignDO campaignDO : mktCampaignDOs) {
            List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(campaignDO.getMktCampaignId());

            if(mktCamStrategyConfRelDOList != null) {
                for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                    TrialOperation trialOperation = new TrialOperation();
                    trialOperation.setCampaignId(campaignDO.getMktCampaignId());
                    trialOperation.setStrategyId(mktCamStrategyConfRelDO.getStrategyConfId());
                    trialOperationService.issueTrialResult(trialOperation);
                }
            }
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", StringUtils.EMPTY);
        return result;
    }

    /**
     * 单位为周的周期性活动
     */
    @Override
    public Map<String, Object> findCampaignCycleWeek() {
        Map<String, Object> result = new HashMap<>();
        List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
        for(MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            String[] execInvl = mktCampaignDO.getExecInvl().split("-");
            if(execInvl[1].equals("3000")) {
                mktCampaignDOs.add(mktCampaignDO);
            }
        }
        for(MktCampaignDO campaignDO : mktCampaignDOs) {
            Date date = DateUtil.getCurrentTime();
            String weekDay = String.valueOf(DateUtil.dateToWeek(date));
            String[] day = campaignDO.getExecInvl().split("-");

            if(day[0].equals(weekDay)) {
                List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(campaignDO.getMktCampaignId());

                if(mktCamStrategyConfRelDOList != null) {
                    for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                        TrialOperation trialOperation = new TrialOperation();
                        trialOperation.setCampaignId(campaignDO.getMktCampaignId());
                        trialOperation.setStrategyId(mktCamStrategyConfRelDO.getStrategyConfId());
                        trialOperationService.issueTrialResult(trialOperation);
                    }
                }
            }
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", StringUtils.EMPTY);
        return result;
    }

    /**
     * 单位为月的周期性活动
     */
    @Override
    public Map<String, Object> findCampaignCycleMonth() {
        Map<String, Object> result = new HashMap<>();
        List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
        for(MktCampaignDO mktCampaignDO : mktCampaignDOList) {
            String[] execInvl = mktCampaignDO.getExecInvl().split("-");
            if(execInvl[1].equals("2000")) {
                mktCampaignDOs.add(mktCampaignDO);
            }
        }
        for(MktCampaignDO campaignDO : mktCampaignDOs) {
            Date date = DateUtil.getCurrentTime();
            String MonthDay = String.valueOf(DateUtil.getDayByDate(date));
            String[] day = campaignDO.getExecInvl().split("-");

            if(day[0].equals(MonthDay)) {
                List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(campaignDO.getMktCampaignId());

                if(mktCamStrategyConfRelDOList != null) {
                    for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                        TrialOperation trialOperation = new TrialOperation();
                        trialOperation.setCampaignId(campaignDO.getMktCampaignId());
                        trialOperation.setStrategyId(mktCamStrategyConfRelDO.getStrategyConfId());
                        trialOperationService.issueTrialResult(trialOperation);
                    }
                }
            }
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", StringUtils.EMPTY);
        return result;
    }

}
