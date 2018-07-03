package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSONArray;
import com.zjhcsoft.eagle.main.dubbo.model.policy.CalcReqModel;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
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
     */
    @RequestMapping("/createTarGrp")
    @CrossOrigin
    public String createTarGrp(@RequestBody TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.createTarGrp(tarGrpDetail);
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
    public String delTarGrpCondition(@Param("conditionId") Long conditionId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.delTarGrpCondition(conditionId);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to delTarGrpCondition for conditionId = {}!" +
                    " Exception: ", conditionId, e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 删除目标分群
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
     * 编辑目标分群条件
     */
    @RequestMapping("/editTarGrpCondition")
    @CrossOrigin
    public String editTarGrpCondition(@Param("conditionId") Long conditionId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.editTarGrpConditionDO(conditionId);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to editTarGrpCondition for conditionId = {}!" +
                    " Exception: ", conditionId, e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 获取目标分群条件信息
     */
    @RequestMapping("/listTarGrpCondition")
    @CrossOrigin
    public String listTarGrpCondition(@Param("mktCamGrpRulId") Long mktCamGrpRulId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.listTarGrpCondition(mktCamGrpRulId);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to listTarGrpCondition for mktCamGrpRulId = {}!" +
                    " Exception: ", mktCamGrpRulId, e);
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
    public String updateTarGrpCondition(TarGrpConditionDO tarGrpConditionDO) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.updateTarGrpConditionDO(tarGrpConditionDO);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to updateTarGrpConditionDO for tarGrpConditionDO = {}!" +
                    " Exception: ", JSONArray.toJSON(tarGrpConditionDO), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 大数据模型保存
     */
    @RequestMapping("/saveBigDataModel")
    @CrossOrigin
    public String saveBigDataModel(@Param("mktCamGrpRulId") Long mktCamGrpRulId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.saveBigDataModel(mktCamGrpRulId);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to saveBigDataModel for mktCamGrpRulId = {}!" +
                    " Exception: ", mktCamGrpRulId, e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    /**
     * 获取大数据模型
     */
    @RequestMapping("/listBigDataModel")
    @CrossOrigin
    public String listBigDataModel(@Param("mktCamGrpRulId") Long mktCamGrpRulId) {
        //模拟大数据返回一个map
        Map<String, Object> maps = new HashMap<>();
        try {
            //大数据返回信息给前台
            maps = tarGrpService.listBigDataModel(mktCamGrpRulId);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to listBigDataModel for mktCamGrpRulId = {}!" +
                    " Exception: ", mktCamGrpRulId, e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    /**
     * 策略试运算（老系统方法）
     */
    @PostMapping("/trycalc")
    @CrossOrigin
    public String trycalc(String serialNum, @RequestBody CalcReqModel calcReqModel) {
        Map<String, String> maps = new HashMap<>();
        try {
            //返回前端策略试运算结果
            maps = tarGrpService.trycalc(calcReqModel, serialNum);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to trycalc for req = {}!" +
                    " Exception: ", JSONArray.toJSON(calcReqModel), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

    /**
     * 策略试运算（新方法）todo
     */
    @RequestMapping("/trycalcNew")
    @CrossOrigin
    public String trycalcNew(@RequestBody CalcReqModel calcReqModel, String serialNum) {
        Map<String, String> maps = new HashMap<>();
        try {
            //返回前端策略试运算结果

        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to trycalcNew for req = {}!" +
                    " Exception: ", JSONArray.toJSON(calcReqModel), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }

}
