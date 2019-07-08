package com.zjtelcom.cpct.dto.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.List;

public class DisplayColumnEntity extends BaseEntity {

    /**
     * 展示列ID
     */
    private Long displayColumnId;

    /**
     * 展示列中文名
     */
    private String displayColumnName;

    /**
     * 展示列编码
     */
    private String displayColumnCode;

    /**
     *展示列类型
     */
    private String displayColumnType;

    /**
     *展示列标签
     */
    private List<DisplayLabelInfo> injectionLabelIds;

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

    public String getDisplayColumnCode() {
        return displayColumnCode;
    }

    public void setDisplayColumnCode(String displayColumnCode) {
        this.displayColumnCode = displayColumnCode;
    }

    public String getDisplayColumnType() {
        return displayColumnType;
    }

    public void setDisplayColumnType(String displayColumnType) {
        this.displayColumnType = displayColumnType;
    }

    public List<DisplayLabelInfo> getInjectionLabelIds() {
        return injectionLabelIds;
    }

    public void setInjectionLabelIds(List<DisplayLabelInfo> injectionLabelIds) {
        this.injectionLabelIds = injectionLabelIds;
    }
}
