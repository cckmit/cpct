package com.zjtelcom.cpct.dto.filter;

import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.dto.channel.OfferDetail;
import com.zjtelcom.cpct.dto.channel.ProductParam;

import java.io.Serializable;
import java.util.List;

public class FilterRuleVO extends FilterRule implements Serializable {
    private String filterTypeName;
    private List<OfferDetail> productList;

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
