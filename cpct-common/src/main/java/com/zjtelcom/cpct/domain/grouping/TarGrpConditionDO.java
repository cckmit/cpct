package com.zjtelcom.cpct.domain.grouping;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class TarGrpConditionDO extends BaseEntity {

    private Long conditionId; //目标分群条件标识
    private Long tarGrpId;//目标分群标识
    private String rootFlag;//是否是根条件，1是 0	否
    private String leftParam;//左边参数，类型为注智标签时，对应为注智标签标识；类型为表达式时，为分群条件ID
    private String leftParamType;//参数类型1000	注智标签 2000	表达式 3000	固定值
    private String operType;//运算类型,1000> 2000< 3000==  4000!=   5000>=  6000<=  7000in   8000&   9000||   7100	not in
    private String rightParam;//右边参数，类型为注智标签时，对应为注智标签标识；类型为表达式时，为分群条件ID
    private String rightParamType;//参数类型1000	注智标签 2000	表达式 3000	固定值
    private String conditionText;//条件表达式的业务含义，用于前台展示
}