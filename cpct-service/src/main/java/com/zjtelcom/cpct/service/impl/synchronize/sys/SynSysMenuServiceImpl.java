package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.service.synchronize.sys.SynSysMenuService;
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
public class SynSysMenuServiceImpl implements SynSysMenuService {
    @Override
    public Map<String, Object> synchronizeSingleMenu(Long menuId, String roleName) {
        return null;
    }

    @Override
    public Map<String, Object> synchronizeBatchMenu(String roleName) {
        return null;
    }
}
