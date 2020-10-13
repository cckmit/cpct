package com.zjtelcom.cpct.enums;

/**
 * @Description ProdCompTypeEnum
 * @Author lincaho
 * @Date 2018/12/06 10:15
 */

public enum ProdCompTypeEnum {


    YUANZI("10", "原子产品"),
    CHANPIN("11", "产品组合"),
    ZUHE("12", "组合产品"),
    QUNZU("13", "群组产品");

    private String id;
    private String value;

    ProdCompTypeEnum(String id, String value) {
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
        for (ProdCompTypeEnum areaCodeEnum : ProdCompTypeEnum.values()) {
            if(id.equals(areaCodeEnum.id)){
                return areaCodeEnum.getValue();
            }
        }
        return null;
    }


}
