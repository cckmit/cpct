package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class InterfaceCfg extends BaseEntity {
    private Long interfaceCfgId;

    private Long evtSrcId;

    private String interfaceName;

    private String interfaceDesc;

    private String interfaceNbr;

    private String interfaceType;

    private String provider;

    private String caller;

    private String protocolType;

}