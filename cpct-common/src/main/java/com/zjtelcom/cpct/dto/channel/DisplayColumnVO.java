package com.zjtelcom.cpct.dto.channel;

/**
 * 展示列实体类
 *
 * @author pengyu
 */
public class DisplayColumnVO{

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
}