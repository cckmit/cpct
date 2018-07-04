package com.zjtelcom.cpct.request.event;

import com.zjtelcom.cpct.dto.event.EventSceneDetail;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 事件场景详情
 * @Author pengy
 * @Date 2018/7/2 16:40
 */
public class ModEventSceneJtReq implements Serializable {

    private static final long serialVersionUID = 2285061118729734048L;
    private List<EventSceneDetail> eventSceneDetails;

    public List<EventSceneDetail> getEventSceneDetails() {
        return eventSceneDetails;
    }

    public void setEventSceneDetails(List<EventSceneDetail> eventSceneDetails) {
        this.eventSceneDetails = eventSceneDetails;
    }
}
