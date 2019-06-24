package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class CatalogTreeTwoVO implements Serializable {
    private Long injectionLabelId;
    private String injectionLabelName;
    private List<LabelVO> children;

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

    public List<LabelVO> getChildren() {
        return children;
    }

    public void setChildren(List<LabelVO> children) {
        this.children = children;
    }
}
