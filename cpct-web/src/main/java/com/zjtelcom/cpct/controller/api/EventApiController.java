package com.zjtelcom.cpct.controller.api;


import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.api.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("${adminPath}/api")
public class EventApiController extends BaseController {


    @Autowired(required = false)
    private EventApiService eventApiService;

    @Autowired(required = false)
    private IContactTaskReceiptService iContactTaskReceiptService;

    /**
     * 事件触发入口
     */
    @RequestMapping("/CalculateCPC")
    @CrossOrigin
    public String eventInput(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPC(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }

    @RequestMapping(value = "/CalculateCPCSync", method = RequestMethod.POST)
    @CrossOrigin
    public String eventInputSync(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {

        Map result = new HashMap();
        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }


    @RequestMapping("/SecondChannelSynergy")
    @CrossOrigin
    public String SecondChannelSynergy(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = eventApiService.secondChannelSynergy(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }


    @RequestMapping("/iContactTaskReceiptService")
    @CrossOrigin
    public String iContactTaskReceiptService(@RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = iContactTaskReceiptService.contactTaskReceipt(params);
        } catch (Exception e) {
            e.printStackTrace();
            return initFailRespInfo(e.getMessage(), "");
        }
        return initSuccRespInfo(result);
    }


}
