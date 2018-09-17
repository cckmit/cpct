package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.service.synchronize.sys.SynSysRoleService;
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
public class SynSysRoleServiceImpl implements SynSysRoleService {
    @Override
    public Map<String, Object> synchronizeSingleRole(Long roleId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchRole(String roleName) {
        return null;
    }
}
