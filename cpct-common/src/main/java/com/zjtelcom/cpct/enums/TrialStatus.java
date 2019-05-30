package com.zjtelcom.cpct.enums;

public enum TrialStatus {

    SAMPEL_GOING("1000", "抽样试算中"),
    SAMPEL_SUCCESS("3000", "抽样试算成功"),
    SAMPEL_FAIL("2000", "抽样试算失败"),

    ALL_SAMPEL_GOING("4000", "全量试算中"),
    ALL_SAMPEL_SUCCESS("5000","全量试算成功"),
    ALL_SAMPEL_FAIL("6000","全量试算失败"),


    IMPORT_GOING("1100", "导入中"),
    IMPORT_SUCCESS("1200","导入成功"),
    IMPORT_FAIL("1300","导入失败"),
    PPM_IMPORT_GOING("1400", "未导入（ppm）"),

    ISSEE_ANALYZE_SUCCESS("7100", "协同-解析成功"),
    ISEE_ANALYZE_FAIL("7200","协同-解析失败"),
    ISEE_PUBLISH_SUCCESS("7300","协同-下发成功"),
    ISSEE_ANALYZE_FAIL_REASON_ONE("7201", "协同-没有清单文件"),
    ISEE_ANALYZE_FAIL_REASON_TWO("7202","协同-清单文件没有数据"),
    ISEE_PUBLISH_FAIL_REASON_THREE("7203","协同-字段定义有重复字段"),

    ISEE_PUBLISH_FAIL("7400", "协同-下发失败"),
    CHANNEL_PUBLISH_SUCCESS("8100", "渠道-下发成功"),
    CHANNEL_PUBLISH_FAIL("8200","渠道-下发失败"),

    UPLOAD_GOING("7000", "下发中"),
    UPLOAD_SUCCESS("8000","下发成功"),
    UPLOAD_FAIL("9000","下发失败");


    private String value;
    private String name;

    TrialStatus(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static TrialStatus getNameByCode(String code) {
        for (TrialStatus trialStatus : TrialStatus.values()) {
            if (code != null && code.equals(trialStatus.value)) {
                return trialStatus;
            }
        }
        return null;
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
