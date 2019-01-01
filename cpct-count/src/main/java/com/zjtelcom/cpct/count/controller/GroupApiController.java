package com.zjtelcom.cpct.count.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.count.service.api.GroupApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/12/28
 * @Description:
 */
@RestController
@RequestMapping("${openPath}")
public class GroupApiController {

    private Logger log = LoggerFactory.getLogger(GroupApiController.class);

    @Autowired
    private GroupApiService groupApiService;


    @PostMapping("group")
    @CrossOrigin
    public String singleEvent(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = groupApiService.groupTrial(params);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
        }
        return JSON.toJSONString(map);
    }






}
