package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpConditionDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description TarGrpController
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/targrp")
public class TarGrpController extends BaseController {

    @Autowired
    private TarGrpService tarGrpService;

    /**
     * 新增客户分群
     */
    @RequestMapping("/saveTagNumFetch")
    @CrossOrigin
    public String saveTagNumFetch(@Param("mktCamGrpRulId") Long mktCamGrpRulId, List<TarGrpConditionDTO> tarGrpConditionDTOList) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = tarGrpService.saveTagNumFetch(mktCamGrpRulId, tarGrpConditionDTOList);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to saveTagNumFetch for mktCamGrpRulId = {},tarGrpConditionDTOList = {}!" +
                    " Exception: ", mktCamGrpRulId, JSONArray.toJSON(tarGrpConditionDTOList), e);
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
     * 更新目标分群条件
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

}
