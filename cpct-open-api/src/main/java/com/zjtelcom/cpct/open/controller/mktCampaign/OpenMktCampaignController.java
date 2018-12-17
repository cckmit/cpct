package com.zjtelcom.cpct.open.controller.mktCampaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.HttpUtil;
import com.zjtelcom.cpct.open.base.controller.BaseController;
import com.zjtelcom.cpct.open.service.mktCampaign.OpenMktCampaignService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     * 查询营销活动
     *
     * @param mktCampaignId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/mktCampaign/{mktCampaignId}", method = RequestMethod.GET)
    public String getContactChannel(@PathVariable String mktCampaignId, HttpServletResponse response) {
        try {
            Map<String, Object> map = openMktCampaignService.queryById(mktCampaignId);
            return JSON.toJSONString(map.get("params"), SerializerFeature.WriteMapNullValue);
        } catch (SystemException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return "";
        }
    }


}