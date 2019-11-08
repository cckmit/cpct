package com.zjtelcom.cpct.service.impl.report;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignReportMapper;
import com.zjtelcom.cpct.service.report.MktCampaingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private MktCampaignReportMapper mktCampaignReportMapper;

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
        if (params.get("startDate") != null && !"".equals(params.get("startDate"))) {
            String startTime = params.get("startDate").toString();
            String[] timeArr = startTime.split("-");
            startDate = string2DateTime4Day(getFisrtDayOfMonth(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1])));
            headParam.put("startDate", startDate);
        }
        if (params.get("endDate") != null && !"".equals(params.get("endTime"))) {
            String endTime = params.get("endDate").toString();
            String[] timeArr = endTime.split("-");
            endDate = string2DateTime4Day(getLastDayOfMonth(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1])));
            headParam.put("endDate", endDate);
        }
        // 总活动量
        List<Map> headList = new ArrayList<>();

        // 总量
        Map<String, Object> totalCountMap = new HashMap<>();
        List<Map<String, Object>> totalList = countHeadInfo(headParam);
        totalCountMap.put("name", "总活动数");
        totalCountMap.put("count", totalList);

        //营销活动
        Map<String, Object> marketCountMap = new HashMap<>();
        headParam.put("mktCampaignType", "(1000, 2000, 3000, 4000)");
        List<Map<String, Object>> marketList = countHeadInfo(headParam);
        marketCountMap.put("name", "营销活动");
        marketCountMap.put("count", marketList);
        headList.add(marketCountMap);

        // 服务活动
        Map<String, Object> serviceCountMap = new HashMap<>();
        headParam.put("mktCampaignType", "(5000)");
        List<Map<String, Object>> serviceList = countHeadInfo(headParam);
        serviceCountMap.put("name", "服务活动");
        serviceCountMap.put("count", serviceList);
        headList.add(serviceCountMap);

        // 服务随销活动
        Map<String, Object> serMarkCountMap = new HashMap<>();
        headParam.put("mktCampaignType", "(6000)");
        List<Map<String, Object>> serMarkList = countHeadInfo(headParam);
        serMarkCountMap.put("name", "服务随销活动");
        serMarkCountMap.put("count", serMarkList);
        headList.add(serMarkCountMap);

        resultMap.put("head", headList);
        resultMap.put("code", CommonConstant.CODE_SUCCESS);
        resultMap.put("msg", "查询成功");
        return resultMap;
    }

    private List<Map<String, Object>> countHeadInfo(Map<String, Object> headParam){
        headParam.put("statusCd", "(2002, 2003, 2004, 2006, 2007, 2008, 2010)"); // 总量
        int totalCount = mktCampaignReportMapper.countByStatus(headParam);
        List<Map<String, Object>> countList = new ArrayList<>();
        Map<String, Object> countMapTotal = new HashMap<>();
        countMapTotal.put("type", "总量");
        countMapTotal.put("num", totalCount);
        Map<String, Object> countMapOn = new HashMap<>();
        headParam.put("statusCd", "(2002, 2003, 2004, 2006, 2008, 2010)"); // 在线的
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
}