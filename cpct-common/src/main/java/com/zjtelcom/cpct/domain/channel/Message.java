package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 信息实体类
 *
 * @author pengyu
 */
public class Message extends BaseEntity {

    /**
     * 信息表ID
     */
    private Long messageId;

    /**
     * 信息中文名
     */
    private String messageName;

    /**
     * 信息编码
     */
    private String messageCode;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }


}