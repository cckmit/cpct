package com.zjtelcom.cpct.controller.system;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${adminPath}/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    @RequestMapping("/listMenu")
    @CrossOrigin
    public String listMenu() {
        List<SysMenu> list = new ArrayList<>();
        try {
            list = sysMenuService.listMenu();
        } catch (Exception e) {
            logger.error("[op:SysMenuController] fail to listMenu Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(list);
    }


    @RequestMapping("/listMenuByRoleId")
    @CrossOrigin
    public String listMenuByRoleId(@RequestParam("roleId") Long roleId) {
        List<SysMenu> list = new ArrayList<>();
        try {
            list = sysMenuService.listMenuByRoleId(roleId);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(list);
    }


}
