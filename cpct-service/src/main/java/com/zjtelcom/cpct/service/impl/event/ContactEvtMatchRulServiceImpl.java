package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtMatchRulService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 事件匹配规则实现类
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class ContactEvtMatchRulServiceImpl extends BaseService implements ContactEvtMatchRulService {

    @Autowired
    private ContactEvtMatchRulMapper contactEvtMatchRulMapper;

    /**
     * 事件匹配规则列表
     */
    @Override
    public Map<String,Object> listEventMatchRuls(ContactEvtMatchRul contactEvtMatchRul) {
        Map<String, Object> maps = new HashMap<>();
        List<ContactEvtMatchRul> listEventMatchRuls = contactEvtMatchRulMapper.listEventMatchRuls(contactEvtMatchRul);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("listEventMatchRuls", listEventMatchRuls);
        return maps;
    }

    /**
     * 新增事件规则
     */
    @Override
    public Map<String, Object> createContactEvtMatchRul(ContactEvtMatchRul contactEvtMatchRul) {
        Map<String, Object> maps = new HashMap<>();
        contactEvtMatchRulMapper.createContactEvtMatchRul(contactEvtMatchRul);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("contactEvtMatchRul", contactEvtMatchRul);
        return maps;
    }


}
