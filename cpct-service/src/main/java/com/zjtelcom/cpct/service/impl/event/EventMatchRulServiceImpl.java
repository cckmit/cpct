package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulMapper;
import com.zjtelcom.cpct.domain.event.EventList;
import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventMatchRulService;
import com.zjtelcom.cpct.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description EventMatchRulServiceImpl
 * @Author pengy
 * @Date 2018/6/21 9:46
 */
@Service
@Transactional
public class EventMatchRulServiceImpl extends BaseService implements EventMatchRulService {

    @Autowired
    private EventMatchRulMapper eventMatchRulMapper;

    /**
     * listEventMatchRul
     */
    @Override
    public List<EventMatchRulDO> listEventMatchRuls(String evtRulName) {
        List<EventMatchRulDO> eventMatchRulDOS = new ArrayList<>();
        try {
            eventMatchRulDOS = eventMatchRulMapper.listEventMatchRuls(evtRulName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:EventMatchRulServiceImpl] fail to listEventMatchRuls ", e);
        }
        return eventMatchRulDOS;
    }
}
