package com.zjtelcom.cpct.enums;

public enum Operator {

    LESS_THAN(2000,"小于"),
    GREATER_THAN(1000,"大于"),
    LESS_THAN_EQUAL(6000,"小于等于"),
    GREATER_THAN_EQUAL(5000,"大于等于"),
    EQUAL(3000,"等于"),
    NOT_EQUAL(4000,"不等于"),
    IN(7000,"包含"),
    NOT_IN(7100,"不包含"),
    AND(8000,"并且"),
    OR(9000,"或者");

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
        return op;
    }

    public static Operator getOperator(String description){
        Operator[] allType = Operator.values();
        Operator op = null;
        if (description==null || description.equals("")){
            return null;
        }
        for (Operator operator : allType){
            if (operator.getDescription().equals(description)){
                op = operator;
            }
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
