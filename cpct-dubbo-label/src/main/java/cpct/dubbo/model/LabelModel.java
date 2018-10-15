package cpct.dubbo.model;

public class LabelModel {
    private Long tagRowId;//标签表主键
    private Long ctasTableDefinitionRowId;//源表定义主键
    private String tagName;//标签名称
    private String sourceTableColumnName;//源表字段
    private String labState;//上线：3；下线：5


    public Long getTagRowId() {
        return tagRowId;
    }

    public void setTagRowId(Long tagRowId) {
        this.tagRowId = tagRowId;
    }

    public Long getCtasTableDefinitionRowId() {
        return ctasTableDefinitionRowId;
    }

    public void setCtasTableDefinitionRowId(Long ctasTableDefinitionRowId) {
        this.ctasTableDefinitionRowId = ctasTableDefinitionRowId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSourceTableColumnName() {
        return sourceTableColumnName;
    }

    public void setSourceTableColumnName(String sourceTableColumnName) {
        this.sourceTableColumnName = sourceTableColumnName;
    }

    public String getLabState() {
        return labState;
    }

    public void setLabState(String labState) {
        this.labState = labState;
    }
}
