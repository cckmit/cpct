package com.zjtelcom.cpct.service.channel;

import com.zjhcsoft.eagle.main.dubbo.model.policy.TagInfoModel;
import com.zjhcsoft.eagle.main.dubbo.model.policy.TagValueInfoModel;
import com.zjtelcom.cpct.bean.RespInfo;


import java.util.List;

public interface SyncLabelService {

    RespInfo syncLabel(List<TagInfoModel> tagInfoModelList);

    RespInfo syncLabelValue(List<TagValueInfoModel> tagValueInfoModelList);
}
