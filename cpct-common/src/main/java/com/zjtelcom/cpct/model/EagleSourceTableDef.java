package com.zjtelcom.cpct.model;

public class EagleSourceTableDef {
    private Long ctasTableDefinitionRowId;

    private String tagTableNameEn;

    private Long dbConfRowId;

    private String tagTableMainFlag;

    private String tagTableNameCn;
    
    private String schemaName;

    /*拓展属性，不作表映射*/
    private String alias;

    public Long getCtasTableDefinitionRowId() {
        return ctasTableDefinitionRowId;
    }

    public void setCtasTableDefinitionRowId(Long ctasTableDefinitionRowId) {
        this.ctasTableDefinitionRowId = ctasTableDefinitionRowId;
    }

    public String getTagTableNameEn() {
        return tagTableNameEn;
    }

    public void setTagTableNameEn(String tagTableNameEn) {
        this.tagTableNameEn = tagTableNameEn;
    }

    public Long getDbConfRowId() {
        return dbConfRowId;
    }

    public void setDbConfRowId(Long dbConfRowId) {
        this.dbConfRowId = dbConfRowId;
    }

    public String getTagTableMainFlag() {
        return tagTableMainFlag;
    }

    public void setTagTableMainFlag(String tagTableMainFlag) {
        this.tagTableMainFlag = tagTableMainFlag;
    }

    public String getTagTableNameCn() {
        return tagTableNameCn;
    }

    public void setTagTableNameCn(String tagTableNameCn) {
        this.tagTableNameCn = tagTableNameCn;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}