package com.zjtelcom.cpct.service.impl.report;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.OrganizationMapper;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.service.report.ActivityStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ActivityStatisticsServiceImpl implements ActivityStatisticsService {

    @Autowired
    private OrganizationMapper organizationMapper;




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
        List<Map<String, Object>> staffOrgId = organizationMapper.getStaffOrgId(staffId);
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
        Page pageInfo = new Page(new PageInfo(list));
        maps.put("C2","浙江分公司");
        maps.put("C2orgId","800000000004");
        maps.put("resultMsg",list);
        Object areaId = params.get("areaId");
        if (areaId != null){
            arrayList = organizationMapper.selectByParentId(Long.valueOf(areaId.toString()));
            Page pageInfo2 = new Page(new PageInfo(arrayList));
            maps.put("resultMsg2",pageInfo2);
        }
        return maps;

    }

//    @Override
//    public Map<String, Object> getStoreForTwo(Map<String, Object> params) {
//        Map<String, Object> maps = new HashMap<>();
//        List<Organization> list=new ArrayList<>();
//        list = organizationMapper.selectMenu();
//        Page pageInfo = new Page(new PageInfo(list));
//        maps.put("C2","浙江分公司");
//        maps.put("C2orgId","800000000004");
//        maps.put("resultMsg",list);
//        return maps;
//    }
//
//    @Override
//    public Map<String, Object> getStoreForThree(Map<String, Object> params) {
//        Map<String, Object> maps = new HashMap<>();
//        List<Organization> list=new ArrayList<>();
//        String areaId = params.get("areaId").toString();
//        list = organizationMapper.selectByParentId(Long.valueOf(areaId));
//        Page pageInfo = new Page(new PageInfo(list));
//        return maps;
//    }
}
