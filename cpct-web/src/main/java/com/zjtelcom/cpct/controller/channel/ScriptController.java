package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.MktScript;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;
import com.zjtelcom.cpct.service.channel.ScriptService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/script")
public class ScriptController extends BaseController  {

    @Autowired
    private ScriptService scriptService;


    /**
     * 获取渠道列表（不分页）
     * @param param
     * @return
     */
    @PostMapping("listScript")
    @CrossOrigin
    public Map<String, Object> getScriptList(@RequestBody HashMap<String,Object> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            String scriptName = null;
            if (param.get("scriptName")!=null){
                scriptName = param.get("scriptName").toString();
            }
            result = scriptService.getScriptList(userId,scriptName);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getScriptList");
            return result;
        }
        return result;

    }

    /**
     * 添加脚本
     */
    @PostMapping("addScript")
    @CrossOrigin
    public Map<String,Object> createMktScript(MktScript addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.createMktScript(userId,addVO);
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
    public Map<String,Object> modMktScript(MktScript editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.modMktScript(userId,editVO);
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
    public Map<String,Object> delMktScript(MktScript scriptId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.delMktScript(userId,scriptId);
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
    public Map<String,Object> qryMktScriptList(@RequestBody QryMktScriptReq req) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = scriptService.qryMktScriptList(1L,req);
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
