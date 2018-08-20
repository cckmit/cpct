package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.InterfaceCfg;

import java.util.Map;

public interface InterfaceCfgService {
    Map<String,Object> createInterfaceCfg(InterfaceCfg interfaceCfg);

    Map<String,Object> modInterfaceCfg(InterfaceCfg interfaceCfg);

    Map<String,Object> delInterfaceCfg(InterfaceCfg interfaceCfg);

    Map<String,Object> listInterfaceCfg(InterfaceCfg interfaceCfg);

    Map<String,Object> getInterfaceCfgDetail(InterfaceCfg interfaceCfg);

}
