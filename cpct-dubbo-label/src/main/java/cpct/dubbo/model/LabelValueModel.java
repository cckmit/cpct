package cpct.dubbo.model;

import java.io.Serializable;

public class LabelValueModel implements Serializable {
    private Long tagValueRowId;
    private Long tagRowId;
    private String tagValueName;
    private String tagValueScopeType;
    private String tagDownValue;
    private String tagTopValue;

    public Long getTagValueRowId() {
        return tagValueRowId;
    }

    public void setTagValueRowId(Long tagValueRowId) {
        this.tagValueRowId = tagValueRowId;
    }

    public Long getTagRowId() {
        return tagRowId;
    }

    public void setTagRowId(Long tagRowId) {
        this.tagRowId = tagRowId;
    }

    public String getTagValueName() {
        return tagValueName;
    }

    public void setTagValueName(String tagValueName) {
        this.tagValueName = tagValueName;
    }

    public String getTagValueScopeType() {
        return tagValueScopeType;
    }

    public void setTagValueScopeType(String tagValueScopeType) {
        this.tagValueScopeType = tagValueScopeType;
    }

    public String getTagDownValue() {
        return tagDownValue;
    }

    public void setTagDownValue(String tagDownValue) {
        this.tagDownValue = tagDownValue;
    }

    public String getTagTopValue() {
        return tagTopValue;
    }

    public void setTagTopValue(String tagTopValue) {
        this.tagTopValue = tagTopValue;
    }
}
