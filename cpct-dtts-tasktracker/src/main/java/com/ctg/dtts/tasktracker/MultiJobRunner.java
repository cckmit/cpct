package com.ctg.dtts.tasktracker;

import com.alibaba.fastjson.JSON;
import com.ctg.dtts.core.logger.Logger;
import com.ctg.dtts.core.logger.LoggerFactory;
import com.ctg.dtts.tasktracker.logger.BizLogger;
import com.ctg.dtts.tasktracker.runner.DttsLoggerFactory;
import com.zjhcsoft.eagle.main.dubbo.service.QuerySaturationService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.LabelSaturationMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.LabelSaturation;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dubbo.out.BlackListService;
import com.zjtelcom.cpct.dubbo.out.CampaignService;
import com.zjtelcom.cpct.dubbo.out.TargetGroupService;
import com.zjtelcom.cpct.dubbo.out.TrialStatusUpService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import com.ztesoft.uccp.dubbo.interfaces.UCCPSendService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


public class MultiJobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiJobRunner.class);

    @Autowired
    TestSpringBean springBean;

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired(required = false)
    private TrialStatusUpService trialStatusUpService;
    @Autowired
    private CampaignService campaignService;
    @Autowired(required = false)
    private TargetGroupService targetGroupService;
    @Autowired(required = false)
    private QuerySaturationService querySaturationService;
    @Autowired(required = false)
    private LabelSaturationMapper labelSaturationMapper;
    @Autowired(required = false)
    private TrialOperationMapper trialOperationMapper;
    @Autowired(required = false)
    private UCCPSendService uCCPSendService;
