package com.zjtelcom.cpct.dto.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class LabelGrpVO extends BaseEntity implements Serializable {
    private Long grpId;
    private String grpName;
    private String grpDesc;
    private List<LabelVO> labelList;


    public Long getGrpId() {
        return grpId;
    }

    public void setGrpId(Long grpId) {
        this.grpId = grpId;
    }

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public String getGrpDesc() {
        return grpDesc;
    }

    public void setGrpDesc(String grpDesc) {
        this.grpDesc = grpDesc;
    }

    public List<LabelVO> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<LabelVO> labelList) {
        this.labelList = labelList;
    }
}
