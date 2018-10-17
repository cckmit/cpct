package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class CatalogTreeParent implements Serializable {
    private Long injectionLabelId;
    private String injectionLabelName;
    private List<LabelCatalogTree> children;

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }

    public String getInjectionLabelName() {
        return injectionLabelName;
    }

    public void setInjectionLabelName(String injectionLabelName) {
        this.injectionLabelName = injectionLabelName;
    }

    public List<LabelCatalogTree> getChildren() {
        return children;
    }

    public void setChildren(List<LabelCatalogTree> children) {
        this.children = children;
    }
}
