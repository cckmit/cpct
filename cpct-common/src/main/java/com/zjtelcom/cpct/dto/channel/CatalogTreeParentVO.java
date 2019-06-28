package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class CatalogTreeParentVO implements Serializable {
    private Long injectionLabelId;
    private String injectionLabelName;
    private List<CatalogTreeFirstVO> children;

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

    public List<CatalogTreeFirstVO> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogTreeFirstVO> children) {
        this.children = children;
    }
}
