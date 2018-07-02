package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventItemMapper;
import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dao.event.EvtSceneCamRelMapper;
import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.event.EventList;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.request.event.CreateContactEvtJtReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description EventServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class ContactEvtServiceImpl extends BaseService implements ContactEvtService {

    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventItemMapper eventItemMapper;
    @Autowired
    private EventSceneMapper eventSceneMapper;
    @Autowired
    private EvtSceneCamRelMapper evtSceneCamRelMapper;

    /**
     * 查询事件列表
     *
     * @return
     */
    @Override
    public List<EventList> listEvents(String contactEvtName) {
        List<EventList> eventLists = new ArrayList<>();
        try {
            eventLists = eventMapper.listEvents(null, contactEvtName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtServiceImpl] fail to listEvents ", e);
        }
        return eventLists;
    }

    /**
     * 新增事件
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String,Object> createContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) {
        ContactEvt contactEvt = new ContactEvt();
        try {
            //改成服务有疑问

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 事件删除
     */
    @Transactional(readOnly = false)
    @Override
    public void delEvent(Long contactEvtId) {
        try {
            eventMapper.delEvent(contactEvtId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtServiceImpl] fail to delEvent ", e);
        }
    }

    /**
     * 关闭事件
     */
    @Transactional(readOnly = false)
    @Override
    public void closeEvent(Long contactEvtId) {
        try {
            eventMapper.updateEventStatusCd(contactEvtId, StatusCode.STATUS_CODE_FAILURE.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtServiceImpl] fail to closeEvent ", e);
        }
    }

    /**
     * 编辑事件
     */
    @Override
    public EventDTO editEvent(Long contactEvtId) {
        EventDTO eventDTO = new EventDTO();
        try {
            eventDTO = (EventDTO) eventMapper.getEventById(contactEvtId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtServiceImpl] fail to editEvent ", e);
        }
        return eventDTO;
    }

    /**
     * 修改事件
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String,Object> modContactEvtJt(CreateContactEvtJtReq createContactEvtJtReq) {
        EventDO eventDO = new EventDO();
        try {

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtServiceImpl] fail to modContactEvtJt ", e);
        }
        return null;
    }


}
