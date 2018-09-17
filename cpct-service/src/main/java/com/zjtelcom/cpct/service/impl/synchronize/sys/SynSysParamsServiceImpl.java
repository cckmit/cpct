package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.service.synchronize.sys.SynSysParamsService;
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
public class SynSysParamsServiceImpl implements SynSysParamsService {
    @Override
    public Map<String, Object> synchronizeSingleParam(Long paramId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchParam(String roleName) {
        return null;
    }
}
