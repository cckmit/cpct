package cpct.dubbo.service;


import cpct.dubbo.model.RecordModel;

import java.util.Map;

public interface SyncLabelService {


    Map<String,Object> syncLabelInfo(RecordModel record);

}
