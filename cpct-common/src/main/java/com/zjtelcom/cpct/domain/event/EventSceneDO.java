package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class EventSceneDO extends BaseEntity {

    private Long eventSceneId;  //事件场景标识
    private String eventSceneNbr;//事件场景编码
    private String eventSceneName;//事件场景名称
    private String eventSceneDesc;//事件场景描述
    private Long eventId;//事件标识
    private Long extEventSceneId;//外部事件场景标识
    private String contactEvtCode;//事件编码

}