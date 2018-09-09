package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class CatalogTreeTwo implements Serializable {
    private Long id;
    private String name;
    private List<CatalogTreeThree> children;

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

    public List<CatalogTreeThree> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogTreeThree> children) {
        this.children = children;
    }
}
