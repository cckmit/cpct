package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ChannelDetail implements Serializable {

    private Long channelId;
    private String channelName;
    private List<ChannelDetail> childrenList;


    public List<ChannelDetail> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<ChannelDetail> childrenList) {
        this.childrenList = childrenList;
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
