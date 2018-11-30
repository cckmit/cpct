package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.domain.grouping.TarGrpDetailDO;

import java.util.List;

/**
 * @Description TarGrp
 * @Author pengy
 * @Date 2018/6/25 11:29
 */
public class TarGrpDTO extends TarGrpDetailDO {

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long mktCamGrpRulId;//活动分群关联id
    private List<TarGrpCondition> tarGrpConditions;//客户分群条件

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getMktCamGrpRulId() {
        return mktCamGrpRulId;
    }

    public void setMktCamGrpRulId(Long mktCamGrpRulId) {
        this.mktCamGrpRulId = mktCamGrpRulId;
    }

    public List<TarGrpCondition> getTarGrpConditions() {
        return tarGrpConditions;
    }

    public void setTarGrpConditions(List<TarGrpCondition> tarGrpConditions) {
        this.tarGrpConditions = tarGrpConditions;
    }
}
