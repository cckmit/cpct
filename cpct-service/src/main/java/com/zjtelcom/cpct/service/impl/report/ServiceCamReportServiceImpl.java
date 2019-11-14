package com.zjtelcom.cpct.service.impl.report;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignReportMapper;
import com.zjtelcom.cpct.dao.channel.ChannelMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.OrganizationMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.enums.OrgEnum;
import com.zjtelcom.cpct.service.report.ServiceCamReportService;
import com.zjtelcom.cpct.util.AcitvityParams;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.MapUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
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
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private ContactChannelMapper channelMapper;



    public String fun2(double f) {
        String s = String.format("%.2f", f);
        return s;
    }
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

        Map<String,String> map = new HashMap<>();
        map.put("mktCampaignId",ChannelUtil.idList2String(campaignList));
        Map<String, Object> taskNum = iReportService.serviceTaskNum(map);
//        Map<String, Object> taskNum = (Map<String, Object>) param.get("task");
        if (taskNum.get("resultCode").equals("1000")){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
//        Map<String, Object> stringObjectMap1 = (Map<String, Object>) param.get("map");
        if (stringObjectMap1.get("resultCode").equals("1000")){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        log.info("【服务活动返回】serviceTaskNum："+JSON.toJSONString(taskNum));
        log.info("【服务活动返回】queryRptOrder："+JSON.toJSONString(stringObjectMap1));
        List<Map<String,Object>> taskNumList = (List<Map<String,Object>>) taskNum.get("rptServCampaignList");
        List<Map<String,Object>> rptList = (List<Map<String,Object>>) stringObjectMap1.get("rptOrderList");

        for (Map<String, Object> pre : preNet) {
            pre.put("startTime",DateUtil.date2StringDateForDay((Date) pre.get("startTime")));
            pre.put("endTime",DateUtil.date2StringDateForDay((Date) pre.get("endTime")));
            for (Map<String, Object> stringMap : taskNumList) {
                if (pre.get("initId").toString().equals(stringMap.get("mktCampaignId").toString())) {
                    pre.put("contactNumber", stringMap.get("taskNum"));
                    break;
                }
            }
            for (Map<String, Object> rptMap : rptList) {
                if (pre.get("initId").toString().equals(rptMap.get("mktCampaignId").toString())) {
                    rptMap.put("contactRate",getPercentFormat(Double.valueOf(rptMap.get("contactRate").toString()),2,2)); //转化率
                    double v = Double.valueOf(rptMap.get("incomeUp").toString()) + Double.valueOf(rptMap.get("incomeDown").toString());
                    rptMap.put("income",fun2(v));
                    String rate = "";
                    if (rptMap.get("revenueReduceRate")!=null && !rptMap.get("revenueReduceRate").toString().equals("") ){
                        if (rptMap.get("revenueReduceRate").toString().contains("%")){
                            rate = rptMap.get("revenueReduceRate").toString();
                        }
                        rate = getPercentFormat(Double.valueOf(rptMap.get("revenueReduceRate").toString()),2,2);
                    }
                    rptMap.put("revenueReduceRate",rate);
                    pre.put("statistics", rptMap);
                    break;
                }
            }
            pre.putIfAbsent("contactNumber",0);
        }
        for (Map<String, Object> pre : inNet) {
            pre.put("startTime",DateUtil.date2StringDateForDay((Date) pre.get("startTime")));
            pre.put("endTime",DateUtil.date2StringDateForDay((Date) pre.get("endTime")));
            for (Map<String, Object> stringMap : taskNumList) {
                if (pre.get("initId").toString().equals(stringMap.get("mktCampaignId").toString())) {
                    pre.put("contactNumber", stringMap.get("taskNum"));
                    break;
                }
            }
            for (Map<String, Object> rptMap : rptList) {
                if (pre.get("initId").toString().equals(rptMap.get("mktCampaignId").toString())) {
                    rptMap.put("contactRate",getPercentFormat(Double.valueOf(rptMap.get("contactRate").toString()),2,2)); //转化率
                    double v = Double.valueOf(rptMap.get("incomeUp").toString()) + Double.valueOf(rptMap.get("incomeDown").toString());
                    rptMap.put("income",fun2(v));
                    String rate = "";
                    if (rptMap.get("revenueReduceRate")!=null && !rptMap.get("revenueReduceRate").toString().equals("") ){
                        if (rptMap.get("revenueReduceRate").toString().contains("%")){
                            rate = rptMap.get("revenueReduceRate").toString();
                        }
                        rate = getPercentFormat(Double.valueOf(rptMap.get("revenueReduceRate").toString()),2,2);
                    }
                    rptMap.put("revenueReduceRate",rate);
                    pre.put("statistics", rptMap);
                    break;
                }
            }
            pre.putIfAbsent("contactNumber",0);
        }
        for (Map<String, Object> pre : outNet) {
            pre.put("startTime",DateUtil.date2StringDateForDay((Date) pre.get("startTime")));
            pre.put("endTime",DateUtil.date2StringDateForDay((Date) pre.get("endTime")));
            for (Map<String, Object> stringMap : taskNumList) {
                if (pre.get("initId").toString().equals(stringMap.get("mktCampaignId").toString())){
                    pre.put("contactNumber",stringMap.get("taskNum"));
                    break;
                }
            }
            for (Map<String, Object> rptMap : rptList) {
                if (pre.get("initId").toString().equals(rptMap.get("mktCampaignId").toString())){
                    rptMap.put("contactRate",getPercentFormat(Double.valueOf(rptMap.get("contactRate").toString()),2,2)); //转化率
                    double v = Double.valueOf(rptMap.get("incomeUp").toString()) + Double.valueOf(rptMap.get("incomeDown").toString());
                    rptMap.put("income",fun2(v));
                    String rate = "";
                    if (rptMap.get("revenueReduceRate")!=null && !rptMap.get("revenueReduceRate").toString().equals("") ){
                        if (rptMap.get("revenueReduceRate").toString().contains("%")){
                            rate = rptMap.get("revenueReduceRate").toString();
                        }
                        rate = getPercentFormat(Double.valueOf(rptMap.get("revenueReduceRate").toString()),2,2);
                    }
                    rptMap.put("revenueReduceRate",rate);
                    pre.put("statistics",rptMap);
                    break;
                }
            }
            pre.putIfAbsent("contactNumber",0);
        }

        Collections.sort(preNet, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer name1 = Integer.valueOf(o1.get("contactNumber").toString()) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(o2.get("contactNumber").toString()) ; //name1是从你list里面拿出来的第二个name
                return name2.compareTo(name1);
            }
        });
        Collections.sort(inNet, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer name1 = Integer.valueOf(o1.get("contactNumber").toString()) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(o2.get("contactNumber").toString()) ; //name1是从你list里面拿出来的第二个name
                return name2.compareTo(name1);
            }
        });
        Collections.sort(outNet, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer name1 = Integer.valueOf(o1.get("contactNumber").toString()) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(o2.get("contactNumber").toString()) ; //name1是从你list里面拿出来的第二个name
                return name2.compareTo(name1);
            }
        });
