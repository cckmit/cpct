package com.zjtelcom.cpct.domain;

import lombok.Data;

/**
 * @Description EventList
 * @Author pengy
 * @Date 2018/6/21 9:58
 */
@Data
public class EventList{

    private Long eventId; //事件主键标识
    private String eventNbr;//记录事件的编码信息
    private String eventName;//记录事件的名称
    private String evtSrcName;//事件源名称
    private String evtMappedAddr;//记录事件的映射地址，事件识别时可通过这个映身地址来适配触点事件，可以是URL地址，APP的类包名或其它识别编码
    private String eventTrigType;//记录事件的触发类型,1000实时触发事件 2000定期触发事件 3000人工触发事件
    private String evtTypeName;//记录事件类型的名称

}
