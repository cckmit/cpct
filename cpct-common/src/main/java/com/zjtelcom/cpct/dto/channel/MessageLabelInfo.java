package com.zjtelcom.cpct.dto.channel;

import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.domain.channel.MessageLabel;

import java.io.Serializable;
import java.util.List;

public class MessageLabelInfo  implements Serializable {
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
    /**
     * 标签信息
     */
    private List<LabelDTO> labelDTOList;

    /**
     * 是否选中
     */
    private String checked;

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
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

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public List<LabelDTO> getLabelDTOList() {
        return labelDTOList;
    }

    public void setLabelDTOList(List<LabelDTO> labelDTOList) {
        this.labelDTOList = labelDTOList;
    }
}
