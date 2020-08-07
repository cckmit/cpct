package com.zjtelcom.cpct.controller.system;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.common.SysmgrResultObject;
import com.ctzj.smt.bss.sysmgr.model.dto.PrivilegeDetail;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.OrganizationMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.dto.channel.SystemUserVO;
import com.zjtelcom.cpct.dto.system.SysStaffDTO;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.PostEnum;
import com.zjtelcom.cpct.service.system.SysStaffService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * 用户模块控制器
 */
@RestController
@RequestMapping("${adminPath}/staff")
public class SysStaffController extends BaseController {

    @Autowired
    private SysStaffService sysStaffService;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private SysAreaMapper sysAreaMapper;


    @RequestMapping("/getSysUser")
    @ResponseBody
    @CrossOrigin
    public Map<String,Object> getSysUser() {
        Map<String,Object> result = new HashMap<>();
        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
        SystemUserVO userVO = BeanUtil.create(userDetail,new SystemUserVO());
        try {
            Long staffId = userDetail.getStaffId();
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
                if (organization!=null){
                    if (organization.getOrgNameC4()!=null && !"".equals(organization.getOrgNameC4())){
                        Organization c4Org = organizationMapper.selectByPrimaryKey(Long.valueOf(organization.getOrgNameC4()));
                        if (c4Org!=null){
                            SysArea sysArea = sysAreaMapper.getByCityFour(c4Org.getRegionId().toString());
                            if (sysArea!=null){
                                userVO.setC4CodeName(sysArea.getAreaId().toString());
                            }
                        }
                    }
                    userVO.setC5CodeName(organization.getOrgNameC5());
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
            userVO.setUserLevel(sysPostCode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",userVO);
        return result;
    }

    @RequestMapping("/getSysMenuList")
    @ResponseBody
    @CrossOrigin
    public Map<String,Object> getSysMenuList() {
        Map<String,Object> result = new HashMap<>();
        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<PrivilegeDetail> parentList = new ArrayList<>();
        for (PrivilegeDetail detail : userDetail.getPrivilegeDetails()){
            if (detail.getPrivFuncRelDetails()==null || detail.getPrivFuncRelDetails().isEmpty() || detail.getPrivFuncRelDetails().get(0)==null
                    || detail.getPrivFuncRelDetails().get(0).getFuncMenuDto()==null){
                continue;
            }
            if (detail.getPrivCode().contains("CPCP") && detail.getPrivFuncRelDetails().get(0).getFuncMenuDto().getParMenuId()==null){
                parentList.add(detail);
            }
        }
        for (PrivilegeDetail parent  : parentList){
            Long parentId = parent.getPrivFuncRelDetails().get(0).getFuncMenuDto().getMenuId();
            Map<String,Object> map = new HashMap<>();
            map.put("parent",parent);
            List<PrivilegeDetail> childList = new ArrayList<>();
            for (PrivilegeDetail detail : userDetail.getPrivilegeDetails()){
                if (detail.getPrivFuncRelDetails()!=null && !detail.getPrivFuncRelDetails().isEmpty() && detail.getPrivFuncRelDetails().get(0)!=null
                        && detail.getPrivFuncRelDetails().get(0).getFuncMenuDto()!=null ) {
                    Long detailId = detail.getPrivFuncRelDetails().get(0).getFuncMenuDto().getParMenuId();
                    if (detailId != null && detailId.equals(parentId)) {
                        childList.add(detail);
                    }
                }
            }
            map.put("childList",childList);
            resultList.add(map);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resultList);
        return result;
    }


    /**
     * 查询员工列表（分页）
     *
     * @return
     */
    @RequestMapping(value = "listStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String listStaff(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        try {
            result = sysStaffService.listStaff(params);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 根据员工id查询员工信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "getStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String getStaff(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long staffId = Long.parseLong(params.get("staffId"));

        try {
            result = sysStaffService.getStaff(staffId);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 新增员工
     *
     * @param sysStaffDTO
     * @return
     */
    @RequestMapping(value = "saveStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String saveStaff(@RequestBody SysStaffDTO sysStaffDTO) {
        Map result = new HashMap();
        try {
            result = sysStaffService.saveStaff(sysStaffDTO);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to saveStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 修改员工
     *
     * @param sysStaff
     * @return
     */
    @RequestMapping(value = "updateStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String updateStaff(@RequestBody SysStaffDTO sysStaff) {
        Map result = new HashMap();
        try {
            result = sysStaffService.updateStaff(sysStaff);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to updateStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 修改员工账号状态
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "changeStatus", method = RequestMethod.POST)
    @CrossOrigin
    public String changeStatus(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long staffId = Long.parseLong(params.get("staffId"));
        Long status = Long.parseLong(params.get("status"));

        try {
            result = sysStaffService.changeStatus(staffId, status);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to updateStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    @CrossOrigin
    public String updatePassword(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long staffId = Long.parseLong(params.get("staffId"));
        String password = params.get("password");

        try {
            result = sysStaffService.updatePassword(staffId, password);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to updateStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 获取当前用户信息
     */
    @RequestMapping(value = "loginInfo", method = RequestMethod.POST)
    @CrossOrigin
    public String loginInfo() {
        Map result = new HashMap();
        Long staffId = UserUtil.loginId();
        try {
            result = sysStaffService.getStaff(staffId);
        }catch (Exception e) {
            logger.error("[op:SysStaffController] fail to loginInfo Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }


    /**
     * getStaffByCode
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "getStaffByCode", method = RequestMethod.POST)
    @CrossOrigin
    public String getStaffByCode(@RequestBody Map<String,String> params) {
        Map result = new HashMap();
        String staffCode = (String) params.get("staffCode");
        try {
            result = sysStaffService.getStaffByCode(staffCode);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to getStaffByCode Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

}
