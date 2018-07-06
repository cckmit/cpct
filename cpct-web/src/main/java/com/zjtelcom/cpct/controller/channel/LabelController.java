package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelGrp;
import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.LabelAddVO;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/label")
public class LabelController extends BaseController {
    @Autowired
    private LabelService labelService;
    @Autowired
    private InjectionLabelMapper labelMapper;

    @PostMapping("queryTriggerByleftOperand")
    @CrossOrigin
    public Map<String, Object> queryTriggerByleftOperand(@RequestBody List<Map<String ,String>> leftOperans) {
        List<Label> labelList = labelMapper.queryTriggerByLeftOpers(leftOperans);
        Map ma = new HashMap();
        ma.put("result",labelList);
        return ma;
    }


    /**
     * 获取标签列表（标签名和域查询）
     */
    @PostMapping("getLabelListByParam")
    @CrossOrigin
    public Map<String, Object> getLabelListByParam(@RequestBody HashMap<String, Object> params) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelListByParam(1L,params);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 添加标签
     */
    @PostMapping("addLabel")
    @CrossOrigin
    public Map<String,Object> addLabel( LabelAddVO addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.addLabel(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 编辑标签
     */
    @PostMapping("editLabel")
    @CrossOrigin
    public Map<String,Object> editLabel( Label editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.editLabel(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除标签
     */
    @PostMapping("deleteLabel")
    @CrossOrigin
    public Map<String,Object> deleteLabel(Long labelId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.deleteLabel(userId,labelId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addScript",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取标签列表
     */
    @PostMapping("getLabelList")
    @CrossOrigin
    public Map<String,Object> getLabelList(@RequestBody QryMktScriptReq req) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelList(1L,req.getParams(),req.getPage(),req.getPageSize());
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }


    /**
     * 获取标签详情
     */
    @GetMapping("getLabelDetail")
    @CrossOrigin
    public Map<String,Object> getLabelDetail(Long labelId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelDetail(1L,labelId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }


    //标签组
    /**
     * 添加标签组
     */
    @PostMapping("addLabelGrp")
    @CrossOrigin
    public Map<String,Object> addLabelGrp( LabelGrp addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.addLabelGrp(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelGrp",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 编辑标签组
     */
    @PostMapping("editLabelGrp")
    @CrossOrigin
    public Map<String,Object> editLabelGrp(LabelGrp editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.editLabelGrp(userId,editVO);

        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editLabelGrp",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除标签组
     */
    @PostMapping("deleteLabelGrp")
    @CrossOrigin
    public Map<String,Object> deleteLabelGrp(Long labelGrpId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.deleteLabelGrp(userId,labelGrpId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteLabelGrp",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取标签组列表
     */
    @PostMapping("getLabelGrpList")
    @CrossOrigin
    public Map<String,Object> getLabelGrpList( @RequestBody HashMap<String, Object> params) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            Integer page = MapUtil.getIntNum(params.get("page"));
            Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
            result = labelService.getLabelGrpList(1L,params,page,pageSize);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取标签组详情
     */
    @GetMapping("getLabelGrpDetail")
    @CrossOrigin
    public Map<String,Object> getLabelGrpDetail( Long labelGrpId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelGrpDetail(1L,labelGrpId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelGrpDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }


    /**
     * 添加标签组成员
     */
    @PostMapping("addLabelGrpMbr")
    @CrossOrigin
    public Map<String,Object> addLabelGrpMbr( LabelGrpMbr addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.addLabelGrpMbr(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelGrpMbr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 编辑标签组成员
     */
    @PostMapping("editLabelGrpMbr")
    @CrossOrigin
    public Map<String,Object> editLabelGrpMbr( Long grpMbrId,Long grpId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.editLabelGrpMbr(userId,grpMbrId,grpId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editLabelGrpMbr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除标签组成员
     */
    @PostMapping("deleteLabelGrpMbr")
    @CrossOrigin
    public Map<String,Object> deleteLabelGrpMbr( Long labelGrpMbrId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.deleteLabelGrpMbr(userId,labelGrpMbrId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteLabelGrpMbr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取标签组成员列表
     */
    @GetMapping("getLabelGrpMbrDetail")
    @CrossOrigin
    public Map<String,Object> getLabelGrpMbrDetail( Long labelGrpMbrId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelGrpMbrDetail(1L,labelGrpMbrId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelGrpMbrDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 添加标签值信息
     */
    @PostMapping("addLabelValue")
    @CrossOrigin
    public Map<String,Object> addLabelValue(LabelValue addVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.addLabelValue(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelValue",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 编辑标签值信息
     */
    @PostMapping("editLabelValue")
    @CrossOrigin
    public Map<String,Object> editLabelValue(LabelValue editVO) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.editLabelValue(userId,editVO);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to editLabelValue",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 删除标签值信息
     */
    @PostMapping("deleteLabelValue")
    @CrossOrigin
    public Map<String,Object> deleteLabelValue(Long labelValueId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.deleteLabelValue(userId,labelValueId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to deleteLabelValue",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 获取标签值列表
     */
    @GetMapping("getLabelValueDetail")
    @CrossOrigin
    public Map<String,Object> getLabelValueDetail( Long labelValueId) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelValueDetail(1L,labelValueId);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelValueDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }



}
