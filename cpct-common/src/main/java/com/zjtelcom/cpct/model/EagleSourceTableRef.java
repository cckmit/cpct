package com.zjtelcom.cpct.model;

public class EagleSourceTableRef {
    private Long ctasTwoTableRefRowId;

    private Long ctasMasterTableRowId;

    private Long ctasSlaveTableRowId;

    private String masterTableColumnName;

    private String slaveTableColumnName;

    private String joinType;

    private String fitDomain;

    /* 拓展字段，不作表映射 */
    private String tableName;

    private String alias;
    
    private String schemaName;

    public Long getCtasTwoTableRefRowId() {
        return ctasTwoTableRefRowId;
    }

    public void setCtasTwoTableRefRowId(Long ctasTwoTableRefRowId) {
        this.ctasTwoTableRefRowId = ctasTwoTableRefRowId;
    }

    public Long getCtasMasterTableRowId() {
        return ctasMasterTableRowId;
    }

    public void setCtasMasterTableRowId(Long ctasMasterTableRowId) {
        this.ctasMasterTableRowId = ctasMasterTableRowId;
    }

    public Long getCtasSlaveTableRowId() {
        return ctasSlaveTableRowId;
    }

    public void setCtasSlaveTableRowId(Long ctasSlaveTableRowId) {
        this.ctasSlaveTableRowId = ctasSlaveTableRowId;
    }

    public String getMasterTableColumnName() {
        return masterTableColumnName;
    }

    public void setMasterTableColumnName(String masterTableColumnName) {
        this.masterTableColumnName = masterTableColumnName;
    }

    public String getSlaveTableColumnName() {
        return slaveTableColumnName;
    }

    public void setSlaveTableColumnName(String slaveTableColumnName) {
        this.slaveTableColumnName = slaveTableColumnName;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public int hashCode() {
        return 60;
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