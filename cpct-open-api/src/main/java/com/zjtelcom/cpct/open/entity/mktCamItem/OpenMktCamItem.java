package com.zjtelcom.cpct.open.entity.mktCamItem;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

@Data
public class OpenMktCamItem extends BaseEntity{

    private Long mktCamItemId;
    private Long mktCampaignId;
    private String mktActivityNbr;
    private String itemType;
    private Long itemId;
    private Long priority;
    private Long itemGroup;
    private String statusCd;
    private String statusDate;
    private String remark;
    private Long lanId;
    private OpenMktCamItemTarGrap tarGrap;


}
