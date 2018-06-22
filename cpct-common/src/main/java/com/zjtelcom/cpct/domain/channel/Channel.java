package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class Channel extends BaseEntity {
    private Long contactChlId;//'触点渠道标识'
    private String contactChlCode;//'触点渠道编码',
    private String contactChlName;//'触点渠道名称',
    private String contactChlType;//'记录渠道类型，LOVB=CHN-0017',//100000-直销渠道/110000-实体渠道/120000-电子渠道/130000-转售
    private String contactChlDesc;//'触点渠道描述',
    private Long regionId;//'记录适用区域标识，指定公共管理区域',

}
