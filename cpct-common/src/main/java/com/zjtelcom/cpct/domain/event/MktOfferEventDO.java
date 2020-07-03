package com.zjtelcom.cpct.domain.event;

import lombok.Data;

@Data
public class MktOfferEventDO {
    private Long offerId;
    private Long mktCampaignId;
    private Long eventId;
    private String eventName;
    private String eventNbr;
}
