package com.zjtelcom.cpct.open.entity.script;

/**
 * @Auther: anson
 * @Date: 2018/11/7
 * @Description:营销活动脚本
 */
public class OpenScript {

    private Integer scriptId;
    private String scriptName;
    private String scriptDesc;
    private String scriptType;
    private String suitChannelType;
    private String execChannel;
    private String statusCd;
    private String statusDate;
    private String remark;

    public Integer getScriptId() {
        return scriptId;
    }

    public void setScriptId(Integer scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getSuitChannelType() {
        return suitChannelType;
    }

    public void setSuitChannelType(String suitChannelType) {
        this.suitChannelType = suitChannelType;
    }

    public String getExecChannel() {
        return execChannel;
    }

    public void setExecChannel(String execChannel) {
        this.execChannel = execChannel;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
