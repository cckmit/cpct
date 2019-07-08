package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.dto.channel.DisplayColumnEntity;
import com.zjtelcom.cpct.dto.channel.ProductParam;
import com.zjtelcom.cpct.request.channel.DisplayAllMessageReq;
import com.zjtelcom.cpct.request.channel.MessageReq;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.util.MapUtil;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
    @PostMapping("queryMessages")
    @CrossOrigin
    public String queryMessages(@RequestBody HashMap<String,String> param) {
        Map<String, Object> map = new HashMap<>();
        String columnType = null;
        if (param.get("displayColumnType")!=null){
            columnType =  param.get("displayColumnType");
        }
        map = messageLabelService.queryMessages(columnType);
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
        String displayType = null;
        if (param.get("displayName")!=null){
            displayName = param.get("displayName");
        }
        if (param.get("displayType")!=null){
            displayType = param.get("displayType");
        }
        map = messageLabelService.queryDisplays(displayName,displayType);
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

    /**
     * 展示列标签展示类型配置
     */
    @PostMapping("configureLabelDisplayType")
    @CrossOrigin
    public String configureLabelDisplayType(@RequestBody HashMap<String, Object> param) {
        Map<String, Object> map = new HashMap<>();
        Long displayColumnId = 0L;
        Long labelId = 0L;
        List<String> labelDisplayTypeId = null;
        if(param.get("displayColumnId") != null) {
            displayColumnId = Long.valueOf(param.get("displayColumnId").toString());
        }
        if(param.get("labelId") != null) {
            labelId = Long.valueOf(param.get("labelId").toString());
        }
        if(param.get("labelDisplayTypeId") != null) {
            labelDisplayTypeId = (List<String>)param.get("labelDisplayTypeId");
        }
        map = messageLabelService.configureLabelDisplayType(displayColumnId,labelId,labelDisplayTypeId);
        return JSON.toJSONString(map);
    }

    /**
     * 查询展示列标签展示类型
     */
    @PostMapping("viewLabelDisplayType")
    @CrossOrigin
    public String viewLabelDisplayType(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> map = new HashMap<>();
        Long displayColumnId = 0L;
        Long labelId = 0L;
        if(param.get("displayColumnId") != null) {
            displayColumnId = Long.valueOf(param.get("displayColumnId").toString());
        }
        if(param.get("labelId") != null) {
            labelId = Long.valueOf(param.get("labelId").toString());
        }
        map = messageLabelService.viewLabelDisplayType(displayColumnId,labelId);
        return JSON.toJSONString(map);
    }

    /**
     * 分页查询出所有展示列列表
     */
    @PostMapping("listDisplaysPage")
    @CrossOrigin
    public String listDisplaysPage(@RequestBody HashMap<String,String> param) {
        Map<String, Object> map = new HashMap<>();
        String displayName = null;
        String displayType = null;
        Integer page = null;
        Integer pageSize = null;
        if (param.get("displayName")!=null){
            displayName = param.get("displayName");
        }
        if (param.get("displayType")!=null){
            displayType = param.get("displayType");
        }
        if(param.get("page") != null) {
            page = MapUtil.getIntNum(param.get("page"));
        }
        if(param.get("pageSize") != null) {
            pageSize = MapUtil.getIntNum(param.get("pageSize"));
        }
        map = messageLabelService.listDisplaysPage(displayName,displayType,page,pageSize);
        return JSON.toJSONString(map);
    }

    /**
     * 新增展示列（新）
     */
    @PostMapping("createDisplayColumn")
    @CrossOrigin
    public String createDisplayColumn(@RequestBody DisplayColumnEntity displayColumnEntity) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.createDisplayColumn(displayColumnEntity);
        return JSON.toJSONString(map);
    }

    /**
     * 修改展示列（新）
     */
    @PostMapping("editDisplayColumn")
    @CrossOrigin
    public String editDisplayColumn(@RequestBody DisplayColumnEntity displayColumnEntity) {
        Map<String, Object> map = new HashMap<>();
        map = messageLabelService.editDisplayColumn(displayColumnEntity);
        return JSON.toJSONString(map);
    }

}
