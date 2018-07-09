package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 事件场景明细
 * @Author pengy
 * @Date 2018/6/26 15:51
 */
public class EventSceneDetail extends EventScene implements Serializable{

    private static final long serialVersionUID = 3765073047877719384L;
    private List<EvtSceneCamRel> evtSceneCamRels ;

    public List<EvtSceneCamRel> getEvtSceneCamRels() {
        return evtSceneCamRels;
    }

    public void setEvtSceneCamRels(List<EvtSceneCamRel> evtSceneCamRels) {
        this.evtSceneCamRels = evtSceneCamRels;
    }
}
