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
import com.zjtelcom.cpct.util.MapUtil;
import lombok.extern.log4j.Log4j;
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
        List<Map<String,String>> rptList = (List<Map<String,String>>) stringObjectMap1.get("rptOrderList");
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



    private String getArea(MktCampaignDO campaignDO){
        String result = "";
        String sysArea = AreaCodeEnum.getSysAreaNameBySysArea(campaignDO.getRegionFlg());
        if (sysArea.equals("C2")){
            result = sysArea;
        }
        if (sysArea.equals("C3")){
            String name = AreaNameEnum.getNameByLandId(campaignDO.getLanId());
            result = sysArea+"-"+name;
        }
        if (sysArea.equals("C4")){
            Organization organization = organizationMapper.selectByPrimaryKey(campaignDO.getLanIdFour());
            String name = organization==null ? "" : organization.getOrgName();
            result = sysArea+"-"+name;
        }

        if (sysArea.equals("C5")){
            Organization organization = organizationMapper.selectByPrimaryKey(campaignDO.getLanIdFive());
            String name = organization==null ? "" : organization.getOrgName();
            result = sysArea+"-"+name;
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
        String date = params.get("startDate").toString();
        String type = paramMap.get("mktCampaignType").toString();
        //总数
        Integer count = mktCampaignMapper.getCountFromActivityTheme(date,type);
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
                Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
                log.info("新活动报表 主题活动 按转换率排序去前五 top5:"+JSON.toJSONString(stringObjectMap));
                List<Map<String,String>> rptList = (List<Map<String,String>>) stringObjectMap.get("rptOrderList");
                for (Map<String, String> stringMap : rptList) {
                    for (MktCampaignDO campaignDO : mktCampaignList) {
                        if (campaignDO.getInitId().equals(stringMap.get("mktCampaignId"))){
                            stringMap.put("mktCampaignName",campaignDO.getMktCampaignName());
                            stringMap.put("area", getArea(campaignDO));
                        }
                    }
                }

                //按收入提高排序取前五
                paramMap.put("sortColumn","incomeUp");
                Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
                log.info("新活动报表 主题活动 按收入提高排序取前五:"+JSON.toJSONString(stringObjectMap1));
                List<Map<String,String>> rptList2 = (List<Map<String,String>>) stringObjectMap.get("rptOrderList");
                for (Map<String, String> stringMap : rptList2) {
                    for (MktCampaignDO campaignDO : mktCampaignList) {
                        if (campaignDO.getInitId().equals(stringMap.get("mktCampaignId"))){
                            stringMap.put("mktCampaignName",campaignDO.getMktCampaignName());
                            stringMap.put("area", getArea(campaignDO));
                        }
                    }
                }
                //主题百分比
                double num = mktCampaignList.size() / count;
                System.out.println("主题百分比是否正常"+num);
                //返回拼装
                themeMap.put("name",stringStringMap.get("label"));
                themeMap.put("number",mktCampaignList.size());
                themeMap.put("value",num);
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
        Map<String,Object> stringOMap = iReportService.queryRptOrder(paramMap);
        List<Map<String,String>> rptList = (List<Map<String,String>>) stringOMap.get("rptOrderList");
        if (rptList.size()!=1 ){
            result.put("code","0001");
            result.put("message","报表查询失败");
            return result;
        }
        dataMap.put("contactNum",rptList.get(0).get("contactNum"));
        dataMap.put("successNum",rptList.get(0).get("orderSuccessNum"));
        dataMap.put("contactRate",rptList.get(0).get("contactRate"));
        //查询出来后按地市和渠道排序
        //地市(ALL表示所有,多个用逗号隔开) 添加11个地市的orgid
        paramMap.put("orglevel2",OrgEnum.getNameByOrgId());
        //查询地市排名
        Map<String,Object> orgMapRes = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 客触数 查询地市排名:"+JSON.toJSONString(orgMapRes));
        List<Map<String,String>> orgList = (List<Map<String,String>>) orgMapRes.get("rptOrderList");
        for (Map<String, String> orgMap : orgList) {
            orgMap.put("name",OrgEnum.getNameByOrgId(Long.valueOf(orgMap.get("orgId"))));
        }
        //按渠道排序
        paramMap.put("rptType","0");
        paramMap.put("sortColumn","channel");
        //查询渠道排序
        Map<String, Object> channelMapRes = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 客触数 查询渠道排序:"+JSON.toJSONString(channelMapRes));
        List<Map<String,String>> channelList = (List<Map<String,String>>) stringOMap.get("rptOrderList");
        for (Map<String, String> channelMap : channelList) {
            Channel channel = channelMapper.selectByCode(channelMap.get("channel"));
            channelMap.put("name",channel==null ? "" : channel.getChannelName());
        }
        dataMap.put("areaList",orgList);
        dataMap.put("channelList",channelList);
        result.put("code","0000");
        result.put("message","成功");
        result.put("data",dataMap);
        return null;
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
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        List<Map<String, String>> data = new ArrayList<>();
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            Object rptOrderList = stringObjectMap.get("rptOrderList");
            if (rptOrderList!=null && ""!=rptOrderList){
                data = (List<Map<String, String>>) stringObjectMap.get("rptOrderList");
                for (Map<String, String> datum : data) {
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
                    paramMap.remove("pageSize");
                    Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
                    log.info("新活动报表 转换率 按地市:"+JSON.toJSONString(stringObjectMap1));
                    List<Map<String,String>> orgList = (List<Map<String,String>>) stringObjectMap1.get("rptOrderList");
                    for (Map<String, String> orgMap : orgList) {
                        orgMap.put("name",OrgEnum.getNameByOrgId(Long.valueOf(orgMap.get("orgId"))));
                    }
                    //按渠道
                    paramMap.put("rptType","0");
                    paramMap.put("channelCode","all");
                    paramMap.put("mktCampaignId",datum.get("mktCampaignId"));
                    paramMap.remove("orglevel2");
                    Map<String, Object> stringObjectMap2 = iReportService.queryRptOrder(paramMap);
                    log.info("新活动报表 转换率 按渠道:"+JSON.toJSONString(stringObjectMap2));
                    List<Map<String,String>> channelList = (List<Map<String,String>>) stringObjectMap2.get("rptOrderList");
                    for (Map<String, String> channelMap : channelList) {
                        Channel channel = channelMapper.selectByCode(channelMap.get("channel"));
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
        log.info("新活动报表 收入拉动 查询一条数据 返回总数:"+JSON.toJSONString(stringObjectMap));
        //返回多条 按活动查询
        paramMap.put("rptType","2");
        //top5
        paramMap.put("pageSize","5");
        //收入提高
        paramMap.put("sortColumn","incomeU");
        //收入拉动top5
        Map<String, Object> stringObjectMap1 = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 收入拉动 收入拉动top5:"+JSON.toJSONString(stringObjectMap1));
        //每个活动需要按地市和渠道排序 取活动id查询地市信息 和渠道信息

        //按地市
        paramMap.put("rptType","1");
        paramMap.put("orglevel2","all");
        Map<String, Object> stringObjectMap2 = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 收入拉动 按地市:"+JSON.toJSONString(stringObjectMap2));

        //按渠道
        paramMap.put("rptType","0");
        paramMap.put("channelCode","all");
        Map<String, Object> stringObjectMap3 = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 收入拉动 按渠道:"+JSON.toJSONString(stringObjectMap3));
        return null;
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
