package com.zjtelcom.cpct.enums;

public enum LabelType {
    CCUST_LEVEL(1000,"客户级"),
    ASSET_LEVEL(2000,"用户级"),
    PRODUCT_LEVEL(3000,"销售品级"),
    ZONE_LEVEL(4000,"区域级");

    private Integer value;
    private String description;

    private LabelType(final Integer value, final String description) {
        this.value = value;
        this.description = description;
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
