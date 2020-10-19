package com.zjtelcom.cpct.enums;

public enum ManageGrade {

    BLOC("10", "集团级"),
    PROVINCE("11","省级"),
    BEN_DI_WANG("12", "本地网级"),
    HAI_WAI("13", "海外级"),
    LIANG_JI("14", "两级共管");


    private String value;
    private String name;

    ManageGrade(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ManageGrade getNameByCode(String code) {
        for (ManageGrade trialStatus : ManageGrade.values()) {
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
