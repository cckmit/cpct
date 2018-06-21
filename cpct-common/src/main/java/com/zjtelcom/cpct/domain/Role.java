package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:HuangHua
 * @Descirption:角色实体类
 * @Date: Created by huanghua on 2018/5/8.
 * @Modified By:
 */
public class Role implements Serializable{

    private static final long serialVersionUID = 1L;

    /**主键id**/
    private int id;

    /**角色名**/
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**创建时间**/
    private Date createDate;

    /**更新时间**/
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
        return "role id:" + this.getId() + ";" + "role name:" + this.getName();
    }
}
