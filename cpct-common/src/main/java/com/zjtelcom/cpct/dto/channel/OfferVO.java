package com.zjtelcom.cpct.dto.channel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OfferVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String offerId;
    private String offerType;
    private String offerSysType;
    private String offerName;
    private String offerSysName;
    private String offerSysPyName;
    private String offerNbr;
    private String offerSysNbr;
    private String offerDesc;
    private Date effDate;
    private String manageGrade;
    private Date expDate;
    private String offerProviderId;
    private String brandId;
    private String valueAddedFlag;
    private Integer initialCredValue;
    private String pricingPlanId;
    private String isIndependent;
    private String remark;
    private String manageRegionId;
    private String statusCd;
    private String createStaff;
    private String updateStaff;
    private Date createDate;
    private Date statusDate;
    private Date updateDate;
}
