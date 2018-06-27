package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description 事件场景
 * @Author pengy
 * @Date 2018/6/26 15:49
 */
@Data
public class EventScene extends BaseEntity{

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long eventSceneId;  //事件场景标识
    private String eventSceneNbr;//事件场景编码
    private String eventSceneName;//事件场景名称
    private String eventSceneDesc;//事件场景描述
    private Long eventId;//事件标识
    private Long extEventSceneId;//外部事件场景标识
    private String contactEvtCode;//事件编码

}
