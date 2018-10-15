package com.zjtelcom.cpct.domain.campaign;

import java.io.Serializable;

public class MktCamDirectoryDO  implements Serializable {
    private Long mktCamDirectoryId;

    private String mktCamDirectoryName;

    private Long mktCamDirectoryParentId;

    public Long getMktCamDirectoryId() {
        return mktCamDirectoryId;
    }

    public void setMktCamDirectoryId(Long mktCamDirectoryId) {
        this.mktCamDirectoryId = mktCamDirectoryId;
    }

    public String getMktCamDirectoryName() {
        return mktCamDirectoryName;
    }

    public void setMktCamDirectoryName(String mktCamDirectoryName) {
        this.mktCamDirectoryName = mktCamDirectoryName;
    }

    public Long getMktCamDirectoryParentId() {
        return mktCamDirectoryParentId;
    }

    public void setMktCamDirectoryParentId(Long mktCamDirectoryParentId) {
        this.mktCamDirectoryParentId = mktCamDirectoryParentId;
    }
}