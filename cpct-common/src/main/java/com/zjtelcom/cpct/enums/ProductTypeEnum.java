package com.zjtelcom.cpct.enums;

public enum ProductTypeEnum {

    OFFER("1000", "减免包"),
    DEPEND_OFFER("2000","依赖包"),
    DIFFERENT_OFFER("3000", "批零差包");


    private String value;
    private String name;

    ProductTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ProductTypeEnum getNameByCode(String code) {
        for (ProductTypeEnum trialStatus : ProductTypeEnum.values()) {
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
