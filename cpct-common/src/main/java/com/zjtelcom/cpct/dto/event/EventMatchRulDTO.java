package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description EventMatchRulDTO
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class EventMatchRulDTO extends BaseEntity implements Serializable {

    private Long evtMatchRulId;//记录事件的规则标识主键
    private Long eventId;//事件主键标识
    private String evtRulName;//记录事件规则的名称
    private String evtRulDesc;//记录事件规则的描述说明
    private String evtRulHandleClass;//记录事件规则的代码实现类名
    private String evtRulExpression;//记录事件规则表达式

}