package com.zjtelcom.cpct.model;

import java.io.Serializable;

public class EagleDatabaseConfig implements Serializable {
    private Long dbConfRowId;

    private String dbConfName;

    private String dataBaseType;

    public Long getDbConfRowId() {
        return dbConfRowId;
    }

    public void setDbConfRowId(Long dbConfRowId) {
        this.dbConfRowId = dbConfRowId;
    }

    public String getDbConfName() {
        return dbConfName;
    }

    public void setDbConfName(String dbConfName) {
        this.dbConfName = dbConfName;
    }

    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }
}