//package com.zjtelcom.cpct.service.impl.event;
//
//import com.zjtelcom.cpct.dao.event.EventMatchRulMapper;
//import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
//import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
//import com.zjtelcom.cpct.service.BaseService;
//import com.zjtelcom.cpct.service.event.EventMatchRulService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Description 事件匹配规则实现类
// * @Author pengy
// * @Date 2018/6/21 9:46
// */
//@Service
//@Transactional
//public class EventMatchRulServiceImpl extends BaseService implements EventMatchRulService {
//
//    @Autowired
//    private EventMatchRulMapper eventMatchRulMapper;
//
//    /**
//     * 事件匹配规则列表
//     */
//    @Override
//    public List<EventMatchRulDTO> listEventMatchRuls(String evtRulName) {
//        List<EventMatchRulDTO> eventMatchRulDTOS = new ArrayList<>();
//        List<EventMatchRulDO> eventMatchRulDOS = new ArrayList<>();
//        EventMatchRulDTO eventMatchRulDTO = new EventMatchRulDTO();
//        try {
//            eventMatchRulDOS = eventMatchRulMapper.listEventMatchRuls(evtRulName);
//            for(EventMatchRulDO eventMatchRulDO :eventMatchRulDOS ){
//                eventMatchRulDTO.setEvtMatchRulId(eventMatchRulDO.getEvtMatchRulId());
//                eventMatchRulDTO.setEvtRulName(eventMatchRulDO.getEvtRulName());
//                eventMatchRulDTOS.add(eventMatchRulDTO);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("[op:EventMatchRulServiceImpl] fail to listEventMatchRuls ", e);
//        }
//        return eventMatchRulDTOS;
//    }
//}
