package com.zjtelcom.cpct.domain.strategy;

import lombok.Data;

import java.util.Date;

@Data
public class MktStrategyConfRegionRelDO {
    private Long mktStrategyConfRegionRelId;

    private Long mktStrategyConfId;

    private Long applyCityId;

    private String applyCounty;

    private String applyBranch;

    private String applyGridding;

    private Long createStaff;

    private Date createDate;

    private Long updateStaff;

    private Date updateDate;

}