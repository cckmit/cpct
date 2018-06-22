package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.InterfaceCfgList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description InterfaceCfgService
 * @Author pengy
 * @Date 2018/6/22 7:09
 */
public interface InterfaceCfgService {

    List<InterfaceCfgList> listInterfaceCfg(Long evtSrcId, String interfaceName, String interfaceType);

}
