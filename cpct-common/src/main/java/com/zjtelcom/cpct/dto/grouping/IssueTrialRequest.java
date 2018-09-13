package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class IssueTrialRequest implements Serializable {
    private Long batchNum;
    private List<Map<String,Object>> mktStrategyConfRuleMapList;

    public Long getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Long batchNum) {
        this.batchNum = batchNum;
    }

    public List<Map<String, Object>> getMktStrategyConfRuleMapList() {
        return mktStrategyConfRuleMapList;
    }

    public void setMktStrategyConfRuleMapList(List<Map<String, Object>> mktStrategyConfRuleMapList) {
        this.mktStrategyConfRuleMapList = mktStrategyConfRuleMapList;
    }
}
