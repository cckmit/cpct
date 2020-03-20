package com.zjtelcom.cpct.service.impl.channel;

import com.alibaba.fastjson.JSON;
import com.asiainfo.policyqry.service.IPolicyQueryService;
import com.asiainfo.policyqry.vo.ActivityPolicyVo;
import com.asiainfo.policyqry.vo.PolicyInfoVo;
import com.asiainfo.policyqry.vo.PolicyQueryByOfferIdVo;
import com.asiainfo.policyqry.vo.PolicyQueryVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.channel.MktCamPolicyMapper;
import com.zjtelcom.cpct.domain.User;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.channel.MktCamPolicy;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.MktCampaignPolicyService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;

@Service
@Slf4j
public class MktCampaignPolicyServiceImpl extends BaseService implements MktCampaignPolicyService {

    @Autowired(required = false)
    private IPolicyQueryService iPolicyQueryService;
    @Autowired(required = false)
    private MktCamPolicyMapper mktCamPolicyMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;


    /**
     * 通过销售品id查询佣金政策列表
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getPolicyListByOfferList(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        PolicyQueryByOfferIdVo policyQueryByOfferIdVo = new PolicyQueryByOfferIdVo();
        Integer page = MapUtil.getIntNum(param.get("page"));
        Integer pageSize = MapUtil.getIntNum(param.get("pageSize"));
        List<Long> ItemList = (List<Long>) param.get("offerIds");
        List<Long> offerIds = new ArrayList<>();
        for (Long offerId : ItemList) {
            MktCamItem camItem = mktCamItemMapper.selectByPrimaryKey(offerId);
            if (camItem!=null && camItem.getItemType().equals("1000")){
                offerIds.add(camItem.getItemId());
            }
        }
        policyQueryByOfferIdVo.setOfferIds(offerIds);

        HashMap<String, Object> policyByOfferId = iPolicyQueryService.getPolicyByOfferId(policyQueryByOfferIdVo);
        logger.info("【通过销售品id查询佣金政策列表】"+JSON.toJSONString(policyByOfferId));
        if (policyByOfferId.get("code").equals("0001")){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","佣金政策查询失败");
            return result;
        }
        PageHelper.startPage(page, pageSize);
        List<PolicyInfoVo> list = (List<PolicyInfoVo>) policyByOfferId.get("list");
        Page pageInfo = new Page(new PageInfo(list));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg", list);
        result.put("pageInfo",pageInfo);
        logger.info("【通过销售品id查询佣金政策列表】"+JSON.toJSONString(result));
        return result;
    }

    /**
     * 政策名称搜索佣金分页
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getPolicyList(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        PolicyQueryVo policyQueryVo = new  PolicyQueryVo();
        Integer page = MapUtil.getIntNum(param.get("page"));
        Integer pageSize = MapUtil.getIntNum(param.get("pageSize"));
        String policyName = MapUtil.getString(param.get("policyName"));
        policyQueryVo.setPageNum(page);
        policyQueryVo.setPageSize(pageSize);
        policyQueryVo.setPolicyName(policyName);
        HashMap<String, Object> policys = iPolicyQueryService.getPolicys(policyQueryVo);
        logger.info("【政策名称搜索佣金分页】"+JSON.toJSONString(policys));
        if (policys.get("code").equals("0001")){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","佣金政策查询失败");
            return result;
        }
        PageHelper.startPage(page, pageSize);
        List<PolicyInfoVo> list = (List<PolicyInfoVo>) policys.get("list");
        Page pageInfo = new Page();
        pageInfo.setPage(page);
        pageInfo.setTotal(MapUtil.getLongNum(policys.get("total")));
        pageInfo.setPageSize(pageSize);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",list);
        result.put("pageInfo",pageInfo);
        logger.info("【政策名称搜索佣金分页】"+JSON.toJSONString(result));
        return result;
    }

    /**
     * 添加活动跟佣金政策关系
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> addCampaignPolicyRel(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        Long campaignId = MapUtil.getLongNum(param.get("campaignId"));
        List<PolicyInfoVo> policyList  = (List<PolicyInfoVo>) param.get("list");
        List<Long> policyIdList = new ArrayList<>();
        mktCamPolicyMapper.deleteByCampaignId(campaignId);
        for (PolicyInfoVo policyInfoVo : policyList) {
            MktCamPolicy camPolicy = new MktCamPolicy();
            camPolicy.setCreateDate(new Date());
            camPolicy.setCreateStaff(UserUtil.getUser().getStaffId());
            camPolicy.setMktCampaignId(campaignId);
            camPolicy.setPolicyDesc(policyInfoVo.getPolicyDesc());
            camPolicy.setPolicyId(policyInfoVo.getPolicyId());
            camPolicy.setPolicyName(policyInfoVo.getPolicyName());
            camPolicy.setStatusCd(STATUSCD_EFFECTIVE);
            camPolicy.setPolicyTypeName(policyInfoVo.getPolicyTypeName());
            camPolicy.setUpdateDate(new Date());
            camPolicy.setUpdateStaff(UserUtil.getUser().getStaffId());
            mktCamPolicyMapper.insert(camPolicy);
            policyIdList.add(policyInfoVo.getPolicyId());
        }
        ActivityPolicyVo activityPolicyVo = new ActivityPolicyVo();
        activityPolicyVo.setActivityId(campaignId);
        activityPolicyVo.setPolicyIds(policyIdList);
        HashMap<String, Object> stringObjectHashMap = iPolicyQueryService.addActivityPolicyRel(activityPolicyVo);
        logger.info("【添加活动跟佣金政策关系】"+JSON.toJSONString(stringObjectHashMap));
        if (stringObjectHashMap.get("code").equals("0001")){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","佣金政策关联活动关系添加失败");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","佣金政策关联活动关系添加成功");
        return result;
    }

    /**
     * 通过活动查询佣金列表
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getPolicyListByCampaign(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<>();
        Long campaignId = MapUtil.getLongNum(param.get("campaignId"));
        List<MktCamPolicy> mktCamPolicies = mktCamPolicyMapper.selectByCampaignId(campaignId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",mktCamPolicies);
        logger.info("【添加活动跟佣金政策关系】"+JSON.toJSONString(result));
        return result;
    }
}
