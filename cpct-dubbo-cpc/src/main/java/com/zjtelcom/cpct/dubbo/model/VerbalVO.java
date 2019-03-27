package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.dto.channel.VerbalConditionVO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 痛痒点
 */
public class VerbalVO implements Serializable {

    private Long verbalId;

    private Long campaignId;

    private Long contactConfId;

    private String scriptDesc;

    private Long channelId;

    private String channelName;

    private Long channelParentId;

    private String channelParentName;

    private List<VerbalConditionVO> conditionList;

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private String remark;


    public Long getChannelParentId() {
        return channelParentId;
    }

    public void setChannelParentId(Long channelParentId) {
        this.channelParentId = channelParentId;
    }

    public String getChannelParentName() {
        return channelParentName;
    }

    public void setChannelParentName(String channelParentName) {
        this.channelParentName = channelParentName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<VerbalConditionVO> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<VerbalConditionVO> conditionList) {
        this.conditionList = conditionList;
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
