package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class OfferDetail implements Serializable {
    private Integer offerId;

    private String offerName;

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }
}
