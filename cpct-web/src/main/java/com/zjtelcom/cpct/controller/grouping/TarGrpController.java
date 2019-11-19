package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.service.grouping.TrialOperationService;
import com.zjtelcom.cpct.util.FastJsonUtils;
import com.zjtelcom.cpct.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description 目标分群Controller
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/targrp")
public class TarGrpController extends BaseController {

    @Autowired
    private TarGrpService tarGrpService;
    @Autowired(required = false)
    private TrialOperationService operationService;


    @RequestMapping("/conditionSwitch")
    @CrossOrigin
    public String conditionSwitch(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            Long conditionId =MapUtil.getLongNum(param.get("conditionId"));
            String type = MapUtil.getString(param.get("type"));
            String  value =  MapUtil.getString(param.get("value"));
            maps = tarGrpService.conditionSwitch(conditionId,type,value);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to conditionSwitch for tarGrpDTO = {}!" +
                    " Exception: ", JSONArray.toJSON(maps), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }




    @PostMapping("labelListByCampaignId")
    @CrossOrigin
    public Map<String, Object> labelListByCampaignId(@RequestBody HashMap<String,Object> param) {
        Map<String,Object> resutlt = new HashMap<>();
        resutlt = tarGrpService.labelListByCampaignId((List<Integer>)param.get("idList"));
        return resutlt;
    }


    @RequestMapping("/createTarGrpByTemplateId")
    @CrossOrigin
    public String createTarGrpByTemplateId(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            Long templateId = Long.valueOf(param.get("templateId").toString());
            Long oldTarGrpId = Long.valueOf(param.get("oldTarGrpId").toString());
            String  needDeleted = param.get("needDeleted")==null ? "1" : param.get("needDeleted").toString();
            maps = tarGrpService.createTarGrpByTemplateId(templateId,oldTarGrpId,needDeleted);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to saveTagNumFetch for tarGrpDTO = {}!" +
                    " Exception: ", JSONArray.toJSON(maps), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    /**
     * 新增目标分群 （暂时废弃）
     */
    @RequestMapping("/saveTagNumFetch")
    @CrossOrigin
    public String saveTagNumFetch(@RequestBody TarGrpDTO tarGrpDTO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.saveTagNumFetch(tarGrpDTO.getMktCamGrpRulId(), tarGrpDTO.getTarGrpConditions());
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to saveTagNumFetch for tarGrpDTO = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpDTO), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 新增目标分群
     * 服务号：5013030003
     */
    @PostMapping("/createTarGrp")
    @CrossOrigin
    public String createTarGrp(@RequestBody TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.createTarGrp(tarGrpDetail,false);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to createTarGrp for tarGrpDetail = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpDetail), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 删除目标分群条件
     */
    @RequestMapping("/delTarGrpCondition")
    @CrossOrigin
    // public String delTarGrpCondition(@RequestBody TarGrpCondition tarGrpCondition,@RequestBody CloseRule closeRule) {
    public String delTarGrpCondition(@RequestBody HashMap<String,Long> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.delTarGrpCondition(param.get("conditionId"), param.get("ruleId"));
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to delTarGrpCondition for tarGrpCondition = {}!" +
                    " Exception: ", JSONArray.toJSON(param.get("conditionId")), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 删除目标分群
     * 服务号：5013030005
     */
    @RequestMapping("/delTarGrp")
    @CrossOrigin
    public String delTarGrp(@RequestBody TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.delTarGrp(tarGrpDetail);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to delTarGrpCondition for tarGrpDetail = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpDetail), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    /**
     * 查看目标分群条件
     */
    @RequestMapping("/editTarGrpCondition")
    @CrossOrigin
    public String editTarGrpCondition(@RequestBody TarGrpCondition tarGrpCondition) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.editTarGrpConditionDO(tarGrpCondition.getConditionId());
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to editTarGrpCondition for tarGrpCondition = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpCondition), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 获取目标分群条件信息
     */
    @RequestMapping("/listTarGrpCondition")
    @CrossOrigin
    public String listTarGrpCondition(@RequestBody MktCamGrpRul mktCamGrpRul) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.listTarGrpCondition(mktCamGrpRul.getTarGrpId());
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to listTarGrpCondition for mktCamGrpRul = {}!" +
                    " Exception: ", JSONArray.toJSON(mktCamGrpRul), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 修改目标分群
     */
    @RequestMapping("/modTarGrp")
    @CrossOrigin
    public String modTarGrp(@RequestBody TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.modTarGrp(tarGrpDetail);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to modTarGrp for modTarGrp = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpDetail), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 修改目标分群条件
     */
    @RequestMapping("/updateTarGrpCondition")
    @CrossOrigin
    public String updateTarGrpCondition(@RequestBody TarGrpCondition tarGrpCondition) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.updateTarGrpCondition(tarGrpCondition);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to updateTarGrpConditionDO for tarGrpConditionDO = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpCondition), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 大数据模型保存
     */
    @RequestMapping("/saveBigDataModel")
    @CrossOrigin
    public String saveBigDataModel(@RequestBody MktCamGrpRul mktCamGrpRul) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.saveBigDataModel(mktCamGrpRul.getMktCamGrpRulId());
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to saveBigDataModel for mktCamGrpRul = {}!" +
                    " Exception: ", JSONArray.toJSON(mktCamGrpRul), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    /**
     * 获取大数据模型
     */
    @RequestMapping("/listBigDataModel")
    @CrossOrigin
    public String listBigDataModel(@RequestBody MktCamGrpRul mktCamGrpRul) {
        //模拟大数据返回一个map
        Map<String, Object> maps = new HashMap<>();
        try {
            //大数据返回信息给前台
            maps = tarGrpService.listBigDataModel(mktCamGrpRul.getMktCamGrpRulId());
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to listBigDataModel for mktCamGrpRul = {}!" +
                    " Exception: ", JSONArray.toJSON(mktCamGrpRul), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 修改目标分群
     */
    @RequestMapping("/modTarGrpOther")
    @CrossOrigin
    public String modTarGrpOther(@RequestBody TarGrp tarGrp) {
        Map<String, Object> maps = new HashMap<>();
        try {
            Integer result = tarGrpService.modTarGrpOther(tarGrp);
            if (result > 0) {
                maps.put("resultCode", CommonConstant.CODE_SUCCESS);
                maps.put("resultMsg", "修改成功！");
            } else {
                maps.put("resultCode", CommonConstant.CODE_SUCCESS);
                maps.put("resultMsg", "修改失败！");
            }
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to modTarGrpOther," +
                    " Exception: ", JSONArray.toJSON(tarGrp), e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "修改失败！");
        }
        return FastJsonUtils.objToJson(maps);
    }



}
