package com.zjtelcom.cpct.dto.synchronize;

import com.zjtelcom.cpct.BaseEntity;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:同步记录
 */
public class SynchronizeRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7939347752913617953L;
    /** 主键标识*/
    private Long createId;

    /** 同步表名称*/
    private String synchronizeName;

    /** 同步操作类型   0新增   1修改   2删除*/
    private Integer synchronizeType;

    /** 同步类型主键id*/
    private String synchronizeId;

    /** 操作角色*/
    private String roleName;

    /** 同步时间*/
    private Date synchronizeTime;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
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

    public Integer getSynchronizeType() {
        return synchronizeType;
    }

    public void setSynchronizeType(Integer synchronizeType) {
        this.synchronizeType = synchronizeType;
    }

    public String getStartTime() {

        if(StringUtils.isNotBlank(startTime) && startTime.length() == 10){
            return startTime + " 00:00:00";
        }
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {

        if(StringUtils.isNotBlank(endTime) && endTime.length() == 10){
            return endTime + " 23:59:59";
        }
        return endTime;
    }

    public void setEndTime(String endTime) {

        this.endTime = endTime;
    }
}
