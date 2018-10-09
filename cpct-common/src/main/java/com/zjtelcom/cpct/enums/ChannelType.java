package com.zjtelcom.cpct.enums;

public enum ChannelType {
    INITIATIVE(0,"人工"),
    PASSIVE(1,"自动");


    private Integer value;
    private String description;


    private ChannelType(Integer value,String description){
        this.value = value;
        this.description = description;
    }

    public static ChannelType getChannelType(Integer value){
        ChannelType[] allType = ChannelType.values();
        ChannelType channelType = allType[value];
        return channelType;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
