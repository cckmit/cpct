package com.zjtelcom.cpct.enums;

public enum LabelCondition {

    SINGLE(1,"单选"),
    MULTI(2,"多选"),
    INPUT(4,"输入框");

    private Integer value;
    private String description;


    private LabelCondition(final Integer value, final String description) {
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