//        mapSort(preNet,"contactNumber");
//        mapSort(inNet,"contactNumber");
//        mapSort(outNet,"contactNumber");
//        Map<String,Object> resultData = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> preMap = new HashMap<>();
        preMap.put("name","入网期");
        preMap.put("size",preNet.size());
        preMap.put("list",preNet);
        list.add(preMap);
        Map<String,Object> inMap = new HashMap<>();
        inMap.put("name","在网期");
        inMap.put("size",inNet.size());
        inMap.put("list",inNet);
        list.add(inMap);
        Map<String,Object> outMap = new HashMap<>();
        outMap.put("name","离网期");
        outMap.put("size",outNet.size());
        outMap.put("list",outNet);
        list.add(outMap);
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",list);
        return result;
    }



    /**
     * 活动匹配地市
     * @param campaignDO
     * @return
     */
    private String getArea(MktCampaignDO campaignDO){
        String result = "";
        String regionFlg = campaignDO.getRegionFlg();
        if (StringUtils.isNotBlank(regionFlg)) {
            String sysArea = AreaCodeEnum.getSysAreaNameBySysArea(campaignDO.getRegionFlg());
            if (regionFlg.equals("C2")) {
                result = sysArea;
            }
            if (regionFlg.equals("C3")) {
                String name = AreaNameEnum.getNameByLandId(campaignDO.getLanId());
                result = sysArea + "-" + name;
            }
            if (regionFlg.equals("C4")) {
                Organization organization = organizationMapper.selectByPrimaryKey(campaignDO.getLanIdFour());
                String name = organization == null ? "" : organization.getOrgName();
                result = sysArea + "-" + name;
            }

            if (regionFlg.equals("C5")) {
                Organization organization = organizationMapper.selectByPrimaryKey(campaignDO.getLanIdFive());
                String name = organization == null ? "" : organization.getOrgName();
                result = sysArea + "-" + name;
            }
            return result;
        }
        return result;
    }




    /**
     *  新活动报表 主题活动
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> activityTheme(Map<String, Object> params) {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        List<Map<String, String>> campaignTheme = sysParamsMapper.listParamsByKey("CAMPAIGN_THEME");
        String date = params.get("endDate").toString();
        String type = paramMap.get("mktCampaignType").toString();
        //总数
        Integer count = mktCampaignMapper.getCountFromActivityTheme(date,type,date);
        log.info("【count】："+JSON.toJSONString(count));
        List<Map<String,Object>> dataMap = new ArrayList<>();
        if (campaignTheme.size()>0 && campaignTheme!=null){
            //主题列表
            for (Map<String, String> stringStringMap : campaignTheme) {
                Map<String,Object> themeMap = new HashMap<>();
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
                log.info("【入参】新活动报表 主题活动 按转换率排序去前五 top5:："+JSON.toJSONString(paramMap));
                Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
                log.info("新活动报表 主题活动 按转换率排序去前五 top5:"+JSON.toJSONString(stringObjectMap));
                List<Map<String,Object>> rptList = (List<Map<String,Object>>) stringObjectMap.get("rptOrderList");
                for (Map<String, Object> stringMap : rptList) {
                    for (MktCampaignDO campaignDO : mktCampaignList) {
                        if (campaignDO.getInitId().toString().equals(stringMap.get("mktCampaignId").toString())){
                            stringMap.put("mktCampaignName",campaignDO.getMktCampaignName());
                            log.info("【活动region】:"+JSON.toJSONString(campaignDO));
                            stringMap.put("area", getArea(campaignDO));
                        }
                    }
                }

                //按收入提高排序取前五
                paramMap.put("sortColumn","incomeUp");
                log.info("【入参】新活动报表 主题活动 按收入提高排序取前五 top5:："+JSON.toJSONString(paramMap));
                Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
                log.info("新活动报表 主题活动 按收入提高排序取前五:"+JSON.toJSONString(stringObjectMap1));
                List<Map<String,Object>> rptList2 = (List<Map<String,Object>>) stringObjectMap.get("rptOrderList");
                for (Map<String, Object> stringMap : rptList2) {
                    for (MktCampaignDO campaignDO : mktCampaignList) {
                        if (campaignDO.getInitId().toString().equals(stringMap.get("mktCampaignId").toString())){
                            stringMap.put("mktCampaignName",campaignDO.getMktCampaignName());
                            stringMap.put("area", getArea(campaignDO));
                        }
                    }
                }
                //主题百分比
                double num = (double) mktCampaignList.size() / (double)count;
                System.out.println("主题百分比是否正常"+num);
                //返回拼装
                themeMap.put("name",stringStringMap.get("label"));
                themeMap.put("number",mktCampaignList.size());
                themeMap.put("value",num*100+"%");
                themeMap.put("conversionList",rptList);
                themeMap.put("incomeList",rptList2);
                dataMap.add(themeMap);
            }
        }
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",dataMap);
        return result;
    }


    /**
     * 客触数
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> contactNumber(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> dataMap = new HashMap<>();

        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        //统计维度(0:按渠道，1按地市) 不用就不传
        paramMap.put("rptType","1");
        //按客触数排序
        paramMap.put("sortColumn","contactNum");
        //查询总数 解析
        log.info("【入参】新活动报表 客触数 ："+JSON.toJSONString(paramMap));
        Map<String,Object> stringOMap = iReportService.queryRptOrder(paramMap);
        log.info("【出参】新活动报表 客触数 ："+JSON.toJSONString(stringOMap));
        List<Map<String,String>> rptList = (List<Map<String,String>>) stringOMap.get("rptOrderList");
        if (rptList.size()!=1 ){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        dataMap.put("contactNum",rptList.get(0).get("contactNum"));
        dataMap.put("successNum",rptList.get(0).get("orderSuccessNum"));
        dataMap.put("contactRate",rptList.get(0).get("contactRate"));
        dataMap.put("orderNum",rptList.get(0).get("orderNum"));
        //查询出来后按地市和渠道排序
        //地市(ALL表示所有,多个用逗号隔开) 添加11个地市的orgid
        paramMap.put("orglevel2",OrgEnum.getNameByOrgId());
        //查询地市排名
        log.info("【入参】新活动报表 客触数  查询地市排名："+JSON.toJSONString(paramMap));
        Map<String,Object> orgMapRes = iReportService.queryRptOrder(paramMap);
        log.info("【出参】新活动报表 客触数 查询地市排名:"+JSON.toJSONString(orgMapRes));
        List<Map<String,Object>> orgList = (List<Map<String,Object>>) orgMapRes.get("rptOrderList");
        for (Map<String, Object> orgMap : orgList) {
            orgMap.put("name",OrgEnum.getNameByOrgId(Long.valueOf(orgMap.get("orgId").toString())));
        }
        //按渠道排序
        paramMap.put("rptType","0");
        paramMap.put("sortColumn","channel");
        //查询渠道排序
        log.info("【入参】新活动报表 客触数  查询渠道排序："+JSON.toJSONString(paramMap));
        Map<String, Object> channelMapRes = iReportService.queryRptOrder(paramMap);
        log.info("【出参】新活动报表 客触数 查询渠道排序:"+JSON.toJSONString(channelMapRes));
        List<Map<String,Object>> channelList = (List<Map<String,Object>>) channelMapRes.get("rptOrderList");
        for (Map<String, Object> channelMap : channelList) {
            Channel channel = channelMapper.selectByCode(channelMap.get("channel").toString());
            channelMap.put("name",channel==null ? "" : channel.getContactChlName());
        }
        dataMap.put("areaList",orgList);
        dataMap.put("channelList",channelList);
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",dataMap);
        return result;
    }

    /**
     * 转换率
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> orderSuccessRate(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        List<Map<String,Object>>  dataList = new ArrayList<>();
        //统计维度(0:按渠道，1按地市) 不用就不传
        paramMap.put("rptType","2");
        paramMap.put("pageSize","5");
        log.info("【入参】新活动报表 转换率  top5："+JSON.toJSONString(paramMap));
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        log.info("【出参】新活动报表 转换率  top5："+JSON.toJSONString(stringObjectMap));
        if (stringObjectMap.get("resultCode").equals("1000")){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        List<Map<String, Object>> data = new ArrayList<>();
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            Object rptOrderList = stringObjectMap.get("rptOrderList");
            if (rptOrderList!=null && ""!=rptOrderList){
                data = (List<Map<String, Object>>) stringObjectMap.get("rptOrderList");
                for (Map<String, Object> datum : data) {
                    Map<String,Object> camMap = new HashMap<>();
                    MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(datum.get("mktCampaignId").toString()));
                    camMap.put("mktCampaignName",campaignDO==null ? "" : campaignDO.getMktCampaignName());
                    camMap.put("conversion",datum.get("contactRate"));
                    camMap.put("conversion",datum.get("contactRate"));
                    camMap.put("area",getArea(campaignDO));
                    log.info("活动报表查询接口:orderSuccessRate"+stringObjectMap);
                    //按地市
                    paramMap.put("rptType","1");
                    paramMap.put("orglevel2","all");
                    paramMap.put("mktCampaignId",datum.get("mktCampaignId"));
                    paramMap.put("pageSize","20");
                    log.info("【入参】新活动报表 转换率  按地市："+JSON.toJSONString(paramMap));
                    Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
                    log.info("【出参】新活动报表 转换率 按地市:"+JSON.toJSONString(stringObjectMap1));
                    if (stringObjectMap1.get("resultCode").equals("1000")){
                     continue;
                    }
                    List<Map<String,Object>> orgList = (List<Map<String,Object>>) stringObjectMap1.get("rptOrderList");
                    for (Map<String, Object> orgMap : orgList) {
                        orgMap.put("name",OrgEnum.getNameByOrgId(Long.valueOf(orgMap.get("orgId").toString())));
                    }
                    //按渠道
                    paramMap.put("rptType","0");
                    paramMap.put("channelCode","all");
                    paramMap.put("mktCampaignId",datum.get("mktCampaignId"));
                    paramMap.remove("orglevel2");
                    log.info("【入参】新活动报表 转换率  按渠道："+JSON.toJSONString(paramMap));
                    Map<String, Object> stringObjectMap2 = iReportService.queryRptOrder(paramMap);
                    log.info("【出参】新活动报表 转换率 按渠道:"+JSON.toJSONString(stringObjectMap2));
                    if (stringObjectMap2.get("resultCode").equals("1000")){
                        continue;
                    }
                    List<Map<String,Object>> channelList = (List<Map<String,Object>>) stringObjectMap2.get("rptOrderList");
                    for (Map<String, Object> channelMap : channelList) {
                        Channel channel = channelMapper.selectByCode(channelMap.get("channel").toString());
                        channelMap.put("name",channel==null ? "" : channel.getChannelName());
                    }
                    camMap.put("areaList",orgList);
                    camMap.put("channelList",channelList);
                    dataList.add(camMap);
                }
            }
        } else {
            Object reqId = stringObjectMap.get("reqId");
            stringObjectMap.put("resultCode", CODE_FAIL);
            stringObjectMap.put("resultMsg", "查询无结果 queryRptBatchOrder error :" + reqId.toString());
        }
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",dataList);
        return result;
    }



    private  List<Map<String,Object>> mapSort (List<Map<String,Object>> list,String sort){
        Collections.sort(list,new Comparator<Map<String,Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // TODO Auto-generated method stub
                return o2.get(sort).toString().compareTo(o1.get(sort).toString());
            }
        });
        return list;
    }

    /**
     * 收入拉动
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> incomePull(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> dataMap = new HashMap<>();
        List<Map<String,Object>>  dataList = new ArrayList<>();
        //按地市
        params.put("rptType","1");
        Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
        String date = params.get("startDate").toString();
        String type = paramMap.get("mktCampaignType").toString();
        //总数
        Integer count = mktCampaignMapper.getCountFromActivityTheme(date,type,date);
        //查询一条数据 返回总数

        log.info("【入参】新活动报表 收入拉动  查询一条数据 ："+JSON.toJSONString(paramMap));
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        log.info("【出参】新活动报表 收入拉动 查询一条数据 返回总数:"+JSON.toJSONString(stringObjectMap));
        if (stringObjectMap.get("resultCode").equals("1000")){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        List<Map<String,Object>> list = (List<Map<String,Object>>) stringObjectMap.get("rptOrderList");

        dataMap.put("target","76%");
        dataMap.put("lowIncome",list.get(0).get("incomeDown"));
        dataMap.put("highIncome",list.get(0).get("incomeUp"));
        dataMap.put("lowIncomeNum",list.get(0).get("downCount"));
        dataMap.put("highIncomeNum",list.get(0).get("upCount"));
        dataMap.put("totalIncome",Double.valueOf(dataMap.get("highIncome").toString()).doubleValue() + Double.valueOf(dataMap.get("lowIncome").toString()).doubleValue());
        dataMap.put("avgIncome","0.00");
        dataMap.put("avgIncomeNum",count - (Integer.valueOf(dataMap.get("highIncomeNum").toString()) + Integer.valueOf(dataMap.get("lowIncomeNum").toString())) );
        dataMap.put("lowPercent",Double.valueOf(dataMap.get("lowIncomeNum").toString()).doubleValue()/(double) count);
        dataMap.put("highPercent",Double.valueOf(dataMap.get("highIncomeNum").toString()).doubleValue()/(double)count);
        dataMap.put("avgPercent",Double.valueOf(dataMap.get("avgIncomeNum").toString()).doubleValue()/(double)count);

        //返回多条 按活动查询
        paramMap.put("rptType","2");
        //top5
        paramMap.put("pageSize","5");
        //收入提高
        paramMap.put("sortColumn","incomeUp");
        //收入拉动top5
        Map<String, Object> map = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 收入拉动 收入拉动top5:"+JSON.toJSONString(map));
        //每个活动需要按地市和渠道排序 取活动id查询地市信息 和渠道信息
        List<Map<String, Object>> data = new ArrayList<>();
        if (map.get("resultCode") != null && "1".equals(map.get("resultCode").toString())) {
            Object rptOrderList = map.get("rptOrderList");
            if (rptOrderList!=null && ""!=rptOrderList){
                data = (List<Map<String, Object>>) map.get("rptOrderList");
                for (Map<String, Object> datum : data) {
                    Map<String,Object> camMap = new HashMap<>();
                    MktCampaignDO campaignDO = mktCampaignMapper.selectCampaignByInitId(Long.valueOf(datum.get("mktCampaignId").toString()));
                    camMap.put("mktCampaignName",campaignDO==null ? "" : campaignDO.getMktCampaignName());
                    camMap.put("income",datum.get("incomeUp"));
                    camMap.put("area",campaignDO==null ? "" : getArea(campaignDO));
                    log.info("活动报表查询接口:orderSuccessRate"+stringObjectMap);
                    //按地市
                    paramMap.put("rptType","1");
                    paramMap.put("orglevel2","all");
                    paramMap.put("mktCampaignId",datum.get("mktCampaignId"));
                    paramMap.put("pageSize",20);
                    Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
                    log.info("新活动报表 转换率 按地市:"+JSON.toJSONString(stringObjectMap1));
                    if (stringObjectMap1.get("resultCode").equals("1000")){
                       continue;
                    }
                    List<Map<String,Object>> orgList = (List<Map<String,Object>>) stringObjectMap1.get("rptOrderList");
                    for (Map<String, Object> orgMap : orgList) {
                        orgMap.put("name",OrgEnum.getNameByOrgId(Long.valueOf(orgMap.get("orgId").toString())));
                        Double totalIncome = Double.valueOf(orgMap.get("incomeUp").toString())+ Double.valueOf(orgMap.get("incomeUp").toString());
                        orgMap.put("totalIncome",totalIncome.toString());
                    }
                    Map<String,Object> areaList = new HashMap<>();
                    areaList.put("incomeSum",mapSort(orgList,"totalIncome"));
                    areaList.put("lowIncome",mapSort(orgList,"incomeDown"));
                    camMap.put("areaList",areaList);

                    //按渠道
                    paramMap.put("rptType","0");
                    paramMap.put("channelCode","all");
                    paramMap.put("mktCampaignId",datum.get("mktCampaignId"));
                    paramMap.remove("orglevel2");
                    Map<String, Object> stringObjectMap2 = iReportService.queryRptOrder(paramMap);
                    log.info("新活动报表 转换率 按渠道:"+JSON.toJSONString(stringObjectMap2));
                    if (stringObjectMap2.get("resultCode").equals("1000")){
                       continue;
                    }
                    List<Map<String,Object>> channelList = (List<Map<String,Object>>) stringObjectMap2.get("rptOrderList");
                    for (Map<String, Object> channelMap : channelList) {
                        Channel channel = channelMapper.selectByCode(channelMap.get("channel").toString());
                        channelMap.put("name",channel==null ? "" : channel.getContactChlName());
                        Double totalIncome = Double.valueOf(channelMap.get("incomeUp").toString())+ Double.valueOf(channelMap.get("incomeUp").toString());
                        channelMap.put("totalIncome",totalIncome.toString());
                    }

                    Map<String,Object> channels = new HashMap<>();
                    channels.put("incomeSum",mapSort(channelList,"totalIncome"));
                    channels.put("lowIncome",mapSort(channelList,"incomeDown"));

                    camMap.put("channelList",channels);
                    dataList.add(camMap);
                }
            }
        } else {
            Object reqId = stringObjectMap.get("reqId");
            stringObjectMap.put("resultCode", CODE_FAIL);
            stringObjectMap.put("resultMsg", "查询无结果 queryRptBatchOrder error :" + reqId.toString());
        }
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",dataList);
        return result;
    }

}
