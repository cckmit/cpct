package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import java.io.Serializable;

/**
 * @Description 事件匹配规则实体类
 * @Author pengy
 * @Date 2018/6/26 13:57
 */
public class ContactEvtMatchRul extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -131913018433324971L;
    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long evtMatchRulId;//记录事件的规则标识主键
    private Long contactEvtId;//事件主键标识
    private String evtRulName;//记录事件规则的名称
    private String evtRulDesc;//记录事件规则的描述说明
    private String evtRulHandleClass;//记录事件规则的代码实现类名
    private String evtRulExpression;//记录事件规则表达式

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getEvtMatchRulId() {
        return evtMatchRulId;
    }

    public void setEvtMatchRulId(Long evtMatchRulId) {
        this.evtMatchRulId = evtMatchRulId;
    }

    public Long getContactEvtId() {
        return contactEvtId;
    }

    public void setContactEvtId(Long contactEvtId) {
        this.contactEvtId = contactEvtId;
    }

    public String getEvtRulName() {
        return evtRulName;
    }

    public void setEvtRulName(String evtRulName) {
        this.evtRulName = evtRulName;
    }

    public String getEvtRulDesc() {
        return evtRulDesc;
    }

    public void setEvtRulDesc(String evtRulDesc) {
        this.evtRulDesc = evtRulDesc;
    }

    public String getEvtRulHandleClass() {
        return evtRulHandleClass;
    }

    public void setEvtRulHandleClass(String evtRulHandleClass) {
        this.evtRulHandleClass = evtRulHandleClass;
    }

    public String getEvtRulExpression() {
        return evtRulExpression;
    }

    public void setEvtRulExpression(String evtRulExpression) {
        this.evtRulExpression = evtRulExpression;
    }
}
