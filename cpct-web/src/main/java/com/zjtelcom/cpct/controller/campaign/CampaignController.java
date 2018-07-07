package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.campaign.MktCampaignDetail;
import com.zjtelcom.cpct.dto.campaign.MktCampaignVO;
import com.zjtelcom.cpct.request.campaign.QryMktCampaignListReq;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

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
     * @param mktCampaignVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String createMktCampaign(@RequestBody MktCampaignVO mktCampaignVO) throws Exception {

        // 存活动
        Map<String, Object> mktCampaignMap = mktCampaignService.createMktCampaign(mktCampaignVO);
        Long mktCampaignId = Long.valueOf(mktCampaignMap.get("mktCampaignId").toString());

        return JSON.toJSONString(mktCampaignMap);
    }

    /**
     * 修改营销活动
     *
     * @param mktCampaignVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/modMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String modMktCampaign(@RequestBody MktCampaignVO mktCampaignVO) throws Exception {
        Map<String, Object> mktCampaignMap = mktCampaignService.modMktCampaign(mktCampaignVO);
        return JSON.toJSONString(mktCampaignMap);
    }


    /**
     * 查询营销活动
     *
     * @param qryMktCampaignListReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCampaign(@RequestBody QryMktCampaignListReq qryMktCampaignListReq) throws Exception {
        Map<String, Object> map = mktCampaignService.qryMktCampaignList(qryMktCampaignListReq);
        return JSON.toJSONString(map);
    }


}
