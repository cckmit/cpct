package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.campaign.MktCampaignDetail;
import com.zjtelcom.cpct.request.campaign.QryMktCampaignListReq;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/campaign")
public class CampaignController extends BaseController {

    @Autowired
    private MktCampaignService mktCampaignService;

    /**
     * 查询活动列表
     *
     * @return
     */
    @RequestMapping(value = "/listCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String qryMktCampaignList(@RequestBody QryMktCampaignListReq qryMktCampaignListReq) {
        Map<String, Object> maps = new HashMap<>();
        maps = mktCampaignService.qryMktCampaignList(qryMktCampaignListReq);
        return JSON.toJSONString(maps);
    }

    /**
     * 新增营销活动
     *
     * @param mktCampaignDetail
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String createMktCampaign(@RequestBody  MktCampaignDetail mktCampaignDetail) throws Exception {


        mktCampaignService.createMktCampaign(mktCampaignDetail);


        return initSuccRespInfo(null);
    }

    /**
     * 修改营销活动
     *
     * @param mktCampaignDetail
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/modMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String modMktCampaign(@RequestBody MktCampaignDetail mktCampaignDetail) throws Exception {


        mktCampaignService.modMktCampaign(mktCampaignDetail);


        return initSuccRespInfo(null);
    }


}
