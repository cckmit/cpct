package com.zjtelcom.cpct.enums;


public enum ProductFilterLabel {

    PROM_LIST(1,"主销售品"),
    PROM_NAME(2,"子销售品");

    private Integer value;
    private String description;

    private ProductFilterLabel(final Integer value, final String description) {
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
