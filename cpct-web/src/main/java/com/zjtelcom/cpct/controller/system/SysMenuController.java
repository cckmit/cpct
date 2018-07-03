package com.zjtelcom.cpct.controller.system;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 获取所有菜单
     * @return
     */
    @RequestMapping(value = "listMenu", method = RequestMethod.POST)
    @CrossOrigin
    public String listMenu() {
        Map result = new HashMap();
        try {
            result = sysMenuService.listMenu();
        } catch (Exception e) {
            logger.error("[op:SysMenuController] fail to listMenu Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 根据角色id获取权限菜单
     * @param params
     * @return
     */
    @RequestMapping(value = "listMenuByRoleId", method = RequestMethod.POST)
    @CrossOrigin
    public String listMenuByRoleId(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long roleId = Long.parseLong(params.get("roleId"));

        try {
            result = sysMenuService.listMenuByRoleId(roleId);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }


}
