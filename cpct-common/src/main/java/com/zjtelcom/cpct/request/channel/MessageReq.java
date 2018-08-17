package com.zjtelcom.cpct.request.channel;

import java.io.Serializable;

public class MessageReq implements Serializable {
    private Long[] messageId;

    public Long[] getMessageId() {
        return messageId;
    }

    public void setMessageId(Long[] messageId) {
        this.messageId = messageId;
    }
}
