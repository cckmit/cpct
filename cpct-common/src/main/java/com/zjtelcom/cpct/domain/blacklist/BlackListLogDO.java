package com.zjtelcom.cpct.domain.blacklist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BlackListLogDO extends BaseEntity implements Serializable {
    private int logId;
    private String method;
    private String args;
    private String returnValue;
    private String assetPhone;
    private String serviceCate;
    private String maketingCate;
    private String publicBenefitCate;
    private String channel;
    private String staffId;
    private String operType;
    private String remark;

    private Date createDate;//创建时间
    private Date updateDate;//更新时间


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getCreateDate(){
        return this.createDate;
    }
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getUpdateDate(){
        return this.updateDate;
    }



}
