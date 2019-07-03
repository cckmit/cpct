package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import com.zjtelcom.cpct.open.entity.RequestTemplateInst;

import java.util.List;

public class ModMktCampaignReq {

    private List<OpenMktCampaignEntity> mktCampaignDetails;
    private RequestTemplateInst requestTemplateInst;

    public List<OpenMktCampaignEntity> getMktCampaignDetails() {
        return mktCampaignDetails;
    }

    public void setMktCampaignDetails(List<OpenMktCampaignEntity> mktCampaignDetails) {
        this.mktCampaignDetails = mktCampaignDetails;
    }

    public RequestTemplateInst getRequestTemplateInst() {
        return requestTemplateInst;
    }

    public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
        this.requestTemplateInst = requestTemplateInst;
    }
}
