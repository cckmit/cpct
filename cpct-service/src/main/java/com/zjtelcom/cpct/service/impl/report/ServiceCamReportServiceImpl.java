package com.zjtelcom.cpct.service.impl.report;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignReportMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.service.report.ServiceCamReportService;
import com.zjtelcom.cpct.util.AcitvityParams;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.MapUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.service.impl.report.ActivityStatisticsServiceImpl.getPercentFormat;

@Service
@Transactional
@Log4j
public class ServiceCamReportServiceImpl implements ServiceCamReportService {
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCampaignReportMapper mktCampaignReportMapper;
    @Autowired(required = false)
    private IReportService iReportService;

    /**
     * 服务活动/服务随销活动报表
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> serviceCamInfo(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        String mktCampaignType = MapUtil.getString(param.get("mktCampaignType"));
        List<Map<String,Object>> preNet = new ArrayList<>();
        List<Map<String,Object>> inNet = new ArrayList<>();
        List<Map<String,Object>> outNet = new ArrayList<>();
        param.put("serviceType","1000");
        preNet = mktCampaignReportMapper.selectServiceCamList(param);
        param.put("serviceType","2000");
        inNet = mktCampaignReportMapper.selectServiceCamList(param);
        param.put("serviceType","3000");
        outNet = mktCampaignReportMapper.selectServiceCamList(param);

        List<Long> campaignList = mktCampaignReportMapper.selectCamListByCampaignType(param);
        param.put("mktCampaignId",ChannelUtil.idList2String(campaignList));
        param.put("rptType","2");
        param.put("sortColumn","contactNum");
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(param);
        log.info("服务活动入参："+JSON.toJSONString(paramMap));

        Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
        if (stringObjectMap1.get("resultCode").equals("1000")){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        List<Map<String,String>> rptList = new ArrayList<>();
        for (Map<String, String> rptMap : rptList) {
            for (Map<String, Object> pre : preNet) {
                if (pre.get("campaignId").toString().equals(rptMap.get("mktCampaignId"))){
                    pre.put("contactNumber",rptMap.get("contactNum"));
                    pre.put("statistics",statistics(rptMap));
                    break;
                }
            }
            for (Map<String, Object> pre : inNet) {
                if (pre.get("campaignId").toString().equals(rptMap.get("mktCampaignId"))){
                    pre.put("contactNumber",rptMap.get("contactNum"));
                    pre.put("statistics",statistics(rptMap));
                    break;
                }
            }
            for (Map<String, Object> pre : outNet) {
                if (pre.get("campaignId").toString().equals(rptMap.get("mktCampaignId"))){
                    pre.put("contactNumber",rptMap.get("contactNum"));
                    pre.put("statistics",statistics(rptMap));
                    break;
                }
            }
        }
        Map<String,Object> resultData = new HashMap<>();
        resultData.put("preNet",preNet);
        resultData.put("inNet",inNet);
        resultData.put("outNet",outNet);
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",resultData);
        return result;
    }

    private  List<Map<String,Object>> statistics(Map<String,String> rptMap){
        List<Map<String, Object>> statisicts = new ArrayList<>();
        //添加框架活动是否字活动
        rptMap.put("yesOrNo", "1");
        Iterator<String> iter = rptMap.keySet().iterator();
        while (iter.hasNext()) {
            HashMap<String, Object> msgMap = new HashMap<>();
            String key = iter.next();
            Object o = rptMap.get(key);
            if ("".equals(o) || "null".equals(o) || null == o){
                o = 0+"";
            }
            if (key.equals("orderNum")) {
                msgMap.put("name", "派单数");
                msgMap.put("nub", o);
                statisicts.add(msgMap);
            }
            if (key.equals("acceptOrderNum")) {
                msgMap.put("name", "接单数");
                msgMap.put("nub", o);
                statisicts.add(msgMap);
            }
            if (key.equals("outBoundNum")) {
                msgMap.put("name", "外呼数");
                msgMap.put("nub", o);
                statisicts.add(msgMap);
            }
            if (key.equals("orderSuccessNum")) {
                msgMap.put("name", "成功数");
                msgMap.put("nub", o);
                statisicts.add(msgMap);
            }
            if (key.equals("acceptOrderRate")) {
                //转换成百分比 保留二位小数位
                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                msgMap.put("name", "接单率");
                msgMap.put("nub", percentFormat);
                statisicts.add(msgMap);
            }
            if (key.equals("outBoundRate")) {
                msgMap.put("name", "外呼率");
                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                msgMap.put("nub", percentFormat);
                statisicts.add(msgMap);
            }
            if (key.equals("orderSuccessRate")) {
                msgMap.put("name", "转化率");
                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                msgMap.put("nub", percentFormat);
                statisicts.add(msgMap);
            }
            if (key.equals("revenueReduceNum")) {
                msgMap.put("name", "收入低迁数");
                msgMap.put("nub", o);
                statisicts.add(msgMap);
            }
            if (key.equals("revenueReduceRate")) {
                msgMap.put("name", "收入低迁率");
                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                msgMap.put("nub", percentFormat);
                statisicts.add(msgMap);
            }
            if (key.equals("orgChannelRate")) {
                msgMap.put("name", "门店有销率");
                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                msgMap.put("nub", percentFormat);
                statisicts.add(msgMap);
            }
        }
        return statisicts;
    }
}
