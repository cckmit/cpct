package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class InterfaceCfg extends BaseEntity {
    private Long interfaceCfgId;

    private Long evtSrcId;//事件源id

    private String interfaceName;

    private String interfaceDesc;

    private String interfaceNbr;

    private String interfaceType;//1000	服务接口;2000	文件接口;3000	数据同步接口;4000	APP探针;5000	页面探针;

    private String provider;

    private String caller;

    private String protocolType;//1000	HTTP;2000	FTP 协议类型


}