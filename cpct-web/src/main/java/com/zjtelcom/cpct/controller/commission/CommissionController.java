//package com.zjtelcom.cpct.controller.commission;
//
//import com.alibaba.fastjson.JSON;
//import com.zjtelcom.cpct.controller.BaseController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import com.web.asiainfo.zhejiang.interfaces.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("${adminPath}/commission")
//public class CommissionController extends BaseController {
//
//    @Autowired(required = false)
//    private PolicyTemplateDataService policyTemplateDataService;
//
//    //佣金政策查询
//    @RequestMapping(value = "/getCommission", method = RequestMethod.POST)
//    @CrossOrigin
//    public String getCommission() {
//        Map<String, Object> maps = new HashMap<>();
//        List<Map<String,String>> commissionList = new ArrayList<>();
//
//        List<Map<String,String>> list = new ArrayList<>();
//        Map<String,String> map = new HashMap<>();
//        map.put("policyTypeId","0");
//        list.add(map);
//        List<Map<String,String>> lists = new ArrayList<>();
//        lists = policyTemplateDataService.policyTemplateData(list);
//
//        if(lists != null) {
//            for (Map<String, String> resultMap : lists) {
//                resultMap.remove("policyTemplateId");
//                resultMap.remove("settOfferId");
//                commissionList.add(resultMap);
//            }
//        }
//
//        maps.put("Commission",commissionList);
//        return JSON.toJSONString(maps);
//    }
//}
