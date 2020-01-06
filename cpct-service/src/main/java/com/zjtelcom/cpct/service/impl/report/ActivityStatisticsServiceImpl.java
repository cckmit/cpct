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
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.OrgRel;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.service.impl.querySaturation.QuerySaturationCpcServiceImpl;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.enums.StatusCode.STATUS_CODE_PRE_PAUSE;
import static com.zjtelcom.cpct.enums.StatusCode.STATUS_CODE_ROLL;

@Service
@Transactional
public class ActivityStatisticsServiceImpl implements ActivityStatisticsService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ActivityStatisticsServiceImpl.class);

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
    @Autowired
    private TrialOperationMapper trialOperationMapper;
    @Autowired
    private UCCPService uccpService;
    @Autowired
    private SysAreaMapper sysAreaMapper;


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
        String sysPostCode = null;
        sysPostCode = systemPostDtoList.get(0).getSysPostCode();
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
            if (AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysPostCode().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.SHENGJI.getSysPostCode().equals(sysPostCode)) {
                //权限对应 超管 省管 显示所有 11个地市
                list = organizationMapper.selectMenu();
            } else if (AreaCodeEnum.sysAreaCode.FENGONGSI.getSysPostCode().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.FENGJU.getSysPostCode().equals(sysPostCode) ||
                    AreaCodeEnum.sysAreaCode.ZHIJU.getSysPostCode().equals(sysPostCode)) {
                //权限对应 C3 C4 C5 如 定位C3地市 并且显示当前C3下所有子节点
                sysPostCode = AreaCodeEnum.getSysAreaBySysPostCode(sysPostCode);
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
            //实时（随销） 5,6 问正义  getRptEventOrder
            channelList = contactChannelMapper.getRealTimeChannel();
        }
        // 批量（派单） 4,5 问正义 queryRptBatchOrder
        if (type != null && "batch".equals(type.toString())) {
            channelList = contactChannelMapper.getBatchChannel();
        }
        hashMap.put("resultMsg", channelList);
        hashMap.put("resultCode", CODE_SUCCESS);
        return hashMap;
    }

    //随销报表查询接口
    //getRptEventOrder
    @Override
    public Map<String, Object> getRptEventOrder(Map<String, Object> params) {
        HashMap<String, Object> paramMap = new HashMap<>();
        //活动名称 模糊搜索
        Object mktCampaignName = params.get("mktCampaignName");
        //活动ID
        Object mktCampaignId = params.get("mktCampaignId");
        if ((mktCampaignName == null || mktCampaignName == "") && (mktCampaignId == null || mktCampaignId == "")) {
//            paramMap.put("resultCode", CODE_FAIL);
//            return paramMap;
            paramMap.put("mktCampaignId", "");
            paramMap.put("mktCampaignName", "");
        }
        if (mktCampaignId != null && mktCampaignId != "") {
            paramMap.put("mktCampaignId", mktCampaignId);
            paramMap.put("mktCampaignName", "");
        }else {
            paramMap.put("mktCampaignName", mktCampaignName);
        }
        //统计日期 必填字段
        Object endDate = params.get("endDate");
        if (endDate != null && endDate != "") {
            //类型转换 YYYYMMMDD YYYY-MM-DD
//            Date date = DateUtil.parseDate(endDate.toString(), "YYYY-MM-DD");
            paramMap.put("endDate", endDate.toString().replaceAll("-", ""));
            //起始统计日期(YYYYMMDD)必填 dubbo接口用
            paramMap.put("startDate", endDate.toString().replaceAll("-", ""));
        } else {
            paramMap.put("resultCode", CODE_FAIL);
            paramMap.put("resultMsg", "时间是必填字段");
            return paramMap;
        }
        //活动状态 支持all
        Object statusCd = params.get("statusCd");
        if (statusCd == "" || statusCd == null) {
            paramMap.put("statusCd", "all");
        } else {
            paramMap.put("statusCd", statusCd);
        }
        //活动类型 支持all
        Object mktCampaignType = params.get("mktCampaignType");
        if (mktCampaignType == "" || mktCampaignType == null) {
            paramMap.put("mktCampaignType", "all");
        } else {
            paramMap.put("mktCampaignType", mktCampaignType);
        }
        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        Object channelCode = params.get("channelCode");
        if (channelCode == "" || channelCode == null) {
            paramMap.put("channelCode", "all");
        } else {
            paramMap.put("channelCode", channelCode);
        }
        // 添加主题过滤 todo 2020 / 1/2 x
        Object theMe = params.get("theMe");
        if (theMe != "" && theMe != null) {
            paramMap.put("theMe", theMe);
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<MktCampaignDO> mktCampaignList = mktCampaignMapper.queryRptBatchOrderForMktCampaign(paramMap);
        //todo 修改为 init_id 原 getMktCampaignId
        if (mktCampaignList.size() > 0 && mktCampaignList != null) {
            for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                stringBuilder.append(mktCampaignDO.getInitId()).append(",");
            }
        } else {
            paramMap.put("resultMsg", "没有找到对应的活动方案");
            paramMap.put("resultCode", CODE_FAIL);
            return paramMap;
        }
        //多个id  “，”拼接 去除最后的一个 ，
        String substring = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        paramMap.put("mktCampaignId", substring);
        //省公司(必填)
        Object orglevel1 = params.get("orglevel1");
        if (orglevel1 == null || orglevel1 == "") {
            paramMap.put("resultMsg", "省公司 必填 ");
            paramMap.put("resultCode", CODE_FAIL);
            return paramMap;
        } else {
            paramMap.put("orglevel1", orglevel1);
        }
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
        paramMap.put("currenPage", page);
        paramMap.put("pageSize", pageSize);
        //销报表查询接口
        Map<String, Object> stringObjectMap = null;
        try {
            stringObjectMap = iReportService.queryRptEventOrder(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("销报表查询接口:stringObjectMap"+stringObjectMap);
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            stringObjectMap = addParams(stringObjectMap, page, pageSize,mktCampaignType);
            Object reqId = stringObjectMap.get("reqId");
            Object total = stringObjectMap.get("total");
            paramMap.put("pageSize",total);
            paramMap.put("page","1");
            if (reqId!=null && reqId!=""){
                redisUtils.set(reqId.toString(),paramMap);
            }
        } else {
            Object reqId = stringObjectMap.get("reqId");
            stringObjectMap.put("resultCode", CODE_FAIL);
            stringObjectMap.put("resultMsg", "查询无结果 queryRptEventOrder error:"+reqId.toString());
        }
        return stringObjectMap;
    }


    //活动报表查询接口  派单
    //queryRptBatchOrder
    @Override
    public Map<String, Object> getRptBatchOrder(Map<String, Object> params) {
        HashMap<String, Object> paramMap = new HashMap<>();
        Map<String, Object> stringObjectMap = new HashMap<>();
        //活动名称 模糊搜索
        Object mktCampaignName = params.get("mktCampaignName");
        //活动ID
        Object mktCampaignId = params.get("mktCampaignId");
        if ((mktCampaignName == null || mktCampaignName == "") && (mktCampaignId == null || mktCampaignId == "")) {
//            paramMap.put("resultCode", CODE_FAIL);
//            return paramMap;
            paramMap.put("mktCampaignId", "");
            paramMap.put("mktCampaignName", "");
        }
        if (mktCampaignId != null && mktCampaignId != "") {
            paramMap.put("mktCampaignId", mktCampaignId);
            paramMap.put("mktCampaignName", "");
        }else {
            paramMap.put("mktCampaignName", mktCampaignName);
        }
        //统计日期 必填字段
        Object endDate = params.get("endDate");
        if (endDate != null && endDate != "") {
            //类型转换 YYYYMMMDD YYYY-MM-DD
//            Date date = DateUtil.parseDate(endDate.toString(), "YYYY-MM-DD");
            paramMap.put("endDate", endDate.toString().replaceAll("-", ""));
            //起始统计日期(YYYYMMDD)必填 dubbo接口用
            paramMap.put("startDate", endDate.toString().replaceAll("-", ""));
        } else {
            paramMap.put("resultCode", CODE_FAIL);
            paramMap.put("resultMsg", "时间是必填字段");
            return paramMap;
        }
        //活动状态 支持all
        Object statusCd = params.get("statusCd");
        if (statusCd == "" || statusCd == null) {
            paramMap.put("statusCd", "all");
        } else {
            paramMap.put("statusCd", statusCd);
        }
        //活动类型 支持all
        Object mktCampaignType = params.get("mktCampaignType");
        if (mktCampaignType == "" || mktCampaignType == null) {
            paramMap.put("mktCampaignType", "all");
        } else {
            paramMap.put("mktCampaignType", mktCampaignType);
        }
        //渠道编码(必填,ALL表示所有,多个用逗号隔开)
        Object channelCode = params.get("channelCode");
        if (channelCode == "" || channelCode == null) {
            paramMap.put("channelCode", "all");
        } else {
            paramMap.put("channelCode", channelCode);
        }
        //活动创建地市
        if (params.get("lanId") != null && params.get("lanId") != "") {
            Object lanId = params.get("lanId");
            paramMap.put("lanId", lanId.toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        //派单增加派单时间段过滤
        Object activeDate = params.get("activeDate");
        Object overDate = params.get("overDate");
        if (activeDate != null && activeDate != "" &&  overDate !=null && overDate!="") {
            //类型转换 YYYYMMMDD YYYY-MM-DD
            paramMap.put("activeDate", activeDate.toString());
            paramMap.put("overDate", overDate.toString());
        }
        // 添加主题过滤 todo 2020 / 1/2 x
        Object theMe = params.get("theMe");
        if (theMe != "" && theMe != null) {
            paramMap.put("theMe", theMe);
        }
        List<String> campaignIdLists =mktCampaignMapper.selectByMktCampaingIDFromTrial(paramMap);
        if (campaignIdLists==null || campaignIdLists.size()<=0) {
            paramMap.put("resultCode", CODE_FAIL);
            paramMap.put("resultMsg", "无区间段派单活动!");
            return paramMap;
        }
        paramMap.put("campaignIdLists",campaignIdLists);
        List<MktCampaignDO> mktCampaignList = mktCampaignMapper.queryRptBatchOrderForMktCampaignFromDate(paramMap);
        //todo 修改为 init_id 原 getMktCampaignId
        if (mktCampaignList.size() > 0 && mktCampaignList != null) {
            for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                stringBuilder.append(mktCampaignDO.getInitId()).append(",");
            }
        } else {
            paramMap.put("resultMsg", "没有找到对应的活动方案");
            paramMap.put("resultCode", CODE_FAIL);
            return paramMap;
        }
        //多个id  “，”拼接
        String substring = stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        paramMap.put("mktCampaignId", substring);
        //省公司(必填)
        Object orglevel1 = params.get("orglevel1");
        if (orglevel1 == null || orglevel1 == "") {
            paramMap.put("resultMsg", "省公司 必填 ");
            paramMap.put("resultCode", CODE_FAIL);
            return paramMap;
        } else {
            paramMap.put("orglevel1", orglevel1);
        }
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
        if (params.get("flag") != null && params.get("flag") != "") {
            //按活动展现还是按批次展现(0:按活动，1：按批次)
            String flag = params.get("flag").toString();
            paramMap.put("flag", flag);
        } else {
            //暂时只支持0
            paramMap.put("flag", 0);
        }
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
        paramMap.put("currenPage", page);
        paramMap.put("pageSize", pageSize);
        //活动报表查询接口
        try {
            stringObjectMap = iReportService.queryRptBatchOrder(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("活动报表查询接口:queryRptBatchOrder"+stringObjectMap);
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            stringObjectMap = addParams(stringObjectMap, page, pageSize,mktCampaignType);
            Object reqId = stringObjectMap.get("reqId");
            Object total = stringObjectMap.get("total");
            paramMap.put("pageSize",total);
            paramMap.put("page","1");
            if (reqId!=null && reqId!=""){
                redisUtils.set(reqId.toString(),paramMap);
            }
        } else {
            Object reqId = stringObjectMap.get("reqId");
            stringObjectMap.put("resultCode", CODE_FAIL);
            stringObjectMap.put("resultMsg", "查询无结果 queryRptBatchOrder error :" + reqId.toString());
        }
        return stringObjectMap;
    }

    @Override
    public Map<String, Object> queryRptBatchOrderTest(Map<String, Object> params) {
        Map<String, Object> stringObjectMap = (Map<String, Object>) params.get("key");
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
        Object mktCampaignType =params.get("mktCampaignType");
        return addParams(stringObjectMap, page, pageSize,mktCampaignType);
    }



    private Map<String, Object> addParams(Map<String, Object> stringObjectMap, Integer page, Integer pageSize,Object ymktCampaignType) {
        Map<String, Object> maps = new HashMap<>();
        List<HashMap<String, Object>> hashMaps = new ArrayList<>();
        List<Map<String, Object>> data = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (stringObjectMap.get("resultCode") != null && "1".equals(stringObjectMap.get("resultCode").toString())) {
            //event 解析                                          派单
            Object rptBatchOrderList1 = stringObjectMap.get("rptBatchOrderList");
            if (rptBatchOrderList1 != null && rptBatchOrderList1 != "") {
                data = (List<Map<String, Object>>) stringObjectMap.get("rptBatchOrderList");
                if (data.size() > 0 && data != null) {
                    for (Map<String, Object> map : data) {
                        HashMap<String, Object> resultMap = new HashMap<>();
                        String mktCampaignId1 = map.get("mktCampaignId").toString();
                        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByInitId(Long.valueOf(mktCampaignId1));
                        if (mktCampaignDO == null){
                            // 如果有为空 跳过
                            continue;
                        }
                        //活动类型 过滤页面筛选条件
                        String mktCampaignType = mktCampaignDO.getMktCampaignType();
                        if (!ymktCampaignType.toString().equals("") && !"all".equals(ymktCampaignType.toString())){
                            if (!mktCampaignType.equals(ymktCampaignType.toString())){
                                continue;
                            }
                        }
                        //活动id
                        resultMap.put("mktCampaignId", mktCampaignDO.getMktCampaignId());
                        //活动名称
                        resultMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName());
                        //活动开始是时间和结束时间
                        resultMap.put("beginTime", fmt.format(mktCampaignDO.getPlanBeginTime()));
                        resultMap.put("endTime", fmt.format(mktCampaignDO.getPlanEndTime()));
                        resultMap.put("mktActivityBnr", mktCampaignDO.getMktActivityNbr());
                        //关单规则名称
//                        String CloseRuleName = mktCampaignMapper.getCloseRuleNameFromMktCamId(mktCampaignDO.getMktCampaignId());
//                        resultMap.put("mktCloseRuleName", CloseRuleName);
                        //所属地市
                        Long lanId = mktCampaignDO.getLanId();
                        SysArea sysArea = sysAreaMapper.selectByPrimaryKey(Integer.valueOf(lanId.toString()));
                        if (mktCampaignDO.getRegionFlg().equals("C4") || mktCampaignDO.getRegionFlg().equals("C5")) {
                            if (mktCampaignDO.getLanIdFour()!=null && mktCampaignDO.getLanIdFour().toString().length()< 6){
                                SysArea sysAreaFour = sysAreaMapper.selectByPrimaryKey(Integer.valueOf(mktCampaignDO.getLanIdFour().toString()));
                                resultMap.put("area", sysArea.getName()+"-"+sysAreaFour.getName());
                            }else {
                                resultMap.put("area", sysArea.getName());
                            }
                        }else {
                            resultMap.put("area", sysArea.getName());
                        }
                        //渠道编码
                        Object channel = map.get("channel");
                        if (channel== null || "" == channel || "null" == channel){
                            resultMap.put("channel", "");
                        }else {
                            // todo 渠道编码展示
                            Channel channel1 = contactChannelMapper.selectByCode(channel.toString());
                            resultMap.put("channel", channel1.getContactChlName());
                        }
                        if (mktCampaignType != null) {
                            Map<String, String> paramsByValue = sysParamsMapper.getParamsByValue("CAM-C-0033", mktCampaignType);
                            resultMap.put("mktCampaignType", paramsByValue.get("PARAM_NAME"));
                        }
                        if (mktCampaignDO.getStatusCd() != null) {
                            //数字 需要转换一下
                            resultMap.put("statusCd", mktCampaignDO.getStatusCd());
                        }
                        List<HashMap<String, Object>> statisicts = new ArrayList<>();
                        //添加框架活动是否字活动
                        map.put("yesOrNo", "1");
                        Iterator<String> iter = map.keySet().iterator();
                        while (iter.hasNext()) {
                            HashMap<String, Object> msgMap = new HashMap<>();
                            String key = iter.next();
                            Object o = map.get(key);
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
                            if (key.equals("yesOrNo")) {
                                MktCampaignRelDO MktCampaignRelDO = mktCampaignRelMapper.selectByZmktCampaignIdAndRelType(mktCampaignId1);
                                if (MktCampaignRelDO != null) {
                                    msgMap.put("name", "是否框架子活动");
                                    msgMap.put("nub", "是");
                                    statisicts.add(msgMap);
                                } else {
                                    msgMap.put("name", "是否框架子活动");
                                    msgMap.put("nub", "否");
                                    statisicts.add(msgMap);
                                }
                            }
                            // todo 新加关单编码 2020 1/2 x
                            if (key.equals("batchNbr")){
                                msgMap.put("name", "批次编码");
                                msgMap.put("nub", o);
                            }
                            if (key.equals("batchNbr")){
                                msgMap.put("name", "派单方式");
                                TrialOperation trialOperation = trialOperationMapper.selectByBatchNum(o.toString());
                                if (trialOperation.getCreateStaff().toString().equals("1000")){
                                    msgMap.put("nub", "标签取数");
                                }else {
                                    msgMap.put("nub", "清单导入");
                                }

                            }
                            if (key.equals("closeNumber")){
                                resultMap.put("mktCloseRuleName", o.toString());
                            }
                        }
                        resultMap.put("statistics", statisicts);
                        hashMaps.add(resultMap);
                    }
                }
            } else {
                data = (List<Map<String, Object>>) stringObjectMap.get("rptEventOrderList");
                if (data.size() > 0 && data != null) {
                    for (Map<String, Object> map : data) {
                        HashMap<String, Object> resultMap = new HashMap<>();
                        String mktCampaignId1 = map.get("mktCampaignId").toString();
                        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByInitId(Long.valueOf(mktCampaignId1));
                        if (mktCampaignDO == null){
                            // 如果有为空 跳过
                            continue;
                        }
                        //活动类型 过滤页面筛选条件
                        String mktCampaignType = mktCampaignDO.getMktCampaignType();
                        if (!ymktCampaignType.toString().equals("") && !"all".equals(ymktCampaignType.toString())){
                            if (!mktCampaignType.equals(ymktCampaignType.toString())){
                                continue;
                            }
                        }
                        //活动id
                        resultMap.put("mktCampaignId", mktCampaignDO.getMktCampaignId());
                        //活动名称
                        resultMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName());
                        //活动开始是时间和结束时间
                        resultMap.put("beginTime", fmt.format(mktCampaignDO.getPlanBeginTime()));
                        resultMap.put("endTime", fmt.format(mktCampaignDO.getPlanEndTime()));
                        resultMap.put("mktActivityBnr", mktCampaignDO.getMktActivityNbr());
                        //关单规则名称
//                        String CloseRuleName = mktCampaignMapper.getCloseRuleNameFromMktCamId(mktCampaignDO.getMktCampaignId());
//                        resultMap.put("mktCloseRuleName", CloseRuleName);
                        //所属地市
                        Long lanId = mktCampaignDO.getLanId();
                        SysArea sysArea = sysAreaMapper.selectByPrimaryKey(Integer.valueOf(lanId.toString()));
                        if (mktCampaignDO.getRegionFlg().equals("C4") || mktCampaignDO.getRegionFlg().equals("C5")) {
                            if (mktCampaignDO.getLanIdFour()!=null && mktCampaignDO.getLanIdFour().toString().length()< 6){
                                SysArea sysAreaFour = sysAreaMapper.selectByPrimaryKey(Integer.valueOf(mktCampaignDO.getLanIdFour().toString()));
                                resultMap.put("area", sysArea.getName()+"-"+sysAreaFour.getName());
                            }else {
                                resultMap.put("area", sysArea.getName());
                            }
                        }else {
                            resultMap.put("area", sysArea.getName());
                        }
                        //渠道编码
                        Object channel = map.get("channel");
                        if (channel== null || "" == channel || "null" == channel){
                            resultMap.put("channel", "");
                        }else {
                            // todo 渠道编码展示
                            Channel channel1 = contactChannelMapper.selectByCode(channel.toString());
                            resultMap.put("channel", channel1.getContactChlName());
                        }
                        if (mktCampaignType != null) {
                            Map<String, String> paramsByValue = sysParamsMapper.getParamsByValue("CAM-C-0033", mktCampaignType);
                            resultMap.put("mktCampaignType", paramsByValue.get("PARAM_NAME"));
                        }
                        if (mktCampaignDO.getStatusCd() != null) {
                            //数字 需要转换一下
                            resultMap.put("statusCd", mktCampaignDO.getStatusCd());
                        }
                        List<HashMap<String, Object>> statisicts = new ArrayList<>();
                        //添加框架活动是否字活动
                        map.put("yesOrNo", "1");
                        Iterator<String> iter = map.keySet().iterator();
                        while (iter.hasNext()) {
                            HashMap<String, Object> msgMap = new HashMap<>();
                            String key = iter.next();
                            Object o = map.get(key);
                            if ("".equals(o) || "null".equals(o) || null == o){
                                o = 0+"";
                            }
                            if (key.equals("contactNum")) {
                                msgMap.put("name", "客户接触数");
                                msgMap.put("nub", o);
                                statisicts.add(msgMap);
                            }
                            if (key.equals("orderNum")) {
                                msgMap.put("name", "商机推荐数");
                                msgMap.put("nub", o);
                                statisicts.add(msgMap);
                            }
                            if (key.equals("orderSuccessNum")) {
                                msgMap.put("name", "商机成功数");
                                msgMap.put("nub", o);
                                statisicts.add(msgMap);
                            }
                            if (key.equals("contactRate")) {
                                msgMap.put("name", "客触转化率");
                                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                                msgMap.put("nub", percentFormat);
                                statisicts.add(msgMap);
                            }
                            if (key.equals("orderRate")) {
                                //转换成百分比 保留二位小数位
                                String percentFormat = getPercentFormat(Double.valueOf(o.toString()), 2, 2);
                                msgMap.put("name", "商机转化率");
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
                            if (key.equals("yesOrNo")) {
                                MktCampaignRelDO MktCampaignRelDO = mktCampaignRelMapper.selectByZmktCampaignIdAndRelType(mktCampaignId1);
                                if (MktCampaignRelDO != null) {
                                    msgMap.put("name", "是否框架子活动");
                                    msgMap.put("nub", "是");
                                    statisicts.add(msgMap);
                                } else {
                                    msgMap.put("name", "是否框架子活动");
                                    msgMap.put("nub", "否");
                                    statisicts.add(msgMap);
                                }
                            }
                            if (key.equals("closeNumber")){
                                resultMap.put("mktCloseRuleName", o.toString());
                            }
                        }
                        resultMap.put("statistics", statisicts);
                        hashMaps.add(resultMap);
                    }
                }
            }
        }
        maps.put("pageSize", stringObjectMap.get("pageSize"));
        maps.put("page", stringObjectMap.get("currenPage"));
        maps.put("total", stringObjectMap.get("total"));
        maps.put("resultMsg", hashMaps);
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("reqId",  stringObjectMap.get("reqId"));
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

    /**
     * 将double类型数据转换为百分比格式，并保留小数点前IntegerDigits位和小数点后FractionDigits位
     *
     * @param d
     * @param IntegerDigits
     * @param FractionDigits
     * @return
     */
    public static String getPercentFormat(double d, int IntegerDigits, int FractionDigits) {
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(IntegerDigits);//小数点前保留几位
        nf.setMinimumFractionDigits(FractionDigits);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    /**
     * 不活跃表表   // 2000-实时, 1000-批量
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getMktCampaignDetails(Map<String, Object> params) {
        HashMap<String, Object> resultMap = new HashMap<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        //TIGGER_TYPE = '1000' # 2000-实时, 1000-批量
        Object tiggerType = params.get("tiggerType");
        if ("".equals(tiggerType.toString()) || tiggerType == null){
            resultMap.put("resultMsg", "实时或批量请传入类型！");
            resultMap.put("resultCode", CODE_FAIL);
            return resultMap;
        }else {
            // 2000-实时, 1000-批量
            hashMap.put("tiggerType",tiggerType);
        }
        //类型 1000（当前时间提前一个月） 2000（当前时间提前二个月） 3000 （当前时间提前三个月）
        Object createDate = params.get("createDate");
        if ("".equals(createDate.toString()) || createDate == null){
            resultMap.put("resultMsg", "时间类型不能为空！");
            resultMap.put("resultCode", CODE_FAIL);
            return resultMap;
        }else {
            String s = DateToString(createDate.toString());
            hashMap.put("createDate",s);
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        //分页参数
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
        if ("1000".equals(tiggerType.toString())){
            PageHelper.startPage(page, pageSize);
            List<MktCampaignDO> mktCampaignList = mktCampaignMapper.getMktCampaignDetails(hashMap);
            Page pageInfo = new Page(new PageInfo(mktCampaignList));
            if (!mktCampaignList.isEmpty()){
                //添加时间格式
                for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                    if (mktCampaignDO.getCreateDate()!=null){
                        mktCampaignDO.setStrBeginTime(fmt.format(mktCampaignDO.getCreateDate()));
                    }
                }
            }
            resultMap.put("pageInfo",pageInfo);
            resultMap.put("resultMsg", mktCampaignList);
            resultMap.put("resultCode", CODE_SUCCESS);
        }else if("2000".equals(tiggerType)){
            Object o = null;
            Map<String, Object> stringObjectMap = iReportService.queryValidCampaign();
            logger.info("查看dubbo返回活动返回结果"+stringObjectMap);
            if (stringObjectMap.get("resultCode")!=null && "1".equals(stringObjectMap.get("resultCode").toString())){
                if ("1000".equals(createDate.toString())){
                    //30Ds 30天有效活动
                    o = stringObjectMap.get("30Ds");
                }else if ("2000".equals(createDate.toString())){
                    //60Ds 60天有效活动
                    o = stringObjectMap.get("60Ds");
                }else if ("3000".equals(createDate.toString())){
                    //90Ds 90天有效活动
                    o = stringObjectMap.get("90Ds");
                }
            }
            String[] split = o.toString().split(",");
            logger.info("查看活动有效时间的长度！@#￥%"+split.length+"和参数!@#$%"+split);
            List<String> userList = new ArrayList<String>();
            Collections.addAll(userList, split);
            logger.info("查看dubbo返回活动天数是啥！！！！！@#￥"+userList);
            PageHelper.startPage(page, pageSize);
            List<MktCampaignDO> mktCampaignList =  mktCampaignMapper.getMktCampaignDetailsForDate(userList);
            Page pageInfo = new Page(new PageInfo(mktCampaignList));
            if (!mktCampaignList.isEmpty()){
                //添加时间格式
                for (MktCampaignDO mktCampaignDO : mktCampaignList) {
                    if (mktCampaignDO.getCreateDate()!=null){
                        mktCampaignDO.setStrBeginTime(fmt.format(mktCampaignDO.getCreateDate()));
                    }
                }
            }
            resultMap.put("pageInfo",pageInfo);
            resultMap.put("resultMsg", mktCampaignList);
            resultMap.put("resultCode", CODE_SUCCESS);
        }
        return resultMap;
    }

    // xyl 活动报表模糊搜索 type 1000 随销  2000 派单
    @Override
    public Map<String, Object> getActivityStatisticsByName(Map<String, Object> params) {
        HashMap<String, Object> result = new HashMap<>();
        Object name = params.get("name");
        if (name!=null){
            List<MktCampaignDO> mktCampaignList = mktCampaignMapper.getActivityStatisticsByName(name.toString());
            result.put("resultMsg",mktCampaignList);
            result.put("resultCode","200");
        }else {
            result.put("resultMsg","name为空");
            result.put("resultCode","500");
        }
        return result;
    }


    public static String DateToString(String s){
        Date date = new Date();//当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//格式化对象
        Calendar calendar = Calendar.getInstance();//日历对象
        calendar.setTime(date);//设置当前日期
        if (s.equals("1000")){
            calendar.add(Calendar.MONTH, -1);//月份减一
            return sdf.format(calendar.getTime());
        }else if ("2000".equals(s)){
            calendar.add(Calendar.MONTH, -2);//月份减二
            return sdf.format(calendar.getTime());
        }else if ("3000".equals(s)){
            calendar.add(Calendar.MONTH, -3);//月份减三
            return sdf.format(calendar.getTime());
        }
//        System.out.println(sdf.format(calendar.getTime()));//输出格式化的日期
        return sdf.format(calendar.getTime());
    }


    /**
     * 超过3个月的未试算或派单的活动下线
     */
    @Override
    public void MoreThan3MonthsOffline() {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("tiggerType", "1000");
            hashMap.put("createDate", "3000");
            hashMap.put("page", 1);
            hashMap.put("pageSize", 9999);
            Map<String, Object> mktCampaignDetails = getMktCampaignDetails(hashMap);
            List<MktCampaignDO> camList = mktCampaignDetails == null ?
                    new ArrayList<MktCampaignDO>() : (List<MktCampaignDO>) mktCampaignDetails.get("resultMsg");
            mktCampaignDetails = getMktCampaignDetails(hashMap);
            StringBuilder sb = new StringBuilder();
            for (MktCampaignDO mktCampaignDO : camList) {
                try {
                    mktCampaignMapper.changeMktCampaignStatus(mktCampaignDO.getMktCampaignId(), STATUS_CODE_PRE_PAUSE.getStatusCode(), new Date(), 0L);
                    String sendContent = "您创建的活动" + mktCampaignDO.getMktCampaignName() + "满足不活跃活动条件，当前活动已被自动过期。";
                    uccpService.sendShortMessage4CampaignStaff(mktCampaignDO, sendContent);
                    sb.append(mktCampaignDO.getMktCampaignId());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            redisUtils.set("OFFLINE_" + DateUtil.date2String(new Date()), sb);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> delectConsumerlogByDate(Map<String, Object> params) {
        HashMap<String, Object> map = new HashMap<>();
        Object createDate = params.get("createDate");
        int result = sysParamsMapper.delectConsumerlogByDate(createDate.toString());
        if (result>0){
            map.put("code","200");
            map.put("msg","成功");
        }else {
            map.put("code","500");
            map.put("msg","失败");
        }
        return map;
    }

}
