package com.zjtelcom.cpct.domain.channel;

import lombok.Data;

import java.util.Date;

@Data
public class MktVerbalCondition {
    private Long conditionId;

    private Long verbalId;

    private String leftParam;//左边参数，类型为注智标签时，对应为注智标签标识；类型为表达式时，为分群条件ID

    private String leftParamType;//参数类型1000	注智标签 2000	表达式 3000	固定值

    private String operType;//运算类型,1000> 2000< 3000==  4000!=   5000>=  6000<=  7000in   8000&   9000||   7100	not in

    private String rightParam;//右边参数，类型为注智标签时，对应为注智标签标识；类型为表达式时，为分群条件ID

    private String rightParamType;//参数类型1000	注智标签 2000	表达式 3000	固定值

    private String statusCd;

    private Long createStaff;

    private Date createDate;

    private Date statusDate;

    private String remark;



}