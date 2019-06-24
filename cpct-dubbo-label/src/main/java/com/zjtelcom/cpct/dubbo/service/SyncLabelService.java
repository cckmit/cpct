package com.zjtelcom.cpct.dubbo.service;


import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.dubbo.model.RecordModel;

import java.util.List;
import java.util.Map;

public interface SyncLabelService {


    Map<String,Object> syncLabelInfo(Map<String,Object> params);

    Map<String, Object> listLabelCatalog();

    Map<String,Object> initialization();

    void initLabelCatalog();

}
