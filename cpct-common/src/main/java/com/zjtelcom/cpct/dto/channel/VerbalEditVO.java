package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VerbalEditVO implements Serializable {

    private Long verbalId;

    private Long contactConfId;//渠道推送配置id

    private String scriptDesc;//脚本内容

    private Long channelId;//渠道id

    private String remark;

    //todo 规则条件
    private List<VerbalConditionAddVO> addVOList;


    public Long getVerbalId() {
        return verbalId;
    }

    public void setVerbalId(Long verbalId) {
        this.verbalId = verbalId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<VerbalConditionAddVO> getAddVOList() {
        return addVOList;
    }

    public void setAddVOList(List<VerbalConditionAddVO> addVOList) {
        this.addVOList = addVOList;
    }
}
