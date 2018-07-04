package com.zjtelcom.cpct.request.event;

import java.io.Serializable;

/**
 * @Description 事件场景请求详细
 * @Author pengy
 * @Date 2018/7/2 16:52
 */
public class QryEventSceneListReq implements Serializable{

    private static final long serialVersionUID = -7467921953057304407L;
    private Long eventSceneId;//事件场景id
    private String eventSceneNbr;//事件场景编码
    private String eventSceneName;//事件场景名称

    public Long getEventSceneId() {
        return eventSceneId;
    }

    public void setEventSceneId(Long eventSceneId) {
        this.eventSceneId = eventSceneId;
    }

    public String getEventSceneNbr() {
        return eventSceneNbr;
    }

    public void setEventSceneNbr(String eventSceneNbr) {
        this.eventSceneNbr = eventSceneNbr;
    }

    public String getEventSceneName() {
        return eventSceneName;
    }

    public void setEventSceneName(String eventSceneName) {
        this.eventSceneName = eventSceneName;
    }
}
