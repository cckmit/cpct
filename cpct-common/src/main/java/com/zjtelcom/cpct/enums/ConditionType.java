package com.zjtelcom.cpct.enums;

public enum ConditionType {
    CHANNEL(0,"痛痒点脚本条件"),
    CONTACT_CONF(1,"推送渠道条件"),
    FILTER_RULE(3,"过滤规则条件");

    private Integer value;
    private String description;


    private ConditionType(final Integer value, final String description) {
        this.value = value;
        this.description = description;
    }

    public static ConditionType getOperator(Integer value){
        ConditionType[] allType = ConditionType.values();
        ConditionType op = null;
        for (ConditionType operator : allType){
            if (operator.getValue().equals(value)){
                op = operator;
            }
        }
        if (op==null){
            return null;
        }
        return op;
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
