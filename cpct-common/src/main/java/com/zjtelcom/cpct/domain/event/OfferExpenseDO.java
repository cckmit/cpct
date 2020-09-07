package com.zjtelcom.cpct.domain.event;

import lombok.Data;

@Data
public class OfferExpenseDO {
    private Long offerId;
    private String offerNbr;
    private String offerName;
    private String templateName;
    private String templateInstName;
    private String parameterName;
    private Long paramValue;
    private Long amount;

}
