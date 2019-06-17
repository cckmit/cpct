package com.zjtelcom.cpct.dubbo.service;


import com.zjtelcom.cpct.dubbo.model.RecordModel;

import java.util.Map;

public interface SyncLabelService {


    Map<String,Object> syncLabelInfo(Map<String,Object> params);

    Map<String, Object> listLabelCatalog();

    Map<String,Object> initialization();

}
