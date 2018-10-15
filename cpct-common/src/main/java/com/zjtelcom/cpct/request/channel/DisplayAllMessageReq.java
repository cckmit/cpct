package com.zjtelcom.cpct.request.channel;

import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dto.channel.DisplayLabelInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 保存展示列所有信息请求
 *
 * @author pengyu
 */
public class DisplayAllMessageReq  implements Serializable {

    private Long displayColumnId;

    private List<DisplayLabelInfo> injectionLabelIds;

    private String labelName;

    private Page page;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Long getDisplayColumnId() {
        return displayColumnId;
    }

    public void setDisplayColumnId(Long displayColumnId) {
        this.displayColumnId = displayColumnId;
    }


    public List<DisplayLabelInfo> getInjectionLabelIds() {
        return injectionLabelIds;
    }

    public void setInjectionLabelIds(List<DisplayLabelInfo> injectionLabelIds) {
        this.injectionLabelIds = injectionLabelIds;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
