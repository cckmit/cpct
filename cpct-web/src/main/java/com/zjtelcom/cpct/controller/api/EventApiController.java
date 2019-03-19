package com.zjtelcom.cpct.controller.api;


import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.IContactTaskReceiptService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dubbo.service.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;


@RestController
@RequestMapping("${adminPath}/api")
public class EventApiController extends BaseController {


    @Autowired(required = false)
    private EventApiService eventApiService;

    @Autowired(required = false)
    private YzServ yzServ;

    @Autowired(required = false)
    private IContactTaskReceiptService iContactTaskReceiptService; //协同中心dubbo

    /**
     * 事件触发入口
     */
//    @RequestMapping("/CalculateCPC")
//    @CrossOrigin
//    public String eventInput(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
//        Map result = new HashMap();
//        try {
//            result = eventApiService.CalculateCPC(params);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return initFailRespInfo(e.getMessage(), "");
//        }
//        return initSuccRespInfo(result);
//    }

    @RequestMapping(value = "/CalculateCPCSync", method = RequestMethod.POST)
    @CrossOrigin
    public String eventInputSync(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {

//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");

        Map<String,Object> result = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        params.put("reqId","EVT" + date + getRandNum(1,999999));

        try {
            result = eventApiService.CalculateCPCSync(params);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("resultCode",CODE_FAIL);
            resultMap.put("resultMsg","失败！");
            return JSON.toJSONString(resultMap);
        }
        resultMap.put("resultCode",CODE_SUCCESS);
        resultMap.put("resultMsg",result);
        return JSON.toJSONString(resultMap);
    }


//    @RequestMapping("/SecondChannelSynergy")
//    @CrossOrigin
//    public String SecondChannelSynergy(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
//        Map result = new HashMap();
//        try {
//            result = eventApiService.secondChannelSynergy(params);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return initFailRespInfo(e.getMessage(), "");
//        }
//        return initSuccRespInfo(result);
//    }


    @RequestMapping(value = "/label", method = RequestMethod.POST)
    @CrossOrigin
    public String label(@RequestBody String params) {
        Map result = new HashMap();
        try {
            result = yzServ.queryYz(params);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }


    @RequestMapping(value = "/cpc", method = RequestMethod.POST)
    @CrossOrigin
    public String cpc(@RequestBody Map<String, Object> params) {
        Map result = new HashMap();
        try {
            result = iContactTaskReceiptService.contactTaskReceipt(params);
            if (result == null) {
                return JSON.toJSONString("失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return JSON.toJSONString(result);
    }

    public static int getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return randNum;
    }


}
