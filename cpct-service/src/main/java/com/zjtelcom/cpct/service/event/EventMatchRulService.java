package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.DTO.EventMatchRulDTO;
import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import java.util.List;

/**
 * @Description EventMatchRulService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventMatchRulService {

    List<EventMatchRulDTO> listEventMatchRuls(String evtRulName);

}
