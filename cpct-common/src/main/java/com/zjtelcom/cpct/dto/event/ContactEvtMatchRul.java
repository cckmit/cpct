package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description 事件匹配规则实体类
 * @Author pengy
 * @Date 2018/6/26 13:57
 */
@Data
public class ContactEvtMatchRul extends BaseEntity {

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long evtMatchRulId;//记录事件的规则标识主键
    private Long contactEvtId;//事件主键标识
    private String evtRulName;//记录事件规则的名称
    private String evtRulDesc;//记录事件规则的描述说明
    private String evtRulHandleClass;//记录事件规则的代码实现类名
    private String evtRulExpression;//记录事件规则表达式

}
