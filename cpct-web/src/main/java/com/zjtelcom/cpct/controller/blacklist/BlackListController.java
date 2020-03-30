package com.zjtelcom.cpct.controller.blacklist;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.blacklist.BlackListCpctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/blacklist")
public class BlackListController extends BaseController {

    @Autowired
    private BlackListCpctService blackListCpctService;

    /**
     * 导出黑名单
     *
     * @return
     */
    @PostMapping("/export")
    @CrossOrigin
    public Map<String, Object> export(){
        Map<String, Object> result = new HashMap<>();
        try {
            result = blackListCpctService.exportBlackListFile();
        } catch (Exception e) {
            logger.error("[op:BlackListController] fail to exportBlackListFile",e);
        }
        return result;
    }


    /**
     * 导入黑名单
     *
     * @return
     */
    @PostMapping("/import")
    @CrossOrigin
    public Map<String, Object> importBlackList(){
        Map<String, Object> result = new HashMap<>();
        try {
            result = blackListCpctService.importBlackListFile();
        } catch (Exception e) {
            logger.error("[op:BlackListController] fail to importBlackListFile",e);
        }
        return result;
    }
}
