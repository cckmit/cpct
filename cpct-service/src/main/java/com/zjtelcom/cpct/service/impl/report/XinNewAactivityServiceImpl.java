package com.zjtelcom.cpct.service.impl.report;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.service.report.XinNewAactivityService;
import com.zjtelcom.cpct.util.AcitvityParams;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

public class XinNewAactivityServiceImpl implements XinNewAactivityService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XinNewAactivityServiceImpl.class);

    @Autowired(required = false)
    private IReportService iReportService;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    /**
     *  新活动报表 主题活动
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> activityTheme(Map<String, Object> params) {
        HashMap<String, Object> resultMap = new HashMap<>();
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        List<Map<String, String>> campaignTheme = sysParamsMapper.listParamsByKey("CAMPAIGN_THEME");
        String date = params.get("startDate").toString();
        String type = paramMap.get("mktCampaignType").toString();
        //总数
        Integer count = mktCampaignMapper.getCountFromActivityTheme(date,type);
        if (campaignTheme.size()>0 && campaignTheme!=null){
            for (Map<String, String> stringStringMap : campaignTheme) {
                String value = stringStringMap.get("value");
                //每个主题个数
                List<MktCampaignDO> mktCampaignList = mktCampaignMapper.selectCampaignTheme(value, date, type);
                StringBuilder stringBuilder = new StringBuilder();
                if (mktCampaignList!=null && mktCampaignList.size()>0){
                    for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                        stringBuilder.append(mktCampaignDO.getInitId()).append(",");
                    }
                }
                //多个id  “，”拼接 去除最后的一个 ，
                String substring = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
                paramMap.put("mktCampaignId", substring);
                //维度 按活动
                paramMap.put("rptType", 2);
                paramMap.put("pageSize","5");
                //按转换率排序去前五 top5
                Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
                logger.info("新活动报表 主题活动 按转换率排序去前五 top5:"+JSON.toJSONString(stringObjectMap));
                //按收入提高排序取前五
                paramMap.put("sortColumn","incomeUp");
                Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
                logger.info("新活动报表 主题活动 按收入提高排序取前五:"+JSON.toJSONString(stringObjectMap1));
                //主题百分比
                double num = mktCampaignList.size() / count;
                System.out.println("主题百分比是否正常"+num);
            }
        }
        return null;
    }


    /**
     * 客触数
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> contactNumber(Map<String, Object> params) {
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        //统计维度(0:按渠道，1按地市) 不用就不传
        paramMap.put("rptType","1");
        //按客触数排序
        paramMap.put("sortColumn","contactNum");
        //查询总数 解析
        Map<String,Object> resultMap = iReportService.queryRptOrder(paramMap);

        //查询出来后按地市和渠道排序
        //地市(ALL表示所有,多个用逗号隔开) 添加11个地市的orgid
        paramMap.put("orglevel2","800000000009,800000000010,800000000011,800000000012,800000000013,800000000014,800000000015,800000000016,800000000017,800000000018,800000000020");
        //查询地市排名
        Map<String,Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 客触数 查询地市排名:"+JSON.toJSONString(stringObjectMap));
        //按渠道排序
        paramMap.put("rptType","0");
        paramMap.put("sortColumn","channel");
        //查询渠道排序
        Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 客触数 查询渠道排序:"+JSON.toJSONString(stringObjectMap1));

        return null;
    }

    /**
     * 转换率
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> orderSuccessRate(Map<String, Object> params) {
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        //统计维度(0:按渠道，1按地市) 不用就不传
        paramMap.put("rptType","2");
        paramMap.put("pageSize","5");
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
//        for 上面的返回接口list
        logger.info("活动报表查询接口:orderSuccessRate"+stringObjectMap);
        //按地市
        paramMap.put("rptType","1");
        paramMap.put("orglevel2","all");
        Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 转换率 按地市:"+JSON.toJSONString(stringObjectMap1));
        //按渠道
        paramMap.put("rptType","0");
        paramMap.put("channelCode","all");
        Map<String, Object> stringObjectMap2 = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 转换率 按渠道:"+JSON.toJSONString(stringObjectMap2));

        List<Map<String, Object>> data = new ArrayList<>();
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            Object rptOrderList = stringObjectMap.get("rptOrderList");
            if (rptOrderList!=null && ""!=rptOrderList){
                data = (List<Map<String, Object>>) stringObjectMap.get("rptOrderList");

            }
        } else {
            Object reqId = stringObjectMap.get("reqId");
            stringObjectMap.put("resultCode", CODE_FAIL);
            stringObjectMap.put("resultMsg", "查询无结果 queryRptBatchOrder error :" + reqId.toString());
        }
        return null;
    }

    /**
     * 收入拉动
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> incomePull(Map<String, Object> params) {
        //按地市
        params.put("rptType","1");
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        //查询一条数据 返回总数
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 收入拉动 查询一条数据 返回总数:"+JSON.toJSONString(stringObjectMap));
        //返回多条 按活动查询
        paramMap.put("rptType","2");
        //top5
        paramMap.put("pageSize","5");
        //收入提高
        paramMap.put("sortColumn","incomeU");
        //收入拉动top5
        Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 收入拉动 收入拉动top5:"+JSON.toJSONString(stringObjectMap1));
        //每个活动需要按地市和渠道排序 取活动id查询地市信息 和渠道信息

        //按地市
        paramMap.put("rptType","1");
        paramMap.put("orglevel2","all");
        Map<String, Object> stringObjectMap2 = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 收入拉动 按地市:"+JSON.toJSONString(stringObjectMap2));

        //按渠道
        paramMap.put("rptType","0");
        paramMap.put("channelCode","all");
        Map<String, Object> stringObjectMap3 = iReportService.queryRptOrder(paramMap);
        logger.info("新活动报表 收入拉动 按渠道:"+JSON.toJSONString(stringObjectMap3));
        return null;
    }


}
