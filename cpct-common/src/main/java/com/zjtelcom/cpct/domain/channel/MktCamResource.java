package com.zjtelcom.cpct.domain.channel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MktCamResource implements Serializable {
    private Long mktCamResourceId;

    private Long mktCampaignId;

    private Long ruleId;

    private Long resourceId;

    private String resourceType;

    private String resourceSubtype;

    private Long resourceTotalNum;

    private Long resourceApplyNum;

    private String receiveType;

    private Long days;

    private Date startTime;

    private Date endTime;

    private String offerId;

    private String dependOfferId;

    private String dependProductId;

    private String frameFlg;

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

    private String remark;

    private Long lanId;

    private String offerCode;

    private String offerName;

    private String camResourceName;

    private String faceAmount;

    private String differentPrice;

    private String releaseType;

    private Date getStartTime;

    private Date getEndTime;

    private String qcCodeUrl;

    private String useArea;

    private String useAreaC4;

    private String publishArea;

    private String applyUser;

    private String applyUserPhone;

    private Long resourceOddNum;

    private Long parentId;

    private String differentOfferId;

    private String dealShops;
    private String postUrl;

    private List<String> dealShopList;
}