package com.zjtelcom.cpct.controller.blacklist;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.blacklist.BlackListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/blacklist")
public class BlackListController extends BaseController {

    @Autowired
    private BlackListService blackListService;

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
            result = blackListService.exportBlackListFile();
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
            result = blackListService.importBlackListFile();
        } catch (Exception e) {
            logger.error("[op:BlackListController] fail to importBlackListFile",e);
        }
        return result;
    }
}
