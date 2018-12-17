package com.zjtelcom.cpct.open.entity.event;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: anson
 * @Date: 2018/10/30
 * @Description:事件源  实体类 匹配集团openapi规则
 */
@Data
public class EventSource extends BaseEntity{

    private Long evtSrcId;//事件源标识
    private String evtSrcCode;//事件源编码
    private String evtSrcName;//事件源名称
    private String evtSrcDesc;//事件源描述
    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消
    private String statusDate;//状态时间   20171221191048格式
    private Long regionId;//记录适用区域标识，指定公共管理区域
    private String remark;//备注


}
