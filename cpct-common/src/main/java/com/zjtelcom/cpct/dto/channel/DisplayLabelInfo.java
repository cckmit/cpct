package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

public class DisplayLabelInfo implements Serializable {

    private Long labelId;
    private Long messageTypeId;


    public Long getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Long messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }
}
