package com.zjtelcom.cpct.dto.filter;

import java.io.Serializable;

public class FilterRuleVO extends FilterRule implements Serializable {
    private String filterTypeName;

    public String getFilterTypeName() {
        return filterTypeName;
    }

    public void setFilterTypeName(String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }
}
