package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtTypeMapper;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtTypeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description EventTypeServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class ContactEvtTypeServiceImpl extends BaseService implements ContactEvtTypeService {

    public static final Long EVT_TYPE_ID_NULL = null;
    public static final Long PAR_EVT_TYPE_ID_NULL = null;
    public static final Long PAR_EVT_TYPE_ID_ZERO = 0L;
    public static final int LIST_SIZE_ZERO = 0;

    @Autowired
    private ContactEvtTypeMapper contactEvtTypeMapper;

    /**
     * 查询事件目录
     */
    @Override
    public  Map<String,Object> qryContactEvtTypeList(QryContactEvtTypeReq qryContactEvtTypeReq) {
        Map<String,Object> maps = new HashMap<>();
        List<ContactEvtType> contactEvtTypes = new ArrayList<>();
        List<EventTypeDTO> contactEvtTypeList = new ArrayList<>();
        try {
            //查询出父级菜单
            qryContactEvtTypeReq.setParEvtTypeId(PAR_EVT_TYPE_ID_NULL);
            qryContactEvtTypeReq.setEvtTypeId(EVT_TYPE_ID_NULL);
            contactEvtTypes = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);
            contactEvtTypeList = generateTree(contactEvtTypes);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtTypeServiceImpl] fail to qryContactEvtTypeList ", e);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtTypes",contactEvtTypeList);
        return maps;
    }

    /**
     * 查询事件目录
     */
    @Override
    public  Map<String,Object> qryContactEvtTypeLists(QryContactEvtTypeReq qryContactEvtTypeReq) {
        Map<String,Object> maps = new HashMap<>();
        List<ContactEvtType> contactEvtTypes = new ArrayList<>();
        try {
            contactEvtTypes = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtTypeServiceImpl] fail to listEventTypes ", e);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtTypes",contactEvtTypes);
        return maps;
    }

    /**
     * 新增事件目录保存
     */
    @Override
    public Map<String,Object> createContactEvtType(ContactEvtType contactEvtType) {
        Map<String,Object> maps = new HashMap<>();
        try {
            contactEvtTypeMapper.createContactEvtType(contactEvtType);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtTypeServiceImpl] fail to createContactEvtType ", e);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 查看事件目录
     */
    @Override
    public Map<String,Object> getEventTypeDTOById(Long evtTypeId) {
        Map<String,Object> maps = new HashMap<>();
        ContactEvtType contactEvtType = new ContactEvtType();
        try {
            contactEvtType = contactEvtTypeMapper.selectByPrimaryKey(evtTypeId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtTypeServiceImpl] fail to getEventTypeDTOById ", e);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtType",contactEvtType);
        return maps;
    }

    /**
     * 修改事件目录
     */
    @Override
    public Map<String,Object> modContactEvtType(ContactEvtType contactEvtType) {
        Map<String,Object> maps = new HashMap<>();
        try {
            contactEvtTypeMapper.modContactEvtType(contactEvtType);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtTypeServiceImpl] fail to modContactEvtType ", e);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除事件目录
     */
    @Override
    public Map<String,Object> delContactEvtType(ContactEvtType contactEvtType) {
        Map<String,Object> maps = new HashMap<>();
        try {
            contactEvtTypeMapper.deleteByPrimaryKey(contactEvtType.getEvtTypeId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:ContactEvtTypeServiceImpl] fail to delContactEvtType ", e);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 生成树
     */
    private List<EventTypeDTO> generateTree(List<ContactEvtType> contactEvtTypes) {
        List<EventTypeDTO> dtoList = new ArrayList<>();
        List<EventTypeDTO> eventTypeDTOS = new ArrayList<>();
        for (ContactEvtType contactEvtType : contactEvtTypes) {
            if (contactEvtType.getParEvtTypeId() == PAR_EVT_TYPE_ID_ZERO) {
                EventTypeDTO eventTypeDTO = new EventTypeDTO();
                eventTypeDTO.setEvtTypeId(contactEvtType.getEvtTypeId());
                eventTypeDTO.setParEvtTypeId(contactEvtType.getParEvtTypeId());
                eventTypeDTO.setContactEvtName(contactEvtType.getContactEvtName());
                dtoList.add(eventTypeDTO);
            }
            // 为一级菜单设置子菜单，getChild是递归调用的
            for (EventTypeDTO eventTypeDTO : dtoList) {
                eventTypeDTO.setEventTypeDTOList(getChild(eventTypeDTO.getEvtTypeId(), eventTypeDTOS));
            }
        }
        return eventTypeDTOS;
    }

    /**
     * 递归查找子菜单
     */
    private List<EventTypeDTO> getChild(Long id, List<EventTypeDTO> rootMenu) {
        // 子菜单
        List<EventTypeDTO> childList = new ArrayList<>();
        for (EventTypeDTO eventTypeDTO : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (eventTypeDTO.getParEvtTypeId() != PAR_EVT_TYPE_ID_ZERO) {
                if (eventTypeDTO.getParEvtTypeId() == id) {
                    EventTypeDTO eventTypeDTOS = new EventTypeDTO();
                    eventTypeDTO.setContactEvtName(eventTypeDTO.getContactEvtName());
                    eventTypeDTO.setEvtTypeId(eventTypeDTO.getEvtTypeId());
                    eventTypeDTO.setParEvtTypeId(eventTypeDTO.getParEvtTypeId());
                    childList.add(eventTypeDTOS);
                }
            }
        }
        // 把子菜单的子菜单再循环一遍
        for (EventTypeDTO eventTypeDTO : childList) {
            QryContactEvtTypeReq qryContactEvtTypeReq = new QryContactEvtTypeReq();
            qryContactEvtTypeReq.setEvtTypeId(EVT_TYPE_ID_NULL);
            qryContactEvtTypeReq.setParEvtTypeId(eventTypeDTO.getEvtTypeId());
            List<ContactEvtType> list = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);
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
