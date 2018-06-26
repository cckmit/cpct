package com.zjtelcom.cpct;

import lombok.Data;

import java.util.Date;

/**
 * baseEntity
 * @author pengy
 * @date 2018/4/8 10:46
 */
@Data
public abstract class BaseEntity {

    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消
    private Long createStaff;//创建人
    private Long updateStaff;//更新人
    private Date createDate;//创建时间
    private Date statusDate;//状态时间
    private Date updateDate;//更新时间
    private String remark;//备注
    private Long lanId;//本地网标识

}
