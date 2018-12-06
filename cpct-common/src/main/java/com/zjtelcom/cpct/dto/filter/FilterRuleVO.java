package com.zjtelcom.cpct.dto.filter;

import com.zjtelcom.cpct.dto.channel.OfferDetail;

import java.io.Serializable;
import java.util.List;

public class FilterRuleVO extends FilterRule implements Serializable {
    private String filterTypeName;
    private Long labelId;
    private String conditionName;
    private String operType;
    private String rightParam;
    private List<OfferDetail> productList;

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getRightParam() {
        return rightParam;
    }

    public void setRightParam(String rightParam) {
        this.rightParam = rightParam;
    }

    public List<OfferDetail> getProductList() {
        return productList;
    }

    public void setProductList(List<OfferDetail> productList) {
        this.productList = productList;
    }

    public String getFilterTypeName() {
        return filterTypeName;
    }

    public void setFilterTypeName(String filterTypeName) {
        this.filterTypeName = filterTypeName;
    }
}
