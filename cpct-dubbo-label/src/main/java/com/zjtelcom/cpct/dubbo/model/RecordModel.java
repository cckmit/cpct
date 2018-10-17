package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.List;

public class RecordModel implements Serializable {

    private LabModel label;
    private List<LabValueModel> labelValueList;

    public LabModel getLabel() {
        return label;
    }

    public void setLabel(LabModel label) {
        this.label = label;
    }

    public List<LabValueModel> getLabelValueList() {
        return labelValueList;
    }

    public void setLabelValueList(List<LabValueModel> labelValueList) {
        this.labelValueList = labelValueList;
    }
}
