package com.zjtelcom.cpct.enums;

public enum CamItemType {

    OFFER("1000", "销售品"),
    PACKAGE("2000","礼包"),
    RESOURCE("3000", "营销资源"),
    SERVICE("4000", "服务"),
    DEPEND_OFFER("5000", "依赖销售品"),
    DEPEND_PRODUCT("6000","依赖产品");


    private String value;
    private String name;

    CamItemType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CamItemType getNameByCode(String code) {
        for (CamItemType trialStatus : CamItemType.values()) {
            if (code != null && code.equals(trialStatus.value)) {
                return trialStatus;
            }
        }
        return null;
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
