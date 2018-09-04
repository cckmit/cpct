/**
 * @(#)MktCampaignServiceImpl.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.CampaignVO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaignRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaignVO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConf;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.enums.ParamKeyEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCampaignRelService;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.service.strategy.MktStrategyConfService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct_prd.dao.MktCampaignPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
     * 获取当前活动的父活动 和 子活动
     *
     * @param mktCampaignId
     * @return
     */
    @Override
    public Map<String, Object> getMktCampaignRel(Long mktCampaignId) {
        Map<String, Object> map = new HashMap<>();
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
        List<MktCampaignRelDO> zCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        for (MktCampaignRelDO mktCampaignRelDO : zCampaignRelDOList) {
            MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignRelDO.getaMktCampaignId());
            Map<String, Object> mktCampaignMap = new HashMap<>();
            mktCampaignMap.put("mktCampaignId", mktCampaignDO.getMktCampaignId());
            mktCampaignMap.put("mktCampaignName", mktCampaignDO.getMktCampaignName());
            parentCampaignMapList.add(mktCampaignMap);
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("childCampaignMapList", childCampaignMapList);
        map.put("parentCampaignMapList", parentCampaignMapList);
        return map;
    }
}