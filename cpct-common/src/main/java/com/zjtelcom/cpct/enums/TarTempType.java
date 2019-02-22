package com.zjtelcom.cpct.enums;

public enum TarTempType {
    CUST_TEMPLETE("1000", "客户模板-自用"),
    PROM_TEMPLETE("3000","销售品模板-ppm"),
    PROM_IMPORT_TEMPLETE("4000","销售品导入模板");

    private String value;
    private String name;

    TarTempType(String value, String name) {
        this.value = value;
        this.name = name;
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
