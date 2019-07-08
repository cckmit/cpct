package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

/**
 * 标签展示DTO
 *
 * @author pengyu
 */
public class LabelDTO implements Serializable {
    /**
     * 标签id
     */
    private Long injectionLabelId;

    /**
     * 标签中文名
     */
    private String injectionLabelName;

    private Long messageType;

    private String labelCode;

    private String labelType;

    private int displayTypeNum;



    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public Long getMessageType() {
        return messageType;
    }

    public void setMessageType(Long messageType) {
        this.messageType = messageType;
    }

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }

    public String getInjectionLabelName() {
        return injectionLabelName;
    }

    public void setInjectionLabelName(String injectionLabelName) {
        this.injectionLabelName = injectionLabelName;
    }

    public int getDisplayTypeNum() {
        return displayTypeNum;
    }

    public void setDisplayTypeNum(int displayTypeNum) {
        this.displayTypeNum = displayTypeNum;
    }
}
