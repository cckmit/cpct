package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.List;

public class LabelCatalogModel implements Serializable {
    private List<LabelCatalog> catalogList;

    public List<LabelCatalog> getCatalogList() {
        return catalogList;
    }

    public void setCatalogList(List<LabelCatalog> catalogList) {
        this.catalogList = catalogList;
    }
}
