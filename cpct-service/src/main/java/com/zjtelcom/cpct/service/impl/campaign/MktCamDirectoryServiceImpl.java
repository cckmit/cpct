/**
 * @(#)MktCamDirectoryServiceImpl.java, 2018/9/12.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamDirectoryMapper;
import com.zjtelcom.cpct.dao.channel.CatalogItemMapper;
import com.zjtelcom.cpct.dao.channel.CatalogMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCamDirectoryDO;
import com.zjtelcom.cpct.domain.channel.CatalogItem;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.dto.campaign.MktCamDirectory;
import com.zjtelcom.cpct.dto.channel.CatalogItemDetail;
import com.zjtelcom.cpct.dto.channel.SystemUserVO;
import com.zjtelcom.cpct.dto.event.Catalog;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.service.campaign.MktCamDirectoryService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

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
    @Autowired
    private CatalogMapper catalogMapper;
    @Autowired
    private CatalogItemMapper catItemMapper;

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
            List<MktCamDirectory> mktCamDirectoryList = listCatalogItemTree();
            directoryMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            directoryMap.put("resultMsg", "查询成功！");
            directoryMap.put("mktCamDirectoryList", mktCamDirectoryList);
        } catch (Exception e) {
            e.printStackTrace();
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

    public  List<MktCamDirectory>  listCatalogItemTree() {
        List<MktCamDirectory> resultList = new ArrayList<>();

        List<CatalogItem> parentList = catItemMapper.selectByParentId(0L);
        boolean flg = false;
        if (UserUtil.getSysUserLevel().equals("C4")){
            flg = true;
        }
        for (CatalogItem parent : parentList) {
            if (flg && !"其它".equals(parent.getCatalogItemName())){
                continue;
            }
            MktCamDirectory detail = new MktCamDirectory();
            detail.setMktCamDirectoryId(parent.getCatalogItemId());
            detail.setMktCamDirectoryName(parent.getCatalogItemName());
            detail.setMktCamDirectoryParentId(parent.getParCatalogItemId());
            list(detail,flg);
            resultList.add(detail);
        }
        return resultList;
    }


    private void  list(MktCamDirectory cat,boolean flg){
        List<MktCamDirectory> childList = new ArrayList<>();
        List<CatalogItem> list = catItemMapper.selectByParentId(cat.getMktCamDirectoryId());
        for (CatalogItem cata : list) {
            if (flg && !"外场营销".equals(cata.getCatalogItemName())){
                continue;
            }
            MktCamDirectory detail = new MktCamDirectory();
            detail.setMktCamDirectoryId(cata.getCatalogItemId());
            detail.setMktCamDirectoryName(cata.getCatalogItemName());
            detail.setMktCamDirectoryParentId(cata.getParCatalogItemId());
            List<CatalogItem> xxx = catItemMapper.selectByParentId(detail.getMktCamDirectoryId());
            if (!xxx.isEmpty()){
                list(detail,flg);
            }
            childList.add(detail);
        }
        cat.setChildMktCamDirectoryList(childList);
    }



}