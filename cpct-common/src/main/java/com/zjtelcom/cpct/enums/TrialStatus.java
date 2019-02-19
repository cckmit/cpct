package com.zjtelcom.cpct.enums;

public enum TrialStatus {

    SAMPEL_GOING("1000", "抽样试算中"),
    SAMPEL_SUCCESS("3000", "抽样试算成功"),
    SAMPEL_FAIL("2000", "抽样试算失败"),

    ALL_SAMPEL_GOING("4000", "全量试算中"),
    ALL_SAMPEL_SUCCESS("5000","全量试算成功"),
    ALL_SAMPEL_FAIL("6000","全量试算失败"),

    UPLOAD_GOING("7000", "下发中"),
    UPLOAD_SUCCESS("8000","下发成功"),
    UPLOAD_FAIL("9000","下发失败");

    private String value;
    private String name;

    TrialStatus(String value, String name) {
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
