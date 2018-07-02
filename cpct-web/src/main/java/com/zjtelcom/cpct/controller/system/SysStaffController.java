package com.zjtelcom.cpct.controller.system;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.system.SysStaffDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户模块控制器
 */
@RestController
@RequestMapping("${adminPath}/staff")
public class SysStaffController extends BaseController {

    @Autowired
    private SysStaffService sysStaffService;

    /**
     * 查询员工列表（分页）
     *
     * @return
     */
    @RequestMapping(value = "listStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String listStaff(@RequestParam("staffAccount") String staffAccount,
                            @RequestParam("staffName") String staffName,
                            @RequestParam("status") Long status,
                            @RequestParam("page") Integer page,
                            @RequestParam("pageSize") Integer pageSize) {
        List<SysStaff> list = new ArrayList<>();
        Map result = new HashMap();
        try {
            result = sysStaffService.listStaff(staffAccount, staffName, status, page, pageSize);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 根据员工id查询员工信息
     *
     * @param staffId
     * @return
     */
    @RequestMapping(value = "getStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String getStaff(@RequestParam("staffId") Long staffId) {
        SysStaff sysStaff = new SysStaff();
        try {
            sysStaff = sysStaffService.getStaff(staffId);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(sysStaff);
    }

    /**
     * 新增员工
     *
     * @param sysStaffDTO
     * @return
     */
    @RequestMapping(value = "saveStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String saveStaff(SysStaffDTO sysStaffDTO) {

        try {
            sysStaffService.saveStaff(sysStaffDTO);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to saveStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    /**
     * 修改员工
     *
     * @param sysStaff
     * @return
     */
    @RequestMapping(value = "updateStaff", method = RequestMethod.POST)
    @CrossOrigin
    public String updateStaff(SysStaffDTO sysStaff) {

        try {
            sysStaffService.updateStaff(sysStaff);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to updateStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    /**
     * 修改员工账号状态
     *
     * @param staffId
     * @param status
     * @return
     */
    @RequestMapping(value = "changeStatus", method = RequestMethod.POST)
    @CrossOrigin
    public String changeStatus(@RequestParam("staffId") Long staffId, @RequestParam("status") Long status) {

        try {
            sysStaffService.changeStatus(staffId, status);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to updateStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    /**
     * 修改密码
     *
     * @param staffId  员工id
     * @param password 新密码
     * @return
     */
    @RequestMapping(value = "updatePassword", method = RequestMethod.POST)
    @CrossOrigin
    public String updatePassword(@RequestParam("staffId") Long staffId, @RequestParam("password") String password) {

        try {
            sysStaffService.updatePassword(staffId, password);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to updateStaff Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }


}
