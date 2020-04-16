package com.zjtelcom.cpct.controller.channel;


import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.channel.MktCampaignPolicyService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/camPolicy")
public class MktCamPolicyController extends BaseController {

    @Autowired
    private MktCampaignPolicyService mktCampaignPolicyService;

    /**
     * 通过销售品id查询佣金政策列表
     * @param param
     * @return
     */
    @PostMapping("getPolicyListByOfferList")
    @CrossOrigin
    public Map<String, Object> getPolicyListByOfferList(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = mktCampaignPolicyService.getPolicyListByOfferList(param);
        } catch (Exception e) {
            logger.error("[op:MktCamPolicyController] fail to getPolicyListByOfferList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getPolicyListByOfferList");
            return result;
        }
        return result;
    }

    /**
     * 政策名称搜索佣金分页
     * @param param
     * @return
     */
    @PostMapping("getPolicyList")
    @CrossOrigin
    public Map<String, Object> getPolicyList(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = mktCampaignPolicyService.getPolicyList(param);
        } catch (Exception e) {
            logger.error("[op:MktCamPolicyController] fail to getPolicyList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getPolicyList");
            return result;
        }
        return result;
    }


    /**
     * 添加活动跟佣金政策关系
     * @param param
     * @return
     */
    @PostMapping("addCampaignPolicyRel")
    @CrossOrigin
    public Map<String, Object> addCampaignPolicyRel(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = mktCampaignPolicyService.addCampaignPolicyRel(param);
        } catch (Exception e) {
            logger.error("[op:MktCamPolicyController] fail to addCampaignPolicyRel",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCampaignPolicyRel");
            return result;
        }
        return result;
    }

    /**
     * 通过活动查询佣金列表
     * @param param
     * @return
     */
    @PostMapping("getPolicyListByCampaign")
    @CrossOrigin
    public Map<String, Object> getPolicyListByCampaign(@RequestBody Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = mktCampaignPolicyService.getPolicyListByCampaign(param);
        } catch (Exception e) {
            logger.error("[op:MktCamPolicyController] fail to getPolicyListByOfferList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getPolicyListByOfferList");
            return result;
        }
        return result;
    }
}
