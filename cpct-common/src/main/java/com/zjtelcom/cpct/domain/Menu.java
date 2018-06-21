package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.Date;

/**
  * 菜单实体类
  * @author nhf
  * @date 2018年5月19日
  */
public class Menu implements Serializable{

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private int id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString(){
        return "menu id:" + this.getId() + ";" + "menu name:" + this.getName();
    }
}
