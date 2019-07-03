package com.zjtelcom.cpct.open.entity.mktCampaignBorninfoOrder;

import com.zjtelcom.cpct.open.entity.RequestTemplateInst;

import java.util.List;

public class CompleteMktCampaignBorninfoOrderDetailJtReq {

    private RequestTemplateInst requestTemplateInst;
    private List<OpenMktCampaignBorninfoOrderEntity> mktCampaignBorninfoOrderDetails;
    private String resultCd;
    private String resultDesc;

    public RequestTemplateInst getRequestTemplateInst() {
        return requestTemplateInst;
    }

    public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
        this.requestTemplateInst = requestTemplateInst;
    }

    public List<OpenMktCampaignBorninfoOrderEntity> getMktCampaignBorninfoOrderDetails() {
        return mktCampaignBorninfoOrderDetails;
    }

    public void setMktCampaignBorninfoOrderDetails(List<OpenMktCampaignBorninfoOrderEntity> mktCampaignBorninfoOrderDetails) {
        this.mktCampaignBorninfoOrderDetails = mktCampaignBorninfoOrderDetails;
    }

    public String getResultCd() {
        return resultCd;
    }

    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }
}
