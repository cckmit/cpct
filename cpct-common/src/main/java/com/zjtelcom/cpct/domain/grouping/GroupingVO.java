package com.zjtelcom.cpct.domain.grouping;

import java.io.Serializable;
import java.util.List;

public class GroupingVO implements Serializable {
    private String name;
    private List<String> valueList;
    private String value;


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

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }
}
