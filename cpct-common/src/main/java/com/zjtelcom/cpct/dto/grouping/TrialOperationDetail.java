package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.domain.grouping.TrialOperation;

import java.io.Serializable;
import java.math.BigDecimal;

public class TrialOperationDetail extends TrialOperation implements Serializable {
    private String cost;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
