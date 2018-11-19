package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtTypeMapper;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;
import com.zjtelcom.cpct.dto.event.EventTypeVO;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtTypeService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtTypeService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

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
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    SynContactEvtTypeService synContactEvtTypeService;

    @Value("${sync.value}")
    private String value;

    /**
     * 查询事件目录
     */
    @Override
    public Map<String, Object> qryContactEvtTypeList(QryContactEvtTypeReq qryContactEvtTypeReq) {
        Map<String, Object> maps = new HashMap<>();
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
        maps.put("contactEvtTypeList", contactEvtTypeList);
        return maps;
    }

    /**
     * 查询事件目录
     */
    @Override
    public Map<String, Object> qryContactEvtTypeLists(QryContactEvtTypeReq qryContactEvtTypeReq) {
        Map<String, Object> maps = new HashMap<>();
        List<ContactEvtType> contactEvtTypes = new ArrayList<>();
        contactEvtTypes = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtTypes", contactEvtTypes);
        return maps;
    }

    /**
     * 新增事件目录保存
     */
    @Override
    public Map<String, Object> createContactEvtType(final ContactEvtType contactEvtType) {
        Map<String, Object> maps = new HashMap<>();
        List<EventTypeDO> evtTypes = contactEvtTypeMapper.listByEvtTypeName(contactEvtType.getContactEvtName());
        if (!evtTypes.isEmpty()){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg","已存在相同名字的目录！");
            return maps;
        }
        contactEvtType.setCreateDate(DateUtil.getCurrentTime());
        contactEvtType.setUpdateDate(DateUtil.getCurrentTime());
        contactEvtType.setStatusDate(DateUtil.getCurrentTime());
        contactEvtType.setUpdateStaff(UserUtil.loginId());
        contactEvtType.setCreateStaff(UserUtil.loginId());
        contactEvtType.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        contactEvtType.setParEvtTypeId(contactEvtType.getParEvtTypeId());
        contactEvtType.setContactEvtTypeCode("ET"+ChannelUtil.getRandomStr(6));
        contactEvtTypeMapper.createContactEvtType(contactEvtType);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtType", contactEvtType);
        maps.put("ruleEvents", null);

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synContactEvtTypeService.synchronizeSingleEventType(contactEvtType.getEvtTypeId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return maps;
    }

    /**
     * 查看事件目录
     */
    @Override
    public Map<String, Object> getEventTypeDTOById(Long evtTypeId) {
        Map<String, Object> maps = new HashMap<>();
        ContactEvtType contactEvtType = contactEvtTypeMapper.selectByPrimaryKey(evtTypeId);
        if (contactEvtType==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "目录不存在");
            return maps;
        }
        EventTypeVO vo = BeanUtil.create(contactEvtType,new EventTypeVO());
        ContactEvtType parent = contactEvtTypeMapper.selectByPrimaryKey(contactEvtType.getParEvtTypeId());
        if (parent!=null){
            vo.setParentName(parent.getContactEvtName());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtType",vo);
        return maps;
    }

    /**
     * 修改事件目录
     */
    @Override
    public Map<String, Object> modContactEvtType(final ContactEvtType contactEvtType) {
        Map<String, Object> maps = new HashMap<>();
        contactEvtType.setUpdateDate(DateUtil.getCurrentTime());
        contactEvtType.setUpdateStaff(UserUtil.loginId());
        contactEvtTypeMapper.modContactEvtType(contactEvtType);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtType", contactEvtType);
        maps.put("ruleEvents", null);

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synContactEvtTypeService.synchronizeSingleEventType(contactEvtType.getEvtTypeId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return maps;
    }

    /**
     * 删除事件目录
     */
    @Override
    public Map<String, Object> delContactEvtType(final ContactEvtType contactEvtType) {
        Map<String, Object> maps = new HashMap<>();
        ContactEvt contactEvt = new ContactEvt();
        QryContactEvtTypeReq qryContactEvtTypeReq = new QryContactEvtTypeReq();
        qryContactEvtTypeReq.setParEvtTypeId(contactEvtType.getEvtTypeId());
        contactEvt.setContactEvtTypeId(contactEvtType.getEvtTypeId());
        List<ContactEvt> contactEvtList = contactEvtMapper.listEvents(contactEvt);
        List<ContactEvtType> contactEvtTypeList = contactEvtTypeMapper.qryContactEvtTypeList(qryContactEvtTypeReq);
        if (contactEvtList.size() > 0) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "事件类型已关联事件，不可删除！");
            return maps;
        } else if (contactEvtTypeList.size() > 0) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "事件类型有子类，不可删除！");
            return maps;
        } else {
            contactEvtTypeMapper.deleteByPrimaryKey(contactEvtType.getEvtTypeId());
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", StringUtils.EMPTY);
            maps.put("ruleEvents", null);
        }

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synContactEvtTypeService.deleteSingleEventType(contactEvtType.getEvtTypeId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return maps;
    }

    /**
     * 生成树
     */
    private List<EventTypeDTO> generateTree(List<ContactEvtType> contactEvtTypes) {
        List<EventTypeDTO> dtoList = new ArrayList<>();
        for (ContactEvtType contactEvtType : contactEvtTypes) {
            if (contactEvtType.getParEvtTypeId() == PAR_EVT_TYPE_ID_ZERO) {
                EventTypeDTO eventTypeDTO = new EventTypeDTO();
                eventTypeDTO.setContactEvtName(contactEvtType.getContactEvtName());
                eventTypeDTO.setParEvtTypeId(contactEvtType.getParEvtTypeId());
                eventTypeDTO.setEvtTypeId(contactEvtType.getEvtTypeId());
                dtoList.add(eventTypeDTO);
            }
            // 为一级菜单设置子菜单，getChild是递归调用的
            for (EventTypeDTO eventTypeDTO : dtoList) {
                eventTypeDTO.setChildren(getChild(eventTypeDTO.getEvtTypeId(), contactEvtTypes));
            }
        }
        return dtoList;
    }

    /**
     * 递归查找子菜单
     */
    private List<EventTypeDTO> getChild(Long id, List<ContactEvtType> rootMenu) {
        // 子菜单
        List<EventTypeDTO> childList = new ArrayList<>();
        for (ContactEvtType eventTypeDTO : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (eventTypeDTO.getParEvtTypeId() != PAR_EVT_TYPE_ID_ZERO) {
                if (eventTypeDTO.getParEvtTypeId() == id) {
                    EventTypeDTO eventTypeDTOS = new EventTypeDTO();
                    eventTypeDTOS.setContactEvtName(eventTypeDTO.getContactEvtName());
                    eventTypeDTOS.setEvtTypeId(eventTypeDTO.getEvtTypeId());
                    eventTypeDTOS.setParEvtTypeId(eventTypeDTO.getParEvtTypeId());
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
                eventTypeDTO.setChildren(getChild(eventTypeDTO.getEvtTypeId(), rootMenu));
            }
        } // 递归退出条件
        if (childList.size() == LIST_SIZE_ZERO) {
            return null;
        }
        return childList;
    }

}
