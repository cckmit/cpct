package cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import cpct.dubbo.model.RecordModel;
import cpct.dubbo.service.SyncLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Description:
 * author: hyf
 * date: 2018/07/17 11:11
 * version: V1.0
 */
@Service
public class SyncLabelServiceImpl  implements SyncLabelService {
    public static final Logger logger = LoggerFactory.getLogger(SyncLabelServiceImpl.class);

    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;


    @Override
    public Map<String, Object> syncLabelInfo(RecordModel record) {
        return null;
    }
}
