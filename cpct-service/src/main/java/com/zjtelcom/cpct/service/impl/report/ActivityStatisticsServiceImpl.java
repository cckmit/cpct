package com.zjtelcom.cpct.service.impl.report;

import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignRelMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.OrgRel;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class ActivityStatisticsServiceImpl implements ActivityStatisticsService {

    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired(required = false)
    private OrgRelMapper orgRelMapper;
    @Autowired(required = false)
    private ChannelMapper channelMapper;
    @Autowired(required = false)
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired(required = false)
    private IReportService iReportService;
    @Autowired(required = false)
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;






    /**
     * 首次 无入参 根据用户登入账权限信息，获取对应的 C2 C3 C4 C5 C6
     * 回显用户所有权限所在的地市 以及节点下的所有子节点
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getStoreForUser(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        List<Organization> list=new ArrayList<>();
        List<Organization> arrayList=new ArrayList<>();
        Long orgId = null;
//        SystemUserDto user = BssSessionHelp.getSystemUserDto();
//        Long staffId = user.getStaffId();
//        //组织树控制权限
//        List<SystemPostDto> systemPostDtoList = user.getSystemPostDtoList();
//        String sysPostCode = systemPostDtoList.get(0).getSysPostCode();
        Long staffId = Long.valueOf(params.get("staffId").toString());
        String sysPostCode = params.get("sysPostCode").toString() ;
        Object areaId = params.get("areaId");
        if (areaId != null && areaId!= ""){
            arrayList = organizationMapper.selectByParentId(Long.valueOf(areaId.toString()));
            Page pageInfo2 = new Page(new PageInfo(arrayList));
            maps.put("resultMsg",arrayList);
            maps.put("resultCode",CODE_SUCCESS);
            return maps;
        }
        List<Map<String, Object>> staffOrgId = organizationMapper.getStaffOrgId(staffId);
        try {
            if (AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.SHENGJI.getSysArea().equals(sysPostCode)){
                //权限对应 超管 省管 显示所有 11个地市
                list = organizationMapper.selectMenu();
            }else if (AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.FENGJU.getSysArea().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea().equals(sysPostCode)) {
                //权限对应 C3 C4 C5 如 定位C3地市 并且显示当前C3下所有子节点
                // C4 显示前一级父节点 以及显示C4下所有子节点
                //确定用户 orgId
                for (Map<String, Object> map : staffOrgId) {
                    Object orgDivision = map.get("orgDivision");
                    Object orgId1 = map.get("ORG_NAME_"+sysPostCode);
                    if (orgId1 == null && areaId!="" && areaId != null){
                        orgId1 = Long.valueOf(areaId.toString());
                    }
                    if (orgDivision != null) {
                        if (orgDivision.toString().equals("30")) {
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        } else if (orgDivision.toString().equals("20")) {
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        } else if (orgDivision.toString().equals("10")) {
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }
                    }
                }
                 if (AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea().equals(sysPostCode)){
                    //定位C3地市 并且显示当前C3下所有子节点
                     Organization organizationC3 = organizationMapper.selectByPrimaryKey(orgId);
                     if (organizationC3!=null && organizationC3.getOrgName()!=null){
                         maps.put("C3",organizationC3.getOrgName());
                         maps.put("C3orgId",orgId);
                     }
                 }else if (AreaCodeEnum.sysAreaCode.FENGJU.getSysArea().equals(sysPostCode)){
                     //定位C4地市 并且显示当前C4下所有子节点
                     Organization organizationC4 = organizationMapper.selectByPrimaryKey(orgId);
                     if (organizationC4!=null && organizationC4.getOrgName()!=null){
                         maps.put("C4",organizationC4.getOrgName());
                         maps.put("C4orgId",orgId);
                     }
                     //并显示C3父节点
                     Object o = staffOrgId.get(0).get("ORG_NAME_" + "C3");
                     if (o!=null){
                         Organization organizationC3 = organizationMapper.selectByPrimaryKey(Long.valueOf(o.toString()));
                         if (organizationC3!=null && organizationC3.getOrgName()!=null){
                             maps.put("C3",organizationC3.getOrgName());
                             maps.put("C3orgId",Long.valueOf(o.toString()));
                         }
                     }
                 }else if (AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea().equals(sysPostCode)){
                     Organization organizationC5 = organizationMapper.selectByPrimaryKey(orgId);
                     if (organizationC5!=null && organizationC5.getOrgName()!=null){
                         maps.put("C5",organizationC5.getOrgName());
                         maps.put("C5orgId",orgId);
                     }
                     Object o3 = staffOrgId.get(0).get("ORG_NAME_" + "C3");
                     Object o4 = staffOrgId.get(0).get("ORG_NAME_" + "C4");
                     if (o3!=null){
                         Organization organizationC3 = organizationMapper.selectByPrimaryKey(Long.valueOf(o3.toString()));
                         if (organizationC3!=null && organizationC3.getOrgName()!=null){
                             maps.put("C3",organizationC3.getOrgName());
                             maps.put("C3orgId",Long.valueOf(o3.toString()));
                         }
                     }
                     if (o4!=null){
                         Organization organizationC4 = organizationMapper.selectByPrimaryKey(Long.valueOf(o4.toString()));
                         if (organizationC4!=null && organizationC4.getOrgName()!=null){
                             maps.put("C4",organizationC4.getOrgName());
                             maps.put("C4orgId",Long.valueOf(o4.toString()));
                         }
                     }
                 }
                    //查询子节点
                     list = organizationMapper.selectByParentId(orgId);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Page pageInfo = new Page(new PageInfo(list));
        maps.put("C2","浙江分公司");
        maps.put("C2orgId","800000000004");
        maps.put("resultMsg",list);
        maps.put("resultCode",CODE_SUCCESS);
        return maps;

    }

    @Override
    public Map<String, Object> getStore(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        List<Channel> channelList = new ArrayList<>();
        List<Organization> organizations = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        Object type = params.get("type");
        if (type == null){
            map.put("resultMsg","请传入type");
            return map;
        }
        Object orgId = params.get("orgId");
        if (orgId==null){
            map.put("resultMsg","请传入orgId");
            return map;
        }
        Organization organization = organizationMapper.selectByPrimaryKey(Long.valueOf(orgId.toString()));
        if (organization==null){
            map.put("resultMsg","请传入正确的参数");
        }
        if ("C5".equals(type.toString())){
            Object o = redisUtils.get("getStore_" + orgId.toString());
            if (o!=null){
                map.put("resultMsg",o);
                map.put("size",channelList.size());
                map.put("resultCode",CODE_SUCCESS);
//                map.put("size", channelList.size());
                return map;
            }
        }
        List<String> strings = areaList(organization.getOrgId(), resultList, organizations);
        strings.add(organization.getOrgId().toString());
        if (strings!=null && strings.size()>0){
            for (String string : strings) {
                List<OrgRel> orgRels = orgRelMapper.selectByAOrgId(string);
                if (orgRels.size()>0 && orgRels!=null){
                    for (OrgRel orgRel : orgRels) {
                        Channel channel = channelMapper.selectByPrimaryKey(orgRel.getzOrgId());
                        channelList.add(channel);
                    }
                }
            }
        }
        if ("C5".equals(type.toString())){
            redisUtils.set("getStore_"+orgId.toString(),channelList);
        }
        map.put("resultMsg",channelList);
        map.put("size",channelList.size());
        map.put("resultCode",CODE_SUCCESS);
        return map;
    }



    public List<String> areaList(Long parentId,List<String> resultList,List<Organization> areas){
        List<Organization> sysAreaList = organizationMapper.selectByParentId(parentId);
        if (sysAreaList.isEmpty()){
            return resultList;
        }
        for (Organization area : sysAreaList){
            resultList.add(area.getOrgId().toString());
            areas.add(area);
            areaList(area.getOrgId(),resultList,areas);
        }
        return resultList;
    }


    @Override
    public Map<String, Object> getChannel(Map<String, Object> params) {
        HashMap hashMap = new HashMap<String,Object>();
        List<Channel> channelList = new ArrayList<>();
        Object type = params.get("type");
        if (type!=null && "realTime".equals(type.toString())){
            //实时 5,6 问正义
            channelList = contactChannelMapper.getRealTimeChannel();
        }
        // 批量 4,5 问正义
        if (type!=null && "batch".equals(type.toString())){
            channelList = contactChannelMapper.getBatchChannel();
        }
        hashMap.put("resultMsg",channelList);
        hashMap.put("resultCode",CODE_SUCCESS);
        return hashMap;
    }

    //销报表查询接口
    @Override
    public Map<String, Object> getRptBatchOrder(Map<String, Object> params) {

        HashMap<String, Object> paramMap = new HashMap<>();
        //起始统计日期(YYYYMMDD)必填
        String startDate = params.get("startDate").toString();
        paramMap.put("startDate",startDate);
        //结束统计日期(YYYYMMDD)必填
        String endDate = params.get("endDate").toString();
        paramMap.put("endDate",endDate);
        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        String channelCode = params.get("channelCode").toString();
        paramMap.put("channelCode",channelCode);
        //活动ID(必填,ALL表示所有,多个用逗号隔开)
        String mktCampaignId = params.get("mktCampaignId").toString();
        paramMap.put("mktCampaignId",mktCampaignId);
        //省公司(必填)
        String orglevel1 = params.get("orglevel1").toString();
        paramMap.put("orglevel1",orglevel1);
        if (params.get("orglevel2")!=null){
            //地市(ALL表示所有,多个用逗号隔开)
            String orglevel2 = params.get("orglevel2").toString();
            paramMap.put("orglevel2",orglevel2);
        }
        if (params.get("orglevel3")!=null){
            //分局(ALL表示所有,多个用逗号隔开)
            String orglevel3 = params.get("orglevel3").toString();
            paramMap.put("orglevel3",orglevel3);
        }
        if (params.get("orglevel4")!=null){
            //支局(ALL表示所有,多个用逗号隔开)
            String orglevel4 = params.get("orglevel4").toString();
            paramMap.put("orglevel4",orglevel4);
        }
        if (params.get("orglevel5")!=null){
            //网格(ALL表示所有,多个用逗号隔开)
            String orglevel5 = params.get("orglevel5").toString();
            paramMap.put("orglevel5",orglevel5);
        }
        if (params.get("orgChannel")!=null){
            //门店(ALL表示所有,多个用逗号隔开)
            String orgChannel = params.get("orgChannel").toString();
            paramMap.put("orgChannel",orgChannel);
        }
        //销报表查询接口
        Map<String, Object> stringObjectMap = iReportService.queryRptEventOrder(paramMap);
        if (stringObjectMap.get("resultCode")!= null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            addParam(stringObjectMap);
        }
        return stringObjectMap;
    }

    private void addParam(Map<String, Object> stringObjectMap) {
        List<Map<String,Object>> rptBatchOrderList = (List<Map<String,Object>>)stringObjectMap.get("rptBatchOrderList");
        if (rptBatchOrderList.size()>0 && rptBatchOrderList!=null){
            for (Map<String, Object> map : rptBatchOrderList) {
                String mktCampaignId1 = map.get("mktCampaignId").toString();
                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(mktCampaignId1));
                //活动名称
                map.put("mktCampaignName",mktCampaignDO.getMktCampaignName());
                //活动开始是时间和结束时间
                map.put("beginTime",mktCampaignDO.getBeginTime());
                map.put("endTime",mktCampaignDO.getEndTime());
                String mktCampaignType = mktCampaignDO.getMktCampaignType();
                //活动类型
                if (mktCampaignType!=null ){
                    Map<String, String> paramsByValue = sysParamsMapper.getParamsByValue("CAM-C-0033",mktCampaignType);
                    map.put("mktCampaignType",paramsByValue.get("PARAM_NAME"));
                }
                //是否框架子活动
                MktCampaignRelDO MktCampaignRelDO = mktCampaignRelMapper.selectByZmktCampaignIdAndRelType(mktCampaignId1);
                if (MktCampaignRelDO!=null){
                    map.put("yesOrNo","是");
                }
                map.put("yesOrNo","否");
            }
        }
    }

    //活动报表查询接口
    @Override
    public Map<String, Object> queryRptBatchOrder(Map<String, Object> params) {
        HashMap<String, Object> paramMap = new HashMap<>();
        Map<String, Object> stringObjectMap = new HashMap<>();
        //起始统计日期(YYYYMMDD)必填
        String startDate = params.get("startDate").toString();
        paramMap.put("startDate",startDate);
        //结束统计日期(YYYYMMDD)必填
        String endDate = params.get("endDate").toString();
        paramMap.put("endDate",endDate);
        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        String channelCode = params.get("channelCode").toString();
        paramMap.put("channelCode",channelCode);
        if ( params.get("mktCampaignId") == null && params.get("dispatchStartDate") ==null){
            return stringObjectMap;
        }
        if (params.get("mktCampaignId")!=null){
            //活动ID(必填,ALL表示所有,多个用逗号隔开)
            String mktCampaignId = params.get("mktCampaignId").toString();
            paramMap.put("mktCampaignId",mktCampaignId);
        }
        if (params.get("dispatchStartDate")!=null){
            //派单开始时间(与活动ID二选一必填,yyyy-MM-dd HH:mm:ss)
            String dispatchStartDate = params.get("dispatchStartDate").toString();
            paramMap.put("dispatchStartDate",dispatchStartDate);
        }
        //省公司(必填)
        String orglevel1 = params.get("orglevel1").toString();
        paramMap.put("orglevel1",orglevel1);
        if (params.get("orglevel2")!=null){
            //地市(ALL表示所有,多个用逗号隔开)
            String orglevel2 = params.get("orglevel2").toString();
            paramMap.put("orglevel2",orglevel2);
        }
        if (params.get("orglevel3")!=null){
            //分局(ALL表示所有,多个用逗号隔开)
            String orglevel3 = params.get("orglevel3").toString();
            paramMap.put("orglevel3",orglevel3);
        }
        if (params.get("orglevel4")!=null){
            //支局(ALL表示所有,多个用逗号隔开)
            String orglevel4 = params.get("orglevel4").toString();
            paramMap.put("orglevel4",orglevel4);
        }
        if (params.get("orglevel5")!=null){
            //网格(ALL表示所有,多个用逗号隔开)
            String orglevel5 = params.get("orglevel5").toString();
            paramMap.put("orglevel5",orglevel5);
        }
        //门店(ALL表示所有,多个用逗号隔开)
        String orgChannel = params.get("orgChannel").toString();
        paramMap.put("orgChannel",orgChannel);
        if (params.get("flag")!=null){
            //网格(ALL表示所有,多个用逗号隔开)
            String flag = params.get("flag").toString();
            paramMap.put("flag",flag);
        }
        //活动报表查询接口
        stringObjectMap = iReportService.queryRptBatchOrder(paramMap);
        if (stringObjectMap.get("resultCode")!= null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            addParam(stringObjectMap);
        }
        return stringObjectMap;
    }

    @Override
    public Map<String, Object> queryRptBatchOrderTest(Map<String, Object> params) {
        Map<String, Object> stringObjectMap = (Map<String, Object>) params.get("key");
//        Map<String, Object> stringObjectMap = new HashMap<>();
        if (stringObjectMap.get("resultCode")!= null && "1".equals(stringObjectMap.get("resultCode").toString())){
            addParam(stringObjectMap);
        }
        return stringObjectMap;
    }


}
