package com.zjtelcom.cpct.dao.event;



import com.zjtelcom.cpct.domain.event.InterfaceCfg;

import java.util.List;

public interface InterfaceCfgMapper {
    int deleteByPrimaryKey(Long interfaceCfgId);

    int insert(InterfaceCfg record);

    InterfaceCfg selectByPrimaryKey(Long interfaceCfgId);

    List<InterfaceCfg> selectAll();

    int updateByPrimaryKey(InterfaceCfg record);
}