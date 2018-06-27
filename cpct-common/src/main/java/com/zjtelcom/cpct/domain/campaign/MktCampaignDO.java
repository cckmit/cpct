package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;
import java.util.Date;

@Data
public class MktCampaignDO extends BaseEntity{

    private Long mktCampaignId;
    private String tiggerType;
    private String mktCampaignName;
    private Date planBeginTime;
    private Date planEndTime;
    private Date beginTime;
    private Date endTime;
    private String mktCampaignType;
    private String mktActivityNbr;
    private String mktActivityTarget;
    private String mktCampaignDesc;
    private String execType;
    private String execInvl;
    private Integer execNum;
    private String statusCd;
    private Long lanId;


}