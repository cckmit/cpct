package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;

/**
 * 展示列实体类
 *
 * @author pengyu
 */
public class DisplayColumnVO implements Serializable {

    /**
     * 展示列ID
     */
    private Long displayColumnId;

    /**
     * 展示列中文名
     */
    private String displayColumnName;
    /**
     *展示列类型
     */
    private String displayColumnType;

    private String displayColumnTypeName;

    private String statusCd;

    private Long createStaff;


    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getDisplayColumnTypeName() {
        return displayColumnTypeName;
    }

    public void setDisplayColumnTypeName(String displayColumnTypeName) {
        this.displayColumnTypeName = displayColumnTypeName;
    }

    public String getDisplayColumnType() {
        return displayColumnType;
    }

    public void setDisplayColumnType(String displayColumnType) {
        this.displayColumnType = displayColumnType;
    }

    public Long getDisplayColumnId() {
        return displayColumnId;
    }

    public void setDisplayColumnId(Long displayColumnId) {
        this.displayColumnId = displayColumnId;
    }

    public String getDisplayColumnName() {
        return displayColumnName;
    }

    public void setDisplayColumnName(String displayColumnName) {
        this.displayColumnName = displayColumnName;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }
}