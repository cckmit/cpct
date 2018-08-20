package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.dto.channel.ProductParam;
import com.zjtelcom.cpct.request.channel.DisplayAllMessageReq;
import com.zjtelcom.cpct.request.channel.MessageReq;
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
     * 查询标签列表
     * @param labelIdList
     * @return
     */
    @PostMapping("qureyLabelListByIdList")
    @CrossOrigin
    public Map<String, Object> qureyLabelListByIdList(@RequestBody ProductParam labelIdList) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.qureyLabelListByIdList(labelIdList);
        return map;
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping("qureyMessageLabelByMessageIdList")
    @CrossOrigin
    public Map<String, Object> qureyMessageLabelByMessageIdList(@RequestBody MessageReq req) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.qureyMessageLabelByMessageIdList(req);
        return map;
    }

    /**
     * 删除展示列标签关联
     * @param
     * @return
     */
    @PostMapping("delColumnLabelRel")
    @CrossOrigin
    public Map<String, Object> delColumnLabelRel(@RequestBody HashMap<String,Long> param) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.delColumnLabelRel(param.get("displayId"),param.get("labelId"));
        return map;
    }

    /**
     * 删除展示列
     * @param req
     * @return
     */
    @PostMapping("delDisplayColumn")
    @CrossOrigin
    public Map<String, Object> delDisplayColumn(@RequestBody  DisplayAllMessageReq req) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.delDisplayColumn(req);
        return map;
    }

    /**
     * 获取展示列标签列表
     * @param req
     * @return
     */
    @PostMapping("queryLabelListByDisplayId")
    @CrossOrigin
    public Map<String, Object> queryLabelListByDisplayId(@RequestBody DisplayColumn req) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.queryLabelListByDisplayId(req);
        return map;
    }

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
    @PostMapping("queryDisplays")
    @CrossOrigin
    public String queryDisplays(@RequestBody HashMap<String,String> param) {
        Map<String, Object> map = new HashMap<>();
        String displayName = null;
        if (param.get("displayName")!=null){
            displayName = param.get("displayName");
        }
        map = messageLabelService.queryDisplays(displayName);
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
