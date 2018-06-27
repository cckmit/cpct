package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VerbalAddVO implements Serializable {
    private Long campaignId;

    private Long contactConfId;

    private String scriptDesc;

    private Long channelId;

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private String remark;

    //todo 规则条件
    private List<VerbalConditionAddVO> addVOList;


    public List<VerbalConditionAddVO> getAddVOList() {
        return addVOList;
    }

    public void setAddVOList(List<VerbalConditionAddVO> addVOList) {
        this.addVOList = addVOList;
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

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
