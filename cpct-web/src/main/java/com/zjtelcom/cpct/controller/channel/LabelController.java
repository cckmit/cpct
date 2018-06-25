package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelGrp;
import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.LabelAddVO;
import com.zjtelcom.cpct.dto.LabelVO;
import com.zjtelcom.cpct.dto.ScriptVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/label")
public class LabelController extends BaseController {
    @Autowired
    private LabelService labelService;

    @PostMapping("addLabel")
    @CrossOrigin
    public RespInfo addLabel(Long userId, LabelAddVO addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.addLabel(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("editLabel")
    @CrossOrigin
    public RespInfo editLabel(Long userId, Label editVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.editLabel(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("deleteLabel")
    @CrossOrigin
    public RespInfo deleteLabel(Long userId, Long labelId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.deleteLabel(userId,labelId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("getLabelList")
    @CrossOrigin
    public RespInfo getLabelList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        List<LabelVO> voList = new ArrayList<>();
        try {
            voList = labelService.getLabelList(1L,params,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,voList);
    }

    @PostMapping("getLabelDetail")
    @CrossOrigin
    public RespInfo getLabelDetail(Long userId, Long labelId) {
        LabelVO vo = new LabelVO();
        try {
            vo = labelService.getLabelDetail(1L,labelId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }


    //标签组

    @PostMapping("addLabelGrp")
    @CrossOrigin
    public RespInfo addLabelGrp(Long userId, LabelGrp addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.addLabelGrp(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelGrp",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("editLabelGrp")
    @CrossOrigin
    public RespInfo editLabelGrp(Long userId, LabelGrp editVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.editLabelGrp(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editLabelGrp",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("deleteLabelGrp")
    @CrossOrigin
    public RespInfo deleteLabelGrp(Long userId, Long labelGrpId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.deleteLabelGrp(userId,labelGrpId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteLabelGrp",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("getLabelGrpList")
    @CrossOrigin
    public RespInfo getLabelGrpList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        List<LabelGrp> voList = new ArrayList<>();
        try {
            voList = labelService.getLabelGrpList(1L,params,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,voList);
    }

    @PostMapping("getLabelGrpDetail")
    @CrossOrigin
    public RespInfo getLabelGrpDetail(Long userId, Long labelGrpId) {
        LabelGrp vo = new LabelGrp();
        try {
            vo = labelService.getLabelGrpDetail(1L,labelGrpId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelGrpDetail",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }



    @PostMapping("addLabelGrpMbr")
    @CrossOrigin
    public RespInfo addLabelGrpMbr(Long userId, LabelGrpMbr addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.addLabelGrpMbr(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelGrpMbr",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("editLabelGrpMbr")
    @CrossOrigin
    public RespInfo editLabelGrpMbr(Long userId, Long grpMbrId,Long grpId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.editLabelGrpMbr(userId,grpMbrId,grpId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editLabelGrpMbr",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("deleteLabelGrpMbr")
    @CrossOrigin
    public RespInfo deleteLabelGrpMbr(Long userId, Long labelGrpMbrId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.deleteLabelGrpMbr(userId,labelGrpMbrId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteLabelGrpMbr",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }


    @PostMapping("getLabelGrpMbrDetail")
    @CrossOrigin
    public RespInfo getLabelGrpMbrDetail(Long userId, Long labelGrpMbrId) {
        LabelGrpMbr vo = new LabelGrpMbr();
        try {
            vo = labelService.getLabelGrpMbrDetail(1L,labelGrpMbrId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelGrpMbrDetail",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }


    @PostMapping("addLabelValue")
    @CrossOrigin
    public RespInfo addLabelValue(Long userId, LabelValue addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.addLabelValue(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelValue",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("editLabelValue")
    @CrossOrigin
    public RespInfo editLabelValue(Long userId, LabelValue editVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.editLabelValue(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editLabelValue",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("deleteLabelValue")
    @CrossOrigin
    public RespInfo deleteLabelValue(Long userId, Long labelValueId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = labelService.deleteLabelValue(userId,labelValueId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteLabelValue",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.ADD_SCRIPT_FAILURE.getErrorMsg(),ErrorCode.ADD_SCRIPT_FAILURE.getErrorCode());
        }
        return respInfo;
    }

    @PostMapping("getLabelValueDetail")
    @CrossOrigin
    public RespInfo getLabelValueDetail(Long userId, Long scriptId) {
        LabelValue vo = new LabelValue();
        try {
            vo = labelService.getLabelValueDetail(1L,scriptId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelValueDetail",e);
            return RespInfo.build(CODE_FAIL,ErrorCode.GET_SCRIPT_LIST.getErrorMsg(),ErrorCode.GET_SCRIPT_LIST.getErrorCode());
        }
        return RespInfo.build(CODE_SUCCESS,vo);
    }



}
