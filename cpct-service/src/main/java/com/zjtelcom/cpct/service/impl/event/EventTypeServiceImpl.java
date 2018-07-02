package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventTypeMapper;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventTypeService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventTypeServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class EventTypeServiceImpl extends BaseService implements EventTypeService {

    public static final Long EVT_TYPE_ID_NULL = null;
    public static final Long PAR_EVT_TYPE_ID_NULL = null;
    public static final Long PAR_EVT_TYPE_ID_ZERO = 0L;
    public static final int LIST_SIZE_ZERO = 0;

    @Autowired
    private EventTypeMapper eventTypeMapper;

    /**
     * 查询事件目录
     */
    @Override
    public List<EventTypeDTO> listEventTypes() {
        List<EventTypeDO> eventLists = new ArrayList<>();
        List<EventTypeDTO> eventTypeDTOS = new ArrayList<>();
        try {
            //查询出父级菜单
            eventLists = eventTypeMapper.listEventTypes(EVT_TYPE_ID_NULL, PAR_EVT_TYPE_ID_NULL);
            eventTypeDTOS = generateTree(eventLists);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventTypeServiceImpl] fail to listEventTypes ", e);
        }
        return eventTypeDTOS;
    }

    /**
     * 新增事件目录保存
     */
    @Override
    public void saveEventType(EventTypeDO eventTypeDO) {
        try {
            eventTypeMapper.saveEventType(eventTypeDO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventTypeServiceImpl] fail to saveEventType ", e);
        }
    }

    /**
     * 编辑事件目录
     */
    @Override
    public EventTypeDTO getEventTypeDTOById(Long evtTypeId) {
        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        try {
            EventTypeDO eventTypeDO =  eventTypeMapper.selectByPrimaryKey(evtTypeId);
            eventTypeDTO = (EventTypeDTO) eventTypeDO;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventTypeServiceImpl] fail to getEventTypeDTOById ", e);
        }
        return eventTypeDTO;
    }

    /**
     * 编辑事件目录保存
     */
    @Override
    public void updateEventType(EventTypeDO eventTypeDO) {
        try {
            eventTypeMapper.updateByPrimaryKey(eventTypeDO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventTypeServiceImpl] fail to updateEventType ", e);
        }
    }

    /**
     * 删除事件目录
     */
    @Override
    public void delEventType(Long evtTypeId) {
        try {
            eventTypeMapper.deleteByPrimaryKey(evtTypeId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventTypeServiceImpl] fail to delEventType ", e);
        }
    }

    /**
     * generate Tree
     */
    private List<EventTypeDTO> generateTree(List<EventTypeDO> eventLists) {
        List<EventTypeDTO> dtoList = new ArrayList<>();
        for (EventTypeDO eventTypeDO : eventLists) {
            if (eventTypeDO.getParEvtTypeId() == PAR_EVT_TYPE_ID_ZERO) {
                EventTypeDTO eventTypeDTO = new EventTypeDTO();
                eventTypeDTO.setEvtTypeId(eventTypeDO.getEvtTypeId());
                eventTypeDTO.setParEvtTypeId(eventTypeDO.getParEvtTypeId());
                eventTypeDTO.setContactEvtName(eventTypeDO.getContactEvtName());
                dtoList.add(eventTypeDTO);
            }
            // 为一级菜单设置子菜单，getChild是递归调用的
            for (EventTypeDTO eventTypeDTO : dtoList) {
                eventTypeDTO.setEventTypeDTOList(getChild(eventTypeDTO.getEvtTypeId(), eventLists));
            }
        }
        return dtoList;
    }

    /**
     * 递归查找子菜单
     */
    private List<EventTypeDTO> getChild(Long id, List<EventTypeDO> rootMenu) {
        // 子菜单
        List<EventTypeDTO> childList = new ArrayList<>();
        for (EventTypeDO eventTypeDO : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (eventTypeDO.getParEvtTypeId() != PAR_EVT_TYPE_ID_ZERO) {
                if (eventTypeDO.getParEvtTypeId() == id) {
                    EventTypeDTO eventTypeDTO = new EventTypeDTO();
                    eventTypeDTO.setEvtTypeId(eventTypeDO.getEvtTypeId());
                    eventTypeDTO.setContactEvtName(eventTypeDO.getContactEvtName());
                    eventTypeDTO.setParEvtTypeId(eventTypeDO.getParEvtTypeId());
                    childList.add(eventTypeDTO);
                }
            }
        }
        // 把子菜单的子菜单再循环一遍
        for (EventTypeDTO eventTypeDTO : childList) {
            List<EventTypeDO> list = eventTypeMapper.listEventTypes(EVT_TYPE_ID_NULL, eventTypeDTO.getEvtTypeId());
            if (list.size() != LIST_SIZE_ZERO) {
                // 递归
                eventTypeDTO.setEventTypeDTOList(getChild(eventTypeDTO.getEvtTypeId(), rootMenu));
            }
        } // 递归退出条件
        if (childList.size() == LIST_SIZE_ZERO) {
            return null;
        }
        return childList;
    }

}
