package com.zjtelcom.cpct.controller.report;

import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.report.TopicManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/${adminPath}/topic")
public class TopicManageController extends BaseController {
    @Autowired
    private TopicManagerService topicManagerService;
    //添加主题
    @PostMapping("/addTopic")
    @CrossOrigin
    public  Map<String,Object> addTopic(@RequestBody Map<String,Object> topicContent){
        Map<String,Object> result = new HashMap<>();
        try {
            result = topicManagerService.addTopic(topicContent);
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  addTopic] fail to listEvents for addTopic = {}! Exception: ", JSONArray.toJSON(topicContent), e);
            return result;
        }
        return result;
    }

    //编辑主题
    @PostMapping("/updateTopic")
    @CrossOrigin
    public  Map<String,Object> updateTopic(@RequestBody Map<String,Object> updateTopic){
        Map<String,Object> result = new HashMap<>();
        try {
            result = topicManagerService.updateTopic(updateTopic);
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  updateTopic] fail to listEvents for updateTopic = {}! Exception: ", JSONArray.toJSON(updateTopic), e);
            return result;
        }
        return result;
    }

    //修改主题状态
    @PostMapping("/updateTopicState")
    @CrossOrigin
    public  Map<String,Object> updateTopicState(@RequestBody Map<String,Object> topicState){
        Map<String,Object> result = new HashMap<>();
//       String state = topicState.get("stateCd");//1000启用，1100失效
        try {
            result = topicManagerService.updateTopicState(topicState);
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  updateTopicState] fail to listEvents for updateTopic = {}! Exception: ", JSONArray.toJSON(topicState), e);
            return result;
        }
        return result;
    }

    //删除主题
    @PostMapping("/deleteTopic")
    @CrossOrigin
    public  Map<String,Object> deleteTopic(@RequestBody Map<String,Object> deleteId){
        Map<String,Object> result = new HashMap<>();
        int topicId = (int)deleteId.get("topicId");
        try {
            result = topicManagerService.deleteTopic(topicId);
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  deleteTopic] fail to listEvents for deleteTopic = {}! Exception: ", JSONArray.toJSON(topicId), e);
            e.printStackTrace();
        }
        return  result;
    }

    //查看主题详情
    @PostMapping("/getTopicInfo")
//    @GetMapping("/getTopicInfo")
    public  Map<String,Object> getTopicInfo(@RequestBody Map<String,Object> topicIdMap){
        int topicId = (int)topicIdMap.get("topicId");
        Map<String,Object> result = new HashMap<>();
        try {
            result = topicManagerService.getTopicInfoById(topicId);
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  deleteTopic] fail to listEvents for getTopicInfo = {}! Exception: ", JSONArray.toJSON(topicId), e);
            e.printStackTrace();
        }
        return  result;
    }

    //查看所有主题
    @PostMapping("/getTopicList")
    @CrossOrigin
    public Map<String, Object> getTopicList(){

        Map<String,Object> result = new HashMap<>();
        try {
            result = topicManagerService.getAllTopic();
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  deleteTopic] fail to listEvents for getTopicList = {}! Exception: ", JSONArray.toJSON(""), e);
            e.printStackTrace();
        }
        return  result;
    }
    //分页获取主题列表
    @PostMapping("/getTopicPageList")
    @CrossOrigin
    public Map<String, Object> getTopicPageList(@RequestBody Map<String,Object> pageParams){

        Map<String,Object> result = new HashMap<>();
        try {
            result = topicManagerService.getTopicPageLists(pageParams);
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  deleteTopic] fail to listEvents for getTopicList = {}! Exception: ", JSONArray.toJSON(pageParams), e);
            e.printStackTrace();
        }
        return  result;
    }




}
