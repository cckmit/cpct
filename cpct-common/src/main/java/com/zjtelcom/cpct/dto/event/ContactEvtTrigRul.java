package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description 事件触发规则
 * @Author pengy
 * @Date 2018/6/26 14:05
 */
@Data
public class ContactEvtTrigRul extends BaseEntity{

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long evtTrigRulId;//记录事件的触发规则标识主键
    private Long contactEvtId;//事件标识
    private String evtRulName;//记录事件规则的名称
    private String evtRulDesc;//记录事件规则的描述说明
    private String evtRulHandleClass;//记录事件规则的代码实现类名
    private String evtRulExpression;//记录事件规则表达式

}
