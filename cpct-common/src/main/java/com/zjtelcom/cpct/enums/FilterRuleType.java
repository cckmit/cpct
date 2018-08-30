package com.zjtelcom.cpct.enums;

public enum FilterRuleType {
    BLACK_LIST(1,"白名单"),
    WHITE_LIST(2,"黑名单"),
    PRODUCT_MUTEX(3,"销售品互斥"),
    EXPRESS(4,"表达式过滤");

    private Integer value;
    private String description;

    private FilterRuleType(final Integer value, final String description) {
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
