package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecordModel implements Serializable {

    private LabModel label;
    private ArrayList<LabValueModel> labelValueList;

    public LabModel getLabel() {
        return label;
    }

    public void setLabel(LabModel label) {
        this.label = label;
    }

    public ArrayList<LabValueModel> getLabelValueList() {
        return labelValueList;
    }

    public void setLabelValueList(ArrayList<LabValueModel> labelValueList) {
        this.labelValueList = labelValueList;
    }
}