//    @Autowired(required = false)
//    private MktDttsLogService mktDttsLogService;
    @Autowired(required = false)
    private BlackListService blackListService;


    private static final String userAcct = "CPCPYX";
    private static final String password = "908234";
    private static final String sceneId = "7149";

    /**
     * 单位为天的周期性营销活动
     */
    public void runCycleMarketCampaignDay() throws Throwable { //不带Job job参数、没有返回，适用于无参数无返回的任务
        try {
            springBean.hello();
            String msg = "runCycleMarketCampaignDay执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"天"的营销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("1000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("1000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleDayRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleMarketCampaignDay failed!", e);
        }
    }

    /**
     * 单位为天的周期性服务活动
     */
    public void runCycleServiceCampaignDay() throws Throwable {
        try {
            springBean.hello();
            String msg = "runCycleServiceCampaignDay执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"天"的服务活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("1000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("5000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleDayRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleServiceCampaignDay failed!", e);
        }
    }

    /**
     * 单位为天的周期性服务随销活动
     */
    public void runCycleServiceAndMarketCampaignDay() throws Throwable {
        try {
            springBean.hello();
            String msg = "runCycleServiceAndMarketCampaignDay执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"天"的服务随销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("1000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("6000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleDayRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleServiceAndMarketCampaignDay failed!", e);
        }
    }

    /**
     * 周期为“天”的方案
     */
    private void cycleDayRule(List<MktCampaignDO> list) {
        try {
            Map<String, Object> result = new HashMap<>();
            //筛选出条件满足的周期性活动id
            List<Integer> campaignIdList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            for (MktCampaignDO campaignDO : list) {
                String planEndTime = sdf.format(campaignDO.getPlanEndTime());
                String planBeginTime = sdf.format(campaignDO.getPlanBeginTime());
                long flagEnd = (sdf.parse(planEndTime).getTime() - sdf.parse(today).getTime()) / (1000 * 3600 * 24);
                long flagBegin = (sdf.parse(today).getTime() - sdf.parse(planBeginTime).getTime()) / (1000 * 3600 * 24);
                //判断当前日期是否在活动的生失效时间内
                if (flagEnd >= 0 && flagBegin >= 0) {
                    String[] execInvl = campaignDO.getExecInvl().split("-");
                    String execInitTime = campaignDO.getExecInitTime();
                    //当前时间与执行时间的相差时间
                    long dayDifference = (sdf.parse(today).getTime() - sdf.parse(execInitTime).getTime()) / (1000 * 3600 * 24);
                    //判断当前时间是否在执行时间之后
                    if (dayDifference >= 0) {
                        int cycleTime = Integer.parseInt(execInvl[0]);
                        //判断相差时间是否符合周期间隔
                        if (dayDifference % cycleTime == 0) {
                            campaignIdList.add(Integer.parseInt(campaignDO.getMktCampaignId().toString()));
                        }
                    }
                }
            }
            System.out.println("*******************周期为“天”的营销活动列表*******************" + campaignIdList);
            result.put("idList", campaignIdList);
            result.put("perCampaign", "PER_CAMPAIGN");
            trialStatusUpService.campaignIndexTask(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 单位为月的周期性营销活动
     */
    public void runCycleMarketCampaignMonth() throws Throwable { //不带Job job参数、没有返回，适用于无参数无返回的任务
        try {
            springBean.hello();
            String msg = "runCycleMarketCampaignMonth执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"月"的营销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("2000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("1000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleMonthRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleMarketCampaignMonth failed!", e);
        }
    }

    /**
     * 单位为月的周期性服务活动
     */
    public void runCycleServiceCampaignMonth() throws Throwable {
        try {
            springBean.hello();
            String msg = "runCycleServiceCampaignMonth执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"月"的服务活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("2000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("5000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleMonthRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleServiceCampaignMonth failed!", e);
        }
    }

    /**
     * 单位为月的周期性服务随销活动
     */
    public void runCycleServiceAndMarketCampaignMonth() throws Throwable {
        try {
            springBean.hello();
            String msg = "runCycleServiceAndMarketCampaignMonth执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"月"的服务随销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("2000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("6000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleMonthRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleServiceAndMarketCampaignMonth failed!", e);
        }
    }

    /**
     * 周期为“月”的方案
     */
    private void cycleMonthRule(List<MktCampaignDO> list) {
        try {
            Map<String, Object> result = new HashMap<>();
            //筛选出条件满足的周期性活动id
            List<Integer> campaignIdList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            int todayDay = DateUtil.getCurrentDay();
            int todayMonth = DateUtil.getCurrentMonth();
            int todayYear = DateUtil.getCurrentYear();
            for (MktCampaignDO campaignDO : list) {
                String planEndTime = sdf.format(campaignDO.getPlanEndTime());
                String planBeginTime = sdf.format(campaignDO.getPlanBeginTime());
                long flagEnd = (sdf.parse(planEndTime).getTime() - sdf.parse(today).getTime()) / (1000 * 3600 * 24);
                long flagBegin = (sdf.parse(today).getTime() - sdf.parse(planBeginTime).getTime()) / (1000 * 3600 * 24);
                //判断当前日期是否在活动的生失效时间内
                if (flagEnd >= 0 && flagBegin >= 0) {
                    boolean flag = false;
                    String[] execInitTime = campaignDO.getExecInitTime().split(",");
                    //判断当前日期是否符合执行时间
                    for (int i = 0; i < execInitTime.length; i++) {
                        if (execInitTime[i].equals(String.valueOf(todayDay))) {
                            flag = true;
                            break;
                        }
                    }
                    //判断相差月份是否符合周期间隔
                    if (flag) {
                        String[] execInvl = campaignDO.getExecInvl().split("-");
                        int cycleTime = Integer.parseInt(execInvl[0]);
                        int monthDifference = (todayYear - DateUtil.getYearByDate(sdf.parse(planBeginTime))) * 12 + todayMonth - DateUtil.getMonthByDate(sdf.parse(planBeginTime));
                        if (monthDifference % cycleTime == 0) {
                            campaignIdList.add(Integer.parseInt(campaignDO.getMktCampaignId().toString()));
                        }
                    }
                }
            }
            System.out.println("*******************周期为“月”的营销活动列表*******************" + campaignIdList);
            result.put("idList", campaignIdList);
            result.put("perCampaign", "PER_CAMPAIGN");
            trialStatusUpService.campaignIndexTask(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 单位为周的周期性营销活动
     */
    public void runCycleMarketCampaignWeek() throws Throwable { //不带Job job参数、没有返回，适用于无参数无返回的任务
        try {
            springBean.hello();
            String msg = "runCycleMarketCampaignWeek执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"周"的营销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("3000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("1000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleWeekRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleMarketCampaignWeek failed!", e);
        }
    }

    /**
     * 单位为周的周期性服务活动
     */
    public void runCycleServiceCampaignWeek() throws Throwable {
        try {
            springBean.hello();
            String msg = "runCycleServiceCampaignMonth执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"周"的服务活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("3000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("5000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleWeekRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleServiceCampaignWeek failed!", e);
        }
    }

    /**
     * 单位为周的周期性服务随销活动
     */
    public void runCycleServiceAndMarketCampaignWeek() throws Throwable {
        try {
            springBean.hello();
            String msg = "runCycleServiceAndMarketCampaignMonth执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //筛选出周期性、已发布的活动
            List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByTypeAndStatus("2000", "2002");
            //筛选出周期为"周"的服务随销活动
            List<MktCampaignDO> mktCampaignDOs = new ArrayList<>();
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                String[] execInvl = mktCampaignDO.getExecInvl().split("-");
                if (execInvl.length < 2) {
                    continue;
                }
                if (execInvl[1].equals("3000") && mktCampaignDO.getMktCampaignCategory().equals("3000") && mktCampaignDO.getMktCampaignType().equals("6000")) {
                    mktCampaignDOs.add(mktCampaignDO);
                }
            }
            cycleWeekRule(mktCampaignDOs);
        } catch (Exception e) {
            LOGGER.info("Run cycleServiceAndMarketCampaignWeek failed!", e);
        }
    }

    /**
     * 周期为“周”的方案
     */
    private void cycleWeekRule(List<MktCampaignDO> list) {
        try {
            Map<String, Object> result = new HashMap<>();
            //筛选出条件满足的周期性活动id
            List<Integer> campaignIdList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            int todayWeekNumber = DateUtil.dateToWeek(sdf.parse(today));
            if (todayWeekNumber == 0) {
                todayWeekNumber = todayWeekNumber + 7;
            }
            for (MktCampaignDO campaignDO : list) {
                String planEndTime = sdf.format(campaignDO.getPlanEndTime());
                String planBeginTime = sdf.format(campaignDO.getPlanBeginTime());
                long flagEnd = (sdf.parse(planEndTime).getTime() - sdf.parse(today).getTime()) / (1000 * 3600 * 24);
                long flagBegin = (sdf.parse(today).getTime() - sdf.parse(planBeginTime).getTime()) / (1000 * 3600 * 24);
                //判断当前日期是否在活动的生失效时间内
                if (flagEnd >= 0 && flagBegin >= 0) {
                    boolean flag = false;
                    String[] execInitTime = campaignDO.getExecInitTime().split(",");
                    //判断当前日期是否符合执行时间
                    for (int i = 0; i < execInitTime.length; i++) {
                        if (execInitTime[i].equals(String.valueOf(todayWeekNumber))) {
                            flag = true;
                            break;
                        }
                    }
                    //判断相差周数是否符合周期间隔
                    if (flag) {
                        int beginWeekNumber = DateUtil.dateToWeek(sdf.parse(planBeginTime));
                        if (beginWeekNumber == 0) {
                            beginWeekNumber = beginWeekNumber + 7;
                        }
                        String[] execInvl = campaignDO.getExecInvl().split("-");
                        int cycleTime = Integer.parseInt(execInvl[0]);
                        long weekDifference = 0;
                        if ((7 - beginWeekNumber) < flagBegin) {
                            weekDifference = (flagBegin - (7 - beginWeekNumber)) / 7 + 1;
                        }
                        if (weekDifference % cycleTime == 0) {
                            campaignIdList.add(Integer.parseInt(campaignDO.getMktCampaignId().toString()));
                        }
                    }
                }
            }
            System.out.println("*******************周期为“周”的营销活动列表*******************" + campaignIdList);
            result.put("idList", campaignIdList);
            result.put("perCampaign", "PER_CAMPAIGN");
            trialStatusUpService.campaignIndexTask(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实时清单方案
     */
    public void runUserListPlan() throws Throwable {
        try {
            springBean.hello();
            String msg = "runUserListPlan执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //系统参数表筛选出活动
            Map<String, Object> result = new HashMap<>();
            List<Integer> campaignIdList = new ArrayList<>();
            List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("MKT_CAM_API_CODE");
            for (SysParams sysParams : sysParamsList) {
                campaignIdList.add(Integer.parseInt(sysParams.getParamValue()));
            }
            System.out.println("*******************实时清单方案活动列表*******************" + campaignIdList);
            result.put("userListCam", "USER_LIST_CAM");
            result.put("idList", campaignIdList);
            trialStatusUpService.campaignIndexTask(result);
        } catch (Exception e) {
            LOGGER.info("Run userListPlan failed!", e);
        }
    }

    /**
     * 标签库饱和度大数据查询
     */
    public void querySaturationCpc() throws Throwable {
        try {
            springBean.hello();
            String msg = "querySaturationCpc执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //取昨天的日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date today = new Date();
            String yesterday = sdf.format(new Date(today.getTime() - 86400000L));
            Map<String, String> map = querySaturationService.querySaturation(yesterday, null);
            List<Map<String, Object>> labelList = (List<Map<String, Object>>) JSON.parse(map.get("labelSaturation"));
            List<LabelSaturation> labelSaturationList = new ArrayList<>();
            if (labelList != null && labelList.size() > 0) {
                for (Map<String, Object> labelMap : labelList) {
                    LabelSaturation labelSaturation = new LabelSaturation();
                    labelSaturation.setLabelCode(labelMap.get("LABEL_ENG_NAME").toString());
                    labelSaturation.setBigdataSaturation(Long.valueOf(labelMap.get("LABEL_NOT_NULL_CNT").toString()));
                    labelSaturation.setSaturationBatchNumber(labelMap.get("DATE_CD").toString());
                    labelSaturation.setCreateDate(new Date());
                    labelSaturation.setUpdateDate(new Date());
                    labelSaturation.setCreateStaff(UserUtil.loginId());
                    labelSaturation.setUpdateStaff(UserUtil.loginId());
                    labelSaturationList.add(labelSaturation);
                }
                labelSaturationMapper.insertBatch(labelSaturationList);
            }
        } catch (Exception e) {
            LOGGER.info("Run querySaturationCpc failed!", e);
        }
    }

    /**
     * es日志上传
     */
    public void esLogLoadToSftp() throws Throwable {
        try {
            springBean.hello();
            String msg = "esLogToSftp执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //日志上传
            trialStatusUpService.cpcLog2WriteFileLabel();
        } catch (Exception e) {
            LOGGER.info("Run esLogToSftp failed!", e);
        }
    }

    /**
     * 活动过期
     */
    public void dueMktCampaign() throws Throwable {
        try {
            springBean.hello();
            String msg = "dueMktCampaign执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //活动过期
            trialStatusUpService.dueMktCampaign();
        } catch (Exception e) {
            LOGGER.info("Run dueMktCampaign failed!", e);
        }
    }

    /**
     * 活动延期短信通知
     */
    public void campaignDelayNotice() throws Throwable {
        try {
            springBean.hello();
            String msg = "campaignDelayNotice执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //活动延期短信通知
            campaignService.campaignDelayNotice();
        } catch (Exception e) {
            LOGGER.info("Run campaignDelayNotice failed!", e);
        }
    }

    /**
     * 分群下发（静态参数表需要配下发id）
     */
    public void tarGrpTemplateScheduledBatchIssue() throws Throwable {
        try {
            springBean.hello();
            String msg = "tarGrpTemplateScheduledBatchIssue执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //分群下发
            targetGroupService.tarGrpTemplateScheduledBatchIssue();
        } catch (Exception e) {
            LOGGER.info("Run tarGrpTemplateScheduledBatchIssue failed!", e);
        }
    }

    /**
     * 批量Excel清单导入下发
     */
    public void importUserListByExcel() throws Throwable {
        try {
            springBean.hello();
            String msg = "importUserListByExcel执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //分群下发
            trialStatusUpService.importUserListByExcel();
        } catch (Exception e) {
            LOGGER.info("Run importUserListByExcel failed!", e);
        }
    }

    /**
     * 销售品下架
     */
    public void sendMsgByOfferOver() throws Throwable {
        try {
            springBean.hello();
            String msg = "sendMsgByOfferOver执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //分群下发
            trialStatusUpService.sendMsgByOfferOver();
        } catch (Exception e) {
            LOGGER.info("Run sendMsgByOfferOver failed!", e);
        }
    }


    /**
     *
     * @throws Throwable
     */
    public void updateRedisOfDateTypeLabel() {
        methodInvoke(targetGroupService);
    }

    /**
     * 定时任务：每个派单成功活动取最后一个批次若该批次下发时间超过20天（可变），查询处理率若低于1%（可变)，则自动失效，并短信通知
     */
    public void issuedSuccessMktCheck() {
        methodInvoke(campaignService);
    }


    // 调用的方法名和注入要被调用的方法名要保持一致
    public <T> void methodInvoke(T correspondingService) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        try {
            springBean.hello();
            String msg = methodName + "执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);

            Class<?> aClass = correspondingService.getClass();
            // Object o = aClass.newInstance();
            Method method = aClass.getMethod(methodName);
            Object invoke = method.invoke(correspondingService);
        }catch (Exception e) {
            LOGGER.info("Run" + methodName + "failed!", e);
        }
    }


    //3-31 11:07 x
    //周期性活动定时任务添加校验接口若未执行的 二次执行
    public void playAgainPeriodicity(){
        LOGGER.info("周期性活动定时任务添加校验接口若未执行的 二次执行启动");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 01:00:00");
        String startTime = sdf.format(d);
        System.out.println("格式化后的日期：" + startTime);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 05:00:00");
        String endTime = sdf2.format(d);
        System.out.println("格式化后的日期：" + endTime);
//        Map<String,Object> dataParam =new HashMap<>();
//        dataParam.put("startTime",startTime);
//        dataParam.put("endTime",endTime);
        List<TrialOperation> trialOperationList = trialOperationMapper.getDataStartToEnd(startTime,endTime);
        List<Integer> stringArraylist = new ArrayList<Integer>();
        if (trialOperationList!=null){
            for (TrialOperation operation : trialOperationList){
                String statusCd = operation.getStatusCd();
                //判断状态
                if (!statusCd.equals("7300") || !statusCd.equals("8100")){
                    stringArraylist.add(Integer.valueOf(operation.getCampaignId().toString()));

                }
            }
            LOGGER.info("二次执行活动个数："+stringArraylist.size());
            //运行周期性任务下发失败或者全量失败的任务再次执行 并下发短信
            if (!stringArraylist.isEmpty()){
                //再次执行
                try {
                    Map<String,Object> param =new HashMap<>();
                    param.put("idList",stringArraylist); //活动id集合
                    param.put("perCampaign","PER_CAMPAIGN");//周期性活动标识
                    trialStatusUpService.campaignIndexTask(param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //下发短信
                LOGGER.info("周期性二次执行我去下发短信了");
                String sendMsg = "周期性活动下发或试算失败，活动id为："+JSON.toJSONString(stringArraylist);
                List<SysParams> send_msg = sysParamsMapper.listParamsByKeyForCampaign("SEND_MSG");
                for (SysParams sysParams : send_msg) {
                    try {
                        sendShortMessage(sysParams.getParamValue(),sendMsg,"571");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }


    //下发短信
    public String sendShortMessage(String targPhone, String sendContent, String lanId) throws Exception {
        HashMap params = new HashMap();
        //请求消息流水，格式：系统编码（6位）+yyyymmddhhmiss+10位序列号
        params.put("TransactionId", userAcct + date2StringDate(new Date()) + getRandom(10));
        //UCCP分配的系统编码
        params.put("SystemCode", userAcct);
        //UCCP分配的帐号
        params.put("UserAcct", userAcct);
        //UCCP分配的认证密码
        params.put("Password", password);
        //场景标识
        params.put("SceneId", sceneId);
        //请求的时间,请求发起的时间,必须为下边的格式
        params.put("RequestTime",date2StringDate(new Date()));
        //接收消息推送的手机号码
        params.put("AccNbr",targPhone);
        //params.put("AccNbr","18957181789");
        //消息内容
        params.put("OrderContent",sendContent);
        //本地网/辖区
        params.put("LanId",lanId);
        Map map = uCCPSendService.sendShortMessage(params);
        if (map == null) return "调用sendShortMessage返回结果异常！";
        if (!map.get("code").equals("0000")) {
            // 短信发送成功记录数据
//            saveShortMessageLog(targPhone, sendContent);
            return map.get("msg").toString();
        } else {
            return "";
        }
    }

    public static String getRandom(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    public static String date2StringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = sdf.format(date);
        return result;
    }

    // 短信发送类型任务记录
//    public void saveShortMessageLog(String targPhone, String sendContent) {
//        try {
//            mktDttsLogService.saveMktDttsLog("9010","成功", new Date(), new Date(), targPhone, sendContent);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * 黑名单导出
     */
    public void exportBlackListFile() throws Throwable {
        try {
            LOGGER.info("黑名单导出exportBlackListFile执行");
            springBean.hello();
            String msg = "exportBlackListFile执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //黑名单导出
            blackListService.exportBlackListFile();
        } catch (Exception e) {
            LOGGER.info("Run exportBlackListFile failed!", e);
        }
    }


    /**
     * 黑名单导入
     */
    public void importBlackListFile() throws Throwable {
        try {
            LOGGER.info("黑名单导入importBlackListFile执行");
            springBean.hello();
            String msg = "importBlackListFile执行任务";
            LOGGER.info(msg);
            BizLogger bizLogger = DttsLoggerFactory.getBizLogger();
            bizLogger.info(msg);
            //黑名单导入
            blackListService.importBlackListFile();
        } catch (Exception e) {
            LOGGER.info("Run importBlackListFile failed!", e);
        }
    }

}
