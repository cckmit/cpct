package com.zjtelcom.cpct.service.impl.api;

import com.zjtelcom.cpct.dao.event.EventMapper;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.service.api.EventApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventApiServiceImpl implements EventApiService {

    @Autowired
    private EventMapper eventMapper;  //事件总表

    @Autowired
    private EventSceneMapper eventSceneMapper; //事件场景






}
