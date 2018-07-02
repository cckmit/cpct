package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;
import com.zjtelcom.cpct.dto.channel.CamScriptEditVO;
import com.zjtelcom.cpct.dto.channel.CamScriptVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.CamScriptService;
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
@RequestMapping("${adminPath}/channel")
public class CamScriptController extends BaseController {

    @Autowired
    private CamScriptService camScriptService;

    /**
     * 添加营销活动脚本
     */
    @PostMapping("addCamScript")
    @CrossOrigin
    public Map<String,Object> addCamScript(CamScriptAddVO addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = camScriptService.addCamScript(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addCamScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 编辑营销活动脚本
     */
    @PostMapping("editCamScript")
    @CrossOrigin
    public  Map<String,Object> editCamScript(CamScriptEditVO editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = camScriptService.editCamScript(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editCamScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除营销活动脚本
     */
    @PostMapping("deleteCamScript")
    @CrossOrigin
    public  Map<String,Object> deleteCamScript(@RequestBody List<Long> camScriptIdList) {
        Map<String,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = camScriptService.deleteCamScript(userId,camScriptIdList);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteCamScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取营销活动脚本列表
     */
    @GetMapping("getCamScriptList")
    @CrossOrigin
    public  Map<String,Object> getCamScriptList(Long campaignId, Long evtContactConfId) {
        Map<String,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        if (campaignId==null || evtContactConfId==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," 未知的活动或渠道信息");
            return result;
        }
        try {
            result = camScriptService.getCamScriptList(userId,campaignId,evtContactConfId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getCamScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取营销活动脚本详情
     */
    @GetMapping("getCamScriptVODetail")
    @CrossOrigin
    public  Map<String,Object> getCamScriptVODetail(Long camScriptId) {
        Map<String,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        CamScriptVO vo = new CamScriptVO();
        try {
            result = camScriptService.getCamScriptVODetail(userId,camScriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getCamScriptVODetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

}
