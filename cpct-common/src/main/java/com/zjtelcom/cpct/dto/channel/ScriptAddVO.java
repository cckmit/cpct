package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class ScriptAddVO implements Serializable {
    private String scriptName;//'记录营销脚本名称',
    private String scriptDesc;//记录营销脚本的具体脚本内容',
    private String scriptType;//记录营销脚本类型,LOVB=CAM-0002',1000 外呼脚本；1100	短信脚本；1200 网厅脚本；1300 直销脚本；1301	营业厅脚本；1302	客户经理脚本；1400	邮件脚本；
    private String suitChannelType;//记录渠道类型，LOVB=CHN-0017'；100000 直销渠道；110000 实体渠道；120000 电子渠道；130000 转售
    private String execChannel;//'记录营销脚本具体的执行渠道标识

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
}
