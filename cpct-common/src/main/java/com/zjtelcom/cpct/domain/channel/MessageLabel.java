package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 信息关联标签实体类
 */
public class MessageLabel extends BaseEntity{

    /**
     * 信息关联标签表ID
     */
    private Long messageLabelId;

    /**
     * 信息表ID
     */
    private Long messageId;

    /**
     * 标签ID
     */
    private Long injectionLabelId;

    public Long getMessageLabelId() {
        return messageLabelId;
    }

    public void setMessageLabelId(Long messageLabelId) {
        this.messageLabelId = messageLabelId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }


}