package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;


public class CampaignInstVO implements Serializable {
    private String offerName;
    private Long tarGrpTempleteId;
    private List<Long> resourceList;
    private List<Long> channelList;


    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public Long getTarGrpTempleteId() {
        return tarGrpTempleteId;
    }

    public void setTarGrpTempleteId(Long tarGrpTempleteId) {
        this.tarGrpTempleteId = tarGrpTempleteId;
    }

    public List<Long> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Long> resourceList) {
        this.resourceList = resourceList;
    }
    public List<Long> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Long> channelList) {
        this.channelList = channelList;
    }
}
