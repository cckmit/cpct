package com.zjtelcom.cpct.enums;

public enum TarTempCreateType {

    NAORMAL_TEMP(1000L,"普通模板"),
    IMPORT_USER_TEMP(2000L,"导入名单模板");

    private Long value;
    private String name;

    TarTempCreateType(Long value, String name) {
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
