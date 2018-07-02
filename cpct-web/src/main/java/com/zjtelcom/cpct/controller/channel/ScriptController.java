package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.ScriptAddVO;
import com.zjtelcom.cpct.dto.channel.ScriptEditVO;
import com.zjtelcom.cpct.dto.channel.ScriptVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.ScriptService;
import com.zjtelcom.cpct.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/script")
public class ScriptController extends BaseController  {

    @Autowired
    private ScriptService scriptService;


    /**
     * 添加脚本
     */
    @PostMapping("addScript")
    @CrossOrigin
    public RespInfo addScript(Long userId, ScriptAddVO addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = scriptService.addScript(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    /**
     * 编辑脚本
     */
    @PostMapping("editScript")
    @CrossOrigin
    public RespInfo editScript(Long userId, ScriptEditVO editVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = scriptService.editScript(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.EDIT_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.EDIT_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    /**
     * 删除脚本
     */
    @PostMapping("deleteScript")
    @CrossOrigin
    public RespInfo deleteScript(Long userId, Long scriptId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = scriptService.deleteScript(userId,scriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.DELETE_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.DELETE_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    /**
     * 获取脚本列表
     */
    @PostMapping("getScriptList")
    @CrossOrigin
    public RespInfo getScriptList(@RequestBody HashMap<String, Object> params) {
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        params.remove("page");
        params.remove("pageSize");
        List<ScriptVO> voList = new ArrayList<>();
        try {
            voList = scriptService.getScriptList(1L,params,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,voList);
    }

    /**
     * 获取脚本详情
     */
    @GetMapping("getScriptVODetail")
    @CrossOrigin
    public RespInfo getScriptVODetail(Long userId, Long scriptId) {
        ScriptVO vo = new ScriptVO();
        try {
            vo = scriptService.getScriptVODetail(userId,scriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptVODetail",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_DETAIL.getErrorMsg(),ErrorCode.GET_SCRIPT_DETAIL.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }



}
