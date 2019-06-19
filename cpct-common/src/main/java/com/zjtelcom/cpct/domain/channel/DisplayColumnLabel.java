package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 展示列关联标签实体类
 *
 * @author pengyu
 */
public class DisplayColumnLabel extends BaseEntity{

    /**
     * 展示列关联指标ID
     */
    private Long displayColumnLabelId;

    /**
     * 指标ID
     */
    private Long injectionLabelId;

    /**
     * 展示列ID
     */
    private Long displayId;


    /**
     * 默认展示列类型
     */
    private Long messageType;

    /**
     * 默认展示列类型
     */
    private  String labelDisplayType;

    public Long getMessageType() {
        return messageType;
    }

    public void setMessageType(Long messageType) {
        this.messageType = messageType;
    }

    public Long getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Long displayId) {
        this.displayId = displayId;
    }

    public Long getDisplayColumnLabelId() {
        return displayColumnLabelId;
    }

    public void setDisplayColumnLabelId(Long displayColumnLabelId) {
        this.displayColumnLabelId = displayColumnLabelId;
    }

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }

    public String getLabelDisplayType() {
        return labelDisplayType;
    }

    public void setLabelDisplayType(String labelDisplayType) {
        this.labelDisplayType = labelDisplayType;
    }
}