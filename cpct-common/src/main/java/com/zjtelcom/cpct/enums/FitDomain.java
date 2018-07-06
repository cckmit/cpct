package com.zjtelcom.cpct.enums;

public enum FitDomain {

    YD(1,"移动"),
    KD(2,"宽带"),
    GH(3,"固话"),
    ITV(4,"ITV");

    private Integer value;
    private String description;

    private FitDomain(final Integer value, final String description) {
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
