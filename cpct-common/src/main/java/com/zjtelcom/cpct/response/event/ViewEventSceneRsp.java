package com.zjtelcom.cpct.response.event;

import com.zjtelcom.cpct.dto.event.EventSceneDetail;

import java.io.Serializable;

/**
 * @Description 事件场景返回实体类
 * @Author pengy
 * @Date 2018/6/26 13:45
 */
public class ViewEventSceneRsp implements Serializable{

    private static final long serialVersionUID = -6736263650886936502L;
    private EventSceneDetail eventSceneDetail;

    public EventSceneDetail getEventSceneDetail() {
        return eventSceneDetail;
    }

    public void setEventSceneDetail(EventSceneDetail eventSceneDetail) {
        this.eventSceneDetail = eventSceneDetail;
    }
}
