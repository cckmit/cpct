package com.zjtelcom.cpct.domain.org;

import java.io.Serializable;

/**
 * @Auther: anson
 * @Date: 2018/10/9
 * @Description:标签组织数前端返回特定类  只返回一些实际有用的信息
 */
public class OrgTreeDO implements Serializable {


    /** 营销区域*/
    private Integer areaId;

    /** 营销区域名称*/
    private String areaName;

    /** 上级营销区域*/
    private Integer sumAreaId;

    /** */
    private Integer areaTypeId;


    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getSumAreaId() {
        return sumAreaId;
    }

    public void setSumAreaId(Integer sumAreaId) {
        this.sumAreaId = sumAreaId;
    }

    public Integer getAreaTypeId() {
        return areaTypeId;
    }

    public void setAreaTypeId(Integer areaTypeId) {
        this.areaTypeId = areaTypeId;
    }
}
