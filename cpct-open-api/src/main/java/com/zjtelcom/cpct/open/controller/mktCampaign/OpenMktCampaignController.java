package com.zjtelcom.cpct.open.controller.mktCampaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.CreateMktCampaignReq;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.ModMktCampaignReq;
import com.zjtelcom.cpct.open.service.mktCampaign.OpenMktCampaignService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: anson
 * @CreateDate: 2018-11-05 17:32:51
 * @version: V 1.0
 * @Description:营销活动  目前只提供活动详情查询
 */
@RestController
@RequestMapping("${openPath}")
public class OpenMktCampaignController extends BaseController {


    @Autowired
    private OpenMktCampaignService openMktCampaignService;


    /**
     * 查询营销活动列表
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCampaign", method = RequestMethod.POST)
    public String getMktCampaignList(@RequestParam(required = false) String mktActivityNbr,
                                     @RequestParam(required = false) String accNum, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCampaignService.getMktCampaignList(mktActivityNbr, accNum);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }

    /**
     * 查询营销活动详情
     *
     * @param mktCampaignId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCampaign/{mktCampaignId}", method = RequestMethod.POST)
    public String getMktCampaignDetail(@PathVariable String mktCampaignId, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCampaignService.getMktCampaignDetail(mktCampaignId);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue,SerializerFeature.DisableCircularReferenceDetect);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }

    /**
     * 新增营服活动
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCampaign", method = RequestMethod.POST)
    public String saveMktCampaign(@RequestBody Map<String,Object> param, HttpServletResponse response) {
        logger.info("新增营服活动入参：" + JSON.toJSONString(param));
        CreateMktCampaignReq requestObject = JSON.parseObject(JSON.toJSONString(param.get("requestObject")),CreateMktCampaignReq.class);
        Map<String, Object> resultMap = openMktCampaignService.addByObject(requestObject);
        response.setStatus(HttpStatus.SC_CREATED);
        return JSON.toJSONString(resultMap, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 修改营服活动
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCampaign", method = RequestMethod.PATCH)
    public String updateMktCampaign(@RequestBody Map<String,Object> param, HttpServletResponse response) {
        try {
            ModMktCampaignReq requestObject = JSON.parseObject(JSON.toJSONString(param.get("requestObject")),ModMktCampaignReq.class);
            Map<String, Object> eventMap = openMktCampaignService.updateMktCampaign(requestObject);
            return JSON.toJSONString(eventMap, SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            //参数错误
            response.setStatus(HttpStatus.SC_CONFLICT);
            return "";
        }
    }
}