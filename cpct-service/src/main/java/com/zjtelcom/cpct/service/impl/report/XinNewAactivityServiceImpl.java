package com.zjtelcom.cpct.service.impl.report;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.OrganizationMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.AreaNameEnum;
import com.zjtelcom.cpct.enums.OrgEnum;
import com.zjtelcom.cpct.service.report.XinNewAactivityService;
import com.zjtelcom.cpct.util.AcitvityParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.service.impl.report.ActivityStatisticsServiceImpl.getPercentFormat;

@Service
@Transactional
public class XinNewAactivityServiceImpl implements XinNewAactivityService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(XinNewAactivityServiceImpl.class);

    @Autowired(required = false)
    private IReportService iReportService;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private ContactChannelMapper channelMapper;


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
            if (sysArea.equals("C2")) {
                result = sysArea;
            }
            if (sysArea.equals("C3")) {
                String name = AreaNameEnum.getNameByLandId(campaignDO.getLanId());
                result = sysArea + "-" + name;
            }
            if (sysArea.equals("C4")) {
                Organization organization = organizationMapper.selectByPrimaryKey(campaignDO.getLanIdFour());
                String name = organization == null ? "" : organization.getOrgName();
                result = sysArea + "-" + name;
            }

            if (sysArea.equals("C5")) {
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
        log.info("客触数查询总数入参："+JSON.toJSONString(paramMap));
        //查询总数 解析
        Map<String,Object> stringOMap = iReportService.queryRptOrder(paramMap);
        log.info("客触数查询总数 解析:"+JSON.toJSONString(stringOMap));
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
            orgMap.put("name", OrgEnum.getNameByOrgId(Long.valueOf(orgMap.get("orgId"))));
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
        log.info("新活动报表 转换率入参:"+JSON.toJSONString(params));
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 转换率:"+JSON.toJSONString(stringObjectMap));
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
        return result;
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
        Integer count = mktCampaignMapper.getCountFromActivityTheme(date,type);
        //查询一条数据 返回总数
        Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 收入拉动 查询一条数据 返回总数:"+JSON.toJSONString(stringObjectMap));
        List<Map<String,String>> list = (List<Map<String,String>>) stringObjectMap.get("rptOrderList");

        dataMap.put("target","76%");
        dataMap.put("lowIncome",list.get(0).get("incomeDown"));
        dataMap.put("highIncome",list.get(0).get("incomeUp"));
        dataMap.put("lowIncomeNum",list.get(0).get("downCount"));
        dataMap.put("highIncomeNum",list.get(0).get("upCount"));
        dataMap.put("totalIncome",Double.valueOf(dataMap.get("highIncome").toString()) + Double.valueOf(dataMap.get("lowIncome").toString()));
        dataMap.put("avgIncome","0.00");
        dataMap.put("avgIncomeNum",count - (Integer.valueOf(dataMap.get("highIncomeNum").toString()) + Integer.valueOf(dataMap.get("lowIncomeNum").toString())) );
        dataMap.put("lowPercent",Integer.valueOf(dataMap.get("lowIncomeNum").toString())/count);
        dataMap.put("highPercent",Integer.valueOf(dataMap.get("highIncomeNum").toString())/count);
        dataMap.put("avgPercent",Integer.valueOf(dataMap.get("avgIncomeNum").toString())/count);

        //返回多条 按活动查询
        paramMap.put("rptType","2");
        //top5
        paramMap.put("pageSize","5");
        //收入提高
        paramMap.put("sortColumn","incomeU");
        //收入拉动top5
        Map<String, Object> map = iReportService.queryRptOrder(paramMap);
        log.info("新活动报表 收入拉动 收入拉动top5:"+JSON.toJSONString(map));
        //每个活动需要按地市和渠道排序 取活动id查询地市信息 和渠道信息
        List<Map<String, String>> data = new ArrayList<>();
        if (map.get("resultCode") != null && "1".equals(map.get("resultCode").toString())) {
            Object rptOrderList = map.get("rptOrderList");
            if (rptOrderList!=null && ""!=rptOrderList){
                data = (List<Map<String, String>>) map.get("rptOrderList");
                for (Map<String, String> datum : data) {
                    Map<String,Object> camMap = new HashMap<>();
                    MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(datum.get("mktCampaignId").toString()));
                    camMap.put("mktCampaignName",campaignDO==null ? "" : campaignDO.getMktCampaignName());
                    camMap.put("income",datum.get("incomeUp"));
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
                        Long totalIncome = Long.valueOf(orgMap.get("incomeUp"))+ Long.valueOf(orgMap.get("incomeUp"));
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
                    List<Map<String,String>> channelList = (List<Map<String,String>>) stringObjectMap2.get("rptOrderList");
                    for (Map<String, String> channelMap : channelList) {
                        Channel channel = channelMapper.selectByCode(channelMap.get("channel"));
                        channelMap.put("name",channel==null ? "" : channel.getChannelName());
                        Long totalIncome = Long.valueOf(channelMap.get("incomeUp"))+ Long.valueOf(channelMap.get("incomeUp"));
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


    private  List<Map<String,String>> mapSort (List<Map<String,String>> list,String sort){
        Collections.sort(list,new Comparator<Map<String,String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                // TODO Auto-generated method stub
                return o1.get(sort).compareTo(o2.get(sort));
            }
        });
        return list;
    }

    /**
     * 活动主题分类和数量
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> activityThemeCount(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            ArrayList<Object> list = new ArrayList<>();
            Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
            List<Map<String, String>> campaignTheme = sysParamsMapper.listParamsByKey("CAMPAIGN_THEME");
            String date = params.get("startDate").toString();
            String type = paramMap.get("mktCampaignType").toString();
            //总数
            Integer count = mktCampaignMapper.getCountFromActivityTheme(date,type);
            if (campaignTheme.size()>0 && campaignTheme!=null){
                for (Map<String, String> stringStringMap : campaignTheme) {
                    String value = stringStringMap.get("value");
                    String label = stringStringMap.get("label");
                    //每个主题个数
                    List<MktCampaignDO> mktCampaignList = mktCampaignMapper.selectCampaignTheme(value, date, type);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name",label);
                    map.put("value",mktCampaignList.size());
                    list.add(map);
                }
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("name","全部");
            map.put("value",count);
            list.add(0,map);
            resultMap.put("resultCode","200");
            resultMap.put("resultMsg",list);
        } catch (Exception e) {
            resultMap.put("resultCode","500");
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 地市查询和渠道查询
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> activityThemeLevelAndChannel(Map<String, Object> params) {
        HashMap<String, Object> resultMap = new HashMap<>();
        ArrayList<String> areaList = new ArrayList<>();
        ArrayList<String> channelLists = new ArrayList<>();
        //地市信息
        List<Organization> organizations = organizationMapper.selectMenuByEleven();
        if (organizations.size()>0 && organizations!=null){
            for (Organization organization : organizations) {
                String nameByOrgId = OrgEnum.getNameByOrgId(organization.getOrgId());
                areaList.add(nameByOrgId);
            }
            resultMap.put("orglevel2",areaList);
        }else {
            resultMap.put("code","500");
        }
        //渠道信息
        List<Channel> channelList = contactChannelMapper.getNewActivityChannel();
        if (channelList.size()>0 &&channelList!=null){
            for (Channel channel : channelList) {
                String channelName = channel.getContactChlName();
                channelLists.add(channelName);
            }
            resultMap.put("code","0000");
            resultMap.put("channel",channelLists);
        }else {
            resultMap.put("code","500");
        }
        return resultMap;
    }


    /**
     * 季度营销活动
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> quarterActivities(Map<String, Object> params) {
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> paramMap = AcitvityParams.ActivityParamsByMap(params);
            //按活动统计
            paramMap.put("rptType","2");
//        //商机成功数 排序 orderSuccessNum
//        paramMap.put("sortColumn","orderSuccessNum");
            StringBuilder stringBuilder = new StringBuilder();
            //活动按主题 选 all或者主题
            Object theMe = params.get("theMe");
            String date = params.get("startDate").toString();
            String type = paramMap.get("mktCampaignType").toString();
            Object statusCd = params.get("statusCd");
            String status = null;
            if (statusCd!=null && ""!=statusCd){
                status = statusCd.toString();
            }
            List<MktCampaignDO> mktCampaignList =null;
            if (theMe!=null && theMe!=""){
                if (theMe.toString().equals("all")){
                    mktCampaignList = mktCampaignMapper.getMktCampaignFromInitIdFromStatus(date,type,status);
                }else {
                    //查询主题 value 参数 对应 theMe 1000 - 1800
                    mktCampaignList = mktCampaignMapper.selectCampaignThemeFromStatus(theMe.toString(), date, type,status);
                }
            }else {
                mktCampaignList = mktCampaignMapper.getMktCampaignFromInitIdFromStatus(date,type,status);
            }
            List<Map<String, Object>> data = new ArrayList<>();
            if (mktCampaignList.size() > 0 && mktCampaignList != null) {
                for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                    //活动级别所有的initId 根据活动维度分页查询所有 默认按成功数排序
                    stringBuilder.append(mktCampaignDO.getInitId()).append(",");
                    //多个id  “，”拼接
                    String substring = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
                    paramMap.put("mktCampaignId", substring);
                }
                //按全部或者按主题查询 活动级别 默认排序 清单列表参数 渲染页面
                log.info("季度营销活动入参："+JSON.toJSONString(paramMap));
                Map<String, Object> stringObjectMap = iReportService.queryRptOrder(paramMap);
                log.info("新活动报表 季度营销活动 按活动:"+JSON.toJSONString(stringObjectMap));
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                List<HashMap<String, Object>> list = new ArrayList<>();
                if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
                    Object rptOrderList = stringObjectMap.get("rptOrderList");
                    data = (List<Map<String, Object>>) stringObjectMap.get("rptOrderList");
                    for (Map<String, Object> datum : data) {
                        HashMap<String, Object> camMap = new HashMap<>();
                        String mktCampaignId1 = datum.get("mktCampaignId").toString();
                        MktCampaignDO campaignDO = mktCampaignMapper.selectByInitId(Long.valueOf(mktCampaignId1));
    //                    MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(datum.get("mktCampaignId").toString()));
                        if (campaignDO == null){
                            // 如果有为空 跳过
                            continue;
                        }
                        camMap.put("mktCampaignName", campaignDO == null ? "" : campaignDO.getMktCampaignName());
                        camMap.put("area", getArea(campaignDO));
                        //成功数
                        camMap.put("orderSuccessNum", datum.get("orderSuccessNum"));
                        //开始和结束时间
                        camMap.put("beginTime", fmt.format(campaignDO.getPlanBeginTime()));
                        camMap.put("endTime", fmt.format(campaignDO.getPlanEndTime()));
                        camMap.put("contactNum",datum.get("contactNum")); //客户接触数
                        camMap.put("orderNum",datum.get("orderNum")); //商机推荐数
                        camMap.put("orderSuccessNum",datum.get("orderSuccessNum")); //商机成功数
                        camMap.put("contactRate",getPercentFormat(Double.valueOf(datum.get("contactRate").toString()),2,2)); //转化率
                        camMap.put("orgChannelRate",getPercentFormat(Double.valueOf(datum.get("orgChannelRate").toString()), 2, 2));//门店有销率
                        camMap.put("incomeUp",datum.get("incomeUp"));//收入提升
                        camMap.put("incomeDown",datum.get("incomeDown")); // 收入低迁
                        camMap.put("upCount",datum.get("upCount"));//收入提升活动数
                        camMap.put("downCount",datum.get("downCount"));//收入低迁活动数
                        //收入拉动
                        camMap.put("income",Double.valueOf(datum.get("incomeUp").toString())+ Double.valueOf(datum.get("incomeDown").toString()));
                        list.add(camMap);
                    }
                    resultMap.put("data",list);
                    resultMap.put("msg","成功");
                    resultMap.put("code","0000");
                }
            }
        } catch (NumberFormatException e) {
            resultMap.put("code","0001");
            resultMap.put("msg","失败");
            e.printStackTrace();
        }
        return resultMap;
    }
}
