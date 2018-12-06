package com.zjtelcom.cpct.dto.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.List;

/**
 * 信息传递实体类
 *
 * @author pengyu
 */
public class MessageLabelDTO extends BaseEntity {

    /**
     * 信息表ID
     */
    private Long messageId;

    /**
     * 信息关联标签表ID
     */
    private List<LabelDTO> messageLabelId;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public List<LabelDTO> getMessageLabelId() {
        return messageLabelId;
    }

    public void setMessageLabelId(List<LabelDTO> messageLabelId) {
        this.messageLabelId = messageLabelId;
    }
}