package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.service.event.ContactEvtTypeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 事件目录controller
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/eventType")
public class ContactEvtTypeController extends BaseController {

    @Autowired
    private ContactEvtTypeService contactEvtTypeService;

    /**
     * 查询事件目录列表
     */
    @RequestMapping("/qryContactEvtTypeLists")
    @CrossOrigin
    public String qryContactEvtTypeLists(QryContactEvtTypeReq qryContactEvtTypeReq) {
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
     * 新增事件目录保存
     */
    @RequestMapping("/createContactEvtType")
    @CrossOrigin
    public String createContactEvtType(ContactEvtType contactEvtType) {
        try {
            contactEvtTypeService.createContactEvtType(contactEvtType);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to createContactEvtType contactEvtType = {}! Exception: ", JSONArray.toJSON(contactEvtType), e);
            return initFailRespInfo(ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
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
    public String modContactEvtType(ContactEvtType contactEvtType) {
        try {
            contactEvtTypeService.modContactEvtType(contactEvtType);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to modContactEvtType contactEvtType = {}! Exception: ", JSONArray.toJSON(contactEvtType), e);
            return initFailRespInfo(ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

    /**
     * 删除事件目录
     */
    @RequestMapping("/delContactEvtType")
    @CrossOrigin
    public String delContactEvtType(ContactEvtType contactEvtType) {
        try {
            contactEvtTypeService.delContactEvtType(contactEvtType);
        } catch (Exception e) {
            logger.error("[op:ContactEvtTypeController] fail to delContactEvtType contactEvtType = {}! Exception: ", JSONArray.toJSON(contactEvtType), e);
            return initFailRespInfo(ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorMsg(), ErrorCode.DEL_EVENTTYPE_FAILURE.getErrorCode());
        }
        return initSuccRespInfo(null);
    }

}
