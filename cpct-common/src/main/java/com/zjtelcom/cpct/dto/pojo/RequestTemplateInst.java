/*
 * 文件名：RequestTemplateInst.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;


import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 配置申请实例
 * @author taowenwu
 * @version 1.0
 * @see RequestTemplateInst
 * @since
 */

public class RequestTemplateInst implements Serializable {
    private Long requestTemplateInstId;

    private String name;

    private String staffNbr;

    private String version;

    private String desc;

    private Date createDate;

    private String templateTypeCd;

    private List<Attr> attrList;

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

    public List<Attr> getAttrList() {
        return attrList;
    }

    public void setAttrList(List<Attr> attrList) {
        this.attrList = attrList;
    }

}
