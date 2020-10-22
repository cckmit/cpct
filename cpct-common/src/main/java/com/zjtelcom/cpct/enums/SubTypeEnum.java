package com.zjtelcom.cpct.enums;

public enum SubTypeEnum {
    //电子券型号
    DEPART_REAL_RESOURCE("105001011", "市场部实体券"),
    DEPART_ELECTRON("105001012", "市场部电子券"),
    CHANNEL_REAL("105001013", "电渠电子券"),
    CHANNEL_OTHERS("105004001", "电渠第三方券");

    private String value;
    private String name;

    SubTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        for (SubTypeEnum trialStatus : SubTypeEnum.values()) {
            if (code != null && code.equals(trialStatus.value)) {
                return trialStatus.getName();
            }
        }
        return null;
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
