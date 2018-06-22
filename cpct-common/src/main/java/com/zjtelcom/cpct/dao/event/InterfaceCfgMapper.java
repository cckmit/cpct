package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.InterfaceCfg;
import com.zjtelcom.cpct.domain.event.InterfaceCfgList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface InterfaceCfgMapper {

    int deleteByPrimaryKey(Long interfaceCfgId);

    int insert(InterfaceCfg record);

    InterfaceCfg selectByPrimaryKey(Long interfaceCfgId);

    List<InterfaceCfgList> listInterfaceCfg(@Param("evtSrcId") Long evtSrcId, @Param("interfaceName") String interfaceName, @Param("interfaceType") String interfaceType);

    int updateByPrimaryKey(InterfaceCfg record);
}