package com.zjtelcom.cpct.controller.system;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/params")
public class SysParamsController extends BaseController {

    @Autowired
    private SysParamsService sysParamsService;

    /**
     * 查询参数列表（分页）
     *
     * @return
     */
    @RequestMapping(value = "listParams", method = RequestMethod.POST)
    @CrossOrigin
    public String listParams(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        String paramName = params.get("paramName");
        Long configType = Long.parseLong(params.get("configType"));
        Integer page = Integer.parseInt(params.get("page"));
        Integer pageSize = Integer.parseInt(params.get("pageSize"));

        try {
            result = sysParamsService.listParams(paramName, configType, page, pageSize);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to listParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 根据参数id查询配置参数信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "getParams", method = RequestMethod.POST)
    @CrossOrigin
    public String getParams(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long paramId = Long.parseLong(params.get("paramId"));

        try {
            result = sysParamsService.getParams(paramId);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to getParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }
        return JSON.toJSON(result).toString();
    }

    /**
     * 新增配置参数
     *
     * @param sysParams
     * @return
     */
    @RequestMapping(value = "saveParams", method = RequestMethod.POST)
    @CrossOrigin
    public String saveParams(@RequestBody SysParams sysParams) {
        Map result = new HashMap();
        try {
            result = sysParamsService.saveParams(sysParams);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to saveParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 修改配置参数
     *
     * @param sysParams
     * @return
     */
    @RequestMapping(value = "updateParams", method = RequestMethod.POST)
    @CrossOrigin
    public String updateParams(@RequestBody SysParams sysParams) {
        Map result = new HashMap();
        try {
            result = sysParamsService.updateParams(sysParams);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to updateParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 删除配置参数
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "delParams", method = RequestMethod.POST)
    @CrossOrigin
    public String delParams(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        Long paramId = Long.parseLong(params.get("paramId"));

        try {
            result = sysParamsService.delParams(paramId);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to delParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 根据关键字获取静态参数list
     * @param params
     * @return
     */
    @RequestMapping(value = "listParamsByKey", method = RequestMethod.POST)
    @CrossOrigin
    public String listParamsByKey(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        String key = params.get("key");

        try {
            result = sysParamsService.listParamsByKey(key);
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to delParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

    /**
     * 获取活动总览页面的筛选条件列表
     * created by linchao
     *
     * @return
     */
    @RequestMapping(value = "/listParamsByKeyForCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String listParamsByKeyForCampaign() {
        Map result = new HashMap();
        try {
            result = sysParamsService.listParamsByKeyForCampaign();
        } catch (Exception e) {
            logger.error("[op:SysParamsController] fail to delParams Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
        }

        return JSON.toJSON(result).toString();
    }

}
