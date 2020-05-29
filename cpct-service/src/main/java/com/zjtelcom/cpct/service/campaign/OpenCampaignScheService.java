package com.zjtelcom.cpct.service.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface OpenCampaignScheService {

    Map<String,Object> openCampaignScheForDay(Long mktCampaignId);

    Map<String,Object> openApimktCampaignBorninfoOrder( MktCampaignDO campaign);


}
