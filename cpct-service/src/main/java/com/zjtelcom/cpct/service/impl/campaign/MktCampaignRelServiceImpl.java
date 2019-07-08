/**
 * @(#)MktCampaignServiceImpl.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCampaignRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:22
 * version: V1.0
 */
@Service
@Transactional
public class MktCampaignRelServiceImpl extends BaseService implements MktCampaignRelService {

    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    /**
     *  获取当前活动的父活动和 子活动
     *
     * @param mktCampaignId
     * @return
     */
    @Override
    public Map<String, Object> getMktCampaignRel(Long mktCampaignId) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 获取活动Id查询其有效的子活动
            List<MktCampaignRelDO> aCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            List<Map<String, Object>> childCampaignMapList = new ArrayList<>();
            for (MktCampaignRelDO mktCampaignRelDO : aCampaignRelDOList) {
                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignRelDO.getzMktCampaignId());
                Map<String, Object> mktCampaignMap = new HashMap<>();
                mktCampaignMap.put("mktCampaignId", mktCampaignDO.getMktCampaignId());
                mktCampaignMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName());
                childCampaignMapList.add(mktCampaignMap);
            }
            // 获取父活动
            List<Map<String, Object>> parentCampaignMapList = new ArrayList<>();
            List<MktCampaignRelDO> zCampaignRelDOList = mktCampaignRelMapper.selectByZmktCampaignId(mktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            for (MktCampaignRelDO mktCampaignRelDO : zCampaignRelDOList) {
                MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignRelDO.getaMktCampaignId());
                Map<String, Object> mktCampaignMap = new HashMap<>();
                mktCampaignMap.put("mktCampaignId", mktCampaignDO.getMktCampaignId());
                mktCampaignMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName());
                parentCampaignMapList.add(mktCampaignMap);
            }
            map.put("resultCode", CommonConstant.CODE_SUCCESS);
            map.put("resultMsg", "查询成功！");
            map.put("childCampaignMapList", childCampaignMapList);
            map.put("parentCampaignMapList", parentCampaignMapList);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", "查询失败！");
        }
        return map;
    }
}