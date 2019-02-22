package com.zjtelcom.cpct.enums;

public enum TrialCreateType {

    IMPORT_USER_LIST(1000L,"清单导入"),
    TRIAL_OPERATION(2000L,"策略试运算");

    private Long value;
    private String name;

    TrialCreateType(Long value, String name) {
        this.value = value;
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



