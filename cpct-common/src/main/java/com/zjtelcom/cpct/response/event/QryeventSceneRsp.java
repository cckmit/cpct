package com.zjtelcom.cpct.response.event;

import com.zjtelcom.cpct.dto.event.EventScene;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 查询事件场景返回详情
 * @Author pengy
 * @Date 2018/7/2 17:02
 */
public class QryeventSceneRsp implements Serializable {

    private static final long serialVersionUID = 3317190993828547809L;

    private List<EventScene> eventScenes;
//    private PageInfo pageInfo;

    public List<EventScene> getEventScenes() {
        return eventScenes;
    }

    public void setEventScenes(List<EventScene> eventScenes) {
        this.eventScenes = eventScenes;
    }
}
