package com.zjtelcom.cpct.controller.system;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${adminPath}/params")
public class SysParamsController extends BaseController {

    @Autowired
    private SysParamsService sysParamsService;

    /**
     * 查询参数列表（分页）
     * @return
     */
    @RequestMapping("/listParams")
    @CrossOrigin
    public String listParams(@RequestParam("paramName") String paramName,
                           @RequestParam("configType") Long configType) {
        List<SysParams> list = new ArrayList<>();
        try {
            list = sysParamsService.listParams(paramName,configType);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to listParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(list);
    }

    /**
     * 根据参数id查询配置参数信息
     * @param paramId
     * @return
     */
    @RequestMapping("/getParams")
    @CrossOrigin
    public String getParams(@RequestParam("paramId") Long paramId) {
        SysParams sysParams = new SysParams();
        try {
            sysParams = sysParamsService.getParams(paramId);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to getParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(sysParams);
    }

    /**
     * 新增配置参数
     * @param sysParams
     * @return
     */
    @RequestMapping("/saveParams")
    @CrossOrigin
    public String saveParams(SysParams sysParams) {

        try {
            sysParamsService.saveParams(sysParams);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to saveParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    /**
     * 修改配置参数
     * @param sysParams
     * @return
     */
    @RequestMapping("/updateParams")
    @CrossOrigin
    public String updateParams(SysParams sysParams) {

        try {
            sysParamsService.updateParams(sysParams);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to updateParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

    /**
     * 删除配置参数
     * @param paramId
     * @return
     */
    @RequestMapping("/delParams")
    @CrossOrigin
    public String delParams(Long paramId) {

        try {
            sysParamsService.delParams(paramId);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to delParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return initSuccRespInfo(null);
    }

}
