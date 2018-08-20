package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 展示列实体类
 *
 * @author pengyu
 */
public class DisplayColumn extends BaseEntity{

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


}