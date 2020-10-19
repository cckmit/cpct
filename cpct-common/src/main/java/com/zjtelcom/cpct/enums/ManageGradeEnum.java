package com.zjtelcom.cpct.enums;

/**
 * @Description ManageGradeEnum
 * @Author lincaho
 * @Date 2018/12/06 10:15
 */

public enum ManageGradeEnum {

    JITUAN("10", "集团级"),
    SHEGN("11", "省级"),
    LOCAL("12", "本地网级"),
    OVERSEAS("13", "海外级"),
    TWO_STAGE("14", "两级共管");

    private String id;
    private String value;

    ManageGradeEnum(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



    public static String getValuedById(String id){
        if (id==null){
            return "";
        }
        for (ManageGradeEnum areaCodeEnum : ManageGradeEnum.values()) {
            if(id.equals(areaCodeEnum.id)){
                return areaCodeEnum.getValue();
            }
        }
        return "";
    }

}
