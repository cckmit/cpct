/**
 * @(#)MktCampaignServiceImpl.java, 2018/6/22.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCamItem;
import com.zjtelcom.cpct.dto.campaign.MktCampaignDetail;
import com.zjtelcom.cpct.dto.campaign.MktStrategyDetail;
import com.zjtelcom.cpct.service.campaign.MktCampaignService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/06/22 22:22
 * version: V1.0
 */
@Service
@Transactional
public class MktCampaignServiceImpl implements MktCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;

    /**
     * 新增营销活动（编码：5013010002 ）
     *
     * @param mktCampaignDetail
     * @return
     */
    @Override
    public int createMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception {

        //获取活动主表信息
        MktCampaignDO mktCampaign = new MktCampaignDO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaign, mktCampaignDetail);//映射字段
        //保存主表信息
        mktCampaignMapper.insert(mktCampaign);
        //获取主表id
        Long mainId = mktCampaign.getMktCampaignId();

        //分群信息
        List<MktCamGrpRul> mktCamGrpRuls = mktCampaignDetail.getMktCamGrpRuls();
        //todo 保存分群信息


        //策略信息
        List<MktStrategyDetail> mktCampaignStrategyDetails = mktCampaignDetail.getMktCampaignStrategyDetails();
        //todo 保存策略

        //营销活动条目
        List<MktCamItem> mktCamItems = mktCampaignDetail.getMktCamItems();
        //todo 营销活动条目

        //事件
//        List<ContactEvt> mktCampaignEvts = mktCampaignDetail.getMktCampaignEvts();
        //事件场景
//        List<EventScene> eventScenes = mktCampaignDetail.getEventScenes();
        //todo 保存事件关联




        return 0;
    }

    /**
     * 修改营销活动（编码：5013010003 ）
     *
     * @param mktCampaignDetail
     * @return
     */
    @Override
    public int modMktCampaign(MktCampaignDetail mktCampaignDetail) throws Exception {
        //映射字段
        MktCampaignDO mktCampaign = new MktCampaignDO();
        CopyPropertiesUtil.copyBean2Bean(mktCampaign, mktCampaignDetail);
        mktCampaignMapper.updateByPrimaryKey(mktCampaign);


        return 0;
    }
}