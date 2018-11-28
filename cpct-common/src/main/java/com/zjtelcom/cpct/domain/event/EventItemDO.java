package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class EventItemDO extends BaseEntity {

    private Long evtItemId;//事件采集项主键
    private Long eventId;//事件标识
    private Long evtTypeId;//事件类型标识
    private String evtItemName;//记录事件采集项的名称
    private String evtItemCode;//记录事件采集项的编码，主要用于格式化
    private String valueDataType;//记录事件采集项值数据类型，1000	日期型 1100	日期时间型 1200	字符型 1300	浮点型 1400	整数型 1500	布尔型 1600	计算型
    private String evtItemFormat;//记录事件采集项格式(正则表达式),用于事件采集项的合法性效验
    private Long isNullable;//记录事件采集项的内容是否可空。1是 0否
    private Integer evtItemLength;//记录事件采集项值的长度
    private Integer standardSort;//记录事件采集项的标准化的顺序

}