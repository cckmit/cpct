package com.zjtelcom.cpct.service.impl.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.MsgTemplateMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.MsgTemplateDO;

import com.zjtelcom.cpct.service.system.MsgTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MsgTemplateServiceImpl implements MsgTemplateService {

    @Autowired
    MsgTemplateMapper msgTemplateMapper;
    @Autowired
    SysParamsMapper sysParamsMapper;

    //    查看流量类型是否已经存在
    private String isMsgTypeExist(Map<String, Object> msgContent){
        String resultMsg=null;
        boolean flag =true;
        List<MsgTemplateDO> msgTemplateDOS = msgTemplateMapper.selectAllMsgTemplate();
        for(MsgTemplateDO msgTemplateDO: msgTemplateDOS){
            if(msgContent.get("msgId")!=null){
                if(msgContent.get("msgId").equals(msgTemplateDO.getMsgId()) && msgContent.get("type").equals(msgTemplateDO.getType())){
                    break;
                }
            }
            if(msgContent.get("type").equals(msgTemplateDO.getType())){
                resultMsg = "模板类型重复";
                return resultMsg;

            }
        }

        JSONArray jsonArray = JSON.parseArray((String)msgContent.get("content"));
        HashSet set = new HashSet();

        for(int i = 0; i < jsonArray.size(); i++){
            Map<String,Object> ele = (Map<String,Object>)jsonArray.get(i);
            String name = (String)ele.get("name");
            if(set.contains(name)){
                resultMsg = "短信类型重复";
                return resultMsg;
            }else {
                set.add(name);
            }
            HashSet set2 = new HashSet();
            JSONArray infoList = (JSONArray)ele.get("infoList");
            for(int j = 0; j < infoList.size();j++){
                Map<String,Object> info = (Map<String, Object>) infoList.get(j);
//            String key =(String)info.keySet().toArray()[0];
                String key =(String) info.get("key");
                if(set2.contains(key)){
                    resultMsg = "短信内容key重复";
                    return resultMsg;
                }else{
                    set2.add(key);
                }
            }
            set2.clear();
        }
        return null;
    }


    //添加短信模板
    @Override
    public Map<String, Object> addMsgTemplate(Map<String, Object> msgContent) {
        Map<String,Object> resultMap = new HashMap<>();
        if(isMsgTypeExist(msgContent)!=null){
            String msgFail = isMsgTypeExist(msgContent);
            Map<String,Object> result = new HashMap<>();
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg",msgFail);
            return  result;
        };

        MsgTemplateDO msgTemplateDO = new MsgTemplateDO();
        msgTemplateDO.setType((String)msgContent.get("type"));
        msgTemplateDO.setTypeName("流量类型");
//        msgTemplateDO.setMsgType((String)msgContent.get("msgType"));
        msgTemplateDO.setContent((String)msgContent.get("content"));
        msgTemplateDO.setCreateDate(new Date());
        msgTemplateDO.setUpdateDate(new Date());
        try {
            msgTemplateMapper.insertMsgTemplate(msgTemplateDO);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        Map<String,Object> result = new HashMap<>();
        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("content",doToResult(msgTemplateDO));

        return result;
    }

    //根据msgId删除短信模板
    @Override
    public Map<String, Object> delMsgTemplate(Map<String, Object> params) {
        Map<String,Object> resultMap = new HashMap<>();
        try {
            msgTemplateMapper.delMsgTemplate((int)params.get("msgId"));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

        resultMap.put("result","删除成功");
        return resultMap;
    }
    //根据msgId更新短信模板
    @Override
    public Map<String, Object> updateMsgTemplate(Map<String, Object> msgContent) {
        if(isMsgTypeExist(msgContent)!=null){
            String msgFail = isMsgTypeExist(msgContent);
            Map<String,Object> result = new HashMap<>();
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg",msgFail);
            return  result;
        };
        Map<String,Object> resultMap = new HashMap<>();
        MsgTemplateDO msgTemplateDO = new MsgTemplateDO();
        msgTemplateDO.setMsgId((int)msgContent.get("msgId"));
        msgTemplateDO.setType((String)msgContent.get("type"));
//        msgTemplateDO.setMsgType((String)msgContent.get("msgType"));
        msgTemplateDO.setContent((String)msgContent.get("content"));
        msgTemplateDO.setUpdateDate(new Date());
        try {
            msgTemplateMapper.updateMsgTemplate(msgTemplateDO);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        Map<String,Object> result = new HashMap<>();
        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("content",doToResult(msgTemplateDO));

        return result;
    }
//    {
//        "activeInfoType": "101",
//            "combinationInfoList": [
//        {
//            "name": "测试类型",
//                "infoList": [
//            {
//                "key": "ceshi",
//                "value": "测试"
//            }
//            ]
//        }
//        ]
//    }

    //DO转换成前端需要格式
    private Map<String,Object> doToResult(MsgTemplateDO msgTemplateDO){
        Map<String,Object> resultMap = new HashMap<>();

        ArrayList<Map<String,Object>> combinationInfoList = new  ArrayList<Map<String,Object>>();
        Map<String,Object> tmp = new HashMap<>();
//    tmp.put("name",msgTemplateDO.getMsgType());
        tmp.put("infoList",JSON.parseArray(msgTemplateDO.getContent()));
//    combinationInfoList.add(tmp);
        resultMap.put("msgId",msgTemplateDO.getMsgId());
        resultMap.put("activeInfoType",msgTemplateDO.getType());
//    模板参数表找到对应中文
        Map<String,String> sysParamsMap = sysParamsMapper.getParamsByValue("COMBINATION_INFO",msgTemplateDO.getType());
        resultMap.put("activeInfoTypeName",sysParamsMap.get("PARAM_NAME"));
        resultMap.put("combinationInfoList",combinationInfoList);
        resultMap.put("combinationInfoList",JSON.parseArray(msgTemplateDO.getContent()));
        return  resultMap;
    }

    //根据msgId获取短信模板详情
    @Override
    public Map<String, Object> getMsgTemplateById(Map<String, Object> idParams) {
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> result= new HashMap<>();
        try {
            MsgTemplateDO msgTemplateDO = msgTemplateMapper.selectTemplateById((int)idParams.get("msgId"));
            result = doToResult(msgTemplateDO);

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        resultMap.put("result",result);
        return  resultMap;
    }

    //    获取所有短信模板
    @Override
    public Map<String, Object> getAllMsgTemplate() {
        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        try {
            List<MsgTemplateDO> msgTemplateDOS = msgTemplateMapper.selectAllMsgTemplate();
            for(MsgTemplateDO msgTemplateDO: msgTemplateDOS){
                Map<String,Object> temp = new HashMap<>();
                temp = doToResult(msgTemplateDO);
                resultList.add(temp);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        resultMap.put("result",resultList);
        return  resultMap;
    }

    //分页获取短信模板，msgType可以模糊查询
    @Override
    public Map<String, Object> getPageMsgTemplate(Map<String, Object> pageParams) {
        Map<String,Object> resultMap = new HashMap<>();
        String pageSize ="10";//默认大小
        if (pageParams.get("pageSize").toString() != ""){
            pageSize = pageParams.get("pageSize").toString();
        }
        try {
            PageHelper.startPage(Integer.parseInt(pageParams.get("pageNumber").toString()),Integer.parseInt(pageSize));
            List<MsgTemplateDO> msgTemplateDOList = msgTemplateMapper.selectPageMsgTemplate((String)pageParams.get("type"),(String)pageParams.get("msgType"),(String)pageParams.get("content"));
            List<Map<String,Object>> resultList = new ArrayList<>();
            for(MsgTemplateDO msgTemplateDO: msgTemplateDOList){
                Map<String,Object> temp = new HashMap<>();
                temp = doToResult(msgTemplateDO);
                resultList.add(temp);
            }
            resultMap.put("msgTemplateDOList",resultList);
            resultMap.put("pageInfo",new Page(new PageInfo<>(msgTemplateDOList)));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        return resultMap;
    }

}
