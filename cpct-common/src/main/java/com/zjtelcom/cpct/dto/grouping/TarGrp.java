package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;

/**
 * @Description 目标分群
 * @Author pengy
 * @Date 2018/6/25 11:29
 */
public class TarGrp extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -7857062893297757250L;
    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long tarGrpId;//目标分群标识
    private String tarGrpName;//目标分群名称
    private String tarGrpDesc;//目标分群的详细描述信息
    private String tarGrpType;//目标分群类型,1000客户 2000产品实例 3000	销售品 4000营销资源 5000礼包

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public String getTarGrpName() {
        return tarGrpName;
    }

    public void setTarGrpName(String tarGrpName) {
        this.tarGrpName = tarGrpName;
    }

    public String getTarGrpDesc() {
        return tarGrpDesc;
    }

    public void setTarGrpDesc(String tarGrpDesc) {
        this.tarGrpDesc = tarGrpDesc;
    }

    public String getTarGrpType() {
        return tarGrpType;
    }

    public void setTarGrpType(String tarGrpType) {
        this.tarGrpType = tarGrpType;
    }
}
