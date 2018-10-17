package com.zjtelcom.cpct.dubbo.service;


import com.zjtelcom.cpct.dubbo.model.RecordModel;

import java.util.Map;

public interface SyncLabelService {


    Map<String,Object> syncLabelInfo(RecordModel record);

}
