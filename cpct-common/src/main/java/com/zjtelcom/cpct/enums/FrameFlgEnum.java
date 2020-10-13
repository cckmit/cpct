package com.zjtelcom.cpct.enums;

public enum FrameFlgEnum {

    YES("yes", "是电子券框架类型"),
    NO("no","不是电子券框架类型");


    private String value;
    private String name;

    FrameFlgEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static FrameFlgEnum getNameByCode(String code) {
        for (FrameFlgEnum trialStatus : FrameFlgEnum.values()) {
            if (code != null && code.equals(trialStatus.value)) {
                return trialStatus;
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
