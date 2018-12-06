package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/12
 * @Description:同步记录操作表
 */
@RestController
@RequestMapping("${adminPath}/synchronize")
public class SynchronizeRecordController extends BaseController {


    @Autowired
    private SynchronizeRecordService synchronizeRecordService;




    /**
     * 查询同步操作日志
     * @param params
     * @return
     */
    @RequestMapping("selectRecord")
    @CrossOrigin
    public String selectRecord(@RequestBody Map<String, Object> params){
        Map<String, Object> maps = new HashMap<>();
        try{
        maps = synchronizeRecordService.selectRecordList(params);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", e.getMessage());
            logger.error("查询同步操作日志！Exception: ", e);
        }
        return JSON.toJSONString(maps);
    }
}
