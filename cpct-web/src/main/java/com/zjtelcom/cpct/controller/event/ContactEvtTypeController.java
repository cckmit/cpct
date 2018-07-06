package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.service.event.ContactEvtTypeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 事件目录controller
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/contactEvtType")
public class ContactEvtTypeController extends BaseController {

    @Autowired
    private ContactEvtTypeService contactEvtTypeService;

    /**
     * 查询事件目录树
     */
    @RequestMapping("/listEventTypes")
    @CrossOrigin
    public String listEventTypes(@RequestBody QryContactEvtTypeReq qryContactEvtTypeReq) {
        Map<String,Object> maps = new HashMap<>();
        try {
            maps = contactEvtTypeService.qryContactEvtTypeList(qryContactEvtTypeReq);
        } catch (Exception e) {
            logger.error("[op:EventTypeController] fail to listEventTypes ! Exception: ", e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询事件目录列表
     */
    @RequestMapping("/qryContactEvtTypeLists")
    @CrossOrigin
    public String qryContactEvtTypeLists(@RequestBody QryContactEvtTypeReq qryContactEvtTypeReq) {
        Map<String,Object> maps = new HashMap<>();
        try {
            maps = contactEvtTypeService.qryContactEvtTypeLists(qryContactEvtTypeReq);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to qryContactEvtTypeList qryContactEvtTypeReq = {} ! Exception: ", JSONArray.toJSON(qryContactEvtTypeReq),e);
            return initFailRespInfo(ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENTTYPE_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增事件目录
     */
    @RequestMapping("/createContactEvtType")
    @CrossOrigin
    public String createContactEvtType(@RequestBody ContactEvtType contactEvtType) {
        Map<String,Object> maps = new HashMap<>();
        try {
            maps = contactEvtTypeService.createContactEvtType(contactEvtType);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to createContactEvtType contactEvtType = {}! Exception: ", JSONArray.toJSON(contactEvtType), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查看事件目录
     */
    @RequestMapping("/viewContactEvtType")
    @CrossOrigin
    public String viewContactEvtType(@Param("evtTypeId") Long evtTypeId) {
        Map<String,Object> maps = new HashMap<>();
        try {
            maps = contactEvtTypeService.getEventTypeDTOById(evtTypeId);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to editEventTypes evtTypeId = {}! Exception: ", evtTypeId, e);
            return initFailRespInfo(ErrorCode.EDIT_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENTTYPE_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改事件目录
     */
    @RequestMapping("/modContactEvtType")
    @CrossOrigin
    public String modContactEvtType(@RequestBody ContactEvtType contactEvtType) {
        Map<String,Object> map = new HashMap<>();
        try {
            map = contactEvtTypeService.modContactEvtType(contactEvtType);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to modContactEvtType contactEvtType = {}! Exception: ", JSONArray.toJSON(contactEvtType), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(map);
    }

    /**
     * 删除事件目录
     */
    @RequestMapping("/delContactEvtType")
    @CrossOrigin
    public String delContactEvtType(@RequestBody ContactEvtType contactEvtType) {
        Map<String,Object> map = new HashMap<>();
        try {
            map = contactEvtTypeService.delContactEvtType(contactEvtType);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to delContactEvtType contactEvtType = {}! Exception: ", JSONArray.toJSON(contactEvtType), e);
            return initFailRespInfo(ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorCode());
        }
        return JSON.toJSONString(map);
    }

}