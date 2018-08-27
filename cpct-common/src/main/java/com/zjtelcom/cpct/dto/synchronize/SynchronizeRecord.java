package com.zjtelcom.cpct.dto.synchronize;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:
 */
public class SynchronizeRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7939347752913617953L;
    /** 主键标识*/
    private Long id;

    /** 同步类型名称*/
    private String synchronizeName;

    /** 同步类型主键id*/
    private String synchronizeId;

    /** 操作角色*/
    private String roleName;

    /** 同步时间*/
    private Date synchronizeTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSynchronizeName() {
        return synchronizeName;
    }

    public void setSynchronizeName(String synchronizeName) {
        this.synchronizeName = synchronizeName;
    }

    public String getSynchronizeId() {
        return synchronizeId;
    }

    public void setSynchronizeId(String synchronizeId) {
        this.synchronizeId = synchronizeId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Date getSynchronizeTime() {
        return synchronizeTime;
    }

    public void setSynchronizeTime(Date synchronizeTime) {
        this.synchronizeTime = synchronizeTime;
    }
}
