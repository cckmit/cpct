package com.zjtelcom.cpct.controller.system;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String listRole(@RequestParam("roleId") Long roleId,
                           @RequestParam("roleName") String roleName,
                           @RequestParam("page") Integer page,
                           @RequestParam("pageSize") Integer pageSize) {
        Map result = new HashMap();
        try {
            result = sysRoleService.listRole(roleId,roleName,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to listRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 根据员工id查询员工信息
     * @param roleId
     * @return
     */
    @RequestMapping(value = "getRole", method = RequestMethod.POST)
    @CrossOrigin
    public String getRole(@RequestParam("roleId") Long roleId) {
        Map result = new HashMap();
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
    public String saveStaff(SysRole sysRole) {
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
     * @param roleId
     * @param list
     * @return
     */
    @RequestMapping(value = "saveAuthority", method = RequestMethod.POST)
    @CrossOrigin
    public String saveAuthority(@RequestParam("roleId") Long roleId,@RequestParam("list") List<Long> list) {
        Map result = new HashMap();
        try {
            result = sysRoleService.saveAuthority(roleId,list);
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
    public String updateRole(SysRole sysRole) {
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
     * @param roleId
     * @return
     */
    @RequestMapping(value = "delRole", method = RequestMethod.POST)
    @CrossOrigin
    public String delRole(Long roleId) {
        Map result = new HashMap();
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
