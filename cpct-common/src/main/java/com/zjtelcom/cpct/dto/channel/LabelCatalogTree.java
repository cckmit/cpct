package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class LabelCatalogTree implements Serializable {
    private Long id;
    private String name;
    private List<LabelCatalogTree> children;
    private List<LabelVO> labelVOList;


    public List<LabelVO> getLabelVOList() {
        return labelVOList;
    }

    public void setLabelVOList(List<LabelVO> labelVOList) {
        this.labelVOList = labelVOList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LabelCatalogTree> getChildren() {
        return children;
    }

    public void setChildren(List<LabelCatalogTree> children) {
        this.children = children;
    }
}
