package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.service.synchronize.sys.SynSysStaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:用户数据同步
 */
@Service
@Transactional
public class SynSysStaffServiceImpl implements SynSysStaffService{


   


    @Override
    public Map<String, Object> synchronizeSingleStaff(Long staffId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchStaff(String roleName) {
        return null;
    }
}
