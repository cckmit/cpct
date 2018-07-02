package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.ScriptAddVO;
import com.zjtelcom.cpct.dto.channel.ScriptEditVO;
import com.zjtelcom.cpct.dto.channel.ScriptVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.ScriptService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public Map<String,Object> addScript(ScriptAddVO addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.addScript(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 编辑脚本
     */
    @PostMapping("editScript")
    @CrossOrigin
    public Map<String,Object> editScript(ScriptEditVO editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.editScript(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除脚本
     */
    @PostMapping("deleteScript")
    @CrossOrigin
    public Map<String,Object> deleteScript(Long scriptId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.deleteScript(userId,scriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取脚本列表
     */
    @PostMapping("getScriptList")
    @CrossOrigin
    public Map<String,Object> getScriptList(@RequestBody HashMap<String, Object> params) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        params.remove("page");
        params.remove("pageSize");
        try {
            result = scriptService.getScriptList(1L,params,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取脚本详情
     */
    @GetMapping("getScriptVODetail")
    @CrossOrigin
    public Map<String,Object> getScriptVODetail( Long scriptId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.getScriptVODetail(userId,scriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptVODetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }



}
