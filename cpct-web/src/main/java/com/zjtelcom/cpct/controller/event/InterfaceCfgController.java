package com.zjtelcom.cpct.controller.event;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.InterfaceCfgList;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.event.InterfaceCfgService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description InterfaceCfgController
 * @Author pengy
 * @Date 2018/6/21 19:43
 */
@RestController
@RequestMapping("${adminPath}/interfaceCfg")
public class InterfaceCfgController extends BaseController {

    @Autowired
    private InterfaceCfgService interfaceCfgService;

    /**
     * query InterfaceCfg list
     */
    @RequestMapping("/listInterfaceCfg")
    @CrossOrigin
    public String listInterfaceCfg(@Param("evtSrcId") Long evtSrcId, @Param("interfaceName") String interfaceName, @Param("interfaceType") String interfaceType) {
        List<InterfaceCfgList> lists = new ArrayList<>();
        try {
            lists = interfaceCfgService.listInterfaceCfg(evtSrcId,interfaceName,interfaceType);
        } catch (Exception e) {
            logger.error("[op:InterfaceCfgController] fail to listInterfaceCfg for evtSrcId = {},interfaceName = {},interfaceType = {}! Exception: ", evtSrcId, interfaceName, interfaceType, e);
            return initFailRespInfo(ErrorCode.SEARCH_INTERFACECFG_FAILURE.getErrorMsg(), ErrorCode.SEARCH_INTERFACECFG_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(lists);
    }

}
