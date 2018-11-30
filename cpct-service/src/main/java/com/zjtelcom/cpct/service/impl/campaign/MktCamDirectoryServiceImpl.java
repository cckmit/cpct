/**
 * @(#)MktCamDirectoryServiceImpl.java, 2018/9/12.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamDirectoryMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamDirectoryDO;
import com.zjtelcom.cpct.dto.campaign.MktCamDirectory;
import com.zjtelcom.cpct.service.campaign.MktCamDirectoryService;
import com.zjtelcom.cpct.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/12 10:16
 * @version: V1.0
 */
@Service
@Transactional
public class MktCamDirectoryServiceImpl implements MktCamDirectoryService {

    @Autowired
    private MktCamDirectoryMapper mktCamDirectoryMapper;

    /**
     * 获取活动目录树
     *
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> listAllDirectoryTree() {
        Map<String, Object> directoryMap = null;
        try {
            directoryMap = new HashMap<>();
            List<MktCamDirectory> mktCamDirectoryList = getChildDirectory(0L);
            directoryMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            directoryMap.put("resultMsg", "查询成功！");
            directoryMap.put("mktCamDirectoryList", mktCamDirectoryList);
        } catch (Exception e) {
            directoryMap.put("resultCode", CommonConstant.CODE_FAIL);
            directoryMap.put("resultMsg", "查询失败！");
        }
        return directoryMap;
    }

    public List<MktCamDirectory> getChildDirectory(Long parentId) {
        List<MktCamDirectoryDO> mktCamDirectoryDOList = mktCamDirectoryMapper.selectByParentId(parentId);
        List<MktCamDirectory> mktCamDirectoryList = new ArrayList<>();
        if (mktCamDirectoryDOList != null && mktCamDirectoryDOList.size() > 0) {
            for (MktCamDirectoryDO mktCamDirectoryDO : mktCamDirectoryDOList) {
                MktCamDirectory mktCamDirectory = BeanUtil.create(mktCamDirectoryDO, new MktCamDirectory());
                List<MktCamDirectory> childDirectoryList = getChildDirectory(mktCamDirectoryDO.getMktCamDirectoryId());
                mktCamDirectory.setChildMktCamDirectoryList(childDirectoryList);
                mktCamDirectoryList.add(mktCamDirectory);
            }
        }
        return mktCamDirectoryList;
    }
}