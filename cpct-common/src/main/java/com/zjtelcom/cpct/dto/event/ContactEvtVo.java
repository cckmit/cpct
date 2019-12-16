package com.zjtelcom.cpct.dto.event;

import lombok.Data;

import java.util.List;

@Data
public class ContactEvtVo extends ContactEvt{

    private List<Long> interfaceIds;//接口配置标识，主键标识
}
