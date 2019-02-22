/**
 * @(#)TarGrpTemplateController.java, 2018/9/6.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.grouping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.grouping.TarGrpTemplateDetail;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
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
public class TarGrpTemplateController  extends BaseController {

    @Autowired
    private TarGrpTemplateService tarGrpTemplateService;

    @RequestMapping("/importUserList4TarTemp")
    @CrossOrigin
    public String importUserList4TarTemp(MultipartFile file, @Param("tarTempName")String tarTempName) {
        Map<String, Object> maps = new HashMap<>();
        try {
            InputStream inputStream = file.getInputStream();
            byte[] bytes = new byte[3];
            inputStream.read(bytes,0,bytes.length);
            String head = ChannelUtil.bytesToHexString(bytes);
            head = head.toUpperCase();
            if (!head.equals("D0CF11") && !head.equals("504B03")){
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg", "文件格式不正确");
                return JSON.toJSONString(maps);
            }
            maps = tarGrpTemplateService.importUserList4TarTemp(file,tarTempName);
        } catch (Exception e) {
            logger.error("[op:TarGrpController] fail to importUserList4TarTemp for tarGrpDTO = {}!" +
                    " Exception: ", JSONArray.toJSON(maps), e);
            return FastJsonUtils.objToJson(maps);
        }
        return FastJsonUtils.objToJson(maps);
    }


    /**
     * 销售品id 获取分群集合
     * @param
     * @return
     */
    @RequestMapping(value = "/getTarGrpTemByOfferId", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> getTarGrpTemByOfferId(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            if (param.get("requestid")==null){
                maps.put("resultCode",CODE_FAIL);
                maps.put("resultMsg","请求需求涵不存在!");
                return maps;
            }
            Long requestId = Long.valueOf(param.get("requestid").toString());
            maps = tarGrpTemplateService.getTarGrpTemByOfferId(requestId);
        } catch (Exception e) {
            e.printStackTrace();
            maps.put("resultCode",CODE_FAIL);
            maps.put("resultMsg","查询失败");
            return maps;
        }
        return maps;
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