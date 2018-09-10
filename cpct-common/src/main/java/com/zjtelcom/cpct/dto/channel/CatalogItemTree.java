package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class CatalogItemTree implements Serializable {
    private Long id;
    private String name;
    private List<CatalogItemTree> children;


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

    public List<CatalogItemTree> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogItemTree> children) {
        this.children = children;
    }
}
