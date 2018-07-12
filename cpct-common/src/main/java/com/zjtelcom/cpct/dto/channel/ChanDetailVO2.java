package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ChanDetailVO2 implements Serializable {
    private String channelName;

    private List<ChannelDetailVO> childrenList;


    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<ChannelDetailVO> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<ChannelDetailVO> childrenList) {
        this.childrenList = childrenList;
    }
}
