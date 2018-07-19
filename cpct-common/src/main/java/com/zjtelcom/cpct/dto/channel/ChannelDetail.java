package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ChannelDetail implements Serializable {

    private Long channelId;
    private String channelName;
    private List<ChannelDetail> children;


    public List<ChannelDetail> getChildren() {
        return children;
    }

    public void setChildren(List<ChannelDetail> children) {
        this.children = children;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
