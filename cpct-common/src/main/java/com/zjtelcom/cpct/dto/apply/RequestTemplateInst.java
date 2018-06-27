package com.zjtelcom.cpct.dto.apply;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Description 配置申请实例
 * @Author pengy
 * @Date 2018/6/26 14:29
 */
@Data
public class RequestTemplateInst{

    private Long requestTemplateInstId;//配置申请实例ID
    private String name;//名称
    private String staffNbr;//创建人工号
    private String version;//版本号
    private String desc;//描述
    private Date createDate;//创建时间
    private String templateTypeCd;//申请类型
//    private List<attr> attrList;//扩展属性

}
