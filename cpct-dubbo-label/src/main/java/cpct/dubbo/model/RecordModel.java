package cpct.dubbo.model;

import java.io.Serializable;
import java.util.List;

public class RecordModel implements Serializable {

    private LabelModel tag;
    private List<LabelValueModel> tagValueList;

    public LabelModel getTag() {
        return tag;
    }

    public void setTag(LabelModel tag) {
        this.tag = tag;
    }

    public List<LabelValueModel> getTagValueList() {
        return tagValueList;
    }

    public void setTagValueList(List<LabelValueModel> tagValueList) {
        this.tagValueList = tagValueList;
    }

}
