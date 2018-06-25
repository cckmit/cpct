package com.zjtelcom.cpct.controller.system;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.dto.system.SysStaffVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    @RequestMapping("/listStaff")
    @CrossOrigin
    public String listStaff(@RequestParam("staffCode") String staffCode,
                            @RequestParam("staffName") String staffName,
                            @RequestParam("status") Long status,
                            @RequestParam("page") Integer page,
                            @RequestParam("pageSize") Integer pageSize) {
        List<SysStaff> list = new ArrayList<>();
        try {
            list = sysStaffService.listStaff(staffCode, staffName, status, page, pageSize);
        } catch (Exception e) {
            logger.error("[op:SysStaffController] fail to eventList Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(list);
    }

    /**
     * 根据员工id查询员工信息
     *
     * @param staffId
     * @return
     */
    @RequestMapping("/getStaff")
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
     * @param sysStaffVO
     * @return
     */
    @RequestMapping("/saveStaff")
    @CrossOrigin
    public String saveStaff(SysStaffVO sysStaffVO) {

        try {
            sysStaffService.saveStaff(sysStaffVO);
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
    @RequestMapping("/updateStaff")
    @CrossOrigin
    public String updateStaff(SysStaffVO sysStaff) {

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
    @RequestMapping("/changeStatus")
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
    @RequestMapping("/updatePassword")
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
