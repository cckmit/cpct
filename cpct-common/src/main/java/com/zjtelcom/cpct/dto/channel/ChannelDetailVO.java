package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ChannelDetailVO  implements Serializable {

    private String channelName;

    private List<ChannelDetail> childrenList;

    public List<ChannelDetail> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<ChannelDetail> childrenList) {
        this.childrenList = childrenList;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
