package com.zjtelcom.cpct.open.entity;

import java.util.Date;

public class RequestTemplateInst {

    private Long requestTemplateInstId;//配置申请实例ID
    private String name;//名称
    private String staffNbr;//创建人工号
    private String version;//版本号
    private String desc;//描述
    private Date createDate;//创建时间
    private String templateTypeCd;//申请类型
    private Attr attr;//扩展属性

    public Long getRequestTemplateInstId() {
        return requestTemplateInstId;
    }

    public void setRequestTemplateInstId(Long requestTemplateInstId) {
        this.requestTemplateInstId = requestTemplateInstId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStaffNbr() {
        return staffNbr;
    }

    public void setStaffNbr(String staffNbr) {
        this.staffNbr = staffNbr;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTemplateTypeCd() {
        return templateTypeCd;
    }

    public void setTemplateTypeCd(String templateTypeCd) {
        this.templateTypeCd = templateTypeCd;
    }

    public Attr getAttr() {
        return attr;
    }

    public void setAttr(Attr attr) {
        this.attr = attr;
    }
}
