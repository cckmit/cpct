package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.dto.campaign.MktCampaignDetailVO;

import java.util.Date;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:21
 * version: V1.0
 */
public interface MktCampaignService {

    Map<String,Object> searchByCampaignId(Long campaignId);

/*    int createMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception;

    int modMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception;*/

    Map<String,Object> qryMktCampaignListPage (Map<String, Object> params);

    Map<String,Object> qryMktCampaignListPageForNoPublish (Map<String, Object> params);

    Map<String,Object> qryMktCampaignListPageForPublish (Map<String, Object> params);

    Map<String,Object> getCampaignList (String mktCampaignName,String mktCampaignType,Long eventId);

    Map<String,Object> getCampaignList4EventScene (String mktCampaignName);

    Map<String,Object> createMktCampaign(MktCampaignDetailVO mktCampaignVO) throws Exception;

    Map<String,Object> delMktCampaign(Long mktCampaignId) throws Exception;

    Map<String,Object> getMktCampaign(Long mktCampaignId) throws Exception;

    Map<String,Object> getAllConfRuleName (Long mktCampaignId) throws Exception;

    Map<String,Object> modMktCampaign(MktCampaignDetailVO mktCampaignVO) throws Exception;

    Map<String,Object> changeMktCampaignStatus(Long mktCampaignId, String statusCd) throws Exception;

//    Map<String,Object> qryMktCampaignList (MktCamVO mktCampaignVO);

    Map<String, Object> publishMktCampaign(Long mktCampaignId) throws Exception;

    Map<String, Object> upgradeMktCampaign(Long mktCampaignId) throws Exception;

    Map<String,Object> qryMktCampaignList4Sync (Map<String,Object> params, Integer page, Integer pageSize);

    Map<String,Object> examineCampaign4Sync (Long campaignId,String statusCd);

    Map<String,Object> getCampaignEndTime4Sync (Long campaignId);

    Map<String,Object> delayCampaign4Sync (Long campaignId, Date lastTime);

    Map<String, Object> getMktCampaignTemplate(Long preMktCampaignId) throws Exception;

    Map<String, Object> copyMktCampaign(Long parentMktCampaignId);

    Map<String, Object> dueMktCampaign();

    Map<String, Object> countMktCampaign(Map<String, Object> params);

    Map<String, Object> channelEffectDateCheck(Map<String, Object> params);

    Result queryDelayCampaignList();

    void campaignDelayNotice();

}