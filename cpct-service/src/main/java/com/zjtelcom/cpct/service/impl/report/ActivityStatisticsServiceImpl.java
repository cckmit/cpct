package com.zjtelcom.cpct.service.impl.report;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignRelMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.OrgRel;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
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
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getStoreForUser(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        List<Organization> list = new ArrayList<>();
        List<Organization> arrayList = new ArrayList<>();
        Long orgId = null;
        SystemUserDto user = BssSessionHelp.getSystemUserDto();
        Long staffId = user.getStaffId();
//        //组织树控制权限
        List<SystemPostDto> systemPostDtoList = user.getSystemPostDtoList();
        String sysPostCode = systemPostDtoList.get(0).getSysPostCode();
//        Long staffId = Long.valueOf(params.get("staffId").toString());
//        String sysPostCode = params.get("sysPostCode").toString();
        Object areaId = params.get("areaId");
        if (areaId != null && areaId != "") {
            arrayList = organizationMapper.selectByParentId(Long.valueOf(areaId.toString()));
            Page pageInfo2 = new Page(new PageInfo(arrayList));
            maps.put("resultMsg", arrayList);
            maps.put("resultCode", CODE_SUCCESS);
            return maps;
        }
        List<Map<String, Object>> staffOrgId = organizationMapper.getStaffOrgId(staffId);
        try {
            if (AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.SHENGJI.getSysArea().equals(sysPostCode)) {
                //权限对应 超管 省管 显示所有 11个地市
                list = organizationMapper.selectMenu();
            } else if (AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.FENGJU.getSysArea().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea().equals(sysPostCode)) {
                //权限对应 C3 C4 C5 如 定位C3地市 并且显示当前C3下所有子节点
                // C4 显示前一级父节点 以及显示C4下所有子节点
                //确定用户 orgId
                for (Map<String, Object> map : staffOrgId) {
                    Object orgDivision = map.get("orgDivision");
                    Object orgId1 = map.get("ORG_NAME_" + sysPostCode);
                    if (orgId1 == null && areaId != "" && areaId != null) {
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
                if (AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea().equals(sysPostCode)) {
                    //定位C3地市 并且显示当前C3下所有子节点
                    Organization organizationC3 = organizationMapper.selectByPrimaryKey(orgId);
                    if (organizationC3 != null && organizationC3.getOrgName() != null) {
                        maps.put("C3", organizationC3.getOrgName());
                        maps.put("C3orgId", orgId);
                    }
                } else if (AreaCodeEnum.sysAreaCode.FENGJU.getSysArea().equals(sysPostCode)) {
                    //定位C4地市 并且显示当前C4下所有子节点
                    Organization organizationC4 = organizationMapper.selectByPrimaryKey(orgId);
                    if (organizationC4 != null && organizationC4.getOrgName() != null) {
                        maps.put("C4", organizationC4.getOrgName());
                        maps.put("C4orgId", orgId);
                    }
                    //并显示C3父节点
                    Object o = staffOrgId.get(0).get("ORG_NAME_" + "C3");
                    if (o != null) {
                        Organization organizationC3 = organizationMapper.selectByPrimaryKey(Long.valueOf(o.toString()));
                        if (organizationC3 != null && organizationC3.getOrgName() != null) {
                            maps.put("C3", organizationC3.getOrgName());
                            maps.put("C3orgId", Long.valueOf(o.toString()));
                        }
                    }
                } else if (AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea().equals(sysPostCode)) {
                    Organization organizationC5 = organizationMapper.selectByPrimaryKey(orgId);
                    if (organizationC5 != null && organizationC5.getOrgName() != null) {
                        maps.put("C5", organizationC5.getOrgName());
                        maps.put("C5orgId", orgId);
                    }
                    Object o3 = staffOrgId.get(0).get("ORG_NAME_" + "C3");
                    Object o4 = staffOrgId.get(0).get("ORG_NAME_" + "C4");
                    if (o3 != null) {
                        Organization organizationC3 = organizationMapper.selectByPrimaryKey(Long.valueOf(o3.toString()));
                        if (organizationC3 != null && organizationC3.getOrgName() != null) {
                            maps.put("C3", organizationC3.getOrgName());
                            maps.put("C3orgId", Long.valueOf(o3.toString()));
                        }
                    }
                    if (o4 != null) {
                        Organization organizationC4 = organizationMapper.selectByPrimaryKey(Long.valueOf(o4.toString()));
                        if (organizationC4 != null && organizationC4.getOrgName() != null) {
                            maps.put("C4", organizationC4.getOrgName());
                            maps.put("C4orgId", Long.valueOf(o4.toString()));
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
        maps.put("C2", "浙江分公司");
        maps.put("C2orgId", "800000000004");
        maps.put("resultMsg", list);
        maps.put("resultCode", CODE_SUCCESS);
        return maps;

    }

    @Override
    public Map<String, Object> getStore(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        List<Channel> channelList = new ArrayList<>();
        List<Organization> organizations = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        Object type = params.get("type");
        if (type == null) {
            map.put("resultMsg", "请传入type");
            map.put("resultCode", CODE_FAIL);
            return map;
        }
        Object orgId = params.get("orgId");
        if (orgId == null) {
            map.put("resultMsg", "请传入orgId");
            map.put("resultCode", CODE_FAIL);
            return map;
        }
        Organization organization = organizationMapper.selectByPrimaryKey(Long.valueOf(orgId.toString()));
        if (organization == null) {
            map.put("resultMsg", "请传入正确的参数");
        }
        if ("C5".equals(type.toString())) {
            Object o = redisUtils.get("getStore_" + orgId.toString());
            if (o != null) {
                map.put("resultMsg", o);
                map.put("size", channelList.size());
                map.put("resultCode", CODE_SUCCESS);
//                map.put("size", channelList.size());
                return map;
            }
        }
        List<String> strings = areaList(organization.getOrgId(), resultList, organizations);
        strings.add(organization.getOrgId().toString());
        if (strings != null && strings.size() > 0) {
            for (String string : strings) {
                List<OrgRel> orgRels = orgRelMapper.selectByAOrgId(string);
                if (orgRels.size() > 0 && orgRels != null) {
                    for (OrgRel orgRel : orgRels) {
                        Channel channel = channelMapper.selectByPrimaryKey(orgRel.getzOrgId());
                        channelList.add(channel);
                    }
                }
            }
        }
        if ("C5".equals(type.toString())) {
            redisUtils.set("getStore_" + orgId.toString(), channelList);
        }
        map.put("resultMsg", channelList);
        map.put("size", channelList.size());
        map.put("resultCode", CODE_SUCCESS);
        return map;
    }


    public List<String> areaList(Long parentId, List<String> resultList, List<Organization> areas) {
        List<Organization> sysAreaList = organizationMapper.selectByParentId(parentId);
        if (sysAreaList.isEmpty()) {
            return resultList;
        }
        for (Organization area : sysAreaList) {
            resultList.add(area.getOrgId().toString());
            areas.add(area);
            areaList(area.getOrgId(), resultList, areas);
        }
        return resultList;
    }


    @Override
    public Map<String, Object> getChannel(Map<String, Object> params) {
        HashMap hashMap = new HashMap<String, Object>();
        List<Channel> channelList = new ArrayList<>();
        Object type = params.get("type");
        if (type != null && "realTime".equals(type.toString())) {
            //实时 5,6 问正义
            channelList = contactChannelMapper.getRealTimeChannel();
        }
        // 批量 4,5 问正义
        if (type != null && "batch".equals(type.toString())) {
            channelList = contactChannelMapper.getBatchChannel();
        }
        hashMap.put("resultMsg", channelList);
        hashMap.put("resultCode", CODE_SUCCESS);
        return hashMap;
    }

    //销报表查询接口
    //getRptEventOrder
    @Override
    public Map<String, Object> getRptEventOrder(Map<String, Object> params) {
        HashMap<String, Object> paramMap = new HashMap<>();
        //活动名称 模糊搜索
        Object mktCampaignName = params.get("mktCampaignName");
        //活动ID
        Object mktCampaignId = params.get("mktCampaignId");
        if ( (mktCampaignName == null || mktCampaignName == "") && (mktCampaignId == null || mktCampaignId == "")) {
            paramMap.put("resultCode", CODE_FAIL);
            return paramMap;
        }
        if ((mktCampaignName != null && mktCampaignName == "")  && (mktCampaignId != null && mktCampaignId == "")) {
            paramMap.put("mktCampaignId", mktCampaignId);
        }
        paramMap.put("mktCampaignName", mktCampaignName);
        paramMap.put("mktCampaignId", mktCampaignId);
        //统计日期
        Object endDate = params.get("endDate");
        if (endDate != null && endDate!="") {
            //类型转换 YYYYMMMDD YYYY-MM-DD
            Date date = DateUtil.parseDate(endDate.toString(), "YYYY-MM-DD");
            paramMap.put("endDate", date);
            //起始统计日期(YYYYMMDD)必填 dubbo接口用
            paramMap.put("startDate", date);
        }
        //活动状态 支持all
        Object statusCd = params.get("statusCd");
        paramMap.put("statusCd", statusCd);
        //活动类型 支持all
        Object mktCampaignType = params.get("mktCampaignType");
        paramMap.put("mktCampaignType", mktCampaignType);
        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        Object channelCode1 = params.get("channelCode");
        paramMap.put("channelCode1", channelCode1);
        StringBuilder stringBuilder = new StringBuilder();
        List<MktCampaignDO> mktCampaignList = mktCampaignMapper.queryRptBatchOrderForMktCampaign(paramMap);
        if (mktCampaignList.size()>0 && mktCampaignList!=null){
            for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                stringBuilder.append(mktCampaignDO.getMktCampaignId()).append(",");
            }
        }else {
            paramMap.put("resultMsg","没有找到对应的活动方案");
            paramMap.put("resultCode",CODE_FAIL);
            return paramMap;
        }
        //多个id  “，”拼接 去除最后的一个 ，
        String substring = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        paramMap.put("mktCampaignId", substring);
        //省公司(必填)
        String orglevel1 = params.get("orglevel1").toString();
        paramMap.put("orglevel1", orglevel1);
        if (params.get("orglevel2") != null && params.get("orglevel2") != "") {
            //地市(ALL表示所有,多个用逗号隔开)
            String orglevel2 = params.get("orglevel2").toString();
            paramMap.put("orglevel2", orglevel2);
        }
        if (params.get("orglevel3") != null && params.get("orglevel3") != "") {
            //分局(ALL表示所有,多个用逗号隔开)
            String orglevel3 = params.get("orglevel3").toString();
            paramMap.put("orglevel3", orglevel3);
        }
        if (params.get("orglevel4") != null && params.get("orglevel4") != "") {
            //支局(ALL表示所有,多个用逗号隔开)
            String orglevel4 = params.get("orglevel4").toString();
            paramMap.put("orglevel4", orglevel4);
        }
        if (params.get("orglevel5") != null && params.get("orglevel5") != "") {
            //网格(ALL表示所有,多个用逗号隔开)
            String orglevel5 = params.get("orglevel5").toString();
            paramMap.put("orglevel5", orglevel5);
        }
        if (params.get("orgChannel") != null && params.get("orgChannel") != "") {
            //门店(ALL表示所有,多个用逗号隔开)
            String orgChannel = params.get("orgChannel").toString();
            paramMap.put("orgChannel", orgChannel);
        }
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
        //销报表查询接口
        Map<String, Object> stringObjectMap = iReportService.queryRptEventOrder(paramMap);
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            stringObjectMap = addParams(stringObjectMap,page,pageSize);
        }
        return stringObjectMap;
    }


    //活动报表查询接口
    //queryRptBatchOrder
    @Override
    public Map<String, Object> getRptBatchOrder(Map<String, Object> params) {
        HashMap<String, Object> paramMap = new HashMap<>();
        Map<String, Object> stringObjectMap = new HashMap<>();
        //活动名称 模糊搜索
        Object mktCampaignName = params.get("mktCampaignName");
        //活动ID
        Object mktCampaignId = params.get("mktCampaignId");
        if ( (mktCampaignName == null || mktCampaignName == "") && (mktCampaignId == null || mktCampaignId == "")) {
            return paramMap;
        }
        if ((mktCampaignName != null && mktCampaignName == "")  && (mktCampaignId != null && mktCampaignId == "")) {
            paramMap.put("mktCampaignId", mktCampaignId);
        }
        paramMap.put("mktCampaignName", mktCampaignName);
        paramMap.put("mktCampaignId", mktCampaignId);
        //统计日期
        Object endDate = params.get("endDate");
        if (endDate != null) {
            //类型转换 YYYYMMMDD YYYY-MM-DD
            String s = dateConvertion(endDate.toString());
            paramMap.put("endDate", s);
            //起始统计日期(YYYYMMDD)必填 dubbo接口用
            paramMap.put("startDate", s);
        }
        //活动状态 支持all
        Object statusCd = params.get("statusCd");
        paramMap.put("statusCd", statusCd);
        //活动类型 支持all
        Object mktCampaignType = params.get("mktCampaignType");
        paramMap.put("mktCampaignType", mktCampaignType);
        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        Object channelCode1 = params.get("channelCode");
        paramMap.put("channelCode1", channelCode1);
        //活动创建地市
        if (params.get("lanId")!=null && params.get("lanId")!=""){
            Object lanId = params.get("lanId");
            paramMap.put("lanId", lanId.toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<MktCampaignDO> mktCampaignList = mktCampaignMapper.queryRptBatchOrderForMktCampaign(paramMap);
        if (mktCampaignList.size()>0 && mktCampaignList!=null){
            for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                stringBuilder.append(mktCampaignDO.getMktCampaignId()).append(",");
            }
        }else {
                paramMap.put("resultMsg","没有找到对应的活动方案");
                paramMap.put("resultCode","200");
                return paramMap;
        }
        //多个id  “，”拼接
        String substring = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        paramMap.put("mktCampaignId", substring);
        //省公司(必填)
        String orglevel1 = params.get("orglevel1").toString();
        paramMap.put("orglevel1", orglevel1);
        if (params.get("orglevel2") != null && params.get("orglevel2")!="") {
            //地市(ALL表示所有,多个用逗号隔开)
            String orglevel2 = params.get("orglevel2").toString();
            paramMap.put("orglevel2", orglevel2);
        }
        if (params.get("orglevel3") != null && params.get("orglevel3")!="") {
            //分局(ALL表示所有,多个用逗号隔开)
            String orglevel3 = params.get("orglevel3").toString();
            paramMap.put("orglevel3", orglevel3);
        }
        if (params.get("orglevel4") != null && params.get("orglevel4")!="") {
            //支局(ALL表示所有,多个用逗号隔开)
            String orglevel4 = params.get("orglevel4").toString();
            paramMap.put("orglevel4", orglevel4);
        }
        if (params.get("orglevel5") != null && params.get("orglevel5")!="") {
            //网格(ALL表示所有,多个用逗号隔开)
            String orglevel5 = params.get("orglevel5").toString();
            paramMap.put("orglevel5", orglevel5);
        }
        //门店(ALL表示所有,多个用逗号隔开)
        String orgChannel = params.get("orgChannel").toString();
        paramMap.put("orgChannel", orgChannel);
//        if (params.get("flag") != null) {
//            按活动展现还是按批次展现(0:按活动，1：按批次) 暂时只支持0
//            String flag = params.get("flag").toString();
            paramMap.put("flag", 0);
//        }
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
        //活动报表查询接口
        stringObjectMap = iReportService.queryRptBatchOrder(paramMap);
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            stringObjectMap = addParams(stringObjectMap, page, pageSize);
        }
        return stringObjectMap;
    }

    @Override
    public Map<String, Object> queryRptBatchOrderTest(Map<String, Object> params) {
        Map<String, Object> stringObjectMap = (Map<String, Object>) params.get("key");
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
        return addParams(stringObjectMap, page, pageSize);
    }

    private Map<String, Object> addParams(Map<String, Object> stringObjectMap, Integer page, Integer pageSize) {
        Map<String, Object> maps = new HashMap<>();
        List<HashMap<String, Object>> hashMaps = new ArrayList<>();
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            PageHelper.startPage(page,pageSize);
            List<Map<String, Object>> rptBatchOrderList = (List<Map<String, Object>>) stringObjectMap.get("rptBatchOrderList");
            if (rptBatchOrderList.size() > 0 && rptBatchOrderList != null) {
                for (Map<String, Object> map : rptBatchOrderList) {
                    HashMap<String, Object> resultMap = new HashMap<>();
                    String mktCampaignId1 = map.get("mktCampaignId").toString();
                    MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(Long.valueOf(mktCampaignId1));
                    //活动名称
                    resultMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName());
                    //活动开始是时间和结束时间
                    resultMap.put("beginTime", mktCampaignDO.getBeginTime());
                    resultMap.put("endTime", mktCampaignDO.getEndTime());
                    String mktCampaignType = mktCampaignDO.getMktCampaignType();
                    //活动类型
                    if (mktCampaignType != null) {
                        Map<String, String> paramsByValue = sysParamsMapper.getParamsByValue("CAM-C-0033", mktCampaignType);
                        resultMap.put("mktCampaignType", paramsByValue.get("PARAM_NAME"));
                    }
                    if (mktCampaignDO.getStatusCd() != null) {
                        //数字 需要转换一下
                        resultMap.put("statusCd", mktCampaignDO.getStatusCd());
                    }
                    List<HashMap<String, Object>> statisicts = new ArrayList<>();
                    Iterator<String> iter = map.keySet().iterator();
                    while (iter.hasNext()) {
                        HashMap<String, Object> msgMap = new HashMap<>();
                        String key = iter.next();
                        Object o = map.get(key);
                        if (key.equals("orderNum")){
                            msgMap.put("name","派单数");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("acceptOrderNum")){
                            msgMap.put("name","接单数");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("outBoundNum")){
                            msgMap.put("name","外呼数");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("orderSuccessNum")){
                            msgMap.put("name","成功数");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("acceptOrderRate")){
                            msgMap.put("name","接单率");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("outBoundRate")){
                            msgMap.put("name","外呼率");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("orderSuccessRate")){
                            msgMap.put("name","转化率");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("revenueReduceNum")){
                            msgMap.put("name","收入低迁数");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("revenueReduceRate")){
                            msgMap.put("name","收入低迁率");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                        if (key.equals("orgChannelRate")){
                            msgMap.put("name","门店有销率");
                            msgMap.put("nub",o);
                            statisicts.add(msgMap);
                        }
                    }
                    resultMap.put("statistics",statisicts);

                    //是否框架子活动
//                    MktCampaignRelDO MktCampaignRelDO = mktCampaignRelMapper.selectByZmktCampaignIdAndRelType(mktCampaignId1);
//                    if (MktCampaignRelDO != null) {
//                        msgMap.put("name","是否框架子活动");
//                        msgMap.put("nub","是");
//                    }else {
//                        msgMap.put("name","是否框架子活动");
//                        msgMap.put("nub","否");
//                    }
//                    statisicts.add(msgMap);
                    hashMaps.add(resultMap);
                }
            }
        }

        Page pageInfo = new Page(new PageInfo(hashMaps));
        maps.put("resultMsg",hashMaps);
        maps.put("resultCode",CODE_SUCCESS);
        return maps;
    }

    /**
     *      *@Description:日期转换，将接口返回的20180524转为2018-05-24
     *      *@author haohaounique
     *      *@Date 2018年5月24日
     *      *@param str 传递的日期字符串
     *      *@return
     *      *@exception :异常返回null,保障数据库的数据一致性,数据库格式yyyyMMdd
     *     
     */
    private static String dateConvertion(String str) {
        Date parse = null;
        String dateString = "";
        try {
            parse = new SimpleDateFormat("yyyyMMdd").parse(str);
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(parse);
        } catch (Exception e) {
            dateString = null;
        }

        return dateString;
    }
}
