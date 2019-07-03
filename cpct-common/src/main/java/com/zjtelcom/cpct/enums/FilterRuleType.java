package com.zjtelcom.cpct.enums;

public enum FilterRuleType {
    BLACK_LIST(1000,"白名单"),
    WHITE_LIST(2000,"黑名单"),
    PRODUCT_MUTEX(3000,"销售品互斥"),
    EXPRESS(4000,"表达式过滤"),
    TIME_SLOT(5000,"时间段过滤"),
    PERTURBED(6000,"过扰规则"),
    RELATION_PRODUCT(7000,"关单销售品规则"),
    DISPATCHING_PERTURBED(8000,"客调过扰规则");
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
