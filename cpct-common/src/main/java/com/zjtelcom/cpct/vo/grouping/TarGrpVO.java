package com.zjtelcom.cpct.vo.grouping;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 标签取数前端展示VO
 * @Author pengy
 * @Date 2018/7/6 17:30
 */
public class TarGrpVO implements Serializable {

    private static final long serialVersionUID = -3329995039052529860L;
    private String fitDomainName;//领域中文名
    private List<TarGrpConditionVO> tarGrpConditionVOs;//分群条件

    public String getFitDomainName() {
        return fitDomainName;
    }

    public void setFitDomainName(String fitDomainName) {
        this.fitDomainName = fitDomainName;
    }

    public List<TarGrpConditionVO> getTarGrpConditionVOs() {
        return tarGrpConditionVOs;
    }

    public void setTarGrpConditionVOs(List<TarGrpConditionVO> tarGrpConditionVOs) {
        this.tarGrpConditionVOs = tarGrpConditionVOs;
    }
}
