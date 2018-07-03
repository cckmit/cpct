package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 目标分群详细信息实体类
 * @Author pengy
 * @Date 2018/6/25 11:29
 */

public class TarGrpDetail extends TarGrp implements Serializable{

    private static final long serialVersionUID = -3616668517100761877L;
    private List<TarGrpCondition> tarGrpConditions;

    public List<TarGrpCondition> getTarGrpConditions() {
        return tarGrpConditions;
    }

    public void setTarGrpConditions(List<TarGrpCondition> tarGrpConditions) {
        this.tarGrpConditions = tarGrpConditions;
    }
}