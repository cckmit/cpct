package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.List;

public class RecordModel implements Serializable {

    private TagModel tag;
    private List<TagValueModel> tagValueList;
    private String operateType;

    public TagModel getTag() {
        return tag;
    }

    public void setTag(TagModel tag) {
        this.tag = tag;
    }

    public List<TagValueModel> getTagValueList() {
        return tagValueList;
    }

    public void setTagValueList(List<TagValueModel> tagValueList) {
        this.tagValueList = tagValueList;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
