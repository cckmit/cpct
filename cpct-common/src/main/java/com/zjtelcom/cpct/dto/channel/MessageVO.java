package com.zjtelcom.cpct.dto.channel;

import java.util.List;

/**
 * 信息展示实体类
 *
 * @author penyu
 */
public class MessageVO {

    /**
     * 信息表ID
     */
    private Long messageId;

    /**
     * 信息中文名
     */
    private String messageName;

    /**
     * 标签信息集合
     */
    private List<LabelDTO> labelDTOList;

    public List<LabelDTO> getLabelDTOList() {
        return labelDTOList;
    }

    public void setLabelDTOList(List<LabelDTO> labelDTOList) {
        this.labelDTOList = labelDTOList;
    }

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
}
