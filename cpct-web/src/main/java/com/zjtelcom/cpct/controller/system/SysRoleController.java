package com.zjtelcom.cpct.controller.system;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${adminPath}/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询角色列表（分页）
     * @return
     */
    @RequestMapping("/listRole")
    @CrossOrigin
    public String listRole(@RequestParam("roleId") Long roleId,
                           @RequestParam("roleName") String roleName) {
        List<SysRole> list = new ArrayList<>();
        try {
            list = sysRoleService.listRole(roleId,roleName);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to listRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(list);
    }

    /**
     * 根据员工id查询员工信息
     * @param roleId
     * @return
     */
    @RequestMapping("/getRole")
    @CrossOrigin
    public String getRole(@RequestParam("roleId") Long roleId) {
        SysRole sysRole = new SysRole();
        try {
            sysRole = sysRoleService.getRole(roleId);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to getRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(sysRole);
    }

    /**
     * 新增角色
     * @param sysRole
     * @return
     */
    @RequestMapping("/saveRole")
    @CrossOrigin
    public String saveStaff(SysRole sysRole) {

        try {
            sysRoleService.saveRole(sysRole);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to saveRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }


    @RequestMapping("/saveAuthority")
    @CrossOrigin
    public String saveAuthority(@RequestParam("roleId") Long roleId,@RequestParam("list") List<Long> list) {

        try {
            sysRoleService.saveAuthority(roleId,list);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to saveRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    /**
     * 修改员工
     * @param sysRole
     * @return
     */
    @RequestMapping("/updateRole")
    @CrossOrigin
    public String updateRole(SysRole sysRole) {

        try {
            sysRoleService.updateRole(sysRole);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to updateRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    @RequestMapping("/delRole")
    @CrossOrigin
    public String delRole(Long roleId) {

        try {
            sysRoleService.delRole(roleId);
        } catch (Exception e) {
            logger.error("[op:SysRoleController] fail to updateRole Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }



}
