package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.request.event.ContactEvtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.request.event.CreateContactEvtReq;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 事件controller
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
@RestController
@RequestMapping("${adminPath}/contactEvt")
public class ContactEvtController extends BaseController {

    @Autowired
    private ContactEvtService contactEvtService;



    @RequestMapping("getEventRelConfig")
    @CrossOrigin
    public Map<String, Object> getEventRelConfig(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.getEventRelConfig(param);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to getEventRelConfig");
        }
        return maps;
    }

    @RequestMapping("editEventRelConfig")
    @CrossOrigin
    public Map<String, Object> editEventRelConfig(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.editEventRelConfig(param);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to editEventRelConfig");
        }
        return maps;
    }

    /**
     * 获取事件类型列表
     * @return
     */
    @RequestMapping("listMktCampaignType")
    @CrossOrigin
    public Map<String, Object> listMktCampaignType() {
        Map<String, Object> maps = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            maps = contactEvtService.listMktCampaignType(userId);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listMktCampaignType");
        }
        return maps;
    }
    /**
     * 查询事件列表（含分页）
     */
    @RequestMapping("/listEvents")
    @CrossOrigin
    public String listEvents(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.listEvents(contactEvtReq.getContactEvt(), contactEvtReq.getPage());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listEvents for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询事件列表（含分页）
     */
    @RequestMapping("/listEventsForCam")
    @CrossOrigin
    public String listEventsForCam(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.listEventsForCam(contactEvtReq.getContactEvt(), contactEvtReq.getPage());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listEvents for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询事件列表（不含分页）
     */
    @RequestMapping("/listEventNoPages")
    @CrossOrigin
    public String listEventNoPages(@RequestBody ContactEvt contactEvt) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.listEventNoPages(contactEvt);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to listEventNoPages for contactEvt = {}! Exception: ", JSONArray.toJSON(contactEvt), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    @RequestMapping("selectContactEvtByChlCode")
    @CrossOrigin
    public String selectContactEvtByChlCode(@RequestBody Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.selectContactEvtByChlCode(params);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to selectContactEvtByChlCode Exception: ", e);
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", e);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除事件
     */
    @RequestMapping("/delEvent")
    @CrossOrigin
    public String delEvent(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.delEvent(contactEvtReq.getContactEvt().getContactEvtId());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to delEvent for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }


    /**
     * 事件详情
     */
    @RequestMapping("/evtDetailsByIdList")
    @CrossOrigin
    public String evtDetailsByIdList(@RequestBody HashMap<String,Object> param) {
        Map<String, Object> maps = new HashMap<>();
        try {
            List<Integer> idList = (List<Integer>) param.get("idList");

            maps = contactEvtService.evtDetailsByIdList(idList);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to evtDetailsByIdList for contactEvt = {}! Exception: ", JSONArray.toJSON(maps), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 事件详情
     */
    @RequestMapping("/evtDetails")
    @CrossOrigin
    public String evtDetails(@RequestBody ContactEvt contactEvt) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.evtDetails(contactEvt);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to evtDetails for contactEvt = {}! Exception: ", JSONArray.toJSON(contactEvt), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增事件(集团)
     */
    @RequestMapping("/createContactEvtJt")
    @CrossOrigin
    public String createContactEvtJt(@RequestBody CreateContactEvtJtReq createContactEvtJtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.createContactEvtJt(createContactEvtJtReq);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to createContactEvtJt for createContactEvtJtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtJtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增事件
     */
    @PostMapping("createContactEvt")
    @CrossOrigin
    public String createContactEvt(@RequestBody CreateContactEvtReq createContactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.createContactEvt(createContactEvtReq);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to createContactEvt for CreateContactEvtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 开启/关闭事件
     */
    @RequestMapping("/closeEvent")
    @CrossOrigin
    public String closeEvent(@RequestBody ContactEvtReq contactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.closeEvent(contactEvtReq.getContactEvt().getContactEvtId(), contactEvtReq.getContactEvt().getStatusCd());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to closeEvent for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查看事件
     */
    @RequestMapping("/editEvent")
    @CrossOrigin
    public String editEvent(@RequestBody ContactEvt contactEvt) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.editEvent(contactEvt.getContactEvtId());
        } catch (Exception e) {
            logger.error("[op:EventController] fail to editEvent for contactEvtReq = {}! Exception: ", JSONArray.toJSON(contactEvt), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改事件（集团）
     */
    @RequestMapping("/modContactEvtJt")
    @CrossOrigin
    public String modContactEvtJt(@RequestBody CreateContactEvtJtReq createContactEvtJtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.modContactEvtJt(createContactEvtJtReq);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to modContactEvtJt for CreateContactEvtJtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtJtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改事件
     */
    @RequestMapping("/modContactEvt")
    @CrossOrigin
    public String modContactEvt(@RequestBody CreateContactEvtReq createContactEvtReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = contactEvtService.modContactEvt(createContactEvtReq);
            final Long eventId = (Long) maps.get("eventId");
        } catch (Exception e) {
            logger.error("[op:EventController] fail to modContactEvt for CreateContactEvtReq = {}! Exception: ", JSONArray.toJSON(createContactEvtReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }


    /**
     * 批量查询事件
     * @return
     */
    @RequestMapping("selectBatchByCode")
    @CrossOrigin
    public Map<String, Object> selectBatchByCode(@RequestBody Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        List<String> contactEvtCodeList = (List<String>) params.get("contactEvtCodeList");
        try {
            maps = contactEvtService.selectBatchByCode(contactEvtCodeList);
        } catch (Exception e) {
            logger.error("[op:EventController] fail to selectBatchByCode", e);
        }
        return maps;
    }

}
