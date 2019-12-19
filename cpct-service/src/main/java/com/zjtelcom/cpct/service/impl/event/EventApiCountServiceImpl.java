package com.zjtelcom.cpct.service.impl.event;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.ChannelMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.event.EventApiCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/10/29 11:33
 * @version: V1.0
 */
@Service
@Transactional
public class EventApiCountServiceImpl implements EventApiCountService {

    private static final Logger logger = LoggerFactory.getLogger(EventApiCountServiceImpl.class);

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    @Autowired
    private ContactEvtMapper contactEvtMapper;

    @Autowired
    private ContactChannelMapper contactChannelMapper;

    @Autowired(required = false)
    private IReportService iReportService;

    @Override
    public Map<String, Object> eventApiCount(Map<String, Object> param) {
        String startDate = (String) param.get("startDate");
        StringBuilder startDateStr = new StringBuilder();
        if (startDate != null && !"".equals(startDate)) {
            String[] startDateArr = startDate.split("-");
            for (String serStr : startDateArr) {
                startDateStr.append(serStr);
            }
        }
        String endDate = (String) param.get("endDate");
        StringBuilder endDateStr = new StringBuilder();
        if (endDate != null && !"".equals(endDate)) {
            String[] endDateArr = endDate.split("-");
            for (String serStr : endDateArr) {
                endDateStr.append(serStr);
            }
        }
        List<String> channelCodeList = (List<String>) param.get("channelCodeList");
        List<Long> mktCampaignIdList = (List<Long>) param.get("mktCampaignIdList");
        List<String> eventCodeList = (List<String>) param.get("eventCodeList");
        Integer pageSize = (Integer) param.get("pageSize");
        Integer currenPage = (Integer) param.get("currenPage");

        StringBuilder initIdStr = new StringBuilder();
        if (mktCampaignIdList != null && mktCampaignIdList.size() > 0 ) {
            if (Integer.valueOf(0).equals(mktCampaignIdList.get(0))) {
                initIdStr.append(0);
            } else {
                List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.selectBatch(mktCampaignIdList);
                for (MktCampaignDO mktCampaignDO : mktCampaignDOList) {
                    initIdStr.append(mktCampaignDO.getInitId() + ",");
                }
                // 移除最后一个","
                initIdStr.delete(initIdStr.length() - 1, initIdStr.length());
            }
        } else {
            initIdStr.append("all");
        }
        StringBuilder channelCodeStr = new StringBuilder();
        if (channelCodeList != null && channelCodeList.size() > 0) {
            if ("0".equals(channelCodeList.get(0))) {
                channelCodeStr.append(0);
            } else {
                for (String channelCode : channelCodeList) {
                    channelCodeStr.append(channelCode + ",");
                }
                // 移除最后一个","
                channelCodeStr.delete(channelCodeStr.length() - 1, channelCodeStr.length());
            }
        } else {
            channelCodeStr.append("all");
        }


        StringBuilder eventCodeStr = new StringBuilder();
        if (eventCodeList != null && eventCodeList.size() > 0) {
            if ("0".equals(eventCodeList.get(0))) {
                eventCodeStr.append(0);
            } else {
                for (String eventCode : eventCodeList) {
                    eventCodeStr.append(eventCode + ",");
                }
                // 移除最后一个","
                eventCodeStr.delete(eventCodeStr.length() - 1, eventCodeStr.length());
            }
        } else {
            eventCodeStr.append("all");
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startDate", startDateStr.toString());
        paramMap.put("endDate", endDateStr.toString());
        paramMap.put("channelCode", channelCodeStr.toString());
        paramMap.put("mktCampaignIds", initIdStr.toString());
        paramMap.put("eventCode", eventCodeStr.toString());
        paramMap.put("pageSize", pageSize.toString());
        paramMap.put("currenPage", currenPage.toString());

        Map<String, Object> resultMap = new HashMap<>();

        logger.info("入参为paramMap：" + JSON.toJSONString(paramMap));
        // 调用协同中心的接口
        Map<String, Object> returnMap = null;
        try {
            returnMap = iReportService.queryRptEventInstList(paramMap);
        } catch (Exception e) {
            return returnMap;
        }

        logger.info("出参数为returnMap：" + JSON.toJSONString(returnMap));

        if("1".equals(returnMap.get("resultCode"))){
            resultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        } else {
            resultMap.put("resultCode", CommonConstant.CODE_FAIL);
        }
        resultMap.put("resultMsg", returnMap.get("resultMsg"));
        resultMap.put("pageSize", returnMap.get("pageSize"));
        resultMap.put("currenPage", returnMap.get("currenPage"));
        resultMap.put("total", returnMap.get("total"));
        List<Map<String, String>> eventCountList = (List<Map<String, String>>) returnMap.get("eventCountList");

        List<Map<String, Object>> eventCountMapList = new ArrayList<>();
        for (Map<String, String> eventCount : eventCountList) {

            Map<String, Object> eventCountMap = new HashMap<>();
            if (eventCount.get("mktCampaignId") != null) {
                Long initId = Long.valueOf(eventCount.get("mktCampaignId"));
                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectCampaignByInitId(initId);
                if (mktCampaignDO != null) {
                    eventCountMap.put("mktCampaignNbr", mktCampaignDO.getMktActivityNbr());    //活动编码
                    eventCountMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName()); //活动名称
                }
            }
            if (eventCount.get("eventCode") != null) {
                ContactEvt eventCode = contactEvtMapper.getEventByEventNbr(eventCount.get("eventCode"));
                if (eventCode != null) {
                    eventCountMap.put("eventCode", eventCount.get("eventCode"));    // 事件编码
                    eventCountMap.put("eventName", eventCode.getContactEvtName());  // 事件名称
                }
            }
            if (eventCount.get("channelCode") != null) {
                Channel channel = contactChannelMapper.selectByCode(eventCount.get("channelCode"));
                if (channel != null) {
                    eventCountMap.put("channelCode", eventCount.get("channelCode"));  // 渠道编码
                    eventCountMap.put("channelName", channel.getChannelName()); // 渠道名称
                }
            }
            try {
                eventCountMap.put("orderDate", strToDateFormat(eventCount.get("orderDate"))); // 统计日期
            } catch (ParseException e) {
                logger.error("orderDate事件转换出错");
            }

            Integer instNum = Integer.valueOf(eventCount.get("instNum"));
            Integer orderNum = Integer.valueOf(eventCount.get("orderNum"));
            eventCountMap.put("instNum", instNum);    // 接入量
            eventCountMap.put("orderNum", orderNum);  // 命中量
            DecimalFormat df = new DecimalFormat("0.00");
            eventCountMap.put("hitRate", df.format(orderNum * 100.0 / instNum) + "%");
            eventCountMapList.add(eventCountMap);
        }
        resultMap.put("eventCountMapList", eventCountMapList);
        return resultMap;
    }



    /**
     * 将字符串格式yyyyMMdd的字符串转为日期，格式"yyyy-MM-dd"
     *
     * @param date 日期字符串
     * @return 返回格式化的日期
     * @throws
     */
    public static String strToDateFormat(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        formatter.setLenient(false);
        Date newDate= formatter.parse(date);
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(newDate);
    }
}