package com.zjtelcom.cpct.enums;

public enum ResourceTypeEnum {
    //电子券型号
    REAL("1001", "实体"),
    ELECTRON("1002","电子");



    private String value;
    private String name;

    ResourceTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        for (ResourceTypeEnum trialStatus : ResourceTypeEnum.values()) {
            if (code != null && code.equals(trialStatus.value)) {
                return trialStatus.getName();
            }
        }
        return "";
    }

    public String getValue() {
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
