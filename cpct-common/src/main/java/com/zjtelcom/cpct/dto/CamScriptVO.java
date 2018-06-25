package com.zjtelcom.cpct.dto;

import java.io.Serializable;

public class CamScriptVO implements Serializable {
    private Long camScriptId;//'营销活动脚本标识，主键',
    private Long mktCampaignId;//'营销活动标识',
    private Long evtContactConfId;//'事件推送策略标识',
    private String scriptDesc;//'记录营销活动该渠道执行环节的具体脚本内容
    private Long lanId;//'记录本地网标识，数据来源于公共管理区域。'

    public Long getCamScriptId() {
        return camScriptId;
    }

    public void setCamScriptId(Long camScriptId) {
        this.camScriptId = camScriptId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getEvtContactConfId() {
        return evtContactConfId;
    }

    public void setEvtContactConfId(Long evtContactConfId) {
        this.evtContactConfId = evtContactConfId;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }
}
