package com.zjtelcom.cpct.service.impl.synchronize.label;

import com.zjtelcom.cpct.service.synchronize.label.SynMessageLabelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
@Service
@Transactional
public class SynMessageLabelServiceImpl implements SynMessageLabelService{

    @Override
    public Map<String, Object> synchronizeSingleMessageLabel(Long messageLabelId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchMessageLabel(String roleName) {
        return null;
    }
}
