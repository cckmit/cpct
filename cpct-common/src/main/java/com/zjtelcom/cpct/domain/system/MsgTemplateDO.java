package com.zjtelcom.cpct.domain.system;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class MsgTemplateDO extends BaseEntity {
    private int msgId;//模板id
    private String type; //短信模板类型编码
    private String typeName;//模板名称
    private String msgType; //消息类型
    private String content;



}
