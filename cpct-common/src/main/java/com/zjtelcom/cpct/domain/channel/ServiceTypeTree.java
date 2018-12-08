package com.zjtelcom.cpct.domain.channel;

import java.io.Serializable;
import java.util.List;

public class ServiceTypeTree extends ServiceType implements Serializable {

    private List<ServiceTypeTree> children;

    public List<ServiceTypeTree> getChildren() {
        return children;
    }

    public void setChildren(List<ServiceTypeTree> children) {
        this.children = children;
    }
}
