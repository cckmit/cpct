package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.CamScriptAddVO;
import com.zjtelcom.cpct.dto.CamScriptEditVO;
import com.zjtelcom.cpct.dto.CamScriptVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/channel")
public class CamScriptController extends BaseController {

    @Autowired
    private CamScriptService camScriptService;


    @PostMapping("addSaddCamScriptcript")
    @CrossOrigin
    public RespInfo addCamScript(Long userId, CamScriptAddVO addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = camScriptService.addCamScript(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addCamScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_CAM_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_CAM_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("editCamScript")
    @CrossOrigin
    public RespInfo editCamScript(Long userId, CamScriptEditVO editVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = camScriptService.editCamScript(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editCamScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.EDIT_CAM_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.EDIT_CAM_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("deleteCamScript")
    @CrossOrigin
    public RespInfo deleteCamScript(@RequestBody List<Long> camScriptIdList) {
        Long userId = 1L;
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = camScriptService.deleteCamScript(userId,camScriptIdList);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteCamScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.DELETE_CAM_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.DELETE_CAM_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @GetMapping("getCamScriptList")
    @CrossOrigin
    public RespInfo getCamScriptList(Long userId, Long campaignId, Long evtContactConfId) {
        if (campaignId==null || evtContactConfId==null){
            return RespInfo.build(CODE_FAIL,"未知的活动或渠道信息",null);
        }
        List<CamScriptVO> voList = new ArrayList<>();
        try {
            voList = camScriptService.getCamScriptList(userId,campaignId,evtContactConfId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getCamScriptList",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_CAM_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_CAM_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,voList);
    }

    @GetMapping("getCamScriptVODetail")
    @CrossOrigin
    public RespInfo getCamScriptVODetail(Long userId, Long camScriptId) {
        CamScriptVO vo = new CamScriptVO();
        try {
            vo = camScriptService.getCamScriptVODetail(userId,camScriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getCamScriptVODetail",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_CAM_SCRIPT_DETAIL.getErrorMsg(),ErrorCode.GET_CAM_SCRIPT_DETAIL.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }

}
