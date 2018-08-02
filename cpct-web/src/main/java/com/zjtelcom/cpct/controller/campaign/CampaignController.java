package com.zjtelcom.cpct.controller.campaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.campaign.MktCampaignVO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.service.thread.TarGrpRule;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("${adminPath}/campaign")
public class CampaignController extends BaseController {

    @Autowired
    private MktCampaignService mktCampaignService;

    @Autowired
    private MktStrategyConfService mktStrategyConfService;

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired
    private RedisUtils redisUtils;
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

    @RequestMapping(value = "/getCampaignList", method = RequestMethod.POST)
    @CrossOrigin
    public String getCampaignList(@RequestBody Map<String, Object> params)throws Exception {
        String mktCampaignName = params.get("mktCampaignName").toString();  // 活动名称
        Long eventId = null;
        if (params.get("eventId")!=null){
            eventId = Long.valueOf(params.get("eventId").toString());
        }
//        String mktCampaignType = params.get("mktCampaignType"); // 活动
        Map<String, Object> map = mktCampaignService.getCampaignList(mktCampaignName,null,eventId);
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/getCampaignList4EventScene", method = RequestMethod.POST)
    @CrossOrigin
    public String getCampaignList4EventScene(@RequestBody Map<String, String> params)throws Exception {
        String mktCampaignName = params.get("mktCampaignName");  // 活动名称
        Map<String, Object> map = mktCampaignService.getCampaignList4EventScene(mktCampaignName);
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

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(@RequestBody Map<String, String> params) throws Exception {
        Long tarGrpId = Long.valueOf(params.get("tarGrpId"));
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=1; i<3; i++){
            MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
            mktStrategyConfRuleDO.setMktStrategyConfRuleId(Long.valueOf(i));
            mktStrategyConfRuleDO.setTarGrpId(null);
            // 线程池执行规则存入redis
            executorService.submit(new TarGrpRule(Long.valueOf(i), Long.valueOf(i), mktStrategyConfRuleDO, redisUtils, tarGrpConditionMapper, injectionLabelMapper));
        }

        return null;
    }
}
