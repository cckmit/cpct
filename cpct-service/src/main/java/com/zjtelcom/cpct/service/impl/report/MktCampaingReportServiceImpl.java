package com.zjtelcom.cpct.service.impl.report;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignReportMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.OrgEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.service.report.MktCampaingReportService;
import com.zjtelcom.cpct.service.report.XinNewAactivityService;
import com.zjtelcom.cpct.service.system.SysAreaService;
import com.zjtelcom.cpct.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static com.zjtelcom.cpct.util.DateUtil.getFisrtDayOfMonth;
import static com.zjtelcom.cpct.util.DateUtil.getLastDayOfMonth;
import static com.zjtelcom.cpct.util.DateUtil.string2DateTime4Day;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/11/07 20:36
 * @version: V1.0
 */
@Service
@Transactional
public class MktCampaingReportServiceImpl implements MktCampaingReportService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private SysAreaService sysAreaService;

    @Autowired
    private MktCampaignReportMapper mktCampaignReportMapper;

    @Autowired
    private ContactChannelMapper contactChannelMapper;

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private ActivityStatisticsService activityStatisticsService;

    @Autowired
    private XinNewAactivityService xinNewAactivityService;


    /**
     * 查询头部信息
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getHeadInfo(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        Date startDate = null;
        Date endDate = null;
        Map<String, Object> headParam = new HashMap<>();
        // 获取起止时间
        Map<String, Object> dateMap = getDate(params);
        headParam.put("startDate", dateMap.get("startDate"));
        // 总活动量
        List<Map> headList = new ArrayList<>();

        // 总量
        Map<String, Object> totalCountMap = new HashMap<>();
        List<Map<String, Object>> totalList = countHeadInfo(headParam);
        totalCountMap.put("name", "总活动数");
        totalCountMap.put("count", totalList);
        headList.add(totalCountMap);

        //营销活动
        Map<String, Object> marketCountMap = new HashMap<>();
        headParam.put("mktCampaignType", "1000");
        List<Map<String, Object>> marketList = countHeadInfo(headParam);
        marketCountMap.put("name", "营销活动");
        marketCountMap.put("count", marketList);
        headList.add(marketCountMap);

        // 服务活动
        Map<String, Object> serviceCountMap = new HashMap<>();
        headParam.put("mktCampaignType", "5000");
        List<Map<String, Object>> serviceList = countHeadInfo(headParam);
        serviceCountMap.put("name", "服务活动");
        serviceCountMap.put("count", serviceList);
        headList.add(serviceCountMap);

        // 服务随销活动
        Map<String, Object> serMarkCountMap = new HashMap<>();
        headParam.put("mktCampaignType", "6000");
        List<Map<String, Object>> serMarkList = countHeadInfo(headParam);
        serMarkCountMap.put("name", "服务随销活动");
        serMarkCountMap.put("count", serMarkList);
        headList.add(serMarkCountMap);

        resultMap.put("data", headList);
        return resultMap;
    }

    /**
     * 活动运营数据
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getOperationInfo(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> sysAreaMap = sysAreaService.listCityByParentId(1);
        List<SysArea> sysAreaList = (List<SysArea>) sysAreaMap.get("sysAreaList");
        Map<String, Object> detailsParams = new HashMap<>();
        detailsParams.put("tiggerType", "2000");
        detailsParams.put("createDate", "3000");
        detailsParams.put("page", 1);
        detailsParams.put("pageSize", 999);
        Map<String, Object> mktCampaignDetails = activityStatisticsService.getMktCampaignDetails(detailsParams);
        List<MktCampaignDO> mktCampaignList = (List<MktCampaignDO>) mktCampaignDetails.get("resultMsg");
        Page pageInfo = (Page) mktCampaignDetails.get("pageInfo");
        // 不活跃活动数量
        int noOperCount = 0;
        if (pageInfo != null) {
            noOperCount = pageInfo.getTotal().intValue();
        }
        List<Long> noOperationIdList = new ArrayList<>();
        for (MktCampaignDO mktCampaignDO : mktCampaignList) {
            noOperationIdList.add(mktCampaignDO.getMktCampaignId());
        }
        List<Map<String, Object>> noOperMapList = new ArrayList<>();
        // 查询所有不活跃报表信息
        List<MktCampaignDO> mktCampaignDOInList = mktCampaignMapper.selectBatch(noOperationIdList);
        for (SysArea sysArea : sysAreaList) {
            Map<String, Object> cityMap = new HashMap<>();
            cityMap.put("name", sysArea.getName());
            cityMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            int count = 0;
            for (MktCampaignDO mktCampaignDO : mktCampaignDOInList) {
                // "不活跃活动"活动判断地市
                if (sysArea.getAreaId().equals(mktCampaignDO.getLanId().intValue())) {
                    count++;
                }
            }
            cityMap.put("count", count);
            noOperMapList.add(cityMap);
        }
        // 排序
        Collections.sort(noOperMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });


        Map<String, Object> marketParam = new HashMap<>();
        // 获取起止时间
        Map<String, Object> dateMap = getDate(params);
        marketParam.put("startDate", dateMap.get("startDate"));
        marketParam.put("endDate", dateMap.get("endDate"));
        //营销活动
        Map<String, Object> marketCountMap = new HashMap<>();
        marketParam.put("mktCampaignType", "1000");
        marketParam.put("statusCd", "(2002, 2006, 2007, 2008, 2010)"); // 总量
        List<MktCampaignDO> mktCampaignDOList = mktCampaignReportMapper.selectByStatus(marketParam);
        List<Map<String, Object>> operMapList = new ArrayList<>();
        // 活跃活动的总量
        int operCount = 0;
        for (SysArea sysArea : sysAreaList) {
            Map<String, Object> cityMap = new HashMap<>();
            cityMap.put("name", sysArea.getName());
            cityMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            int count = 0;
            for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                // 判断不是"不活跃活动"
                if (!noOperationIdList.contains(mktCampaignDO.getMktCampaignId())
                        && sysArea.getAreaId().equals(mktCampaignDO.getLanId().intValue())) {
                    count++;
                    operCount++;
                }
            }
            cityMap.put("count", count);
            operMapList.add(cityMap);
        }
        // 总量 =  活跃活动数量 + 不活跃活动数量
        int totalCount =  operCount + noOperCount;
        // 排序
        Collections.sort(operMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, Object> operMap = new HashMap<>();
        operMap.put("name", "有运营活动");
        operMap.put("count", operCount);
        operMap.put("percent", df.format(operCount * 100.0 / totalCount) + "%");
        operMap.put("city", operMapList);

        Map<String, Object> noOperMap = new HashMap<>();
        noOperMap.put("name", "无运营活动");
        noOperMap.put("count", noOperCount);
        noOperMap.put("percent", df.format( noOperCount * 100.0 / totalCount) + "%");
        noOperMap.put("city", noOperMapList);
        List<Map<String, Object>> operationMapList = new ArrayList<>();
        operationMapList.add(operMap);
        operationMapList.add(noOperMap);
        result.put("data", operationMapList);
        return result;
    }


    /**
     * 活动渠道
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getChannelInfo(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        //  List<Channel> channelList = contactChannelMapper.findChildListByTriggerType();
        List<Map<String, Object>> channelList = new ArrayList<>();
        Map<String, Object> channelListMap = xinNewAactivityService.activityThemeLevelAndChannel(params);
        channelList = (List<Map<String, Object>>) channelListMap.get("channel");
        List<Map<String, Object>> channelMapList = new ArrayList<>();
        for (Map<String, Object> channel : channelList) {
            Map<String, Object> channelMap =new HashMap<>();
            channelMap.put("name", channel.get("name"));
            channelMap.put("code", channel.get("type"));
            Long contactChlId =  (Long) channel.get("id");
            List<Long> initList = mktCamChlConfMapper.countCamByChannel(contactChlId);
            channelMap.put("count", initList.size());
            channelMapList.add(channelMap);
        }
        // 排序
        Collections.sort(channelMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });

        // 查询是否单渠道活动
        Map<String, Object> dateMap = getDate(params);
        Map<String, Object> channelParam = new HashMap<>();
        channelParam.put("startDate", dateMap.get("startDate"));
        channelParam.put("statusCd", "(2002, 2006, 2008, 2010)"); // 总量
        //查询单渠道协同活动
        channelParam.put("oneChannelFlg", "true");
        int oneChannelCount = mktCampaignReportMapper.countByStatus(channelParam);
        Map<String, Object> oneChannelMap = new HashMap<>();
        oneChannelMap.put("name", "单渠道活动");
        oneChannelMap.put("count", oneChannelCount);
        //查询渠道协同活动
        channelParam.put("oneChannelFlg", "false");
        int noOneChannelCount = mktCampaignReportMapper.countByStatus(channelParam);
        Map<String, Object> noOneChannelMap = new HashMap<>();
        noOneChannelMap.put("name", "渠道协同活动");
        noOneChannelMap.put("count", noOneChannelCount);
        List<Map<String, Object>> countList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        int totalCount = oneChannelCount + noOneChannelCount;
        // 获取百分比
        oneChannelMap.put("percent", df.format(oneChannelCount * 100.0 / totalCount) + "%");
        noOneChannelMap.put("percent", df.format(noOneChannelCount * 100.0 / totalCount) + "%");
        countList.add(oneChannelMap);
        countList.add(noOneChannelMap);
        resultMap.put("type", countList);
        resultMap.put("channel", channelMapList);
        result.put("data", resultMap);
        return result;
    }


    /**
     * 活动类型
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getTypeInfo(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        // 获取起止时间
        Map<String, Object> dateMap = getDate(params);
        params.put("startDate", dateMap.get("startDate"));
        params.put("endDate", dateMap.get("endDate"));
        Map<String, Object> sysAreaMap = sysAreaService.listCityByParentId(1);
        List<SysArea> sysAreaList = (List<SysArea>) sysAreaMap.get("sysAreaList");

        DecimalFormat df = new DecimalFormat("0.00");
        // 随销活动（实时营销活动）
        params.put("tiggerType", StatusCode.REAL_TIME_CAMPAIGN.getStatusCode());
        int realTimeCount = mktCampaignReportMapper.countByStatus(params);
        Map<String, Object> realTimeMap = new HashMap<>();
        realTimeMap.put("count", realTimeCount);
        realTimeMap.put("name", "随销");


        // 派单活动（批量营销活动）
        params.put("tiggerType", StatusCode.BATCH_CAMPAIGN.getStatusCode());
        int batchCount = mktCampaignReportMapper.countByStatus(params);
        // 总量=随销+派单
        int tableTotal = realTimeCount + batchCount;
        Map<String, Object> batchMap = new HashMap<>();
        batchMap.put("count", batchCount);
        batchMap.put("name", "派单");

        Map<String, Object> trilParamMap = new HashMap<>();
        trilParamMap.putAll(params);
        List<Map<String, Object>> trilMapList = new ArrayList<>();
        int labelListTotal = 0;
        // 标签取数
        trilParamMap.put("trilType", "2000"); // 试算类型
        // int labelCount = mktCampaignMapper.countByTrial(trilParamMap);
        List<MktCampaignDO> labelList = mktCampaignReportMapper.selectByTrial(trilParamMap);
        int labelCount = labelList.size();
        Map<String, Object> trilMap = new HashMap<>();
        trilMap.put("name", "派单活动 标签取数");
        trilMap.put("count", labelCount);
        List<Map<String, Object>> sysAreaMapList = new ArrayList<>();

        for (SysArea sysArea : sysAreaList) {
            Map<String, Object> cityMap = new HashMap<>();
            cityMap.put("name", sysArea.getName());
            cityMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            int count = 0;
            for (MktCampaignDO mktCampaignDO : labelList) {
                if (sysArea.getAreaId().equals(mktCampaignDO.getLanId().intValue())) {
                    count++;
                }
            }
            cityMap.put("count", count);
            sysAreaMapList.add(cityMap);
        }
        // 排序
        Collections.sort(sysAreaMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });
        trilMap.put("city", sysAreaMapList);


        // 清单导入
        trilParamMap.put("trilType", "1000");// 试算类型
        //  int listCount = mktCampaignMapper.countByTrial(trilParamMap);
        List<MktCampaignDO> trilList = mktCampaignReportMapper.selectByTrial(trilParamMap);
        int listCount = trilList.size();
        Map<String, Object> listMap = new HashMap<>();
        listMap.put("name", "派单活动 清单取数");
        listMap.put("count", listCount);
        if (tableTotal != 0) {
            trilMap.put("percent", df.format(labelCount * 100.0 / tableTotal) + "%");
            listMap.put("percent", df.format(listCount * 100.0 / tableTotal) + "%");
        } else {
            trilMap.put("percent", "0.00%");
            listMap.put("percent", "0.00%");
        }
        List<Map<String, Object>> listMapList = new ArrayList<>();
        for (SysArea sysArea : sysAreaList) {
            Map<String, Object> cityMap = new HashMap<>();
            cityMap.put("name", sysArea.getName());
            cityMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            int count = 0;
            for (MktCampaignDO mktCampaignDO : trilList) {
                if (sysArea.getAreaId().equals(mktCampaignDO.getLanId().intValue())) {
                    count++;
                }
            }
            cityMap.put("count", count);
            listMapList.add(cityMap);
        }
        // 排序
        Collections.sort(listMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });
        listMap.put("city", listMapList);

        trilMapList.add(trilMap);
        trilMapList.add(listMap);
        batchMap.put("classify", trilMapList);

        // 随销活动的classify
        Map<String, Object> reaListMap = new HashMap<>();
        reaListMap.put("name", "随销活动");
        reaListMap.put("count", realTimeCount);
        if (tableTotal != 0) {
            reaListMap.put("percent", df.format(realTimeCount * 100.0 / tableTotal) + "%");
        } else {
            reaListMap.put("percent", "0.00%");
        }
        List<Map<String, Object>> realMapList = new ArrayList<>();
        realMapList.add(reaListMap);
        realTimeMap.put("classify", realMapList);


        // 混合活动（混合营销活动）
        params.put("tiggerType", StatusCode.MIXTURE_CAMPAIGN.getStatusCode());
        int mixtureCount = mktCampaignMapper.countByStatus(params);
        Map<String, Object> mixtureMap = new HashMap<>();
        mixtureMap.put("count", mixtureCount);
        mixtureMap.put("name", "混合活动");

        //     int tableTotal = realTimeCount + batchCount + mixtureCount;
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("count", tableTotal);
        totalMap.put("name", "总量");
        if (tableTotal != 0) {
            totalMap.put("percent", df.format(tableTotal * 100.0 / tableTotal) + "%");
            realTimeMap.put("percent", df.format(realTimeCount * 100.0 / tableTotal) + "%");
            batchMap.put("percent", df.format(batchCount * 100.0 / tableTotal) + "%");
            mixtureMap.put("percent", df.format(mixtureCount * 100.0 / tableTotal) + " %");
        } else {
            realTimeMap.put("percent", "0.00%");
            batchMap.put("percent", "0.00%");
            mixtureMap.put("percent", "0.00%");
        }
        List<Map<String, Object>> tableDateList = new ArrayList<>();
        // tableDateList.add(totalMap);
        tableDateList.add(realTimeMap);   // 随销活动
        tableDateList.add(batchMap);      // 派单活动
        // tableDateList.add(mixtureMap);
        resultMap.put("data", tableDateList);
        return resultMap;
    }


    /**
     * 时间分布
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getTimeInfo(Map<String, Object> params) {
        // 判断当前季度
        Integer currentMonth = DateUtil.getCurrentMonth();
        int num = 0;
        if (currentMonth < 4) {
            num = 1;
        } else  if (currentMonth > 3 && currentMonth < 7) {
            num = 2;
        } else  if (currentMonth > 6 && currentMonth < 10) {
            num = 3;
        } else  if (currentMonth > 9 && currentMonth < 13) {
            num = 4;
        }

        Map<String, Object> firstMap = new HashMap<>();
        Map<String, Object> secondMap = new HashMap<>();
        Map<String, Object> thirdMap = new HashMap<>();
        Map<String, Object> fouthMap = new HashMap<>();
        int first = 0;
        int second = 0;
        int third = 0;
        int fouth = 0;
        int total = 0;
        for (int i = 1; i <= num ; i++) {
            // 第一季度
            if (i == 1) {
                params.put("startDate", "2019-01-01");
                params.put("endDate", "2019-03-31");
                Map<String, Object> dateMap = getDate(params);
                params.put("startDate", (Date) dateMap.get("startDate"));
                params.put("endDate", (Date) dateMap.get("endDate"));
                first = mktCampaignReportMapper.countByTime(params);
                //  total += first;
                firstMap.put("name", "一季度");
                firstMap.put("count", first);
            } else  if (i == 2) {
                params.put("startDate", "2019-04-01");
                params.put("endDate", "2019-06-30");
                Map<String, Object> dateMap = getDate(params);
                params.put("startDate", (Date) dateMap.get("startDate"));
                params.put("endDate", (Date) dateMap.get("endDate"));
                second = mktCampaignReportMapper.countByTime(params);
                total += second;
                secondMap.put("name", "二季度");
                secondMap.put("count", second);
            } else if (i == 3) {
                params.put("startDate", "2019-07-01");
                params.put("endDate", "2019-09-30");
                Map<String, Object> dateMap = getDate(params);
                params.put("startDate", (Date) dateMap.get("startDate"));
                params.put("endDate", (Date) dateMap.get("endDate"));
                third = mktCampaignReportMapper.countByTime(params);
                total += third;
                thirdMap.put("name", "三季度");
                thirdMap.put("count", third);
            } else if (i == 4) {
                params.put("startDate", "2019-010-01");
                params.put("endDate", "2019-12-31");
                Map<String, Object> dateMap = getDate(params);
                params.put("startDate", (Date) dateMap.get("startDate"));
                params.put("endDate", (Date) dateMap.get("endDate"));
                fouth = mktCampaignReportMapper.countByTime(params);
                total += fouth;
                fouthMap.put("name", "四季度");
                fouthMap.put("count", fouth);
            }
        }

        DecimalFormat df = new DecimalFormat("0.00");
        //  firstMap.put("percent", df.format(first * 100.0 / total) + "%");
        secondMap.put("percent", df.format(second * 100.0 / total) + "%");
        thirdMap.put("percent", df.format(third * 100.0 / total) + "%");
        fouthMap.put("percent", df.format(fouth * 100.0 / total) + "%");
        List<Map<String, Object>> timeMapList = new ArrayList<>();
        //    timeMapList.add(firstMap);
        timeMapList.add(secondMap);
        timeMapList.add(thirdMap);
        timeMapList.add(fouthMap);
        Map<String, Object> result = new HashMap<>();
        result.put("data", timeMapList);
        return result;
    }



    /**
     * 活动区域接口
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getRegionInfo(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        params.put("startDate", DateUtil.string2DateTime4Day(params.get("startDate").toString()));
        if( params.containsKey("endDate")){
            params.remove("endDate");
        }
        Map<String, Object> resultData = new HashMap<>();

        // 饼图
        List<Map> localList = new ArrayList<>();
        Map<String, Object> map = mktCampaignReportMapper.selectCamSumByArea1(params);
        Integer group = ((Long) map.get("C1")).intValue();
        Integer province = ((Long) map.get("C2")).intValue();
        Integer city =  ((Long) map.get("C3")).intValue();
        Integer district = ((Long) map.get("C4")).intValue();

        Integer sum = group + province + city + district;

        localList.add(putParam(new HashMap<>(), "省级", province, sum));
        localList.add(putParam(new HashMap<>(), "地市级", city, sum));
        localList.add(putParam(new HashMap<>(), "区县级", district, sum));
        localList.add(putParam(new HashMap<>(), "集团级", group, sum));

        // 柱状图
        Map<String, Object> sysAreaMap = sysAreaService.listCityByParentId(1);
        List<SysArea> sysAreaList = (List<SysArea>) sysAreaMap.get("sysAreaList");
        // 总量
        List<Map<String, Object>> totalMapList = new ArrayList<>();
        // 地市
        List<Map<String, Object>> cityMapList = new ArrayList<>();
        // 区县
        List<Map<String, Object>> countyMapList = new ArrayList<>();
        for (SysArea sysArea : sysAreaList) {
            params.put("statusCd", "(2002, 2006, 2008, 2010)");
            params.put("lanId", sysArea.getAreaId());
            params.put("regionFlg", "('C3')");
            int c3 = mktCampaignReportMapper.countByStatus(params);
            Map<String, Object> citysMap = new HashMap();
            citysMap.put("name", sysArea.getName());
            citysMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            citysMap.put("count", c3);
            cityMapList.add(citysMap);

            params.put("regionFlg", "('C4', 'C5')");
            int c4c5 = mktCampaignReportMapper.countByStatus(params);
            Map<String, Object> countyMap = new HashMap();
            countyMap.put("name", sysArea.getName());
            countyMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            countyMap.put("count", c4c5);
            countyMapList.add(countyMap);

            Map<String, Object> totalMap = new HashMap();
            totalMap.put("name", sysArea.getName());
            totalMap.put("orgid", OrgEnum.getLanIdByName(sysArea.getName()));
            totalMap.put("count", c3 + c4c5);
            totalMapList.add(totalMap);
        }

        Map<String, Object> cityCountMap = new HashMap();

        Collections.sort(totalMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });
        Collections.sort(cityMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });
        Collections.sort(countyMapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer count1 = (Integer) o1.get("count");
                Integer count2 = (Integer) o2.get("count");
                return count2.compareTo(count1);
            }
        });
        cityCountMap.put("total", totalMapList);
        cityCountMap.put("city", cityMapList);
        cityCountMap.put("county", countyMapList);
        resultData.put("city", cityCountMap);
        resultMap.put("data", resultData);
        return resultMap;
    }


    public Map putParam(Map param, String level, Integer num1, Integer num2) {
        param.put("name", level);
        param.put("count", num1.toString());
        if (num1 == null || num1 == 0) {
            param.put("percent", "0.00%");
        } else {
            param.put("percent", calculatePercentage(num1, num2));
        }
        return param;
    }


    public String calculatePercentage(Integer num1, Integer num2) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result + "%";
    }


    //统计头部信息
    private List<Map<String, Object>> countHeadInfo(Map<String, Object> headParam) {
        if(headParam.containsKey("endDate")){
            headParam.remove("endDate");
        }
        headParam.put("statusCd", "(2002, 2006, 2008, 2010)"); // 总量
        int totalCount = mktCampaignReportMapper.countByStatus(headParam);
        List<Map<String, Object>> countList = new ArrayList<>();
        Map<String, Object> countMapTotal = new HashMap<>();
        countMapTotal.put("type", "总量");
        countMapTotal.put("num", totalCount);
        Map<String, Object> countMapOn = new HashMap<>();
        headParam.put("statusCd", "(2002， 2008)"); // 在线的
        headParam.put("endDate", new Date());
        int onCount = mktCampaignReportMapper.countByStatus(headParam);
        countMapOn.put("type", "在线");
        countMapOn.put("num", onCount);
        Map<String, Object> countMapOff = new HashMap<>();
        countMapOff.put("type", "下线");
        countMapOff.put("num", (totalCount - onCount));
        countList.add(countMapTotal);
        countList.add(countMapOn);
        countList.add(countMapOff);
        return countList;
    }


    // 获取起止时间
    private Map<String, Object> getDate(Map<String, Object> params) {
        Map<String, Object> dateMap = new HashMap<>();
        Date startDate = null;
        Date endDate = null;
        if (params.get("startDate") != null && !"".equals(params.get("startDate"))) {
            String startTime = params.get("startDate").toString();
            String[] timeArr = startTime.split("-");
            startDate = string2DateTime4Day(getFisrtDayOfMonth(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1])));
            dateMap.put("startDate", startDate);
        }
        if (params.get("endDate") != null && !"".equals(params.get("endTime"))) {
            String endTime = params.get("endDate").toString();
            String[] timeArr = endTime.split("-");
            endDate = string2DateTime4Day(getLastDayOfMonth(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1])));
            dateMap.put("endDate", endDate);
        }
        return dateMap;
    }
}