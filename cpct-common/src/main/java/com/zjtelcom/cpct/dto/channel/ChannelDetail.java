package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ChannelDetail implements Serializable {

    private Long channelId;
    private String channelName;
    private String channelCode;
    private String remark;
    private List<ChannelDetail> children;


    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

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
