/**
 * @(#)TarGrpTemplateController.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.dto.grouping.TarGrpDTO;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateDetail;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

/**
 * Description:
 * author: linchao
 * date: 2018/09/06 17:50
 * version: V1.0
 */
@RestController
@RequestMapping("${adminPath}/tarGrpTemplate")
public class TarGrpTemplateController {

    @Autowired
    private TarGrpTemplateService tarGrpTemplateService;




    /**
     * 销售品id 获取分群集合
     * @param
     * @return
     */
    @RequestMapping(value = "/getTarGrpTemByOfferId", method = RequestMethod.POST)
    @CrossOrigin
    public String getTarGrpTemByOfferId(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            if (param.get("requestid")==null){
                maps.put("resultCode",CODE_FAIL);
                maps.put("resultMsg","请求需求涵不存在!");
                return JSON.toJSONString(maps);
            }
            Long requestId = Long.valueOf(param.get("requestId").toString());
            maps = tarGrpTemplateService.getTarGrpTemByOfferId(requestId);
        } catch (Exception e) {
            return FastJsonUtils.objToJson(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增目标分群模板
     */
    @RequestMapping(value = "/saveTarGrpTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public String saveTarGrpTemplate(@RequestBody TarGrpTemplateDetail tarGrpTemplateDetail) {
        Map<String, Object> map = tarGrpTemplateService.saveTarGrpTemplate(tarGrpTemplateDetail);
        return JSON.toJSONString(map);
    }

    /**
     * 获取目标分群模板
     */
    @RequestMapping(value = "/getTarGrpTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public String getTarGrpTemplate(@RequestBody Map<String, String> params) {
        Long tarGrpTemplateId = Long.valueOf(params.get("tarGrpTemplateId"));
        Map<String, Object> map = tarGrpTemplateService.getTarGrpTemplate(tarGrpTemplateId);
        return JSON.toJSONString(map);
    }

    /**
     * 更新目标分群模板
     *
     * @param tarGrpTemplateDetail
     * @return
     */
    @RequestMapping(value = "/updateTarGrpTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public String updateTarGrpTemplate(@RequestBody TarGrpTemplateDetail tarGrpTemplateDetail) {
        Map<String, Object> map = tarGrpTemplateService.updateTarGrpTemplate(tarGrpTemplateDetail);
        return JSON.toJSONString(map);
    }


    /**
     * 删除目标分群模板
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/deleteTarGrpTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteTarGrpTemplate(@RequestBody Map<String, String> params) {
        Long tarGrpTemplateId = Long.valueOf(params.get("tarGrpTemplateId"));
        Map<String, Object> map = tarGrpTemplateService.deleteTarGrpTemplate(tarGrpTemplateId);
        return JSON.toJSONString(map);
    }


    /**
     * 获取目标分群列表
     *
     * @return
     */
    @RequestMapping(value = "/listTarGrpTemplateAll", method = RequestMethod.POST)
    @CrossOrigin
    public String listTarGrpTemplateAll() {
        Map<String, Object> map = tarGrpTemplateService.listTarGrpTemplateAll();
        return JSON.toJSONString(map);
    }

    /**
     * 获取目标分群列表(分页)
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/listTarGrpTemplatePage", method = RequestMethod.POST)
    @CrossOrigin
    public String listTarGrpTemplatePage(@RequestBody Map<String, String> params) {
        String tarGrpTemplateName = params.get("tarGrpTemplateName");
        String tarGrpType = params.get("tarGrpType");
        Integer page = Integer.parseInt(params.get("page"));
        Integer pageSize = Integer.parseInt(params.get("pageSize"));
        Map<String, Object> map = tarGrpTemplateService.listTarGrpTemplatePage(tarGrpTemplateName,tarGrpType, page, pageSize);
        return JSON.toJSONString(map);
    }
}