package com.zjtelcom.cpct.domain.event;

import lombok.Data;

@Data
public class MktOfferEventDO {
    private int offerId;
    private int mktCampaignId;
    private int eventId;
    private String eventName;
}
