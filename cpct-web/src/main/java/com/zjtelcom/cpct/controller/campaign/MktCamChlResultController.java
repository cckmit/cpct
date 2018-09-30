/**
 * @(#)MktCamChlConfController.java, 2018/7/4.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.campaign.MktCamChlResult;
import com.zjtelcom.cpct.service.campaign.MktCamChlResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 协同渠道
 * author: linchao
 * date: 2018/07/04 14:54
 * version: V1.0
 */

@RestController
@RequestMapping("${adminPath}/mktCamChlResult")
public class MktCamChlResultController extends BaseController {

    @Autowired
    private MktCamChlResultService mktCamChlResultService;


    /**
     * 新增二次协同（多个结果）
     *
     * @param mktCamChlResultList
     * @return
     */
    @RequestMapping(value = "/saveMktCamChlResult", method = RequestMethod.POST)
    @CrossOrigin
    public String saveMktCamChlResult(@RequestBody List<MktCamChlResult> mktCamChlResultList) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        try {
            if (mktCamChlResultList != null) {
                for (MktCamChlResult mktCamChlResult : mktCamChlResultList) {
                    mktCamChlResultService.saveMktCamChlResult(mktCamChlResult);
                }
            }
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResultList", mktCamChlResultList);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to save mktCamChlResultList = {} Exception: ", JSON.toJSON(mktCamChlResultList), e);
        }
        return JSON.toJSONString(mktCamChlResultMap);
    }

    /**
     * 更新二次协同渠道配置
     *
     * @param mktCamChlResultList
     * @return
     */
    @RequestMapping(value = "/updateMktCamChlResult", method = RequestMethod.POST)
    @CrossOrigin
    public String updateMktCamChlConf(@RequestBody List<MktCamChlResult> mktCamChlResultList) {
        Map<String, Object> mktCamChlResultMap = new HashMap<>();
        try {
            if (mktCamChlResultList != null) {
                for (MktCamChlResult mktCamChlResult : mktCamChlResultList) {
                    if (mktCamChlResult.getMktCamChlResultId() != null && mktCamChlResult.getMktCamChlResultId() != 0) {
                        mktCamChlResultService.updateMktCamChlResult(mktCamChlResult);
                    } else {
                        mktCamChlResultService.saveMktCamChlResult(mktCamChlResult);
                    }
                }
            }
            mktCamChlResultMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlResultMap.put("mktCamChlResultList", mktCamChlResultList);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to update mktCamChlResultList = {} Exception: ", JSON.toJSON(mktCamChlResultList), e);
        }
        return JSON.toJSONString(mktCamChlResultMap);
    }

    /**
     * 获取协同渠道配置
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/getMktCamChlResult", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCamChlConf(@RequestBody Map<String, Object> params) {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        List<Integer> mktCamChlResultIdList = (List<Integer>) params.get("mktCamChlResultIdList");
        List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
        try {
            if (mktCamChlResultIdList != null) {
                for (Integer mktCamChlResultId : mktCamChlResultIdList) {
                    Map<String, Object> mktCamChlResultMap = mktCamChlResultService.getMktCamChlResult(Long.valueOf(mktCamChlResultId));
                    mktCamChlResultList.add((MktCamChlResult) mktCamChlResultMap.get("mktCamChlResult"));
                }
            }
            mktCamChlConfMap.put("mktCamChlResultList", mktCamChlResultList);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to get mktCamChlResultList by mktCamChlResultIdList = {} Exception: ", JSON.toJSON(mktCamChlResultIdList), e);
        }
        return JSON.toJSONString(mktCamChlConfMap);
    }

    /**
     * 删除协同渠道配置
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/deleteMktCamChlResult", method = RequestMethod.POST)
    @CrossOrigin
    public String deleteMktCamChlConf(@RequestBody Map<String, String> params) {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        Long mktCamChlResultId = Long.valueOf(params.get("mktCamChlResultId"));
        try {
            mktCamChlConfMap = mktCamChlResultService.deleteMktCamChlResult(mktCamChlResultId);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to delete mktCamChlResultId = {} Exception: ", mktCamChlResultId, e);
        }
        return JSON.toJSONString(mktCamChlConfMap);
    }


    /**
     * 查询所有 有二次协同 且二次协同为工单，且有效的
     *
     * @return
     */
    @RequestMapping(value = "/selectResultByMktCampaignId", method = RequestMethod.POST)
    @CrossOrigin
    public String selectResultByMktCampaignId() {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap = mktCamChlResultService.selectResultList();
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfController] failed to select Result By MktCampaignId Exception: ", e);
        }
        return JSON.toJSONString(resultMap);
    }



}