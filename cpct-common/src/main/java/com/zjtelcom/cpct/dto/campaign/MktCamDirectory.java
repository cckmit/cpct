package com.zjtelcom.cpct.dto.campaign;

import java.io.Serializable;
import java.util.List;
/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/12 10:16
 * @version: V1.0
 */
public class MktCamDirectory implements Serializable {
    private Long mktCamDirectoryId;

    private String mktCamDirectoryName;

    private Long mktCamDirectoryParentId;

    private List<MktCamDirectory> childMktCamDirectoryList;

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

    public List<MktCamDirectory> getChildMktCamDirectoryList() {
        return childMktCamDirectoryList;
    }

    public void setChildMktCamDirectoryList(List<MktCamDirectory> childMktCamDirectoryList) {
        this.childMktCamDirectoryList = childMktCamDirectoryList;
    }
}