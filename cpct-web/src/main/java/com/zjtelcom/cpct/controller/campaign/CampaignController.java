package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.campaign.MktCampaignVO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.request.campaign.QryMktCampaignListReq;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/campaign")
public class CampaignController extends BaseController {

    @Autowired
    private MktCampaignService mktCampaignService;

    @Autowired
    private MktStrategyConfService mktStrategyConfService;
    /**
     * 查询活动列表(分页)
     *
     * @return
     */
    @RequestMapping(value = "/listCampaignPage", method = RequestMethod.POST)
    @CrossOrigin
    public String qryMktCampaignList(@RequestBody Map<String, String> params) throws Exception {
        String mktCampaignName = params.get("mktCampaignName");  // 活动名称
        String statusCd = params.get("statusCd");               // 活动状态
        String tiggerType = params.get("tiggerType");           // 活动触发类型
        String mktCampaignType = params.get("mktCampaignType"); // 活动
        Integer page = Integer.parseInt(params.get("page"));    // 页码
        Integer pageSize = Integer.parseInt(params.get("pageSize")); // 条数
        Map<String, Object> map = mktCampaignService.qryMktCampaignListPage(mktCampaignName, statusCd, tiggerType, mktCampaignType, page, pageSize);
        return JSON.toJSONString(map);
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
        if (mktCampaignVO.getMktStrategyConfDetailList().size() > 0) {
            for (MktStrategyConfDetail mktStrategyConfDetail : mktCampaignVO.getMktStrategyConfDetailList()) {
                mktStrategyConfDetail.setMktCampaignId(mktCampaignId);
                mktStrategyConfService.saveMktStrategyConf(mktStrategyConfDetail);
            }
        }
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
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String getMktCampaign(@RequestBody Map<String, String> params) throws Exception {
        Long mktCampaignId = Long.valueOf(params.get("mktCampaignId"));
        Map<String, Object> map = mktCampaignService.getMktCampaign(mktCampaignId);
        return JSON.toJSONString(map);
    }


    /**
     * 删除营销活动
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delMktCampaign", method = RequestMethod.POST)
    @CrossOrigin
    public String delMktCampaign(@RequestBody Map<String, String> params) throws Exception {
        Long mktCampaignId = Long.valueOf(params.get("mktCampaignId"));
        Map<String, Object> map = mktCampaignService.delMktCampaign(mktCampaignId);
        return JSON.toJSONString(map);
    }

    /**
     * 更改营销活动转态
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/changeMktCampaignStatus", method = RequestMethod.POST)
    public String changeMktCampaignStatus(@RequestBody Map<String, String> params) throws Exception {
        Long mktCampaignId = Long.valueOf(params.get("mktCampaignId"));
        String statusCd = params.get("statusCd");
        Map<String, Object> map = mktCampaignService.changeMktCampaignStatus(mktCampaignId, statusCd);

        return JSON.toJSONString(map);
    }
}
