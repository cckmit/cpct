package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.TagInfoModel;
import com.zjtelcom.cpct.dto.channel.TagValueInfoModel;

import java.util.List;

public interface SyncLabelService {

    RespInfo syncLabel(List<TagInfoModel> tagInfoModelList);

    RespInfo syncLabelValue(List<TagValueInfoModel> tagValueInfoModelList);
}
