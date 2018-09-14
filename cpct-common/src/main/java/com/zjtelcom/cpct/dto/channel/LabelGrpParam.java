package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class LabelGrpParam implements Serializable {
    private Long labelGrpId;
    private List<Long> labelIdList;

    public Long getLabelGrpId() {
        return labelGrpId;
    }

    public void setLabelGrpId(Long labelGrpId) {
        this.labelGrpId = labelGrpId;
    }

    public List<Long> getLabelIdList() {
        return labelIdList;
    }

    public void setLabelIdList(List<Long> labelIdList) {
        this.labelIdList = labelIdList;
    }
}
