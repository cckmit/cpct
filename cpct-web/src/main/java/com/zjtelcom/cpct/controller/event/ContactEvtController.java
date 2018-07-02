//package com.zjtelcom.cpct.controller.event;
//
//import com.alibaba.fastjson.JSONArray;
//import com.zjtelcom.cpct.controller.BaseController;
//import com.zjtelcom.cpct.dto.event.EventDTO;
//import com.zjtelcom.cpct.dto.event.EventList;
//import com.zjtelcom.cpct.enums.ErrorCode;
//import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
//import com.zjtelcom.cpct.service.event.ContactEvtService;
//import org.apache.ibatis.annotations.Param;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Description 事件controller
// * @Author pengy
// * @Date 2018/6/20 17:55
// */
//@RestController
//@RequestMapping("${adminPath}/event")
//public class ContactEvtController extends BaseController {
//
//    @Autowired
//    private ContactEvtService contactEvtService;
//
//    /**
//     * 查询事件列表
//     */
//    @RequestMapping("/listEvents")
//    @CrossOrigin
//    public String listEvents(@Param("eventName") String contactEvtName) {
//        List<EventList> eventLists = new ArrayList<>();
//        try {
//            eventLists = contactEvtService.listEvents(contactEvtName);
//        } catch (Exception e) {
//            logger.error("[op:EventController] fail to listEvents for contactEvtName = {}! Exception: ",  contactEvtName, e);
//            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
//        }
//        return initSuccRespInfo(eventLists);
//    }
//
//    /**
//     * 删除事件
//     */
//    @RequestMapping("/delEvent")
//    @CrossOrigin
//    public String delEvent(@Param("eventId") Long eventId) {
//        try {
//            contactEvtService.delEvent(eventId);
//        } catch (Exception e) {
//            logger.error("[op:EventController] fail to delEvent for eventId = {}! Exception: ", eventId, e);
//            return initFailRespInfo(ErrorCode.DEL_EVENT_FAILURE.getErrorMsg(), ErrorCode.DEL_EVENT_FAILURE.getErrorCode());
//        }
//        return initSuccRespInfo(null);
//    }
//
//    /**
//     * 新增事件
//     */
//    @RequestMapping("/createContactEvtJt")
//    @CrossOrigin
//    public String createContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) {
//        try {
//            contactEvtService.createContactEvtJt(createContactEvtJtReq);
//        } catch (Exception e) {
//            logger.error("[op:EventController] fail to createContactEvtJt for createContactEvtJtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtJtReq), e);
//            return initFailRespInfo(ErrorCode.SAVE_EVENT_FAILURE.getErrorMsg(), ErrorCode.SAVE_EVENT_FAILURE.getErrorCode());
//        }
//        return initSuccRespInfo(null);
//    }
//
//    /**
//     * 删除事件
//     */
//    @RequestMapping("/closeEvent")
//    @CrossOrigin
//    public String closeEvent(@Param("eventId") Long eventId) {
//        try {
//            contactEvtService.closeEvent(eventId);
//        } catch (Exception e) {
//            logger.error("[op:EventController] fail to closeEvent for eventId = {}! Exception: ", eventId, e);
//            return initFailRespInfo(ErrorCode.CLOSE_EVENT_FAILURE.getErrorMsg(), ErrorCode.CLOSE_EVENT_FAILURE.getErrorCode());
//        }
//        return initSuccRespInfo(null);
//    }
//
//    /**
//     * 编辑事件
//     */
//    @RequestMapping("/editEvent")
//    @CrossOrigin
//    public String editEvent(@Param("eventId") Long eventId) {
//        EventDTO eventDTO = new EventDTO();
//        try {
//            eventDTO = contactEvtService.editEvent(eventId);
//        } catch (Exception e) {
//            logger.error("[op:EventController] fail to editEvent for eventId = {}! Exception: ", eventId, e);
//            return initFailRespInfo(ErrorCode.EDIT_EVENT_FAILURE.getErrorMsg(), ErrorCode.EDIT_EVENT_FAILURE.getErrorCode());
//        }
//        return initSuccRespInfo(eventDTO);
//    }
//
//    /**
//     * 修改事件
//     */
//    @RequestMapping("/modContactEvtJt")
//    @CrossOrigin
//    public String modContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) {
//        try {
//            contactEvtService.modContactEvtJt(createContactEvtJtReq);
//        } catch (Exception e) {
//            logger.error("[op:EventController] fail to updateEvent for createContactEvtJtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtJtReq), e);
//            return initFailRespInfo(ErrorCode.UPDATE_EVENT_FAILURE.getErrorMsg(), ErrorCode.UPDATE_EVENT_FAILURE.getErrorCode());
//        }
//        return initSuccRespInfo(null);
//    }
//
//}
