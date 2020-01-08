package com.zjtelcom.cpct.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.core.model.ResponseVoResult;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.system.MsgTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/${adminPath}/msgTemplate")
public class MsgTemplateController extends BaseController {

    @Autowired
    MsgTemplateService msgTemplateService;

    private Map<String,Object> reponseSuccess(String code, Object object){
        Map<String,Object> result = new HashMap<>();
        result.put("resultCode",code);
        result.put("content",object);
        return  result;
    }
    private Map<String,Object> reponseFail(String code,String msg){
        Map<String,Object> result = new HashMap<>();
        result.put("resultCode",code);
        result.put("resultMsg",msg);
        return  result;
    }
//   {
////        "activeInfoType": "101",
////                "combinationInfoList": [
////            {
////                "name": "测试类型",
////                "infoList": [
////                {
////                "key": "ceshi",
////                    "value": "测试"
////                }
////            ]
////            }
////        ]
//    }
//
//// "type": "101",
////"msgType": "测试类型",
////"content":"{'key'：'value'}"

    private Map<String,Object> paramsTransfer(@RequestBody  Map<String,Object> msgContent){
        Map<String,Object> result = new HashMap<>();
        if(msgContent.get("msgId")!= null){
            result.put("msgId",msgContent.get("msgId"));
        }
        result.put("type",msgContent.get("activeInfoType"));
        ArrayList<Map<String,Object>> combinationInfoList= (ArrayList<Map<String,Object>>)msgContent.get("combinationInfoList");
//    result.put("msgType",combinationInfoList.get(0).get("name"));
//    ArrayList<Map<String,Object>> infoList =  (ArrayList<Map<String,Object>>)combinationInfoList.get(0).get("infoList");
        String content = JSON.toJSONString(combinationInfoList);
        result.put("content",content);
        return  result;
    }


    //添加短信模板
    @RequestMapping(value = "/addMsgTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> addMsgTemplate(@RequestBody Map<String,Object> msgContent){

        Map<String,Object> result = new HashMap<>();
        try {
            result = msgTemplateService.addMsgTemplate(paramsTransfer(msgContent));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[MsgTemplateController 短信模板  addMsgTemplate] fail to listEvents for addMsgTemplate = {}! Exception: ", JSONArray.toJSON(msgContent), e);

            return reponseFail(CommonConstant.CODE_FAIL,"添加失败");
        }
        return  result;
    }

    //删除短信模板
    @RequestMapping(value = "/deleteMsgTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> delMsgTemplate(@RequestBody Map<String,Object> params){

        Map<String,Object> result = new HashMap<>();
        try {
            result = msgTemplateService.delMsgTemplate(params);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[MsgTemplateController 短信模板  delMsgTemplate] fail to listEvents for delMsgTemplate = {}! Exception: ", JSONArray.toJSON(params), e);
            return reponseFail(CommonConstant.CODE_FAIL,"删除失败");
        }
        return reponseSuccess(CommonConstant.CODE_SUCCESS,result);
    }

    //更新短信模板
    @RequestMapping(value = "/updateMsgTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> updateMsgTemplate(@RequestBody Map<String,Object> msgContent){
        Map<String,Object> result = new HashMap<>();
        try {
            result = msgTemplateService.updateMsgTemplate(paramsTransfer(msgContent));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[MsgTemplateController 短信模板  updateMsgTemplate] fail to listEvents for updateMsgTemplate = {}! Exception: ", JSONArray.toJSON(msgContent), e);
            return reponseFail(CommonConstant.CODE_FAIL,"更新失败");
        }
        ResponseVoResult responseVoResult = new ResponseVoResult<>(ResponseVoResult.ResponseVoEnum.SUCCESS,result);
        return result;
    }
    //根据id获取短信模板
    @RequestMapping(value = "/getMsgTemplateById", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> getMsgTemplateById(@RequestBody Map<String,Object> idParams){
        Map<String,Object> result = new HashMap<>();
        try {
            result = msgTemplateService.getMsgTemplateById(idParams);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[MsgTemplateController 短信模板  getMsgTemplateById] fail to listEvents for getMsgTemplateById = {}! Exception: ", JSONArray.toJSON(idParams), e);
            return reponseFail(CommonConstant.CODE_FAIL,"获取失败");
        }
        ResponseVoResult responseVoResult = new ResponseVoResult<>(ResponseVoResult.ResponseVoEnum.SUCCESS,result);
        return reponseSuccess(CommonConstant.CODE_SUCCESS,result);
    }


    //获取短信模板列表
    @RequestMapping(value = "/getAllMsgTemplate", method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> getAllMsgTemplate(){
        Map<String,Object> result = new HashMap<>();
        try {
            result = msgTemplateService.getAllMsgTemplate();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[MsgTemplateController 短信模板  getAllMsgTemplate] fail to listEvents for getAllMsgTemplate = {}! Exception: ", JSONArray.toJSON(""), e);
            return reponseFail(CommonConstant.CODE_FAIL,"获取所有模板失败");
        }
        ResponseVoResult responseVoResult = new ResponseVoResult<>(ResponseVoResult.ResponseVoEnum.SUCCESS,result);
        return reponseSuccess(CommonConstant.CODE_SUCCESS,result);
    }
    //模糊搜索分页获取短信模板
    @RequestMapping(value = "/getPageMsgTemplate",method = RequestMethod.POST)
    @CrossOrigin
    public Map<String,Object> getPageMsgTemplate(@RequestBody Map<String,Object> pageParams){
        Map<String,Object> result = new HashMap<>();
        try {
            result = msgTemplateService.getPageMsgTemplate(pageParams);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[MsgTemplateController 短信模板  getPageMsgTemplate] fail to listEvents for getPageMsgTemplate = {}! Exception: ", JSONArray.toJSON(pageParams), e);
            return reponseFail(CommonConstant.CODE_FAIL,"获取分页失败");
        }
        ResponseVoResult responseVoResult = new ResponseVoResult<>(ResponseVoResult.ResponseVoEnum.SUCCESS,result);
        return reponseSuccess(CommonConstant.CODE_SUCCESS,result);
    }

}
