package com.zjtelcom.cpct.domain.openApi.mktCamEvtRel;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

@Data
public class OpenMktCamEvtRel extends BaseEntity{

    private Long mktCampEvtRelId;
    private Long mktCampaignId;
    private Long contactEvtId;
    private String statusCD;
    private String statusDate;
    private String remark;
    private String lanId;
    private OpenEventRef eventRef;


}
