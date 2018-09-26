package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;
import com.zjtelcom.cpct.dto.channel.VerbalVO;

import java.util.List;

/**
 * @author:sunpeng
 * @descirption:营销活动渠道推送配置
 * @date: 2018/6/26.
 */
public class MktCamChlConf extends BaseEntity {

    /**
     * 通用数据操作类型
     */
    private String actType;

    /**
     * 执行渠道推送配置标识
     */
    private Long evtContactConfId;

    /**
     * 执行渠道推送配置名称
     */
    private String evtContactConfName;

    /**
     * 痛痒点话术描述
     */
    private String scriptDesc;

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;

    /**
     * 推送渠道标识
     */
    private Long contactChlId;

    /**
     * 推送方式
     */
    private String pushType;

    /**
     * 计算表达式
     */
    private String ruleExpression;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getEvtContactConfId() {
        return evtContactConfId;
    }

    public void setEvtContactConfId(Long evtContactConfId) {
        this.evtContactConfId = evtContactConfId;
    }

    public String getEvtContactConfName() {
        return evtContactConfName;
    }

    public void setEvtContactConfName(String evtContactConfName) {
        this.evtContactConfName = evtContactConfName;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }
}
