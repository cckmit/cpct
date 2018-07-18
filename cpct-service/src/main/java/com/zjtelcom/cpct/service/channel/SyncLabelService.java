package com.zjtelcom.cpct.service.channel;

import com.zjhcsoft.eagle.main.dubbo.model.policy.TagInfoModel;
import com.zjhcsoft.eagle.main.dubbo.model.policy.TagValueInfoModel;
import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.RecordModel;


import java.util.List;
import java.util.Map;

public interface SyncLabelService {

//    RespInfo syncLabel(List<TagInfoModel> tagInfoModelList);
//
//    RespInfo syncLabelValue(List<TagValueInfoModel> tagValueInfoModelList);

    Map<String,Object> syncLabelInfo(RecordModel record);
}
