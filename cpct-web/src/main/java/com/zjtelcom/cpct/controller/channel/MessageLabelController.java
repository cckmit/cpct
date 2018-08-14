package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.request.channel.DisplayAllMessageReq;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/messageLabel")
public class MessageLabelController extends BaseController {

    @Autowired
    private MessageLabelService messageLabelService;

    /**
     * 根据信息id查询出标签
     */
    @PostMapping("queryLabelByMessage")
    @CrossOrigin
    public String queryLabelByMessage(@RequestBody Message message) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.qureyMessageLabel(message);
        return JSON.toJSONString(map);
    }

    /**
     * 查询出所有信息列表
     */
    @GetMapping("queryMessages")
    @CrossOrigin
    public String queryMessages() {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.queryMessages();
        return JSON.toJSONString(map);
    }

    /**
     * 新增标签组
     */
    @PostMapping("createLabelGroup")
    @CrossOrigin
    public String createLabelGroup(@RequestBody DisplayColumn displayColumn) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.createLabelGroup(displayColumn);
        return JSON.toJSONString(map);
    }

    /**
     * 查询出所有展示列列表
     */
    @GetMapping("queryDisplays")
    @CrossOrigin
    public String queryDisplays() {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.queryDisplays();
        return JSON.toJSONString(map);
    }

    /**
     * 保存展示列所有信息
     */
    @PostMapping("createDisplayAllMessage")
    @CrossOrigin
    public String createDisplayAllMessage(@RequestBody DisplayAllMessageReq displayAllMessageReq) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.createDisplayAllMessage(displayAllMessageReq);
        return JSON.toJSONString(map);
    }

    /**
     * 编辑展示列
     */
    @PostMapping("viewDisplayColumn")
    @CrossOrigin
    public String viewDisplayColumn(@RequestBody DisplayAllMessageReq displayAllMessageReq) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.viewDisplayColumn(displayAllMessageReq);
        return JSON.toJSONString(map);
    }

}
