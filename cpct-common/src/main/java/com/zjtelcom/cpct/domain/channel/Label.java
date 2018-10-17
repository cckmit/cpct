package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class Label extends BaseEntity {
    private Long injectionLabelId;
    private Long tagRowId;
    private String injectionLabelCode;//标签的编码
    private String injectionLabelName;//标签的名称
    private String injectionLabelDesc;//标签的详细描述信息
    private String labelType;//1000	客户注智标签;2000	产品注智标签;3000	销售品注智标签;4000	营销资源注智标签;5000 礼包注智标签
    private String labelValueType;//标签值的类型   1000输入型;2000	枚举型
    private String labelDataType;//1000	日期型;1100	日期时间型;1200	字符型;1300	浮点型;1400	整数型;1500	布尔型;1600	计算型

    private String rightOperand;//右操作符（标签值）
    private String conditionType;//单选多选框
    private String operator;//运算符
    private Integer scope;
    private Integer isShared;
    private String className;
    private String catalogId;
    private String labRelevantFlag;

    private String labMissRate;

    private String labObjectCode;

    private String labObject;

    private String labLevel1;

    private String labLevel1Name;

    private String labLevel2;

    private String labLevel2Name;

    private String labLevel3;

    private String labLevel3Name;

    private String labLevel4;

    private String labLevel4Name;

    private String labLevel5;

    private String labLevel5Name;

    private String labLevel6;

    private String labLevel6Name;

    private String labUpdateFeq;

    private String labBusiDesc;

    private String labTechDesc;

    private String labState;

    private String labManageType;

    private String labTagCode;


}
