package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.InterfaceCfgMapper;
import com.zjtelcom.cpct.domain.event.InterfaceCfgList;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.InterfaceCfgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description InterfaceCfgServiceImpl
 * @Author pengy
 * @Date 2018/6/22 7:10
 */
@Service
@Transactional
public class InterfaceCfgServiceImpl extends BaseService implements InterfaceCfgService{

    @Autowired
    private InterfaceCfgMapper interfaceCfgMapper;

    /**
     * query InterfaceCfg list
     */
    @Override
    public List<InterfaceCfgList> listInterfaceCfg(Long evtSrcId, String interfaceName, String interfaceType) {
        List<InterfaceCfgList> lists = new ArrayList<>();
        try {
            lists = interfaceCfgMapper.listInterfaceCfg(evtSrcId,interfaceName,interfaceType);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:UserServiceImpl] fail to listEvents ", e);
        }
        return lists;
    }


}
