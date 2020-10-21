package com.zjtelcom.cpct.dto.channel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MktCamResourceVO implements Serializable {
    private Long mktCampaignId;

    private String camResourceName;

    private String resourceType;

    private String resourceSubtype;

    private String frameFlg;

    private String statusCd;

    private String getStartTime;

    private String getEndTime;

    private String useArea;

    private Integer page;

    private Integer pageSize;

    private String typeString;

    private String useAreaInfo;

    private String mktCampaignNbr;

    private Long resourceApplyNum;

    private String staffName;

    private String staffTel;

}
