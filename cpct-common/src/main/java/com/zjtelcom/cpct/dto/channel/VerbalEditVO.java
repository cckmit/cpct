package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VerbalEditVO implements Serializable {

    private Long verbalId;

    private Long campaignId;

    private Long contactConfId;

    private String scriptDesc;

    private Long channelId;

    private List<VerbalConditionEditVO> editVOList;


    public List<VerbalConditionEditVO> getEditVOList() {
        return editVOList;
    }

    public void setEditVOList(List<VerbalConditionEditVO> editVOList) {
        this.editVOList = editVOList;
    }

    public Long getVerbalId() {
        return verbalId;
    }

    public void setVerbalId(Long verbalId) {
        this.verbalId = verbalId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getContactConfId() {
        return contactConfId;
    }

    public void setContactConfId(Long contactConfId) {
        this.contactConfId = contactConfId;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }


}
