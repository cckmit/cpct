package com.zjtelcom.cpct.open.entity.mktCamChlConf;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class OpenMktCamChlConf extends BaseEntity {

    private Long evtContactConfId;
    private String evtContactConfName;
    private Long mktCampaignId;
    private String mktActivityNbr;
    private Long contactChlId;
    private String pushType;
    private String statusCd;
    private String statusDate;
    private String remark;
    List<OpenMktCamChlConfAttr> mktCamChlConfAttr;

}
