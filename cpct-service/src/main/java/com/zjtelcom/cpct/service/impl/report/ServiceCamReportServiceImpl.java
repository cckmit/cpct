package com.zjtelcom.cpct.service.impl.report;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignReportMapper;
import com.zjtelcom.cpct.dao.channel.ChannelMapper;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.channel.OrganizationMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
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
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
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


    @Override
    public Map<String, Object> selectOrgIdByStaffId(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        String organizationId = null;
        try {
            Long staffId = Long.valueOf(param.get("staffId").toString());
            SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
            Long orgId = null;
            List<Map<String, Object>> staffOrgId = organizationMapper.getStaffOrgId(staffId);
            if (!staffOrgId.isEmpty() && staffOrgId.size() > 0){
                for (Map<String, Object> map : staffOrgId) {
                    Object orgDivision = map.get("orgDivision");
                    Object orgId1 = map.get("orgId");
                    if (orgDivision!=null){
                        if (orgDivision.toString().equals("30")) {
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }else if (orgDivision.toString().equals("20")){
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }else if (orgDivision.toString().equals("10")){
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }
                    }
                }
            }
            try {
                Organization organization = organizationMapper.selectByPrimaryKey(orgId);
                if (organization!=null && organization.getOrgNameC3()!=null && !"".equals(organization.getOrgNameC3())){
                    Organization c3Org = organizationMapper.selectByPrimaryKey(Long.valueOf(organization.getOrgNameC3()));
                    if (c3Org!=null){
                        Long landIdByRegionId = AreaCodeEnum.getLandIdByRegionId(c3Org.getRegionId());
                        organizationId = ChannelUtil.getOrgByArea(landIdByRegionId==null ? "" : landIdByRegionId.toString());
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            String sysPostCode = null;
            ArrayList<String> arrayList = new ArrayList<>();
            List<SystemPostDto> systemPostDtoList = userDetail.getSystemPostDtoList();
            //岗位信息查看最大权限作为岗位信息
            if (systemPostDtoList.size()>0 && systemPostDtoList!=null){
                for (SystemPostDto systemPostDto : systemPostDtoList) {
                    arrayList.add(systemPostDto.getSysPostCode());
                }
            }
            if (arrayList.contains(AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.SHENGJI.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.SHENGJI.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.FENGONGSI.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.FENGJU.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.FENGJU.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.ZHIJU.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea();
            }else {
                sysPostCode = AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
            }
            if ("C1".equals(sysPostCode) || "C2".equals(sysPostCode)){
                organizationId = "800000000004";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        result.put("code","0000");
        result.put("message","success");
        result.put("data",organizationId);
        return result;
    }
}
