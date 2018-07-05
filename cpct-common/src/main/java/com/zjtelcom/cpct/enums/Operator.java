package com.zjtelcom.cpct.enums;

public enum Operator {

    LESS_THAN(60,"小于"),
    GREATER_THAN(62,"大于"),
    LESS_THAN_EQUAL(8814,"小于等于"),
    GREATER_THAN_EQUAL(8815,"大于等于"),
    EQUAL(61,"等于"),
    NOT_EQUAL(8800,"不等于"),
    IN(8714,"包含"),
    NOT_IN(8713,"不包含");

    private Integer value;
    private String description;


    private Operator(final Integer value, final String description) {
        this.value = value;
        this.description = description;
    }

    public static Operator getOperator(Integer value){
        Operator[] allType = Operator.values();
        Operator op = null;
        for (Operator operator : allType){
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
