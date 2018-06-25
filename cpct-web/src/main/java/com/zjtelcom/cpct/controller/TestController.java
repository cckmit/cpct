package com.zjtelcom.cpct.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description test
 * @Author pengy
 * @Date 2018/6/25 11:42
 */
@RestController
@RequestMapping("${adminPath}/test")
public class TestController extends BaseController {

    @Autowired
    private TarGrpService tarGrpService;

    @RequestMapping("/test")
    @CrossOrigin
    public String test() {
        TarGrpDetail tarGrpDetail = new TarGrpDetail();
        tarGrpDetail.setTarGrpType("1000");
        tarGrpDetail.setStatusCd("1000");
        Map<String, Object> maps = tarGrpService.createTarGrp(tarGrpDetail);
        return JSON.toJSONString(maps);

    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        System.out.println(list.size());
    }

}
