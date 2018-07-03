package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Map;

public class QryMktScriptReq implements Serializable {
    private Map<String,Object> params;
    private Integer page;
    private Integer pageSize;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
