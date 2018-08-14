package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

/**
 * 展示列关联标签实体类
 *
 * @author pengyu
 */
public class DisplayColumnLabel extends BaseEntity{

    /**
     * 展示列关联指标ID
     */
    private Long displayColumnLabelId;

    /**
     * 指标ID
     */
    private Long injectionLabelId;

    public Long getDisplayColumnLabelId() {
        return displayColumnLabelId;
    }

    public void setDisplayColumnLabelId(Long displayColumnLabelId) {
        this.displayColumnLabelId = displayColumnLabelId;
    }

    public Long getInjectionLabelId() {
        return injectionLabelId;
    }

    public void setInjectionLabelId(Long injectionLabelId) {
        this.injectionLabelId = injectionLabelId;
    }
}