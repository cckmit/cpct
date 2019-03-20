package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zjtelcom.cpct.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/message")
public class MessageController extends BaseController {

    @Autowired
    IntfCfgParamController intfCfgParamController;
    @Autowired
    MktObjKeywordsRelController mktObjKeywordsRelController;
    @Autowired
    MktKeywordsController mktKeywordsController;
    @Autowired
    PushCtrlRulController pushCtrlRulController;
    @Autowired
    ContactChlAblAttrController contactChlAblAttrController;
    @Autowired
    ContactChlAblController contactChlAblController;
    @Autowired
    ContactChlConvertCfgController contactChlConvertCfgController;

    /*
    ** 报文控制
    */
    @PostMapping("createMessage")
    @CrossOrigin
    public Map<String,Object> createMessage(@RequestBody Map<String,Object> params) {
        Map<String,Object> result = new HashMap<>();
        JSONObject jsonObject  = JSON.parseObject(params.get("params").toString());
        Map<String,Object> map = jsonObject;
        String type = String.valueOf(map.get("type"));
        if(type.equals("null")) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","选择不能为空");
            return result;
        }
        map.remove("type");
        switch(type) {
            case "1":
                result = intfCfgParamController.createIntfCfgParam(map);
                break;
            case "2":
                result = mktObjKeywordsRelController.createMktObjKeywordsRel(map);
                break;
            case "3":
                result = mktKeywordsController.createMktKeywords(map);
                break;
            case "4":
                result = pushCtrlRulController.createPushCtrlRul(map);
                break;
            case "5":
                result = contactChlAblAttrController.createContactChlAblAttr(map);
                break;
            case "6":
                result = contactChlAblController.createContactChlAbl(map);
                break;
            case "7":
                result = contactChlConvertCfgController.createContactChlConvertCfg(map);
                break;
        }
        return result;
    }
}
