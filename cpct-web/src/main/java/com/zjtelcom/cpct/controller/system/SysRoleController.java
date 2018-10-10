package com.zjtelcom.cpct.controller.system;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("${adminPath}/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询角色列表（分页）
     * @return
     */
    @RequestMapping(value = "listRole", method = RequestMethod.POST)
    @CrossOrigin
    public String listRole(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        String roleName = params.get("roleName");
        Integer page = Integer.parseInt(params.get("page"));
        Integer pageSize = Integer.parseInt(params.get("pageSize"));

        try {
            result = sysRoleService.listRole(roleName,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to listRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 根据员工id查询员工信息
     * @param params
     * @return
     */
    @RequestMapping(value = "getRole", method = RequestMethod.POST)
    @CrossOrigin
    public String getRole(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long roleId = Long.parseLong(params.get("roleId"));

        try {
            result = sysRoleService.getRole(roleId);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to getRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 新增角色
     * @param sysRole
     * @return
     */
    @RequestMapping(value = "saveRole", method = RequestMethod.POST)
    @CrossOrigin
    public String saveStaff(@RequestBody SysRole sysRole) {
        Map result = new HashMap();
        try {
            result = sysRoleService.saveRole(sysRole);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to saveRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 保存权限
     * @param params
     * @return
     */
    @RequestMapping(value = "saveAuthority", method = RequestMethod.POST)
    @CrossOrigin
    public String saveAuthority(@RequestBody Map<String,Object> params) {
        Map result = new HashMap();

        try {
            result = sysRoleService.saveAuthority(params);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to saveRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 修改员工
     * @param sysRole
     * @return
     */
    @RequestMapping(value = "updateRole", method = RequestMethod.POST)
    @CrossOrigin
    public String updateRole(@RequestBody SysRole sysRole) {
        Map result = new HashMap();
        try {
            result = sysRoleService.updateRole(sysRole);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to updateRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 删除角色
     * @param params
     * @return
     */
    @RequestMapping(value = "delRole", method = RequestMethod.POST)
    @CrossOrigin
    public String delRole(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long roleId = Long.parseLong(params.get("roleId"));

        try {
            result = sysRoleService.delRole(roleId);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to updateRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 获取角色下拉框数据
     * @return
     */
    @RequestMapping(value = "listRoleDropDown", method = RequestMethod.POST)
    @CrossOrigin
    public String listRoleDropDown() {
        Map result = new HashMap();
        try {
            result = sysRoleService.listRoleAll();
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to listRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }


}
