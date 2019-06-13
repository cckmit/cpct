package com.zjtelcom.cpct.controller.channel;

import com.mysql.jdbc.util.ResultSetUtil;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.channel.LabelAddVO;
import com.zjtelcom.cpct.dto.channel.LabelEditVO;
import com.zjtelcom.cpct.dto.channel.LabelGrpParam;
import com.zjtelcom.cpct.dto.channel.QryMktScriptReq;
import com.zjtelcom.cpct.dto.pojo.Result;
import com.zjtelcom.cpct.service.channel.LabelCatalogService;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    private LabelCatalogService labelCatalogService;


    @PostMapping("batchAdd")
    @CrossOrigin
    public Map<String, Object> batchAdd(@RequestBody HashMap<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            List<String> stringList = (List<String>)param.get("nameList");
            result = labelCatalogService.batchAdd(stringList,Long.valueOf(param.get("parentId").toString()),Long.valueOf(param.get("level").toString()));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelCatalog",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addLabelCatalog");
            return result;
        }
        return result;
    }



    @PostMapping("addLabelCatalog")
    @CrossOrigin
    public Map<String, Object> addLabelCatalog(@RequestBody LabelCatalog param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelCatalogService.addLabelCatalog(param);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to addLabelCatalog",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addLabelCatalog");
            return result;
        }
        return result;
    }

    @PostMapping("listLabelCatalog")
    @CrossOrigin
    public Map<String, Object> listLabelCatalog(@RequestBody HashMap<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            String type = MapUtil.getString(param.get("type"));
            result = labelCatalogService.listLabelCatalog(type);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to listLabelCatalog",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listLabelCatalog");
            return result;
        }
        return result;
    }

    @PostMapping("listLabelByCatalogId")
    @CrossOrigin
    public Map<String, Object> listLabelByCatalogId(@RequestBody HashMap<String,Long> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelCatalogService.listLabelByCatalogId(param.get("catalogId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to listLabelByCatalogId",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listLabelByCatalogId");
            return result;
        }
        return result;
    }



//    @PostMapping("syncLabelInfo")
//    @CrossOrigin
//    public  Map<String, Object> syncLabelInfo(@RequestBody RecordModel record) {
//        Long userId = UserUtil.loginId();
//        Map<String,Object> result = new HashMap<>();
//        try {
//            result = syncLabelService.syncLabelInfo(record);
//        } catch (Exception e) {
//            logger.error("[op:ScriptController] fail to syncLabelInfo",e);
//            result.put("resultCode",CODE_FAIL);
//            result.put("resultMsg"," fail to syncLabelInfo");
//            return result;
//        }
//        return result;
//    }



    @PostMapping("shared")
    @CrossOrigin
    public Map<String, Object> shared(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.shared(userId,param.get("labelId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to shared",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to shared");
            return result;
        }
        return result;
    }


    /**
     * 通过标签组获取标签列表
     * @param param
     * @return
     */
    @PostMapping("getLabelListByLabelGrp")
    @CrossOrigin
    public Map<String, Object> getLabelListByLabelGrp(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelListByLabelGrp(userId,param.get("grpId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getScriptList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;

    }

    /**
     * 获取标签列表（@符标签名查询）
     */
    @PostMapping("getLabelNameListByParam")
    @CrossOrigin
    public Map<String, Object> getLabelNameListByParam(@RequestBody HashMap<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelNameListByParam(params);
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelNameListByParam",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getLabelNameListByParam");
            return result;
        }
        return result;
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
    public Map<String,Object> addLabel( @RequestBody LabelAddVO addVO) {
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
    public Map<String,Object> editLabel(@RequestBody Label label) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            LabelEditVO editVO = BeanUtil.create(label,new LabelEditVO());
            editVO.setLabelId(label.getInjectionLabelId());
            if (label.getOperator()!=null && !label.getOperator().equals("")){
                editVO.setOperatorList(ChannelUtil.StringToList(label.getOperator()));
            }
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
    public Map<String,Object> deleteLabel(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            Long labelId = param.get("labelId");
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
            result = labelService.getLabelList(userId,req.getLabelName(),req.getLabelCode(),req.getScope(),req.getConditionType(),req.getFitDomain(),req.getPage(),req.getPageSize());
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
    @PostMapping("getLabelDetail")
    @CrossOrigin
    public Map<String,Object> getLabelDetail(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            Long labelId = param.get("labelId");
            result = labelService.getLabelDetail(userId,labelId);
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
    public Map<String,Object> addLabelGrp(@RequestBody LabelGrp addVO) {
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
    public Map<String,Object> editLabelGrp(@RequestBody LabelGrp editVO) {
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
    public Map<String,Object> deleteLabelGrp(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.deleteLabelGrp(userId,param.get("labelGrpId"));
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
            result = labelService.getLabelGrpList(userId,params);
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
    @PostMapping("getLabelGrpDetail")
    @CrossOrigin
    public Map<String,Object> getLabelGrpDetail(@RequestBody HashMap<String,Long> param) {
        Long userId = UserUtil.loginId();
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.getLabelGrpDetail(1L,param.get("labelGrpId"));
        } catch (Exception e) {
            logger.error("[op:ScriptController] fail to getLabelGrpDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        return result;
    }

    /**
     * 标签组关联标签
     */
    @PostMapping("relateLabelGrp")
    @CrossOrigin
    public Map<String,Object> relateLabelGrp(@RequestBody LabelGrpParam param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = labelService.relateLabelGrp(param);
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
    public Map<String,Object> addLabelGrpMbr(@RequestBody LabelGrpMbr addVO) {
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

    /**
     * 派单规则通过标签指定人或区域
     * 查询派单标签
     */
    @PostMapping("distributeListRule")
    @CrossOrigin
    public Map<String,Object> distributeListRule(@RequestBody Map<String,Integer> params) {
        Map<String,Object> result = new HashMap<>();
        try {
            return labelService.distributeListRule(params.get("labelType"));
        } catch (Exception e) {
            logger.error("[op:LabelController] fail to distributeListRule", e);
            // return ResultUtil.responseFailResult();
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            result.put("resultObject","");
            return result;
        }
    }
}
