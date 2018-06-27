package com.zjtelcom.cpct.domain.campaign;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class MktCamGrpRul extends BaseEntity {

    private Long mktCamGrpRulId;

    private Long mktCampaignId;

    private Long tarGrpId;

    private Long lanId;


}